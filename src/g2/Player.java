package g2;

import java.util.*;

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

          List<Game> reallocatedPlayerGames = new ArrayList<>();
          Map<Integer, Double> rankedMap = new HashMap<Integer, Double>();
          Map<Integer, String> rankedMapS = new HashMap<Integer, String>();

          List<Game> wonGames = getWinningGames(playerGames);
          List<Game> drawnGames = getDrawnGames(playerGames);
          List<Game> lostGames = getLosingGames(playerGames);

          List<Game> lostOrDrawnGamesWithReallocationCapacity = new ArrayList<>(lostGames);
          lostOrDrawnGamesWithReallocationCapacity.addAll(drawnGames);
          for(Game lostGame : lostGames) {
               if (lostGame.maxPlayerGoalsReached()) {
                    lostOrDrawnGamesWithReallocationCapacity.remove(lostGame);
               }
          }
          for(Game drawnGame : drawnGames) {
               if (drawnGame.maxPlayerGoalsReached()) {
                    lostOrDrawnGamesWithReallocationCapacity.remove(drawnGame);
               }
          }

          if(!gameHistory.getAllGamesMap().isEmpty() && !gameHistory.getAllAverageRankingsMap().isEmpty()) {
               List<Double> averageRank = new ArrayList<Double>(gameHistory.getAllAverageRankingsMap().get(round-1).values());
               for(int i = 0; i < 9; i++) {
                    int opoID = i;
                    if(i >= teamID) opoID = opoID + 1;
                    Double opoRank = averageRank.get(opoID);
                    Double ourRank = averageRank.get(teamID);
                    rankedMap.put(gameHistory.getAllGamesMap().get(round - 1).get(teamID).get(i).getID(),(Math.abs(ourRank-opoRank)));
                    rankedMapS.put(gameHistory.getAllGamesMap().get(round - 1).get(teamID).get(i).getID(),gameHistory.getAllGamesMap().get(round - 1).get(teamID).get(i).getScoreAsString());
               }
               System.out.println(rankedMapS);
               //System.out.println(gameHistory.getAllGamesMap().get(round - 1).get(teamID).get(8).getNumOpponentGoals());
          }

