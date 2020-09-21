package g5; // TODO modify the package name to reflect your team

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import sim.Game;
import sim.GameHistory;
import sim.SimPrinter;

public class Player extends sim.Player {

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

    // get game IDs where opponents have higher ranks
    public List<Integer> getGamesToBeatIDs(Integer round, GameHistory gameHistory) {
        List<Integer> gamesToBeat = new ArrayList<Integer>();
        Map<Integer, Double> rankings = gameHistory.getAllAverageRankingsMap().get(round-1);

        for (Map.Entry<Integer, Double> ranking : rankings.entrySet()) {
            Integer otherTeamID = ranking.getKey();
            if (!otherTeamID.equals(teamID) && ranking.getValue() <= rankings.get(teamID)) {
                gamesToBeat.add(otherTeamID);
            }
        }
        return gamesToBeat;
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
        List<Integer> gamesToBeat = new ArrayList<Integer>();
        if (round > 1) {
            // get game IDs where opponents have higher ranks
            gamesToBeat = getGamesToBeatIDs(round, gameHistory);
        }

        int excessGoals = 0;
        List<Game> reallocatedGames = new ArrayList<Game>();
        List<Game> losingGames = new ArrayList<Game>();
        List<Game> tieGames = new ArrayList<Game>();
        List<Game> winningGames = new ArrayList<Game>();

        for (Game game : playerGames) {
            int playerGoals = game.getNumPlayerGoals();
            int margin = playerGoals - game.getNumOpponentGoals();
            // get winning games and retrieve the excess goals
            if (margin > 0) {
                int subtract = Math.min(margin-1, game.getHalfNumPlayerGoals());
                excessGoals += subtract;
                game.setNumPlayerGoals(playerGoals-subtract);
                winningGames.add(game);
            }
            // get tied games and allocate 1 if max score hasn't yet been reached
            else if (margin == 0) {
                // if against opponent that is ranked higher, allocate more points
                if (excessGoals > 0 && playerGoals < 8 && gamesToBeat.contains(game.getID())) {
                    excessGoals -= 1;
                    game.setNumPlayerGoals(playerGoals+1);
                    playerGoals += 1;
                }
                if (excessGoals > 0 && playerGoals < 8) {
                    excessGoals -= 1;
                    game.setNumPlayerGoals(playerGoals+1);
                }
                tieGames.add(game);
            }
            // get losing games
            else {
                losingGames.add(game);
            }
        }

        // sort losing games by smallest margin to win
        losingGames.sort(Comparator.comparingInt(g -> (g.getNumOpponentGoals() - g.getNumPlayerGoals())));

        // reallocate goals to the losing games
        for (Game game : losingGames) {
            int opponentGoals = game.getNumOpponentGoals();
            int playerGoals = game.getNumPlayerGoals();
            int margin = opponentGoals - game.getNumPlayerGoals();
            if (excessGoals > margin && opponentGoals < 8) {
                excessGoals -= margin+1;
                game.setNumPlayerGoals(margin+1);
            }
            // if against opponent that is ranked higher, allocate more points
            if (excessGoals > 0 && playerGoals < 8 && gamesToBeat.contains(game.getID())) {
                excessGoals -= 1;
                game.setNumPlayerGoals(playerGoals+1);
            }
            reallocatedGames.add(game);
        }

        // If there are extra goals left over, redistribute them to the tied games and winning games
        // TODO: break down into helper functions
        for (Game game : tieGames) {
            int playerGoals = game.getNumPlayerGoals();
            if (excessGoals > 0 && playerGoals < 8) {
                excessGoals -= 1;
                game.setNumPlayerGoals(playerGoals+1);
            }
            reallocatedGames.add(game);
        }

        for (Game game : winningGames) {
            int playerGoals = game.getNumPlayerGoals();
            if (excessGoals > 0 && playerGoals < 8) {
                excessGoals -= 1;
                game.setNumPlayerGoals(playerGoals+1);
            }
            reallocatedGames.add(game);
        }

        return reallocatedGames;
    }
}