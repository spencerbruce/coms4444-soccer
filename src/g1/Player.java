package g1; // TODO modify the package name to reflect your team

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.PriorityQueue;

import sim.Game;
import sim.GameHistory;
import sim.SimPrinter;

public class Player extends sim.Player {
     //team to (gap to allocated)
     //HashMap<Integer, Map<Integer, PriorityQueue<Integer>>> map = new HashMap();
     // team --> (margin --> avg)
     //HashMap<Integer, Map<Integer, Double>> map = new HashMap<>();
     // gameID -> teamID
     //HashMap<Integer, Integer> gameIDToTeam = new HashMap<>();
     //gap to (frequency, avg allocated across all teams)
     HashMap<Integer, ArrayList<Double>> map = new HashMap<>();

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
          System.out.println("\nRound is " + round);
          // if(round > 1) {
          //      addAllGaps(round, gameHistory, opponentGamesMap);
          //      printMap(round);
          // }

          // for(Game game : playerGames) {
          //      System.out.println("GameID is " + game.getID());
          // }

          //don't use:
          //nothing with gameHistory
          //disregard opponentGamesMap
               // used to detect trends in opponent's reallocations 
          
          
          //use:
          //player games, indexed by round
          //how many we have available to reallocate?
          
          
          // if our team won, remove as many points as possible (half at most)
          // this ensures a win while maximizing points 


          // if it was a draw, either remove all points or do not remove any and hope the opponent removes points
          

             // strategy for future deliverable? vvv
          //sort by won games how much we won by
		//sort lost games by how much we lost by
          //match these up
          // apparently we don't have access to who we played so we cannot predict how a certain team will respond to us 

          // if we won:
               // take away our score - opponents score + 1 so long as our score / 2 is not > this number 

          //information available to us:
          //round rankings for every round
          //average rankings for every round
          //all games all teams have for every round
          //all rounds everyone's points
          //all rounds cumulative points map


          //helper methods:
          //sort

          //methods:
          //1. calculate what opponent is doing -> hashmap stuff
          //2. minimax
          //3. aim for 18 points
          //4. minimize dead weight games
           
          List<Game> reallocatedPlayerGames = new ArrayList<>();
          
          List<Game> wonGames = getWinningGames(playerGames);
          List<Game> drawnGames = getDrawnGames(playerGames);
          List<Game> lostGames = getLosingGames(playerGames);
          
          // List<Game> lostOrDrawnGamesWithReallocationCapacity = new ArrayList<>(lostGames);
          // lostOrDrawnGamesWithReallocationCapacity.addAll(drawnGames);
          for(Game lostGame : lostGames)
               if(lostGame.maxPlayerGoalsReached())
                    lostGames.remove(lostGame);
          for(Game drawnGame : drawnGames)
               if(drawnGame.maxPlayerGoalsReached())
                    drawnGames.remove(drawnGame);
          
               
          

               //if we're more than double the score of the opponent, remove score/2
               //else remove our score-their score - 1

               //allocate to random game


               //class 2:
               //sort
               //average points each other team takes away
               //hashset int -> list<int> (margin -> list of how many the opponents changed)
               //aim for 17/18 amount of points


               // sort by max margin for wins, all draws, then smallest margin for losses
               // within each game, find the smallest point games which we won -- take away those games (sacrificial loss)
               // we can further the sorting algorithm by adding feasibility of winning to each category ^ 
                    // take top 6 in this sort
                    // then starting from 6 and working up to 1, allocate goals

               //how many other team has to reallocate?

               //information available to us:


               //helper methods:
               //sort
                    // win: biggest margin --> smallest margin
                    // draw: lowest points --> highest points 
                    // loss: lowest margin --> highest margin 
                    
               //check likelihood of win
                    //uses opponents player stats/history 
               //check for # of possible points in player + opponent's reallocation hand 
               //

               //methods:
               //1. calculate what opponent is doing -> hashmap stuff
               //2. minimax (1 opponent vs 9 opponents)?
               //3. aim for 18 points
               //4. minimize dead weight games

