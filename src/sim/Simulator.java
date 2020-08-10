package sim;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Simulator {
	
	private static GameHistory gameHistory;
	private static List<PlayerWrapper> playerWrappers;
	private static Integer[][] randomGameGrid;
    private static Random random;
	
	// Constants
	private static int seed = 42;
	private static int fps = 30;
	private static int rounds = 10;
	
    private static int currentRound = 0;
    private static long timeout = 1000;
    private static boolean showGUI = false;
    private static String version = "1.0";
    	
	private static void setUpStructures() {
		gameHistory = new GameHistory();
		playerWrappers = new ArrayList<>();
		randomGameGrid = new Integer[playerWrappers.size()][playerWrappers.size()];
		random = new Random(seed);
	}
	
	private static Map<Integer, PlayerPoints> computeTeamPoints(Map<Integer, List<Game>> roundGamesMap) {
		Map<Integer, PlayerPoints> roundPointsMap = new HashMap<>();
		for(Map.Entry<Integer, List<Game>> gamesEntry : roundGamesMap.entrySet()) {
			PlayerPoints playerPoints = new PlayerPoints();
			for(Game game : gamesEntry.getValue()) {
				if(Player.hasWonGame(game))
					playerPoints.addWinPoints();
				else if(Player.hasLostGame(game))
					playerPoints.addLossPoints();
				else if(Player.hasDrawnGame(game))
					playerPoints.addDrawPoints();
			}
			roundPointsMap.put(gamesEntry.getKey(), playerPoints);
		}
		return roundPointsMap;
	}

	private static Map<Integer, PlayerPoints> computeCumulativeTeamPoints(Map<Integer, PlayerPoints> roundPointsMap) {
		Map<Integer, PlayerPoints> roundCumulativePointsMap = new HashMap<>();
		
		Map<Integer, Map<Integer, PlayerPoints>> allCumulativePointsMap = gameHistory.getAllCumulativePointsMap();
		for(Map.Entry<Integer, PlayerPoints> newPointsEntry : roundPointsMap.entrySet()) {
			int roundCumulativePoints = newPointsEntry.getValue().getTotalPoints();
			for(Integer round : allCumulativePointsMap.keySet())
				roundCumulativePoints += allCumulativePointsMap.get(round).get(newPointsEntry.getKey()).getTotalPoints();
			roundCumulativePointsMap.put(newPointsEntry.getKey(), new PlayerPoints(roundCumulativePoints));
		}
		
		return roundCumulativePointsMap;
	}
	
	private static Map<Integer, Double> computeRankings(Map<Integer, PlayerPoints> pointsMap) {
		Map<Integer, Double> rankingsMap = new HashMap<>();
		
		Map<Integer, PlayerPoints> rankedPointsMap = pointsMap.entrySet()
				  .stream()
				  .sorted(Map.Entry.comparingByValue())
				  .collect(Collectors.toMap(
				    Map.Entry::getKey, 
				    Map.Entry::getValue,
				    (oldPoints, newPoints) -> oldPoints, LinkedHashMap::new));
						
		List<Integer> rankedTeamIDs = new ArrayList<>(rankedPointsMap.keySet());
		Collections.reverse(rankedTeamIDs);
				
		int indexOfFirstTiedElement = 0;
		for(int i = 0; i < rankedTeamIDs.size() - 1; i++) {
			int currTeamID = rankedTeamIDs.get(i);
			int currTeamNumPoints = rankedPointsMap.get(currTeamID).getTotalPoints();
			int nextTeamID = rankedTeamIDs.get(i + 1);
			int nextTeamNumPoints = rankedPointsMap.get(nextTeamID).getTotalPoints();
			
			if(currTeamNumPoints == nextTeamNumPoints)
				continue;
			else if(indexOfFirstTiedElement != i) {
				double averageRank = ((double) (indexOfFirstTiedElement + i)) / 2 + 1;
				for(int j = indexOfFirstTiedElement; j <= i; j++)
					rankingsMap.put(rankedTeamIDs.get(j), averageRank);
				indexOfFirstTiedElement = i + 1;
			}
			else {
				rankingsMap.put(currTeamID, (double) (i + 1));
				indexOfFirstTiedElement = i + 1;
			}
		}
		
		if(indexOfFirstTiedElement != rankedTeamIDs.size() - 1) {
			double averageRank = ((double) (indexOfFirstTiedElement + rankedTeamIDs.size() - 1)) / 2 + 1;
			for(int i = indexOfFirstTiedElement; i < rankedTeamIDs.size(); i++)
				rankingsMap.put(rankedTeamIDs.get(i), averageRank);
		}
		else
			rankingsMap.put(rankedTeamIDs.get(rankedTeamIDs.size() - 1), (double) rankedTeamIDs.size());

		return rankingsMap;
	}	

	private static void updateGameHistory(Integer round,
										  Map<Integer, List<Game>> roundGamesMap,
										  Map<Integer, PlayerPoints> roundPointsMap,
										  Map<Integer, PlayerPoints> roundCumulativePointsMap,
										  Map<Integer, Double> roundRankingsMap,
										  Map<Integer, Double> roundAverageRankingsMap) {
		gameHistory.addRoundGames(round, roundGamesMap);
		gameHistory.addRoundPoints(round, roundPointsMap);
		gameHistory.addRoundCumulativePoints(round, roundCumulativePointsMap);
		gameHistory.addRoundRankings(round, roundRankingsMap);
		gameHistory.addRoundAverageRankings(round, roundAverageRankingsMap);
	}

	private static void runSimulation() {
		
	}
	
	private static void parseCommandLineArguments(String[] args) {
		
	}
	
	private static void generateRandomGameGrid() {
		for(int i = 0; i < randomGameGrid.length; i++)
			for(int j = 0; j < randomGameGrid[i].length; j++) {
				if(i == j)
					randomGameGrid[i][j] = 0;
				else
					randomGameGrid[i][j] = random.nextInt(8) + 1;
			}
	}
	
	private static List<Game> assignGamesToPlayers(PlayerWrapper playerWrapper) {
		List<Game> playerGames = new ArrayList<>();
		
		int indexOfPlayerWrapper = playerWrappers.indexOf(playerWrapper);
		for(int i = 0; i < randomGameGrid[indexOfPlayerWrapper].length; i++) {
			if(i == indexOfPlayerWrapper)
				continue;

			int gameID = i + 1;
			int numPlayerGoals = randomGameGrid[indexOfPlayerWrapper][i];
			int numOpponentGoals = randomGameGrid[i][indexOfPlayerWrapper];
			
			Game game = new Game(gameID, new Score(numPlayerGoals, numOpponentGoals));
			playerGames.add(game);
		}
		
		return playerGames;
	}
	
	private static Object deepClone(Object obj) {
		return null;
	}
	
	private static PlayerWrapper loadPlayerWrapper(String playerName) {
		return null;
	}
	
	private static Player loadPlayer(String playerName) {
		return null;
	}
	
	private static void updateGUI(HTTPServer server, String content) {
		
	}
	
	private static String getGUIState(double fps) {
		return null;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		setUpStructures();
		parseCommandLineArguments(args);
		runSimulation();
	}
}