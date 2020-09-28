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
     // Round # --> TeamID --> Available Points
     Map<Integer, Map<Integer, Integer>> availableForReallocation = new HashMap<>();

     // Round # --> TeamID --> List< # wins, # draws, # losses> 
     Map<Integer, Map<Integer, List<Integer>>> allTeamWinsDrawsLosses = new HashMap<>();

     // TeamID --> Margin --> Average point reallocation  
          // winning a game 8-6 is very different than winning a game 2-0, even with the same margin
          // the following arrangement would be necessary, with a large number of total rounds for 
          // data to be worth it 
          // Team ID --> point value --> Win --> Margin --> Average Response
          //                         --> Draw --> Margin(0) --> Average Response
          //                         --> Loss --> Margin --> Average Response

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
          // if(round > 1) {
          //      addAllGaps(round, gameHistory, opponentGamesMap);
          //      printMap(round);
          // }

          // for(Game game : playerGames) {
          //      this.simPrinter.println("GameID is " + game.getID());
          // }

          List<Game> reallocatedPlayerGames = new ArrayList<>();
          
          List<Game> wonGames = getWinningGames(playerGames);
          List<Game> drawnGames = getDrawnGames(playerGames);
          List<Game> lostGames = getLosingGames(playerGames);
          
          for(Game lostGame : lostGames)
               if(lostGame.maxPlayerGoalsReached())
                    lostGames.remove(lostGame);
          
          this.sortGamesLists(wonGames, drawnGames, lostGames);

          // Stores round available points in a map : Round # --> TeamID --> Available Points
          this.addTeamsReallocatablePts(this.teamID, round, wonGames, drawnGames);
          this.addOpponentsReallocatablePts(opponentGamesMap, round);
          availableForReallocation.forEach((key,value) -> this.simPrinter.println("Round " + key + " => " + value));
          this.simPrinter.println();
          // this.getStatsGames(wonGames, drawnGames, lostGames);

          int cutoff = 50;
          int rand = random.nextInt(100);
     
          //int cutoff = (strategy1Avg + strategy2Avg)/2*100;
          if(rand > cutoff) {
               this.simPrinter.println("Random variable: " + rand + " --> strategy 1\n");
               strategy1(wonGames, drawnGames, lostGames, round);
          }
          else {
               this.simPrinter.println("Random variable: " + rand + " --> strategy 2\n");
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

               - strategy 2.5 = strategy 2 but without the contingency of # of reallocatable goals ???
               - strategy 2.5 = strategy 2 but with the contingency of # of games opponent has won ???
               - strategy 2.5 = strategy 2 but with the contingency of rank ???
               - strategy X = Maxing out draw each round, or max out success each round 

               sort draws in another direction? 

               - strategy 3 = take from draws, give to losses, then give to successes
               - strategy 4 = take from draws, give to success, then give to losses
               - strategy 5 = tale from draws and wins and give to draws and wins? 
     
          TODO: see how much of a threat all other teams are
               This is better than calculating it on an opponent-by-opponent basis for our round-prioritization perspective 
          TODO: instead of randomly selecting a strategy, prioritize rank, prev # of wins, or something else
               sub-TODO: implement multiple strategies, calcualte how successful they were and apply weights with time
               sub-TODO: Maybe count how many games are in won/lost/drawn to determine which points to reallocate?
                    $ how much they have to reallocate
                    X how many games they won/loss/drawn (average? or current tally?)
                         ^^^ this is basically rank lol
                    $ the average performance of the previous strategies
                    Scott has been working on this! If strategy 1 has been working more than strategy 2, choose strategy 1
                    Exponentially weighted moving average -- gives more weight to the recent trials than ancient ones
                         does the excess data make this stronger or weaker? 

       
          TODO: calculate likeliness of opponent to remove points from draw, hope that they will remove and you won't need to
               ^^ let's use the available points for now maybe?
          TOOD?: EWMA for how people reallocate goals/if it's the same across the board or not? 
               Doesn't solve the problem with the hashmap tho 
          
          TODO: implement strategies game-by-game, not round-by-round
               Does not bode well for us taking a round approach to maximizing wins or draws, for example 
               BUT can use for mini-decisions (like whether or not to give points to a drawn game)
          TOOD: take goals from winning games when the margin is small?
               tends to make results worse, maybe not ^
          TODO: check to see if opponent is in the top ranks
               not necessary, we are taking the approach round-by-round not game-by-game
          TODO: switch strategies X% into the total number of rounds?
               Naive approach, our current implementation with weighted average is stronger
          TODO: map previous teams responses based on margins 
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
          
          // give up on draws only if we have more available points than the opponent
          for (Game drawnGame : drawnGames) {
               if (this.availableForReallocation.get(round).get(this.teamID) > this.availableForReallocation.get(round).get(drawnGame.getID())) {
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

     // TODO: calculate how much a team has to reallocate, take advantage of those that are not doing well
     // by implementing this into our Win/Loss/Draw sorting
     private void addTeamsReallocatablePts(Integer teamID, Integer round, List<Game> wonGames, List<Game> drawnGames) {
          // calculates the total number of points available to a team per round and adds it to the HashMap
          int availablePoints = 0;
          for (Game g : wonGames) {
               availablePoints += g.getHalfNumPlayerGoals();
          }
          for (Game g : drawnGames) {
               availablePoints += g.getHalfNumPlayerGoals();
          }
          if (availableForReallocation.get(round) == null) {
               availableForReallocation.put(round, new HashMap<Integer, Integer>());
          }
          availableForReallocation.get(round).put(teamID, availablePoints);
     }

     private void addOpponentsReallocatablePts(Map<Integer, List<Game>> opponentGamesMap, Integer round) {
          opponentGamesMap.forEach( (key,value) 
               -> addTeamsReallocatablePts(key, round, getWinningGames(value), getDrawnGames(value))
          );
     }

     private Map<String, Integer> getStatsGames(List<Game> wonGames, List<Game> drawnGames, List<Game> lostGames) {
          //returns dictionary with the sizes of won/loss/drawn
          Map<String, Integer> roundStats = new HashMap<String, Integer>();
          roundStats.put("W", wonGames.size());
          roundStats.put("D", drawnGames.size());
          roundStats.put("L", lostGames.size());

          System.out.println(" Game Statistics " + roundStats);
          return roundStats;
     }

}