          //have enough data, will go opponent expected value route
          // VSCode glitching w/ internet caused lossy code here vvv
          // if(round >= 5) {
          //      List<Game> toAdd = new ArrayList<>(drawnGames);
          //      toAdd.addAll(lostGames);

          //      int toAllocate = 0;
          //      for(Game game : wonGames) {
          //           int margin = game.getNumPlayerGoals() - game.getNumOpponentGoals();
          //           expVal = getExpectedVal(margin);

          //           if(margin > 1) {
          //                toAllocate += margin - 1;
                         
          //           }
          //      }

               //need to calculate expected value of opponent for each game
               
               //want to add 1 above expected value to as many games as we can

               //sort by score - expected value for wins highest to lowest
               //#available for allocation

               //while we have available goals to allocate
               //sort by expected value - current for draws/losses lowest to highest

               
          // }
             
          /*     
               ArrayList<Game> toReturn = new ArrayList<>();
               toReturn.addAll(wonGames);
               toReturn.addAll(toAdd);
               return toReturn;ws: lowest poitns --> highest points 

          drawnGames.sort(
               (Game g1, Game g2) -> (g1.getNumPlayerGoals() - g2.getNumOpponentGoals())
          );

          // losses: lowest margin --> highest margin 
          lostGames.sort(
               (Game g1, Game g2) -> (g2.getNumPlayerGoals() - g2.getNumOpponentGoals() 
               - (g1.getNumPlayerGoals() - g1.getNumOpponentGoals()))
          );

          System.out.println("won games are:\nP O\n---");
          }
          System.out.println("\nlost games are:\nP O\n---");
          for(Game lost : lostGames) {
               System.out.println(lost.getScoreAsString());
          }
          */


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

          System.out.println("won games are:");
          for(Game won : wonGames) {
               System.out.println(won.getScoreAsString());
          }
          System.out.println("\ndrawn games are:");
          for(Game won : drawnGames) {
               System.out.println(won.getScoreAsString());
          }
          System.out.println("\nlost games are:");
          for(Game won : lostGames) {
               System.out.println(won.getScoreAsString());
          }

          // as per g5's previous approach, we will reallocate goals from wins to draws in order to maximize the number of wins 
          int excessGoals = 0;
          for (Game winningGame : wonGames) {
               if (drawnGames.size() == 0) 
                    break;
               int playerGoals = winningGame.getNumPlayerGoals();
               int margin = playerGoals - winningGame.getNumOpponentGoals();
               // randomize whether we are leaving a margin of 1 or 2 on the win
               int subtractedGoals = Math.min(this.random.nextInt(2) + margin - 2, winningGame.getHalfNumPlayerGoals());
               System.out.println("Subtracted goals: " + subtractedGoals);
               excessGoals += subtractedGoals;
               winningGame.setNumPlayerGoals(playerGoals - subtractedGoals);
          }
          
          // now that we have the hopeful max # of points from the wins, we reallocate to draws 
          for (Game drawnGame : drawnGames) {
               int playerGoals = drawnGame.getNumPlayerGoals();
               int addedGoals = this.random.nextInt(2);
               // distribute goals once for many draws, randomizing the amount 
               if (excessGoals > 0 && playerGoals < 8) {
                    if ((playerGoals + addedGoals) > 8) addedGoals = 1;
                    excessGoals -= addedGoals;
                    System.out.println("Added goals to draw: " + addedGoals);
                    drawnGame.setNumPlayerGoals(playerGoals + addedGoals);
               }
          }
          System.out.println(excessGoals + " excess goals");
          // reallocate to losses if there are any left 
          for (Game lostGame : lostGames) {
               int playerGoals = lostGame.getNumPlayerGoals();
               // int addedGoals = 1;
               int addedGoals = Math.min(excessGoals, Math.min(lostGame.getNumOpponentGoals() - playerGoals + 1, 8 - playerGoals));
               // int addedGoals = Math.min(excessGoals, lostGame.getNumOpponentGoals() - playerGoals + 1);
               // distrubute goals one-by-one? 
               
               // distribute goals once for many losses, randomizing the amount 
               if (excessGoals > 0 && playerGoals < 8) {
                    excessGoals += addedGoals;
                    System.out.println("Added goals to loss: " + addedGoals);
                    lostGame.setNumPlayerGoals(playerGoals + addedGoals);
               }
          }     
          reallocatedPlayerGames.addAll(wonGames);
          reallocatedPlayerGames.addAll(drawnGames);
          reallocatedPlayerGames.addAll(lostGames);

