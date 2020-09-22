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

     //opponent margin to (frequency, avg allocated across all teams)
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
          System.out.println("Round is " + round);
          if(round > 1) {
               addAllGaps(round, gameHistory, opponentGamesMap);
               printMap(round);
          }

          for(Game game : playerGames) {
               System.out.println("GameID is " + game.getID());
          }

          // TODO add your code here to reallocate player goals

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

               //aim for x amount of points

               //maximin only 2 round 


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

          for(int gap : map.keySet()) {
               System.out.println("that gap EV for "+ gap + " is " + getExpectedVal(gap));
          }

          if(round >= 5) {
               List<Game> toAdd = new ArrayList<>(drawnGames);
               toAdd.addAll(lostGames);

               int toAllocate = 0;
               for(Game game : wonGames) {
                    int margin = game.getNumOpponentGoals() - game.getNumPlayerGoals();
                    int expVal = (int) getExpectedVal(margin) + game.getNumOpponentGoals();

                    System.out.println("Score is " + game.getScoreAsString());
                    System.out.println("expVal is " + expVal);

                    int expMargin = game.getNumPlayerGoals() - expVal;

                    //if we think we'll still be winning after opponent reallocates, we take off points so we win by 1
                    if(expMargin > 1) {
                         int excess = expMargin - 1;
                         toAllocate += excess;
                         game.setNumPlayerGoals(game.getNumPlayerGoals() - excess);
                    }
                    //if we don't think we'll be winning after opponent reallocates, give up on game
                    else {
                         int halfVal = game.getHalfNumPlayerGoals();
                         toAllocate += halfVal;
                         game.setNumPlayerGoals(game.getNumPlayerGoals() - halfVal);
                    }
               }

               //lowest to highest for opponent expected val - our score (expected gap after reallocation)
               toAdd.sort(
                    (Game g1, Game g2) -> 
                    (g1.getNumOpponentGoals() + getExpectedVal(g1.getNumPlayerGoals()-g1.getNumOpponentGoals()) - g1.getNumPlayerGoals())
                    - (g2.getNumOpponentGoals() + getExpectedVal(g2.getNumPlayerGoals()-g2.getNumOpponentGoals()) - g2.getNumPlayerGoals())
               );
               int idx = 0;

               for(Game game : toAdd) {
                    System.out.println("Scoreee here is " + game.getScoreAsString());
               }

               while(idx < toAdd.size() && toAllocate > 0) {
                    Game g1 = toAdd.get(idx);
                    int expectedVal = g1.getNumOpponentGoals() + getExpectedVal(g1.getNumPlayerGoals()-g1.getNumOpponentGoals());
                    int needed = expectedVal - g1.getNumPlayerGoals() + 1;

                    if(g1.getNumPlayerGoals() + 1 > 8) {
                         continue;
                    }

                    if(needed > toAllocate) {
                         needed = toAllocate;
                    }

                    g1.setNumPlayerGoals(g1.getNumPlayerGoals() + needed);
                    idx++;
               }

               ArrayList<Game> toReturn = new ArrayList<>();
               toReturn.addAll(wonGames);
               toReturn.addAll(toAdd);
               return toReturn;


               //need to calculate expected value of opponent for each game
               
               //want to add 1 above expected value to as many games as we can

               //sort by score - expected value for wins highest to lowest
               //#available for allocation

               //while we have available goals to allocate
               //sort by expected value - current for draws/losses lowest to highest
          }

          return playerGames;
             
               
          // wins: biggest margin --> smallest margin
          /*wonGames.sort(
               (Game g1, Game g2) -> (g2.getNumPlayerGoals() - g2.getNumOpponentGoals() 
               - (g1.getNumPlayerGoals() - g1.getNumOpponentGoals()))
          );

          // draws: lowest poitns --> highest points 
          drawnGames.sort(
               (Game g1, Game g2) -> (g1.getNumPlayerGoals() - g2.getNumOpponentGoals())
          );

          // losses: lowest margin --> highest margin 
          lostGames.sort(
               (Game g1, Game g2) -> (g2.getNumPlayerGoals() - g2.getNumOpponentGoals() 
               - (g1.getNumPlayerGoals() - g1.getNumOpponentGoals()))
          );

          System.out.println("won games are:\nP O\n---");
          for(Game won : wonGames) {
               System.out.println(won.getScoreAsString());
          }
          System.out.println("\ndrawn games are:\nP O\n---");
          for(Game drawn : drawnGames) {
               System.out.println(drawn.getScoreAsString());
          }
          System.out.println("\nlost games are:\nP O\n---");
          for(Game lost : lostGames) {
               System.out.println(lost.getScoreAsString());
          }

          int excessGoals = 0;
          for (Game winningGame : wonGames) {
               if (drawnGames.size() == 0) 
                    break;
               // randomize whether we are leaving a margin of 1 or 2 on the win
               // distrubute goals one-by-one? 
                              int subtract = Math.min(this.random.nextInt(2) + margin - 2, game.getHalfNumPlayerGoals());
               int subtractedGoals = winningGame.getHalfNumPlayerGoals() - this.random.nextInt(1) + 1;
               excessGoals += subtractedGoals;
               game.setNumPlayerGoals(winningGame.getHalfNumPlayerGoals() - subtractedGoals);
               
          }
          
          for (Game drawnGame : drawnGames) {
               int playerGoals = drawnGame.getNumPlayerGoals();

          }*/
          
     }

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

                         arr.add((double) allocated);
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

     //takes in opponent's margin, returns what we expect them to reallocate
     int getExpectedVal(int margin) {
          //if that gap has been encountered before, return average
          if(map.containsKey(margin)) {
               return (int) ((double) map.get(margin).get(1));
          }
          //if margin hasn't been encountered before, find a close one
          else {
               for(int i = margin; i > - 8; i--) {
                    if(map.containsKey(margin)) {
                         return (int) ((double) map.get(margin).get(1));
                    }
               }
               for(int i = margin; i < 8; i++) {
                    if(map.containsKey(margin)) {
                         return (int) ((double) map.get(margin).get(1));
                    }
               }
               return 8;
          }
     }

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
               /*for(int gap : map.get(team).keySet()) {
                    System.out.println("gap is " + gap);
                    System.out.println("avg is " + map.get(team).get(gap));
                    // for(int reallocated: map.get(team).get(gap)) {
                    //      System.out.println(reallocated);
                    // }
               }*/
          }
     }



     // stolen from random >:D
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



