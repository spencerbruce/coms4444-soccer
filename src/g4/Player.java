package g4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import sim.Game;
import sim.GameHistory;
import sim.SimPrinter;

public class Player extends sim.Player {

	private int goalBank;

	/**
	 * Player constructor
	 *
	 * @param teamID     team ID
	 * @param rounds     number of rounds
	 * @param seed       random seed
	 * @param simPrinter simulation printer
	 *
	 */
	public Player(Integer teamID, Integer rounds, Integer seed, SimPrinter simPrinter) {
		super(teamID, rounds, seed, simPrinter);
		this.goalBank = 0;
	}

	/**
	 * Reallocate player goals
	 *
	 * @param round            current round
	 * @param gameHistory      cumulative game history from all previous rounds
	 * @param playerGames      state of player games before reallocation
	 * @param opponentGamesMap state of opponent games before reallocation (map of
	 *                         opponent team IDs to their games)
	 * @return state of player games after reallocation
	 *
	 */
	public List<Game> reallocate(Integer round, GameHistory gameHistory, List<Game> playerGames,
			Map<Integer, List<Game>> opponentGamesMap) {
		// TODO: add your code here to reallocate player goals
		List<Game> reallocatedPlayerGames = new ArrayList<>();

		List<Game> wonGames = getWinningGames(playerGames);
		List<Game> drawnGames = getDrawnGames(playerGames);
		List<Game> lostGames = getLosingGames(playerGames);

		printGameList(playerGames);

		//System.out.println("Reallocating goals");
		calculateBank(playerGames);
		//System.out.println("Bank:" + goalBank);
		transferGoalsToLostGames(lostGames);
		transferGoalsToDrawnGames(drawnGames);


		// adjust winning games score
		int goalsTakenFromWins = 0;
		for(Game winningGame : wonGames) {    
			goalsTakenFromWins += winningGame.getNumPlayerGoals() - winningGame.getNumOpponentGoals() - 1; 
			winningGame.setNumPlayerGoals(winningGame.getNumOpponentGoals() + 1);
		}

		//System.out.println("Goals taken:" + goalsTakenFromWins);

		reallocatedPlayerGames.addAll(lostGames);
		reallocatedPlayerGames.addAll(drawnGames);
		reallocatedPlayerGames.addAll(wonGames);
		printGameList(reallocatedPlayerGames);
		
		// check constraints and return
		if(checkConstraintsSatisfied(playerGames, reallocatedPlayerGames))
			return reallocatedPlayerGames;
			
    	return playerGames;
	}

	/**
	 * Calculates Goal Bank
	 *
	 * @param playerGames      state of player games before reallocation
	 *
	 */
	private void calculateBank(List<Game> playerGames) {
		for (Game game : playerGames) {
			int numPlayerGoals = game.getNumPlayerGoals();
			int numOpponentGoals = game.getNumOpponentGoals();
			if (numPlayerGoals > numOpponentGoals)
				this.goalBank += numPlayerGoals - numOpponentGoals - 1;
		}
	}

	private List<Game> getWinningGames(List<Game> playerGames) {
		List<Game> winningGames = new ArrayList<>();
		for (Game game : playerGames) {
			int numPlayerGoals = game.getNumPlayerGoals();
			int numOpponentGoals = game.getNumOpponentGoals();
			if (numPlayerGoals > numOpponentGoals)
				winningGames.add(game.cloneGame());
		}
		return winningGames;
	}

	private List<Game> getDrawnGames(List<Game> playerGames) {
		List<Game> drawnGames = new ArrayList<>();
		for (Game game : playerGames) {
			int numPlayerGoals = game.getNumPlayerGoals();
			int numOpponentGoals = game.getNumOpponentGoals();
			if (numPlayerGoals == numOpponentGoals)
				drawnGames.add(game.cloneGame());
		}
		return drawnGames;
	}

	private List<Game> getLosingGames(List<Game> playerGames) {
		List<Game> losingGames = new ArrayList<>();
		for (Game game : playerGames) {
			int numPlayerGoals = game.getNumPlayerGoals();
			int numOpponentGoals = game.getNumOpponentGoals();
			if (numPlayerGoals < numOpponentGoals)
				losingGames.add(game.cloneGame());
		}
		return losingGames;
	}

	// add points from goalBank to lost games
	private void transferGoalsToLostGames(List<Game> lostGames) {
		sortGamesByAmountWon(lostGames);
		for (Game game : lostGames) {
			int lostBy = game.getNumOpponentGoals() - game.getNumPlayerGoals();
			int goalsToAdd = lostBy + 1;
			if (goalBank >= goalsToAdd) {
				transferFromBank(game, goalsToAdd);
			}
			else {
				transferFromBank(game, goalBank);
			}
		}
	}

	// add 1 point from goalBank to drawn games
	private void transferGoalsToDrawnGames(List<Game> drawnGames) {
		for (Game game : drawnGames) {
			if (goalBank > 0) {
				transferFromBank(game, 1);
			}
		}
	}

	/*
	sort games by amount won (decreasing)
	ex: [5, 3, 2, 2, 0, 0, -1, -4]
	*/
	private void sortGamesByAmountWon(List<Game> games) {
		Collections.sort(games, (g1, g2) -> {
			int g1Diff = g1.getNumPlayerGoals() - g1.getNumOpponentGoals();
			int g2Diff = g2.getNumPlayerGoals() - g2.getNumOpponentGoals();
			return g2Diff - g1Diff;
		});
	}

	private void transferFromBank(Game game, int goals) {
		goalBank -= goals;
		int currentGoals = game.getNumPlayerGoals();
		game.setNumPlayerGoals(currentGoals + goals);
	}

	// only used for internal testing
	private void printGameList(List<Game> games) {
		for (Game game : games) {
			System.out.print(game.getID().toString() + ": ");
			System.out.print("player: " + game.getNumPlayerGoals().toString());
			System.out.print(", opp: " + game.getNumOpponentGoals().toString());
			System.out.println("");
		}
	}
}