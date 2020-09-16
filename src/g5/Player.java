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
        int excessGoals = 0;
        List<Game> reallocatedGames = new ArrayList<Game>();
        List<Game> losingGames = new ArrayList<Game>();
        List<Game> tieGames = new ArrayList<Game>();
        List<Game> winningGames = new ArrayList<Game>();

        for (Game game : playerGames) {
            int playerGoals = game.getNumPlayerGoals();
            int margin = playerGoals - game.getNumOpponentGoals();
            if (margin > 0) {
                int subtract = Math.min(margin-1, game.getHalfNumPlayerGoals());
                excessGoals += subtract;
                game.setNumPlayerGoals(playerGoals-subtract);
                winningGames.add(game);
            }
            else if (margin == 0) {
                if (excessGoals > 0 && playerGoals < 8) {
                    excessGoals -= 1;
                    game.setNumPlayerGoals(playerGoals+1);
                }
                tieGames.add(game);
            }
            else {
                losingGames.add(game);
            }
        }

        losingGames.sort(Comparator.comparingInt(g -> (g.getNumOpponentGoals() - g.getNumPlayerGoals())));

        for (Game game : losingGames) {
            int opponentGoals = game.getNumOpponentGoals();
            int margin = opponentGoals - game.getNumPlayerGoals();
            if (excessGoals > margin && opponentGoals < 8) {
                excessGoals -= margin+1;
                game.setNumPlayerGoals(margin+1);
            }
            reallocatedGames.add(game);
        }

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