/*package g1; // TODO modify the package name to reflect your team

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
     //HashMap<Integer, Map<Integer, Double>> map = new HashMap();
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
     /*public Player(Integer teamID, Integer rounds, Integer seed, SimPrinter simPrinter) {
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
     /*public List<Game> reallocate(Integer round, GameHistory gameHistory, List<Game> playerGames, Map<Integer, List<Game>> opponentGamesMap) {
          System.out.println("Round is " + round);
          if(round > 1) {
               addAllGaps(round, gameHistory, opponentGamesMap);
               //printMap(round);
          }

          for(Game game : playerGames) {
               System.out.println("GameID is " + game.getID());
          }

          System.out.println("gameHistory is " + gameHistory.)

          // TODO add your code here to reallocate player goals

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
          
          // return null; // TODO modify the return statement to return your list of reallocated player games

             // strategy for future deliverable? vvv
          //sort by won games how much we won by
		//sort lost games by how much we lost by
		//match these up

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
          
          List<Game> lostOrDrawnGamesWithReallocationCapacity = new ArrayList<>(lostGames);
          lostOrDrawnGamesWithReallocationCapacity.addAll(drawnGames);
          for(Game lostGame : lostGames)
               if(lostGame.maxPlayerGoalsReached())
                    lostOrDrawnGamesWithReallocationCapacity.remove(lostGame);
          for(Game drawnGame : drawnGames)
               if(drawnGame.maxPlayerGoalsReached())
                    lostOrDrawnGamesWithReallocationCapacity.remove(drawnGame);
          
               
               //if we're more than double the score of the opponent, remove score/2
               //else remove our score-their score - 1

               //allocate to random game

          //closest losses -> furthest losses
          lostOrDrawnGamesWithReallocationCapacity.sort(
               (Game g1, Game g2) -> (g2.getNumPlayerGoals() - g2.getNumOpponentGoals() 
               - (g1.getNumPlayerGoals() - g1.getNumOpponentGoals()))
          );

          System.out.println("lost games are ");
          for(Game lost : lostOrDrawnGamesWithReallocationCapacity) {
               System.out.println("score is " + lost.getScoreAsString());
          }

          //closest wins -> furthest wins
          wonGames.sort(
               (Game g1, Game g2) -> (g1.getNumPlayerGoals() - g1.getNumOpponentGoals() 
               - (g2.getNumPlayerGoals() - g2.getNumOpponentGoals()))
          );
          System.out.println("new round");

          for(Game winningGame : wonGames) { 
               System.out.println("score is " + winningGame.getScoreAsString());   		 
               
               if(lostOrDrawnGamesWithReallocationCapacity.size() == 0)
                    break;

               Game randomLostOrDrawnGame = lostOrDrawnGamesWithReallocationCapacity.get(this.random.nextInt(lostOrDrawnGamesWithReallocationCapacity.size()));

               int halfNumPlayerGoals = winningGame.getHalfNumPlayerGoals();

			   int numRandomGoals;

			   int ourScore = winningGame.getScore().getNumPlayerGoals();
			   int theirScore = winningGame.getScore().getNumOpponentGoals();

			   if(halfNumPlayerGoals > theirScore) {
				   numRandomGoals = halfNumPlayerGoals;
			   }
			   else {
				   numRandomGoals = ourScore - theirScore - 1;
			   }

               winningGame.setNumPlayerGoals(winningGame.getNumPlayerGoals() - numRandomGoals);
               randomLostOrDrawnGame.setNumPlayerGoals(randomLostOrDrawnGame.getNumPlayerGoals() + numRandomGoals);
               
               if(randomLostOrDrawnGame.maxPlayerGoalsReached())
                    lostOrDrawnGamesWithReallocationCapacity.remove(randomLostOrDrawnGame);
          }
          
          reallocatedPlayerGames.addAll(wonGames);
          reallocatedPlayerGames.addAll(drawnGames);
          reallocatedPlayerGames.addAll(lostGames);

          if(checkConstraintsSatisfied(playerGames, reallocatedPlayerGames))
               return reallocatedPlayerGames;
          return playerGames;

     }

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
                    System.out.println("testing GameID is " + gameID);
                    Game gamePrev = idsToGamePrev.get(gameID);
                    Game gameCurr = idsToGameCurr.get(gameID);
                    int gap = gamePrev.getNumPlayerGoals() - gamePrev.getNumOpponentGoals();
                    int allocated = gameCurr.getNumPlayerGoals() - gamePrev.getNumPlayerGoals();

                    if(!map.containsKey(team)) {
                         HashMap<Integer, Double> gapsToAllocated = new HashMap<>();
                         map.put(team, gapsToAllocated);
                    } 
                    if (!map.get(team).containsKey(gap)) {
                         double avg = (double) allocated;
                         map.get(team).put(gap, avg);
                    }
                    else {
                         double avg = (map.get(team).get(gap)*(round-1)+allocated)/(round);
                    }
               }
              // System.out.println("done here");
          }
          //System.out.println("doneee");
     }


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

     /*HashMap<Integer, Game> convertList(List<Game> li) {
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



     // stolen from random >:D
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
}*/
