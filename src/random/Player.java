// TODO change the package name to reflect your team
package random;

import java.util.Random;
import java.util.List;
import java.util.Map;

import sim.Game;
import sim.GameHistory;

public class Player extends sim.Player {

     private int teamID, rounds, seed;
     private Random random;
     
     /**
      * Player constructor
      *
      * @param teamID  team ID
      * @param rounds  number of rounds
      * @param seed    random seed
      *
      */
     public Player(int teamID, int rounds, int seed) {
          this.teamID = teamID;
          this.rounds = rounds;
          this.seed = seed;
          this.random = new Random(seed);
     }

     /**
      * Reallocate player goals
      *
      * @param gameHistory      cumulative game history from all previous rounds
      * @param playerGames      state of player games before reallocation
      * @param opponentGameMap  state of opponent games before reallocation (map of opponent team IDs to their games)
      * @return                 state of player games after reallocation
      *
      */
     public List<Game> reallocate(GameHistory gameHistory, List<Game> playerGames, Map<Integer, List<Game>> opponentGameMap) {
		return playerGames;
          // TODO add your code here to reallocate player goals
          
     }
}