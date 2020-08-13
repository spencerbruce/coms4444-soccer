package random;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sim.Game;
import sim.GameHistory;

public class Player extends sim.Player {

     /**
      * Player constructor
      *
      * @param teamID  team ID
      * @param rounds  number of rounds
      * @param seed    random seed
      *
      */
     public Player(Integer teamID, Integer rounds, Integer seed) {
    	 super(teamID, rounds, seed);
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
   	 
    	 for(Game winningGame : wonGames) {    		 
    		 
    		 if(lostOrDrawnGamesWithReallocationCapacity.size() == 0)
    			 break;

    		 Game randomLostOrDrawnGame = lostOrDrawnGamesWithReallocationCapacity.get(this.random.nextInt(lostOrDrawnGamesWithReallocationCapacity.size()));

    		 int halfNumPlayerGoals = winningGame.getHalfNumPlayerGoals();
    		 int numRandomGoals = (int) Math.min(this.random.nextInt(halfNumPlayerGoals) + 1, Game.getMaxGoalThreshold() - randomLostOrDrawnGame.getNumPlayerGoals());

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