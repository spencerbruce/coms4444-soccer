package g1; 

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import sim.Game;
import sim.GameHistory;
import sim.SimPrinter;

public class Player extends sim.Player {
     // for use of calculating how many points the opponents have to reallocate each round 
     // TeamID --> Available Points
     Map<Integer, Integer> availableForReallocation = new HashMap<>();
     
     //how fast to change moving average
     float alpha = 0.2f;
     int lastRoundScore = 0;
     float avg1 = 0;
     float avg2 = 0;
     float avg3 = 0;
     int freq1 = 0;
     int freq2 = 0;
     int freq3 = 0;
     int cutoff = 50;
     int lastStrat = 0;

     /**
      * Player constructor
      *
      * @param teamID      team ID
      * @param rounds      number of rounds
      * @param seed        random seed
      * @param simPrinter  simulation printer
      *
      */
     public Player(Integer teamID, Integer rounds, Integer seed, SimPrinter simPrinter) {
          super(teamID, rounds, seed, simPrinter);
          this.simPrinter = new SimPrinter(true);
     }

     /**
      * Reallocate player goals
      *
      * @param round             current round
      * @param gameHistory       cumulative game history from all previous rounds
      * @param playerGames       state of player games before reallocation
      * @param opponentGamesMap  state of opponent games before reallocation (map of opponent team IDs to their games)
      * @return                  state of player games after reallocation
      *
      */
     public List<Game> reallocate(Integer round, GameHistory gameHistory, List<Game> playerGames, Map<Integer, List<Game>> opponentGamesMap) {
          
          // 'remember' what your team got last round to guage improvements
          lastRoundScore = gameHistory.getAllRoundPointsMap().get(round-1).get(teamID).getTotalPoints();
          this.simPrinter.println("\nStarting reallocation");
          this.simPrinter.println("Round is " + round);
          this.simPrinter.println("Last round strategy was " + lastStrat);
          this.simPrinter.println("Last round scored " + lastRoundScore);

          //update exponentially moving average for strategy that was last used
          float temp = updateStrats();
          this.simPrinter.println("Avg is: " + temp);

          // prepare containers for adjusting the player games 
          List<Game> reallocatedPlayerGames = new ArrayList<>();
          List<Game> wonGames = getWinningGames(playerGames);
          List<Game> drawnGames = getDrawnGames(playerGames);
          List<Game> lostGames = getLosingGames(playerGames);
          this.sortGamesLists(wonGames, drawnGames, lostGames);

          // Stores round available points in a map : Round # --> TeamID --> Available Points
          this.addTeamsReallocatablePts(this.teamID, wonGames, drawnGames);
          this.addOpponentsReallocatablePts(opponentGamesMap);
          // availableForReallocation.forEach((key,value) -> this.simPrinter.println("Team " + key + " => " + value));


          int currStrat = 1;
          //if a strategy hasn't been tried, try it!
          if(freq1 == 0) {
               currStrat = 1;
          }
          else if(freq2 == 0) {
               currStrat = 2;
          }
          else if(freq3 == 0) {
               currStrat = 3;
          }
          
          //otherwise use weighted randomness
          else {
               int[] stats = calcStats(avg1, avg2, avg3);
               int cutoff1 = stats[0];
               int cutoff2 = stats[1];
               int max = stats[2];
          
               int rand = random.nextInt(max);
               
               this.simPrinter.println("max is " + max);
               this.simPrinter.println("average 1 is "+avg1);
               this.simPrinter.println("average 2 is "+ avg2);
               this.simPrinter.println("average 3 is "+ avg3);
               this.simPrinter.println("cutoff1 is " + cutoff1);
               this.simPrinter.println("cutoff2 is " + cutoff2);
               this.simPrinter.println("rand is " + rand);
               if(rand < cutoff1) {
                    currStrat = 1;
               }
               else if(rand < cutoff2) {
                    currStrat = 2;
               }
               else {
                    currStrat = 3;
               }
          
          }
     
          if(currStrat == 1) {
               this.simPrinter.println("strategy here is 1");
               freq1++;
               lastStrat = 1;
               //this.simPrinter.println("Random variable: " + rand + " --> strategy 1\n");
               strategy2composite(wonGames, drawnGames, lostGames, round, gameHistory);
          }
          else if(currStrat == 2) {
               this.simPrinter.println("strategy here is 2");
               freq2++;
               lastStrat = 2;
               //this.simPrinter.println("Random variable: " + rand + " --> strategy 2\n");
               strategy5(wonGames, drawnGames, lostGames, round);
          }
          else {
               this.simPrinter.println("strategy here is 3");
               freq3++;
               lastStrat = 3;
               //this.simPrinter.println("Random variable: " + rand + " --> strategy 2\n");
               strategy2(wonGames, drawnGames, lostGames, round);
          }

          reallocatedPlayerGames.addAll(wonGames);
          reallocatedPlayerGames.addAll(drawnGames);
          reallocatedPlayerGames.addAll(lostGames);

          if(checkConstraintsSatisfied(playerGames, reallocatedPlayerGames))
               return reallocatedPlayerGames;
          this.simPrinter.println("Group 1 is breaking constraints");
          return playerGames;
     }


