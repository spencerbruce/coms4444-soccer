package g4;

import java.util.ArrayList;
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
		// TODO add your code here to reallocate player goals
		List<Game> reallocatedPlayerGames = new ArrayList<>();

		List<Game> wonGames = getWinningGames(playerGames);
		List<Game> drawnGames = getDrawnGames(playerGames);
		List<Game> lostGames = getLosingGames(playerGames);
		
		calculateBank(playerGames);
		
		return null; // TODO modify the return statement to return your list of reallocated player
						// games
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
}