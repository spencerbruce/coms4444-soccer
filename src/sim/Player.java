package sim;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class Player {
	
	/*
	 * Check if the following constraints are satisfied:
	 * 
	 * 1. Number of player goals should stay between 0 and 8, inclusive
	 * 2. Number of opponent goals for each game must not change
	 * 3. Number of player goals for winning games should not increase
	 * 4. Number of player goals for winning games should not decrease by more than half
	 * 5. Number of player goals added and subtracted must match
	 * 
	 * @param originalPlayerGames     list of original player games 
	 * @param reallocatedPlayerGames  list of reallocated player games
	 * @return                        constraints are satisfied
	 * 
	 */
	public boolean checkConstraintsSatisfied(List<Game> originalPlayerGames, List<Game> reallocatedPlayerGames) {
		
		Map<Integer, Game> originalPlayerGameMap = new HashMap<>();
		for(Game originalPlayerGame : originalPlayerGames)
			originalPlayerGameMap.put(originalPlayerGame.getID(), originalPlayerGame);
		Map<Integer, Game> reallocatedPlayerGameMap = new HashMap<>();
		for(Game reallocatedPlayerGame : reallocatedPlayerGames)
			reallocatedPlayerGameMap.put(reallocatedPlayerGame.getID(), reallocatedPlayerGame);
		
		int totalNumOriginalPlayerGoals = 0, totalNumReallocatedPlayerGoals = 0;
		for(Game originalPlayerGame : originalPlayerGames) {
			
			if(!reallocatedPlayerGameMap.containsKey(originalPlayerGame.getID()))
				continue;

			Game reallocatedPlayerGame = reallocatedPlayerGameMap.get(originalPlayerGame.getID());
			boolean isOriginalWinningGame = originalPlayerGame.getNumPlayerGoals() > originalPlayerGame.getNumOpponentGoals();
			
			// Constraint 1
			if(reallocatedPlayerGame.getNumPlayerGoals() < 0 || reallocatedPlayerGame.getNumPlayerGoals() > 8)
				return false;
			
			// Constraint 2
			if(originalPlayerGame.getNumOpponentGoals() != reallocatedPlayerGame.getNumOpponentGoals())
				return false;
			
			// Constraint 3
			boolean numPlayerGoalsIncreased = reallocatedPlayerGame.getNumPlayerGoals() > originalPlayerGame.getNumPlayerGoals();
			if(isOriginalWinningGame && numPlayerGoalsIncreased)
				return false;
			
			// Constraint 4
			int halfNumPlayerGoals = originalPlayerGame.getHalfNumPlayerGoals();
			boolean numReallocatedPlayerGoalsLessThanHalf = 
					reallocatedPlayerGame.getNumPlayerGoals() < (originalPlayerGame.getNumPlayerGoals() - halfNumPlayerGoals);
			if(isOriginalWinningGame && numReallocatedPlayerGoalsLessThanHalf)
				return false;
			
			totalNumOriginalPlayerGoals += originalPlayerGame.getNumPlayerGoals();
			totalNumReallocatedPlayerGoals += reallocatedPlayerGame.getNumPlayerGoals();
		}
		
		// Constraint 5
		if(totalNumOriginalPlayerGoals != totalNumReallocatedPlayerGoals)
			return false;
		
		return true;
	}

    public abstract List<Game> reallocate(int round, GameHistory gameHistory, List<Game> playerGames, Map<Integer, List<Game>> opponentGameMap);
    
    public boolean hasWonGame(Game game) {
    	return game.getNumPlayerGoals() > game.getNumOpponentGoals();
    }

    public boolean hasLostGame(Game game) {
    	return game.getNumPlayerGoals() < game.getNumOpponentGoals();    	
    }
    
    public boolean hasDrawnGame(Game game) {
    	return game.getNumPlayerGoals() == game.getNumOpponentGoals();    	
    }
}