     /* strategy methods
          Randomly choose between the following two strategies, 50/50:
               - strategy 1 = sort wins from biggest margin --> smallest margin
                                   draws from highest points --> lowest points 
                                   losses from lowest margin --> highest margin 
                              Prioritize winning draws with random variation of points 
                              taken from wins and given to draws
               - strategy 2 = same sort as strategy 1, useful if there is a large number of losses 
                              Prioritize trying to win losses, which means sacrificing both wins and draws 
                              Sacrifice draws only if we have more reallocatable goals than the opponent 
                              Still randomizing taking goals from wins
                              give to losses
               - strategy  2 composite
               - strategy 5 

          TODO: instead of randomly selecting a strategy,
          ??? no need --> prioritize rank, prev # of wins, or something else
               sub-TODO: implement multiple strategies, calcualte how successful they were and apply weights with time
               sub-TODO: Maybe count how many games are in won/lost/drawn to determine which points to reallocate?
                    $ how much they have to reallocate
                    X how many games they won/loss/drawn (average? or current tally?)
                         ^^^ this is basically rank lol
                    $ the average performance of the previous strategies
                    Scott has been working on this! If strategy 1 has been working more than strategy 2, choose strategy 1
                    Exponentially weighted moving average -- gives more weight to the recent trials than ancient ones
                         does the excess data make this stronger or weaker? 

     */
     
     private void strategy1(List<Game> wonGames, List<Game> drawnGames, List<Game> lostGames, Integer round) {
          // as per g5's previous approach, we will reallocate goals from wins to draws in order to maximize the number of wins 
          int excessGoals = 0;
          for (Game winningGame : wonGames) {
               if (drawnGames.size() == 0 && lostGames.size() == 0) 
                    break;
               int playerGoals = winningGame.getNumPlayerGoals();
               int margin = playerGoals - winningGame.getNumOpponentGoals();
               // randomize whether we are leaving a margin of 1 or 2 on the win
               int subtractedGoals = Math.min(this.random.nextInt(2) + margin - 2, winningGame.getHalfNumPlayerGoals());
               subtractedGoals = Math.max(subtractedGoals,0);
               this.simPrinter.println("Subtracted goals from wins: " + subtractedGoals);
               excessGoals += subtractedGoals;
               winningGame.setNumPlayerGoals(playerGoals - subtractedGoals);
          }
          
          while (excessGoals > 0) {
               // now that we have the hopeful max # of points from the wins, we reallocate to draws 
               for (Game drawnGame : drawnGames) {
                    int playerGoals = drawnGame.getNumPlayerGoals();
                    int addedGoals = this.random.nextInt(2);
                    // distribute goals once for many draws, randomizing the amount 
                    // if (this.availableForReallocation.get(round).get(this.teamID) this.availableForReallocation.get(round).get(drawnGame.getID()) 
                    //      && excessGoals > 0 && playerGoals < 8) {
                    if (excessGoals > 0 && playerGoals < 8) {
                         if ((playerGoals + addedGoals) > 8) addedGoals = 1;
                         excessGoals -= addedGoals;
                         this.simPrinter.println("Added goals to draw: " + addedGoals);
                         drawnGame.setNumPlayerGoals(playerGoals + addedGoals);
                    }
               }
               // reallocate to losses if there are any left 
               for (Game lostGame : lostGames) {
                    int playerGoals = lostGame.getNumPlayerGoals();
                    // int addedGoals = 1;
                    int addedGoals = Math.min(excessGoals, Math.min(lostGame.getNumOpponentGoals() - playerGoals + 1, 8 - playerGoals));
                    // int addedGoals = Math.min(excessGoals, lostGame.getNumOpponentGoals() - playerGoals + 1);
                    // distribute goals once for many losses, randomizing the amount 
                    if (excessGoals > 0 && playerGoals < 8 && addedGoals > 0) {
                         excessGoals -= addedGoals;
                         this.simPrinter.println("Added goals to loss: " + addedGoals);
                         lostGame.setNumPlayerGoals(playerGoals + addedGoals);
                    }
               }     
          }
          this.simPrinter.println();
     }

