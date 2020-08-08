package sim;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameHistory {

	private Map<Integer, Map<Integer, Integer>> allRankingsMap = new HashMap<>();
	private Map<Integer, Map<Integer, Integer>> averageRankingsMap = new HashMap<>();
	private Map<Integer, Map<Integer, List<Game>>> gamesMap = new HashMap<>();
	private Map<Integer, Map<Integer, PlayerPoints>> pointsMap = new HashMap<>();
	
	public Map<Integer, Map<Integer, Integer>> getAllRankingsMap() {
		return allRankingsMap;
	}
	
	public void addRoundRankings(Integer round, Map<Integer, Integer> roundRankingsMap) {
		allRankingsMap.put(round, roundRankingsMap);
	}
	
	public Map<Integer, Map<Integer, Integer>> getAverageRankingsMap() {
		return averageRankingsMap;
	}

	public void addAverageRankings(Integer round, Map<Integer, Integer> roundAverageRankingsMap) {
		averageRankingsMap.put(round, roundAverageRankingsMap);
	}
	
	public Map<Integer, Map<Integer, List<Game>>> getGamesMap() {
		return gamesMap;
	}
	
	public Map<Integer, Map<Integer, PlayerPoints>> getPointsMap() {
		return pointsMap;
	}
	
	public void addGames(Integer round, Map<Integer, List<Game>> roundGamesMap) {
		gamesMap.put(round, roundGamesMap);
	}

	public void addPoints(Integer round, Map<Integer, PlayerPoints> roundPointsMap) {
		pointsMap.put(round, roundPointsMap);
	}
}