package g4;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sim.Game;
import sim.GameHistory;
import sim.SimPrinter;

public class Player extends sim.Player {

	private int goalBank;
	private final MovePredictor movePredictor;
	private int threshold;
	private SimPrinter simPrinter;
	private int teamId;
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
		this.simPrinter = simPrinter;
		this.goalBank = 0;
		this.movePredictor = new MovePredictor(simPrinter);
		this.threshold = 3;
		this.teamId = teamID;
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
		//pointPredictor.trackData(opponentGamesMap);

		List<Game> reallocatedPlayerGames = new ArrayList<>();

		List<Game> wonGames = getWinningGames(playerGames);
		List<Game> drawnGames = getDrawnGames(playerGames);
		List<Game> lostGames = getLosingGames(playerGames);
		List<Integer> targetTeamID = reallocateLeapFrog(round, gameHistory, playerGames, opponentGamesMap);

		// System.out.println("Reallocating goals");
		calculateBank(wonGames, targetTeamID.get(0), targetTeamID.get(1));
		this.movePredictor.trackData(opponentGamesMap);
		// System.out.println(this.goalBank);
		// System.out.println("Bank:" + goalBank);
		transferGoalsToLostGames(lostGames);
		transferGoalsToDrawnGames(drawnGames);

		// adjust winning games score
		int goalsTakenFromWins = 0;
		for (Game winningGame : wonGames) {
			goalsTakenFromWins += winningGame.getNumPlayerGoals() - winningGame.getNumOpponentGoals() - 1;
			winningGame.setNumPlayerGoals(winningGame.getNumOpponentGoals() + 1);
		}
		// System.out.println("Goals taken:" + goalsTakenFromWins);

		reallocatedPlayerGames.addAll(lostGames);
		reallocatedPlayerGames.addAll(drawnGames);
		reallocatedPlayerGames.addAll(wonGames);
		this.goalBank = 0;
		// check constraints and return
		if (checkConstraintsSatisfied(playerGames, reallocatedPlayerGames))
			return reallocatedPlayerGames;

		return playerGames;
	}

	public List<Integer> reallocateLeapFrog(Integer round, GameHistory gameHistory, List<Game> playerGames,
			Map<Integer, List<Game>> opponentGamesMap) {
			Map<Integer, Double> currentAverages = gameHistory.getAllAverageRankingsMap().get(round);
			double nextSmallest = Integer.MAX_VALUE;
			int nextSmallestId = 0;
			double nextLargest = Integer.MIN_VALUE;
			int nextLargestId = 0;
			double currentTeam = currentAverages.get(this.teamId);
			for(Map.Entry<Integer, Double> entry : currentAverages.entrySet()){
				if(entry.getValue() <= currentTeam){
					if(nextSmallest > entry.getValue() && entry.getKey() != this.teamId){
						nextSmallest = entry.getValue();
					}
				} else {
					if(nextLargest < entry.getValue() && entry.getKey() != this.teamId){
						nextLargest = entry.getValue();
					}
				}
				this.simPrinter.println(entry.getValue());
			}
			List<Integer> teams = new ArrayList<Integer>();
			teams.add(nextLargestId);
			teams.add(nextSmallestId);
			return teams;
	}

	// algorithm #2:
	// induce losses for higher ranked teams
	// risk losing to teams that are lower ranked
	// don't use this algorithm when player is rank 1
	// TODO: figure out the highest rank at which you would want to use this algorithm
	// TODO: figure out hows many teams above you want to attack (1 at the moment)
	// TODO: figure out hows many teams below you are willing to lose against (1 at the moment)
	public List<Game> reallocateAttackHigherRanks(Integer round, GameHistory gameHistory, List<Game> playerGames,
			Map<Integer, List<Game>> opponentGamesMap) {
			
			List<Game> reallocatedPlayerGames = new ArrayList<>();
			List<Game> wonGames = getWinningGames(playerGames);
			List<Game> drawnGames = getDrawnGames(playerGames);
			List<Game> lostGames = getLosingGames(playerGames);
			
			Double highestRank = Double.MIN_VALUE;
			Double lowestRank = Double.MAX_VALUE;
			Map<Integer, Double> currentAverages = gameHistory.getAllAverageRankingsMap().get(round);
			List<Double> highRankedTeams = new ArrayList<Double>();
			List<Double> lowRankedTeams = new ArrayList<Double>();
			double playerRank = currentAverages.get(this.teamId);

			int numGoalsToReallocate = 0;
			
			for(Map.Entry<Integer, Double> entry : currentAverages.entrySet()){
				if(entry.getValue() < playerRank){
					if(lowestRank > entry.getValue()){
						lowestRank = entry.getValue();
					}
				} else {
					if(highestRank < entry.getValue()){
						highestRank = entry.getValue();
					}
				}
			}

			// TODO: figure out how to get scores knowing team ID

			reallocatedPlayerGames.addAll(lostGames);
			reallocatedPlayerGames.addAll(drawnGames);
			reallocatedPlayerGames.addAll(wonGames);

			// check constraints and return
			if (checkConstraintsSatisfied(playerGames, reallocatedPlayerGames)) {
				return reallocatedPlayerGames;
			}

			return playerGames;
	}

	/**
	 * Calculates Goal Bank
	 *
	 * @param playerGames state of player games before reallocation
	 *
	 */
	private void calculateBank(List<Game> playerGames, int target1, int target2) {
		for (Game game : playerGames) {
			int numPlayerGoals = game.getNumPlayerGoals();
			int numOpponentGoals = game.getNumOpponentGoals();
			this.goalBank += numPlayerGoals - numOpponentGoals - 1;
//			System.out.println(game.getID() + ": " + this.goalBank + " " + numPlayerGoals + " " + numOpponentGoals);

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
				this.goalBank -= goalsToAdd;
			} else {
				transferFromBank(game, goalBank);
				this.goalBank = 0;
			}
		}
	}

	// add 1 point from goalBank to drawn games
	private void transferGoalsToDrawnGames(List<Game> drawnGames) {
		for (Game game : drawnGames) {
			if (goalBank > 0) {
				transferFromBank(game, 1);
				this.goalBank--;
			}
		}
	}

	/*
	 * sort games by amount won (decreasing) ex: [5, 3, 2, 2, 0, 0, -1, -4]
	 */
	private void sortGamesByAmountWon(List<Game> games) {
		games.sort((g1, g2) -> {
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
			simPrinter.print(game.getID().toString() + ": <= GAME ID ");
			simPrinter.print("player: " + game.getNumPlayerGoals().toString());
			simPrinter.print(", opp: " + game.getNumOpponentGoals().toString());
			simPrinter.println();
		}
	}
}