     private void strategy2(List<Game> wonGames, List<Game> drawnGames, List<Game> lostGames, Integer round) {
          // this.simPrinter.println("Top of Strategy 2");
          // as per g5's previous approach, we will reallocate goals from wins to draws in order to maximize the number of wins 
          int excessGoals = 0;
          HashMap<Game,Integer> excessSuccess = new HashMap<>();
          for (Game winningGame : wonGames) {
               if (drawnGames.size() == 0 && lostGames.size() == 0) 
                    break;
               int playerGoals = winningGame.getNumPlayerGoals();
               int margin = playerGoals - winningGame.getNumOpponentGoals();
               // randomize whether we are leaving a margin of 1 or 2 on the win
               int subtractedGoals = Math.min(this.random.nextInt(2) + margin - 2, winningGame.getHalfNumPlayerGoals());
               subtractedGoals = Math.max(subtractedGoals,0);
               this.simPrinter.println("Subtracted goals from wins: " + subtractedGoals);
               excessGoals += subtractedGoals;
               if (subtractedGoals > 0)
                         excessSuccess.put(winningGame, subtractedGoals);
               winningGame.setNumPlayerGoals(playerGoals - subtractedGoals);
          }
          // this.simPrinter.println("here2");
          
          // give up on draws only if we have more available points than the opponent
          for (Game drawnGame : drawnGames) {
               if (this.availableForReallocation.get(this.teamID) > this.availableForReallocation.get(drawnGame.getID())) {
                    int playerGoals = drawnGame.getNumPlayerGoals();
                    int half = drawnGame.getHalfNumPlayerGoals();
                    this.simPrinter.println("Subtracted goals from draws: " + half);
                    excessGoals+= half;
                    if (half > 0)
                         excessSuccess.put(drawnGame, half);
                    drawnGame.setNumPlayerGoals(playerGoals-half);
               }
          }

          // this.simPrinter.println("here3");

          // add all goals to losses
          while (excessGoals > 0) {
               // reallocate to losses if there are any left 
               for (Game lostGame : lostGames) {
                    int playerGoals = lostGame.getNumPlayerGoals();
                    // int addedGoals = 1;
                    int addedGoals = Math.min(excessGoals, Math.min(lostGame.getNumOpponentGoals() - playerGoals + 1, 8 - playerGoals));
                    // int addedGoals = Math.min(excessGoals, lostGame.getNumOpponentGoals() - playerGoals + 1);

                    this.simPrinter.println("playerGoals is " + playerGoals);
                    this.simPrinter.println("addedGoals is " + addedGoals);
                    
                    // distribute goals once for many losses, randomizing the amount 
                    if (excessGoals > 0 && playerGoals < 8 && addedGoals > 0) {
                         excessGoals -= addedGoals;
                         this.simPrinter.println("Added goals to loss: " + addedGoals);
                         lostGame.setNumPlayerGoals(playerGoals + addedGoals);
                    }
               }

               // now that we have the hopeful max # of points from the wins, we reallocate to draws 
               for (Game drawnGame : drawnGames) {
                    int playerGoals = drawnGame.getNumPlayerGoals();
                    int addedGoals = this.random.nextInt(2);
                    // distribute goals once for many draws, randomizing the amount 
                    // if (this.availableForReallocation.get(round).get(this.teamID) this.availableForReallocation.get(round).get(drawnGame.getID()) 
                    //      && excessGoals > 0 && playerGoals < 8) {
                    if (excessGoals > 0 && playerGoals < 8) {
                         if ((playerGoals + addedGoals) > 8) addedGoals = 1;
                         excessGoals -= addedGoals;
                         this.simPrinter.println("Added goals to draw: " + addedGoals);
                         drawnGame.setNumPlayerGoals(playerGoals + addedGoals);
                    }
               }
               // add back to wins if there are any left 
               for (Game wonGame : excessSuccess.keySet()) {
                    if(excessGoals == 0) {
                         break;
                    }
                    int playerGoals = wonGame.getNumPlayerGoals();
                    int margin = excessSuccess.get(wonGame);

                    if(margin > excessGoals) {
                         margin = excessGoals;
                    }

                    //replacing that game with new score
                    wonGames.remove(wonGame);
                    wonGame.setNumPlayerGoals(playerGoals + margin);
                    wonGames.add(wonGame);
                    excessGoals -= margin;
                    this.simPrinter.println("Added goals back to won game: " + margin);
               } 
               // this.simPrinter.println("excess is "+ excessGoals);
               // this.simPrinter.println("here4");     
          }
          // this.simPrinter.println("here5\n");
     }

