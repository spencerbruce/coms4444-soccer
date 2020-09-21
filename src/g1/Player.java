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
     HashMap<Integer, Map<Integer, PriorityQueue<Integer>>> map = new HashMap();

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
               //.out.println("team is " + team);
               HashMap<Integer, Game> idsToGamePrev = convertList(prevRound.get(team));
               HashMap<Integer, Game> idsToGameCurr = convertList(opponentGamesMap.get(team));

               for(int gameID: idsToGamePrev.keySet()) {
                    Game gamePrev = idsToGamePrev.get(gameID);
                    Game gameCurr = idsToGameCurr.get(gameID);
                    int gap = gamePrev.getNumPlayerGoals() - gamePrev.getNumOpponentGoals();
                    int allocated = gameCurr.getNumPlayerGoals() - gamePrev.getNumPlayerGoals();
                    /*System.out.println("prev score is " + gamePrev.getScoreAsString());
                    System.out.println("current score is " + gameCurr.getScoreAsString());
                    System.out.println("allocated is " + allocated);
                    System.out.println("gameid is " + gameID);*/

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
               for(int gap : map.get(team).keySet()) {
                    System.out.println("gap is " + gap);
                    System.out.println("reallocations include:");
                    for(int reallocated: map.get(team).get(gap)) {
                         System.out.println(reallocated);
                    }
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
}