//          if(!gameHistory.getAllAverageRankingsMap().isEmpty()) {
//
//               List<Double> averageRank = new ArrayList<Double>(gameHistory.getAllAverageRankingsMap().get(round-1).values());
//
//
//               System.out.println(averageRank);
//
//               //Collections.sort(averageRank);
//
//               System.out.println(averageRank);
//               System.out.println(gameHistory.getAllAverageRankingsMap().get(round-1));
//          }

          Comparator<Game> rangeComparatorWon = (Game g1, Game g2) ->
          {return (g1.getNumPlayerGoals()-g1.getNumOpponentGoals()) - (g2.getNumPlayerGoals()-g2.getNumOpponentGoals());};
          Comparator<Game> rangeComparatorRank = (Game g1, Game g2) ->
          {return (int) Math.round((rankedMap.get(g1.getID()) - rankedMap.get(g2.getID()))*1000);};

          Collections.sort(wonGames, rangeComparatorWon.reversed());


          if(round > rounds/4) {
               Collections.sort(lostOrDrawnGamesWithReallocationCapacity, rangeComparatorRank.reversed());
          }

          int i = 0;
          for(Game lossOrDrew : lostOrDrawnGamesWithReallocationCapacity) {
               System.out.println(lossOrDrew.getID() + " " + lossOrDrew.getScoreAsString());
               int rangeWon = Math.min((wonGames.get(i).getNumPlayerGoals()-wonGames.get(i).getNumOpponentGoals()),
                       wonGames.get(i).getHalfNumPlayerGoals());
               int rangeLD = lossOrDrew.getNumOpponentGoals() - lossOrDrew.getNumPlayerGoals();
               if(rangeLD < rangeWon && lossOrDrew.getNumPlayerGoals() + rangeLD + 1 <= 8) {
                    lossOrDrew.setNumPlayerGoals(lossOrDrew.getNumPlayerGoals() + rangeLD + 1);
                    wonGames.get(i).setNumPlayerGoals(wonGames.get(i).getNumPlayerGoals() - rangeLD - 1);
                    //lostOrDrawnGamesWithReallocationCapacity.remove(lossOrDrew);
                    i += 1;
               }
          }

          Collections.sort(lostOrDrawnGamesWithReallocationCapacity, rangeComparatorWon);

          i = 0;
          for(Game lossOrDrew : lostOrDrawnGamesWithReallocationCapacity) {
               System.out.println(lossOrDrew.getID() + " " + lossOrDrew.getScoreAsString());
               int rangeWon = Math.min((wonGames.get(i).getNumPlayerGoals()-wonGames.get(i).getNumOpponentGoals()),
                       wonGames.get(i).getHalfNumPlayerGoals());
               int rangeLD = lossOrDrew.getNumOpponentGoals() - lossOrDrew.getNumPlayerGoals();
               if(rangeLD < rangeWon && lossOrDrew.getNumPlayerGoals() + rangeLD + 1 <= 8) {
                    lossOrDrew.setNumPlayerGoals(lossOrDrew.getNumPlayerGoals() + rangeLD + 1);
                    wonGames.get(i).setNumPlayerGoals(wonGames.get(i).getNumPlayerGoals() - rangeLD - 1);
                    //lostOrDrawnGamesWithReallocationCapacity.remove(lossOrDrew);
                    i += 1;
               }
          }

          reallocatedPlayerGames.addAll(wonGames);
          reallocatedPlayerGames.addAll(drawnGames);
          reallocatedPlayerGames.addAll(lostGames);

          if(checkConstraintsSatisfied(playerGames, reallocatedPlayerGames)) {
               return reallocatedPlayerGames;
          }
          return playerGames;
     }

     private List<Game> getWinningGames(List<Game> playerGames) {
          List<Game> winningGames = new ArrayList<>();
          for(Game game : playerGames) {
               int numPlayerGoals = game.getNumPlayerGoals();
               int numOpponentGoals = game.getNumOpponentGoals();
               if(numPlayerGoals > numOpponentGoals)
                    winningGames.add(game.cloneGame());
          }
          return winningGames;
     }

     private List<Game> getDrawnGames(List<Game> playerGames) {
          List<Game> drawnGames = new ArrayList<>();
          for(Game game : playerGames) {
               int numPlayerGoals = game.getNumPlayerGoals();
               int numOpponentGoals = game.getNumOpponentGoals();
               if(numPlayerGoals == numOpponentGoals)
                    drawnGames.add(game.cloneGame());
          }
          return drawnGames;
     }

     private List<Game> getLosingGames(List<Game> playerGames) {
          List<Game> losingGames = new ArrayList<>();
          for(Game game : playerGames) {
               int numPlayerGoals = game.getNumPlayerGoals();
               int numOpponentGoals = game.getNumOpponentGoals();
               if(numPlayerGoals < numOpponentGoals)
                    losingGames.add(game.cloneGame());
          }
          return losingGames;
     }

     public int checkConstraintsSatisfiedTest(List<Game> originalPlayerGames, List<Game> reallocatedPlayerGames) {

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
               boolean isOriginalWinningGame = hasWonGame(originalPlayerGame);
               boolean isOriginalLosingGame = hasLostGame(originalPlayerGame);
               boolean isOriginalDrawnGame = hasDrawnGame(originalPlayerGame);

               // Constraint 1
               if(reallocatedPlayerGame.getNumPlayerGoals() < 0 || reallocatedPlayerGame.getNumPlayerGoals() > Game.getMaxGoalThreshold()) {
                    return 1;
               }

               // Constraint 2
               if(!originalPlayerGame.getNumOpponentGoals().equals(reallocatedPlayerGame.getNumOpponentGoals())) {
                    return 2;
               }

               // Constraint 3
               boolean numPlayerGoalsIncreased = reallocatedPlayerGame.getNumPlayerGoals() > originalPlayerGame.getNumPlayerGoals();
               if(isOriginalWinningGame && numPlayerGoalsIncreased) {
                    return 3;
               }

               // Constraint 4
               int halfNumPlayerGoals = originalPlayerGame.getHalfNumPlayerGoals();
               boolean numReallocatedPlayerGoalsLessThanHalf =
                       reallocatedPlayerGame.getNumPlayerGoals() < (originalPlayerGame.getNumPlayerGoals() - halfNumPlayerGoals);
               if((isOriginalWinningGame || isOriginalDrawnGame) && numReallocatedPlayerGoalsLessThanHalf) {
                    return 4;
               }

               totalNumOriginalPlayerGoals += originalPlayerGame.getNumPlayerGoals();
               totalNumReallocatedPlayerGoals += reallocatedPlayerGame.getNumPlayerGoals();

               // Constraint 5
               boolean numPlayerGoalsDecreased = reallocatedPlayerGame.getNumPlayerGoals() < originalPlayerGame.getNumPlayerGoals();
               if(isOriginalLosingGame && numPlayerGoalsDecreased) {
                    return 5;
               }

          }

          // Constraint 6
          if(totalNumOriginalPlayerGoals != totalNumReallocatedPlayerGoals) {
               return 6;
          }

          return 7;
     }
}