     private void strategy2composite(List<Game> wonGames, List<Game> drawnGames, List<Game> lostGames, Integer round, GameHistory GH) {
          // this.simPrinter.println("Top of Strategy 2");
          // as per g5's previous approach, we will reallocate goals from wins to draws in order to maximize the number of wins 
          int excessGoals = 0;
          HashMap<Game,Integer> excessSuccess = new HashMap<>();
          for (Game winningGame : wonGames) {
               if (drawnGames.size() == 0 && lostGames.size() == 0) 
                    break;
               int playerGoals = winningGame.getNumPlayerGoals();
               int margin = playerGoals - winningGame.getNumOpponentGoals();
               // only take away points if we are doing worse then the other team
               if (GH.getAllRoundRankingsMap().get(round).get(this.teamID) > GH.getAllRoundRankingsMap().get(round).get(this.teamID)) {
                    // randomize whether we are leaving a margin of 1 or 2 on the win
                    int subtractedGoals = Math.min(this.random.nextInt(2) + margin - 2, winningGame.getHalfNumPlayerGoals());
                    subtractedGoals = Math.max(subtractedGoals,0);
                    this.simPrinter.println("Subtracted goals from wins: " + subtractedGoals);
                    if (subtractedGoals > 0)
                         excessSuccess.put(winningGame, subtractedGoals);
                    excessGoals += subtractedGoals;
                    winningGame.setNumPlayerGoals(playerGoals - subtractedGoals);
               }
          }
          // this.simPrinter.println("here2");
          
          // give up on draws only if we have less available points than the opponent
          for (Game drawnGame : drawnGames) {
               if (this.availableForReallocation.get(this.teamID) < this.availableForReallocation.get(drawnGame.getID())) {
                    int playerGoals = drawnGame.getNumPlayerGoals();
                    int half = drawnGame.getHalfNumPlayerGoals();
                    this.simPrinter.println("Subtracted goals from draws: " + half);
                    excessGoals+= half;
                    if (half > 0)
                         excessSuccess.put(drawnGame, half);
                    drawnGame.setNumPlayerGoals(playerGoals-half);
               }
          }

          // add all goals to losses
          while (excessGoals > 0) {
               // reallocate to losses if there are any left 
               for (Game lostGame : lostGames) {
                    int playerGoals = lostGame.getNumPlayerGoals();
                    // int addedGoals = 1;
                    int addedGoals = Math.min(excessGoals, Math.min(lostGame.getNumOpponentGoals() - playerGoals + 1, 8 - playerGoals));

                    this.simPrinter.println("playerGoals is " + playerGoals);
                    this.simPrinter.println("addedGoals is " + addedGoals);
                    
                    // distribute goals once for many losses, randomizing the amount 
                    if (excessGoals > 0 && playerGoals < 8 && addedGoals > 0) {
                         excessGoals -= addedGoals;
                         this.simPrinter.println("Added goals to loss: " + addedGoals);
                         lostGame.setNumPlayerGoals(playerGoals + addedGoals);
                    }
               }

               // add back to wins if there are any left 
               for (Game wonGame : excessSuccess.keySet()) {
                    if(excessGoals == 0) {
                         break;
                    }
                    int playerGoals = wonGame.getNumPlayerGoals();
                    int margin = excessSuccess.get(wonGame);

                    if(margin > excessGoals) {
                         margin = excessGoals;
                    }

                    //replacing that game with new score
                    wonGames.remove(wonGame);
                    wonGame.setNumPlayerGoals(playerGoals + margin);
                    wonGames.add(wonGame);
                    excessGoals -= margin;
                    this.simPrinter.println("Added goals back to won game: " + margin);
               } 
          

          }
          // this.simPrinter.println("here5\n");
     }

     private void strategy3(List<Game> wonGames, List<Game> drawnGames, List<Game> lostGames, Integer round) {
          // Take goals from draws, reallocate to losses first, wins second
          int excessGoals = 0;
          // give up on draws only if we have more available points than the opponent
          for (Game drawnGame : drawnGames) {
               if (this.availableForReallocation.get(this.teamID) > this.availableForReallocation.get(drawnGame.getID())) {
                    int playerGoals = drawnGame.getNumPlayerGoals();
                    int half = drawnGame.getHalfNumPlayerGoals();
                    this.simPrinter.println("Subtracted goals from draws: " + half);
                    excessGoals+= half;
                    drawnGame.setNumPlayerGoals(playerGoals-half);
               }
          }

          // add all goals to losses
          while (excessGoals > 0) {
               // reallocate to losses if there are any left
               for (Game lostGame : lostGames) {
                    int playerGoals = lostGame.getNumPlayerGoals();
                    int addedGoals = Math.min(excessGoals, Math.min(lostGame.getNumOpponentGoals() - playerGoals + 1, 8 - playerGoals));

                    // distribute goals once for many losses, randomizing the amount
                    if (excessGoals > 0 && playerGoals < 8 && addedGoals > 0) {
                         excessGoals -= addedGoals;
                         this.simPrinter.println("Added goals to loss: " + addedGoals);
                         lostGame.setNumPlayerGoals(playerGoals + addedGoals);
                    }
               }
               
               // now that we have the hopeful max # of points from the wins, we reallocate to draws 
               for (Game drawnGame : drawnGames) {
                    int playerGoals = drawnGame.getNumPlayerGoals();
                    int addedGoals = this.random.nextInt(2);
                    // distribute goals once for many draws, randomizing the amount 
                    // if (this.availableForReallocation.get(round).get(this.teamID) this.availableForReallocation.get(round).get(drawnGame.getID()) 
                    //      && excessGoals > 0 && playerGoals < 8) {
                    if (excessGoals > 0 && playerGoals < 8) {
                         if ((playerGoals + addedGoals) > 8) addedGoals = 1;
                         excessGoals -= addedGoals;
                         this.simPrinter.println("Added goals to draw: " + addedGoals);
                         drawnGame.setNumPlayerGoals(playerGoals + addedGoals);
                    }
               }
               
          }
          this.simPrinter.println();
     }

