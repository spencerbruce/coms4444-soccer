package sim;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public abstract class Player {

    public Integer teamID, rounds, seed;
    public Random random;

    /**
     * Player constructor
     *
     * @param teamID  team ID
     * @param rounds  number of rounds
     * @param seed    random seed
     *
     */
	public Player(Integer teamID, Integer rounds, Integer seed) {
        this.teamID = teamID;
        this.rounds = rounds;
        this.seed = seed;
        this.random = new Random(seed);		
	}
	
	public Integer getID() {
		return teamID;
	}
	
	public Integer getNumRounds() {
		return rounds;
	}
	
	public Integer getSeed() {
		return seed;
	}
	
	public Random getRandomGenerator() {
		return random;
	}
	
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
	public static boolean checkConstraintsSatisfied(List<Game> originalPlayerGames, List<Game> reallocatedPlayerGames) {
		
		Map<Integer, Game> originalPlayerGamesMap = new HashMap<>();
		for(Game originalPlayerGame : originalPlayerGames)
			originalPlayerGamesMap.put(originalPlayerGame.getID(), originalPlayerGame);
		Map<Integer, Game> reallocatedPlayerGamesMap = new HashMap<>();
		for(Game reallocatedPlayerGame : reallocatedPlayerGames)
			reallocatedPlayerGamesMap.put(reallocatedPlayerGame.getID(), reallocatedPlayerGame);
		
		int totalNumOriginalPlayerGoals = 0, totalNumReallocatedPlayerGoals = 0;
		for(Game originalPlayerGame : originalPlayerGames) {
			
			if(!reallocatedPlayerGamesMap.containsKey(originalPlayerGame.getID()))
				continue;

			Game reallocatedPlayerGame = reallocatedPlayerGamesMap.get(originalPlayerGame.getID());
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

    public abstract List<Game> reallocate(Integer round, GameHistory gameHistory, List<Game> playerGames, Map<Integer, List<Game>> opponentGamesMap);
    
    public static boolean hasWonGame(Game game) {
    	return game.getNumPlayerGoals() > game.getNumOpponentGoals();
    }

    public static boolean hasLostGame(Game game) {
    	return game.getNumPlayerGoals() < game.getNumOpponentGoals();    	
    }
    
    public static boolean hasDrawnGame(Game game) {
    	return game.getNumPlayerGoals() == game.getNumOpponentGoals();    	
    }
}