          if(checkConstraintsSatisfied(playerGames, reallocatedPlayerGames))
               return reallocatedPlayerGames;
          return playerGames;
     }


     //      for(Game winningGame : wonGames) { 
     //           System.out.println("score is " + winningGame.getScoreAsString());   		 
               
     //           if(lostOrDrawnGamesWithReallocationCapacity.size() == 0)
     //                break;

     //           Game randomLostOrDrawnGame = lostOrDrawnGamesWithReallocationCapacity.get(this.random.nextInt(lostOrDrawnGamesWithReallocationCapacity.size()));

     //           int halfNumPlayerGoals = winningGame.getHalfNumPlayerGoals();

	// 		   int numRandomGoals = 0;

	// 		   int ourScore = winningGame.getScore().getNumPlayerGoals();
	// 		   int theirScore = winningGame.getScore().getNumOpponentGoals();

	// 		   if(halfNumPlayerGoals > theirScore) {
	// 			   numRandomGoals = halfNumPlayerGoals;
     //              }
     //              else if (ourScore == 0) {
     //                   ;
     //              }
	// 		   else {
	// 			   numRandomGoals = ourScore - theirScore - 1;
	// 		   }

     //           winningGame.setNumPlayerGoals(winningGame.getNumPlayerGoals() - numRandomGoals);
     //           randomLostOrDrawnGame.setNumPlayerGoals(randomLostOrDrawnGame.getNumPlayerGoals() + numRandomGoals);
               
     //           if(randomLostOrDrawnGame.maxPlayerGoalsReached())
     //                lostOrDrawnGamesWithReallocationCapacity.remove(randomLostOrDrawnGame);
     //      }
          
     //      reallocatedPlayerGames.addAll(wonGames);
     //      reallocatedPlayerGames.addAll(drawnGames);
     //      reallocatedPlayerGames.addAll(lostGames);

     //      if(checkConstraintsSatisfied(playerGames, reallocatedPlayerGames))
     //           return reallocatedPlayerGames;
     //      return playerGames;

     // }

     /*
     private void addAllGaps(int round, GameHistory gameHistory, Map<Integer, List<Game>> opponentGamesMap) {
          Map<Integer, Map<Integer, List<Game>>> allGamesMap = gameHistory.getAllGamesMap();
          Map<Integer, List<Game>> prevRound = allGamesMap.get(round-2);

          //convert List<Game> to HashMap<Integer, Game> where int is ID
          for(int team: prevRound.keySet()) {
               if(team == teamID) {
                    continue;
               }

               HashMap<Integer, Game> idsToGamePrev = convertList(prevRound.get(team));
               HashMap<Integer, Game> idsToGameCurr = convertList(opponentGamesMap.get(team));

               for(int gameID: idsToGamePrev.keySet()) {
                    Game gamePrev = idsToGamePrev.get(gameID);
                    Game gameCurr = idsToGameCurr.get(gameID);
                    int gap = gamePrev.getNumPlayerGoals() - gamePrev.getNumOpponentGoals();
                    int allocated = gameCurr.getNumPlayerGoals() - gamePrev.getNumPlayerGoals();

                    if(!map.containsKey(gap)) {
                         ArrayList<Double> arr = new ArrayList<>();
                         arr.add(1.0);
                         arr.add(allocated);
                         map.put(gap, arr);
                    } 
                    else {
                         double freq = map.get(gap).get(0);
                         double prevavg = map.get(gap).get(1);
                         double avg = (freq*prevavg+allocated)/(freq+1);

                         ArrayList<Double> arr = new ArrayList<>();
                         arr.add(freq+1);
                         arr.add(avg);
                         map.put(gap, arr);
                         //double avg = (map.get(team).get(gap)*(round-1)+allocated)/(round);
                    }
               }
              // System.out.println("done here");
          }
          //System.out.println("doneee");
     }
     */


     /*
     private void addAllGaps(int round, GameHistory gameHistory, Map<Integer, List<Game>> opponentGamesMap) {
          Map<Integer, Map<Integer, List<Game>>> allGamesMap = gameHistory.getAllGamesMap();
          Map<Integer, List<Game>> prevRound = allGamesMap.get(round-2);

          //convert List<Game> to HashMap<Integer, Game> where int is ID
          

          for(int team: prevRound.keySet()) {
               if(team == teamID) {
                    continue;
               }
               //.out.println("team is " + team);
               HashMap<Integer, Game> idsToGamePrev = convertList(prevRound.get(team));
               HashMap<Integer, Game> idsToGameCurr = convertList(opponentGamesMap.get(team));

      

              for(int gameID: idsToGamePrev.keySet()) {
                    Game gamePrev = idsToGamePrev.get(gameID);
                    Game gameCurr = idsToGameCurr.get(gameID);
                    int gap = gamePrev.getNumPlayerGoals() - gamePrev.getNumOpponentGoals();
                    int allocated = gameCurr.getNumPlayerGoals() - gamePrev.getNumPlayerGoals();

                    if(!map.containsKey(team)) {
                         HashMap<Integer, PriorityQueue<Integer>> gapsToAllocated = new HashMap();
                         map.put(team, gapsToAllocated);
                    }
                    
                    if (!map.get(team).containsKey(gap)) {
                         PriorityQueue<Integer> pq = new PriorityQueue();
                         map.get(team).put(gap, pq);
                    }

                    map.get(team).get(gap).add(allocated);
               }
              // System.out.println("done here");
          }
          //System.out.println("doneee");
     }*/

     /* glitched out, did this to try and preserve 
     HashMap<Integer, Game> convertList(List<Game> li) {
          HashMap<Integer, Game> converted = new HashMap();
          for(Game game : li) {
               converted.put(game.getID(), game);
          }
          //System.out.println("converted is " + converted);
          return converted;
     }

     void printMap(int round) {
          System.out.println("round is " + round);
          for(int team : map.keySet()) {
               System.out.println("team is " + team);
               for(int gap : map.get(team).keySet()) {
                    System.out.println("gap is " + gap);
                    System.out.println("avg is " + map.get(team).get(gap));
                    // for(int reallocated: map.get(team).get(gap)) {
                    //      System.out.println(reallocated);
                    // }
               }
          }
     }

     double getExpectedVal(int margin) {
          if(map.containsKey(margin)) {
               return map.get(margin).get(1);
          }
          else {
               for(int)
          }
     }
     */

     // added from last fork merge vvv
     // HashMap<Integer, Game> convertList(List<Game> li) {
     //      HashMap<Integer, Game> converted = new HashMap();
     //      for(Game game : li) {
     //           converted.put(game.getID(), game);
     //      }
     //      //System.out.println("converted is " + converted);
     //      return converted;
     // }

     // void printMap(int round) {
     //      System.out.println("round is " + round);
     //      for(int team : map.keySet()) {
     //           System.out.println("team is " + team);
     //           for(int gap : map.get(team).keySet()) {
     //                System.out.println("gap is " + gap);
     //                System.out.println("reallocations include:");
     //                // for(int reallocated: map.get(team).get(gap)) {
     //                //      System.out.println(reallocated);
     //                // }
     //           }
     //      }
     // }

     // helper methods stolen from random >:D
     private List<Game> getWinningGames(List<Game> playerGames) {
    	 List<Game> winningGames = new ArrayList<>();
    	 for(Game game : playerGames) {
    		 int numPlayerGoals = game.getNumPlayerGoals();
    		 int numOpponentGoals = game.getNumOpponentGoals();
    		 if(numPlayerGoals > numOpponentGoals)
    			 winningGames.add(game.cloneGame());
    	 }
    	 return winningGames;
     }

     private List<Game> getDrawnGames(List<Game> playerGames) {
    	 List<Game> drawnGames = new ArrayList<>();
    	 for(Game game : playerGames) {
    		 int numPlayerGoals = game.getNumPlayerGoals();
    		 int numOpponentGoals = game.getNumOpponentGoals();
    		 if(numPlayerGoals == numOpponentGoals)
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
}