     //Try to get as many games to 6 as we can
     private void strategy4(List<Game> wonGames, List<Game> drawnGames, List<Game> lostGames, Integer round) {
          //holds all the won games that have less than six goals after taking goals away
          HashMap<Game,Integer> wonGamesLessThanSix = new HashMap<>();

          int excessGoals = 0;
          //take away points from won games
          for (Game winningGame : wonGames) {
               if (drawnGames.size() == 0 && lostGames.size() == 0) 
                    break;

               int playerGoals = winningGame.getNumPlayerGoals();
               int subtractedGoals;
               
               //flag indicates a game with under six goals
               int flag = 0;
               
               //if over 6 goals, make the score 6
               if(playerGoals >= 6) {
                    subtractedGoals = playerGoals - 6;
               }
               //otherwise take all the points away we can up to half the won game
               else {
                    subtractedGoals = winningGame.getHalfNumPlayerGoals();
                    flag = 1;
               }

               //update score and excess goals
               subtractedGoals = Math.max(subtractedGoals,0);
               this.simPrinter.println("Subtracted goals from wins: " + subtractedGoals);
               excessGoals += subtractedGoals;
               winningGame.setNumPlayerGoals(playerGoals - subtractedGoals);

               //add game under six to to-add
               if(flag == 1) {
                    wonGamesLessThanSix.put(winningGame, subtractedGoals);
               }
          }

          //loop through drawn games, take away points over 6
          for (Game drawnGame : drawnGames) {
               int playerGoals = drawnGame.getNumPlayerGoals();

               //take away from draws over 6 points
               if(playerGoals > 6) {
                    int subtractedGoals = playerGoals - 6;
                    subtractedGoals = Math.max(subtractedGoals,0);
                    this.simPrinter.println("Subtracted goals from wins: " + subtractedGoals);
                    excessGoals += subtractedGoals;
                    drawnGame.setNumPlayerGoals(playerGoals - subtractedGoals);
               }
          }

          List<Game> drawnAndLosses = new ArrayList<>();
          drawnAndLosses.addAll(drawnGames);
          drawnAndLosses.addAll(lostGames);

          //sort draws and losses from highest to lowest
          drawnAndLosses.sort(
               (Game g1, Game g2) -> (g2.getNumPlayerGoals() - (g1.getNumPlayerGoals()))
          );

          for(Game game : drawnAndLosses) {
               this.simPrinter.println("score is " + game.getScoreAsString());
          }
          
          //distribute excess goals
          while (excessGoals > 0) {
               
               for (Game game : drawnAndLosses) {
                    int playerGoals = game.getNumPlayerGoals();

                    //if less goals than 6, reallocate to 6
                    if(excessGoals > 0 && playerGoals < 6) {
                         int addedGoals = 6 - playerGoals;
                         if(addedGoals > excessGoals) {
                              addedGoals = excessGoals;
                         }

                         excessGoals -= addedGoals;
                         this.simPrinter.println("Added goals to game: " + addedGoals);
                         game.setNumPlayerGoals(playerGoals + addedGoals);
                    }
               }
               // add back to wins if there are any left 
               if(excessGoals > 0)
                    for (Game wonGame : wonGamesLessThanSix.keySet()) {
                         if(excessGoals == 0) {
                              break;
                         }
                         int playerGoals = wonGame.getNumPlayerGoals();
                         int margin = wonGamesLessThanSix.get(wonGame);

                         if(margin > excessGoals) {
                              margin = excessGoals;
                         }

                         //replacing that game with new score
                         wonGames.remove(wonGame);
                         wonGame.setNumPlayerGoals(playerGoals + margin);
                         wonGames.add(wonGame);
                         excessGoals -= margin;
                         this.simPrinter.println("Added goals back to won game: " + margin);
                    }     
          }
          this.simPrinter.println();
     }
     
     //modify approach to beating teams using the amount of allocatable goals they have for a given round
     private void strategy5(List<Game> wonGames, List<Game> drawnGames, List<Game> lostGames, Integer round) {
          //holds how many points we took away from won games
          HashMap<Game,Integer> wonGamesTakenAway = new HashMap<>();
          
          int excessGoals = 0;

          List<Game> order = new ArrayList<>();
          order.addAll(wonGames);
          order.addAll(drawnGames);
          order.addAll(lostGames);

          //sort all games by how many points the opposing team has to allocate
          order.sort(
               (Game g1, Game g2) -> (availableForReallocation.get(g1.getID()) - availableForReallocation.get((g2.getID())))
          );
          
          this.simPrinter.println("lowest to highest to allocate:");
          for(Game game : order) {
               this.simPrinter.println("team " + game.getID() + " can allocate " + availableForReallocation.get(game.getID()));
          }

          //holds the teamID to the rank of how many goals they can allocate this round
          HashMap<Integer,Integer> idToAllocatableOrder = new HashMap<>();
          
          for(int i = 0; i < order.size(); i++) {
               idToAllocatableOrder.put(order.get(i).getID(), i);
          }


          //loop through won games, take away points
          for (Game winningGame : wonGames) {

               if (drawnGames.size() == 0 && lostGames.size() == 0) 
                    break;

               int playerGoals = winningGame.getNumPlayerGoals();
               int subtractedGoals;
               
               //if they are in the upper half of teams wrt allocatable goals, give up on game
               if(idToAllocatableOrder.get(winningGame.getID()) > order.size()/2) {
                    subtractedGoals = winningGame.getHalfNumPlayerGoals();
               }
               //if they are in the lower half of teams wrt allocatable goals, leave some goals
               else {
                    int winningBy = winningGame.getNumPlayerGoals() - winningGame.getNumOpponentGoals();
                    subtractedGoals = Math.min(winningBy - 2, winningGame.getHalfNumPlayerGoals());
               }

               subtractedGoals = Math.max(subtractedGoals,0);
               excessGoals += subtractedGoals;
               winningGame.setNumPlayerGoals(playerGoals - subtractedGoals);
               
               if(subtractedGoals > 0) {
                    wonGamesTakenAway.put(winningGame, subtractedGoals);
               }

               this.simPrinter.println("team rank is " + idToAllocatableOrder.get(winningGame.getID()));
               this.simPrinter.println("score is " + winningGame.getScoreAsString());
               this.simPrinter.println("Subtracted goals from wins: " + subtractedGoals);
          }

          List<Game> drawnAndLosses = new ArrayList<>();
          drawnAndLosses.addAll(lostGames);

          // give up on draws only if we have more available points than the opponent
          for (Game drawnGame : drawnGames) {
               //giving up on draw
               if (this.availableForReallocation.get(this.teamID) > this.availableForReallocation.get(drawnGame.getID())) {
                    int playerGoals = drawnGame.getNumPlayerGoals();
                    int half = drawnGame.getHalfNumPlayerGoals();
                    this.simPrinter.println("Subtracted goals from draws: " + half);
                    excessGoals+= half;
                    drawnGame.setNumPlayerGoals(playerGoals-half);
                    wonGamesTakenAway.put(drawnGame, half);
               }
               //try to win draw
               else {
                    drawnAndLosses.add(drawnGame);
               }
          }

          ////maybe sort?
          
          while (excessGoals > 0) {
               // now that we have the hopeful max # of points from the wins, we reallocate to draws 
               for (Game game : drawnAndLosses) {
                    int playerGoals = game.getNumPlayerGoals();

                    if(excessGoals > 0) {
                         int losingBy = game.getNumOpponentGoals()- game.getNumPlayerGoals();

                         int halfOpponent = game.getNumOpponentGoals()/2;

                         //if they have a lot of goals to allocate, be aggressive
                         int addedGoals;
                         if(idToAllocatableOrder.get(game.getID()) > order.size()/2) {
                              addedGoals = losingBy + 1;
                         }
                         //otherwise, be conservative, assume the opponent will take away a lot of goals from their won game
                         else {
                              addedGoals = losingBy - halfOpponent + 2;
                              addedGoals = Math.min(losingBy+1, addedGoals);
                         }

                         //ensure valid allocation
                         addedGoals = Math.max(0, addedGoals);
                         if(addedGoals + game.getNumPlayerGoals() > 8) {
                              addedGoals = 8-game.getNumPlayerGoals();
                         }
                         if(addedGoals > excessGoals) {
                              addedGoals = excessGoals;
                         }

                         excessGoals -= addedGoals;
                         this.simPrinter.println("Added goals to game: " + addedGoals);
                         game.setNumPlayerGoals(playerGoals + addedGoals);
                    }
               }
               // add back to wins if there are any left 
               if(excessGoals > 0)
                    for (Game wonGame : wonGamesTakenAway.keySet()) {
                         if(excessGoals == 0) {
                              break;
                         }
                         int playerGoals = wonGame.getNumPlayerGoals();
                         int margin = wonGamesTakenAway.get(wonGame);

                         if(margin > excessGoals) {
                              margin = excessGoals;
                         }

                         //replacing that game with new score
                         wonGames.remove(wonGame);
                         wonGame.setNumPlayerGoals(playerGoals + margin);
                         wonGames.add(wonGame);
                         excessGoals -= margin;
                         this.simPrinter.println("Added goals back to won game: " + margin);


                    }     
          }
          wonGamesTakenAway = new HashMap<>();
          this.simPrinter.println();
     }

     //calculate cutoff values for weighted randomness
     private int[] calcStats(float av1, float av2, float av3) {
         float cutoff1 = av1*av1*av1*av1;
         float cutoff2 = cutoff1 + av2*av2*av2*av2;
         float total = cutoff2 + av3*av3*av3*av3;

         cutoff1 = (int) ((cutoff1/total)*100);
         cutoff2 = (int) ((cutoff2/total)*100);
         total = 100;

         return new int[] {(int) cutoff1, (int) cutoff2, (int) total};
     }

     
     private void strategy6(List<Game> wonGames, List<Game> drawnGames, List<Game> lostGames,  Integer round) {
          HashMap<Game,Integer> wonGamesTakenAway = new HashMap<>();
          int excessGoals = 0;

          List<Game> order = new ArrayList<>();
          order.addAll(wonGames);
          order.addAll(drawnGames);
          order.addAll(lostGames);

          order.sort(
               (Game g1, Game g2) -> (availableForReallocation.get(g1.getID()) - availableForReallocation.get((g2.getID())))
          );
          
          this.simPrinter.println("lowest to highest:");
          for(Game game : order) {
               this.simPrinter.println("team " + game.getID() + " can allocate " + availableForReallocation.get(game.getID()));
          }



          HashMap<Integer,Integer> idToAllocatableOrder = new HashMap<>();

          for(int i = 0; i < order.size(); i++) {
               idToAllocatableOrder.put(order.get(i).getID(), i);
          }


          for (Game winningGame : wonGames) {

               if (drawnGames.size() == 0 && lostGames.size() == 0) 
                    break;

               //take away points from won game
               int playerGoals = winningGame.getNumPlayerGoals();

               int margin;
               
               //if they have a lot of goals to allocate, give up on game
               if(idToAllocatableOrder.get(winningGame.getID()) > order.size()/2) {
                    //System.out.println("here1");
                    margin = winningGame.getHalfNumPlayerGoals();
               }
               //if they don't have a lot of goals to allocate, leave some goals
               else {
                    //System.out.println("here2");
                    int winningBy = winningGame.getNumPlayerGoals() - winningGame.getNumOpponentGoals();
                    margin = Math.min(winningBy - 2, winningGame.getHalfNumPlayerGoals());
               }

               int subtractedGoals = margin;
               subtractedGoals = Math.max(subtractedGoals,0);
               this.simPrinter.println("team rank is " + idToAllocatableOrder.get(winningGame.getID()));
               this.simPrinter.println("score is " + winningGame.getScoreAsString());
               this.simPrinter.println("Subtracted goals from wins: " + subtractedGoals);
               excessGoals += subtractedGoals;
               winningGame.setNumPlayerGoals(playerGoals - subtractedGoals);
               
               if(subtractedGoals > 0) {
                    wonGamesTakenAway.put(winningGame, subtractedGoals);
               }
          }

          
          for(Game lostGame : lostGames) {
               int playerGoals = lostGame.getNumPlayerGoals();
               int opponentGoals = lostGame.getNumOpponentGoals();
               int margin = opponentGoals - playerGoals;
               int variable = this.random.nextInt(3);
               int addedGoals = 0;
               if(margin > 1){
                    addedGoals = opponentGoals-variable-playerGoals; 
               } else {
                    addedGoals = 1; //Makes the score draw or leaves it one point behind. can't take points from losses.
               }
               excessGoals -= addedGoals;
               lostGame.setNumPlayerGoals(playerGoals + addedGoals);
               this.simPrinter.println("Added goals to loss: " + addedGoals);
          }
          

          // add back to wins if there are any left 
          if(excessGoals > 0)
          for (Game wonGame : wonGamesTakenAway.keySet()) {
               if(excessGoals == 0) {
                    break;
               }
               int playerGoals = wonGame.getNumPlayerGoals();
               int margin = wonGamesTakenAway.get(wonGame);

               if(margin > excessGoals) {
                    margin = excessGoals;
               }

               //replacing that game with new score
               wonGames.remove(wonGame);
               wonGame.setNumPlayerGoals(playerGoals + margin);
               wonGames.add(wonGame);
               excessGoals -= margin;
               this.simPrinter.println("Added goals back to won game: " + margin);


          }  
          wonGamesTakenAway = new HashMap<>();     
          this.simPrinter.println();
     }

     private void sortGamesLists(List<Game> wonGames, List<Game> drawnGames, List<Game> lostGames) {
          // wins: biggest margin --> smallest margin
          wonGames.sort(
               (Game g1, Game g2) -> (g2.getNumPlayerGoals() - g2.getNumOpponentGoals() 
               - (g1.getNumPlayerGoals() - g1.getNumOpponentGoals()))
          );

          // draws: highest points --> lowest points 
          drawnGames.sort(
               (Game g1, Game g2) -> (g2.getNumPlayerGoals() - g1.getNumOpponentGoals())
          );

          // losses: lowest margin --> highest margin 
          lostGames.sort(
               (Game g1, Game g2) -> (g2.getNumPlayerGoals() - g2.getNumOpponentGoals() 
               - (g1.getNumPlayerGoals() - g1.getNumOpponentGoals()))
          );
       
          this.simPrinter.println("\nwon games are:");
          for(Game won : wonGames) {
               this.simPrinter.println(won.getScoreAsString());
          }
          this.simPrinter.println("\ndrawn games are:");
          for(Game won : drawnGames) {
               this.simPrinter.println(won.getScoreAsString());
          }
          this.simPrinter.println("\nlost games are:");
          for(Game won : lostGames) {
               this.simPrinter.println(won.getScoreAsString());
          }
          this.simPrinter.println();
     }
    
     // helper methods stolen from random >:D
     private List<Game> getWinningGames(List<Game> playerGames) {
    	     List<Game> winningGames = new ArrayList<>();
    	     for(Game game : playerGames) {
    		     int numPlayerGoals = game.getNumPlayerGoals();
    		     int numOpponentGoals = game.getNumOpponentGoals();
    		     if (numPlayerGoals > numOpponentGoals)
    			    winningGames.add(game.cloneGame());
    	     }
    	     return winningGames;
     }

     private List<Game> getDrawnGames(List<Game> playerGames) {
    	     List<Game> drawnGames = new ArrayList<>();
    	     for(Game game : playerGames) {
    	   	    int numPlayerGoals = game.getNumPlayerGoals();
    		    int numOpponentGoals = game.getNumOpponentGoals();
    		    if (numPlayerGoals == numOpponentGoals)
    			    drawnGames.add(game.cloneGame());
    	     }
    	     return drawnGames;
     }
     
     private List<Game> getLosingGames(List<Game> playerGames) {
    	     List<Game> losingGames = new ArrayList<>();
    	     for(Game game : playerGames) {
    		    int numPlayerGoals = game.getNumPlayerGoals();
    		    int numOpponentGoals = game.getNumOpponentGoals();
    		    if(numPlayerGoals < numOpponentGoals)
    			    losingGames.add(game.cloneGame());
    	     }
    	     return losingGames;
     }

     private void addTeamsReallocatablePts(Integer teamID, List<Game> wonGames, List<Game> drawnGames) {
          // calculates the total number of points available to a team per round and adds it to the HashMap
          int availablePoints = 0;
          for (Game g : wonGames) {
               availablePoints += g.getHalfNumPlayerGoals();
          }
          for (Game g : drawnGames) {
               availablePoints += g.getHalfNumPlayerGoals();
          }
          availableForReallocation.put(teamID, availablePoints);
     }

     private void addOpponentsReallocatablePts(Map<Integer, List<Game>> opponentGamesMap) {
          opponentGamesMap.forEach( (key,value) 
               -> addTeamsReallocatablePts(key, getWinningGames(value), getDrawnGames(value))
          );
     }

     // exponentially weighted moving average calculator 
     private float updateStrats() {
          if (lastStrat == 1) { 
               if (freq1 == 1) 
                    avg1 = lastRoundScore;
               else 
                    avg1 = alpha*lastRoundScore + (1-alpha)*avg1;
               return avg1;
          }
          if (lastStrat == 2) { 
               if (freq2 == 1) 
                    avg2 = lastRoundScore;
               else 
                    avg2 = alpha*lastRoundScore + (1-alpha)*avg2;
               return avg2;
          } 
          if (lastStrat == 3) { 
               if (freq3 == 1) 
                    avg3 = lastRoundScore;
               else 
                    avg3 = alpha*lastRoundScore + (1-alpha)*avg3;
               return avg3;
          } 
          return 0.0f;
     }
}