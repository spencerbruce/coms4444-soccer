package g1_2; // TODO modify the package name to reflect your team

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.PriorityQueue;

import sim.Game;
import sim.GameHistory;
import sim.SimPrinter;

public class Player extends sim.Player {
     //team to (gap to allocated)
     //HashMap<Integer, Map<Integer, PriorityQueue<Integer>>> map = new HashMap();
     // team --> (margin --> avg)
     //HashMap<Integer, Map<Integer, Double>> map = new HashMap<>();
     // gameID -> teamID
     //HashMap<Integer, Integer> gameIDToTeam = new HashMap<>();
     //gap to (frequency, avg allocated across all teams)
     HashMap<Integer, ArrayList<Double>> map = new HashMap<>();
     double stategy1Avg = 1;
     double strategy2Avg = 1;
     int round1 = 0;
     int round2 = 0;
     int cutoff = 50;

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


        //don't use:
        //nothing with gameHistory
        //disregard opponentGamesMap
            // used to detect trends in opponent's reallocations 
        
        
        //use:
        //player games, indexed by round
        //how many we have available to reallocate?
        
        
        // if our team won, remove as many points as possible (half at most)
        // this ensures a win while maximizing points 


        // if it was a draw, either remove all points or do not remove any and hope the opponent removes points
        

            // strategy for future deliverable? vvv
        //sort by won games how much we won by
    //sort lost games by how much we lost by
        //match these up
        // apparently we don't have access to who we played so we cannot predict how a certain team will respond to us 

        // if we won:
            // take away our score - opponents score + 1 so long as our score / 2 is not > this number 

        //information available to us:
        //round rankings for every round
        //average rankings for every round
        //all games all teams have for every round
        //all rounds everyone's points
        //all rounds cumulative points map


        //helper methods:
        //sort

        //methods:
        //1. calculate what opponent is doing -> hashmap stuff
        //2. minimax
        //3. aim for 18 points
        //4. minimize dead weight games
        
        List<Game> reallocatedPlayerGames = new ArrayList<>();
        
        List<Game> wonGames = getWinningGames(playerGames);
        List<Game> drawnGames = getDrawnGames(playerGames);
        List<Game> lostGames = getLosingGames(playerGames);
        
        for(Game lostGame : lostGames)
            if(lostGame.maxPlayerGoalsReached())
                lostGames.remove(lostGame);
        for(Game drawnGame : drawnGames)
            if(drawnGame.maxPlayerGoalsReached())
                drawnGames.remove(drawnGame);

        int strategy = 0;
       /* if(round1 == 0) {
            strategy = 1;
        }
        if(round2 == 0) {
            strategy = 2;
        }*/

        //return strategy2(wonGames, lostGames, drawnGames, reallocatedPlayerGames, playerGames);

        int rand = random.nextInt(100);
        System.out.println("rand is " + rand);

        //int cutoff = (strategy1Avg + strategy2Avg)/2*100;
        if(rand > cutoff) {
            return strategy1(wonGames, lostGames, drawnGames, reallocatedPlayerGames, playerGames);
        }
        else {
            return strategy2(wonGames, lostGames, drawnGames, reallocatedPlayerGames, playerGames);
        }

        /*if(checkConstraintsSatisfied(playerGames, reallocatedPlayerGames)) {
            System.out.println("Did NOT satisfy constraints0");
            return reallocatedPlayerGames;
       }*/

        

        //return strategy1(wonGames, lostGames, drawnGames, reallocatedPlayerGames, playerGames);

        //return strategy2(wonGames, lostGames, drawnGames, reallocatedPlayerGames, playerGames);
        
            
        

            //if we're more than double the score of the opponent, remove score/2
            //else remove our score-their score - 1

            //allocate to random game


            //class 2:
            //sort
            //average points each other team takes away
            //hashset int -> list<int> (margin -> list of how many the opponents changed)
            //aim for 17/18 amount of points


            // sort by max margin for wins, all draws, then smallest margin for losses
            // within each game, find the smallest point games which we won -- take away those games (sacrificial loss)
            // we can further the sorting algorithm by adding feasibility of winning to each category ^ 
                // take top 6 in this sort
                // then starting from 6 and working up to 1, allocate goals

            //how many other team has to reallocate?

            //information available to us:


            //helper methods:
            //sort
                // win: biggest margin --> smallest margin
                // draw: lowest points --> highest points 
                // loss: lowest margin --> highest margin 
                
            //check likelihood of win
                //uses opponents player stats/history 
            //check for # of possible points in player + opponent's reallocation hand 
            //

            //methods:
            //1. calculate what opponent is doing -> hashmap stuff
            //2. minimax (1 opponent vs 9 opponents)?
            //3. aim for 18 points
            //4. minimize dead weight games

            //need to calculate expected value of opponent for each game
            
            //want to add 1 above expected value to as many games as we can

            //sort by score - expected value for wins highest to lowest
            //#available for allocation

            //while we have available goals to allocate
            //sort by expected value - current for draws/losses lowest to highest

        // wins: biggest margin --> smallest margin
     }

     List<Game> strategy1(List<Game> wonGames, List<Game> lostGames, List<Game> drawnGames, List<Game> reallocatedPlayerGames, List<Game> playerGames) {
        /*if(checkConstraintsSatisfied(playerGames, reallocatedPlayerGames)) {
            System.out.println("Did NOT satisfy constraints0");
            return reallocatedPlayerGames;
       }*/

         for(Game game : playerGames) {
             System.out.println("beginning: ID " + game.getID() + ", " + game.getScoreAsString());
         }

        // wins: biggest margin --> smallest margin
        wonGames.sort(
          (Game g1, Game g2) -> (g2.getNumPlayerGoals() - g2.getNumOpponentGoals() 
          - (g1.getNumPlayerGoals() - g1.getNumOpponentGoals()))
        );

        // draws: highest points --> lowest points 
        drawnGames.sort(
          (Game g1, Game g2) -> (g2.getNumPlayerGoals() - g1.getNumOpponentGoals())
        );

        // losses: lowest margin --> highest margin 
          lostGames.sort(
               (Game g1, Game g2) -> (g2.getNumPlayerGoals() - g2.getNumOpponentGoals() 
               - (g1.getNumPlayerGoals() - g1.getNumOpponentGoals()))
          );

        // as per g5's previous approach, we will reallocate goals from wins to draws in order to maximize the number of wins 
        int excessGoals = 0;
        for (Game winningGame : wonGames) {
             if (drawnGames.size() == 0) 
                  break;
             int playerGoals = winningGame.getNumPlayerGoals();
             int margin = playerGoals - winningGame.getNumOpponentGoals();
             // randomize whether we are leaving a margin of 1 or 2 on the win
             int subtractedGoals = Math.min(this.random.nextInt(2) + margin - 2, winningGame.getHalfNumPlayerGoals());
             subtractedGoals = Math.max(subtractedGoals,0);
             System.out.println("Subtracted goals: " + subtractedGoals);
             excessGoals += subtractedGoals;
             winningGame.setNumPlayerGoals(playerGoals - subtractedGoals);
        }
        
        // now that we have the hopeful max # of points from the wins, we reallocate to draws 
        for (Game drawnGame : drawnGames) {
             int playerGoals = drawnGame.getNumPlayerGoals();
             int addedGoals = this.random.nextInt(2);
             // distribute goals once for many draws, randomizing the amount 
             if (excessGoals > 0 && playerGoals < 8) {
                  if ((playerGoals + addedGoals) > 8) addedGoals = 1;
                  excessGoals -= addedGoals;
                  System.out.println("Added goals to draw: " + addedGoals);
                  drawnGame.setNumPlayerGoals(playerGoals + addedGoals);
             }
        }

        System.out.println(excessGoals + " excess goals");
        // reallocate to losses if there are any left 
        for (Game lostGame : lostGames) {
             int playerGoals = lostGame.getNumPlayerGoals();
             // int addedGoals = 1;
             int addedGoals = Math.min(excessGoals, Math.min(lostGame.getNumOpponentGoals() - playerGoals + 1, 8 - playerGoals));
             // int addedGoals = Math.min(excessGoals, lostGame.getNumOpponentGoals() - playerGoals + 1);
             
             // distribute goals once for many losses, randomizing the amount 
             if (excessGoals > 0 && playerGoals < 8 && addedGoals > 0) {
                  excessGoals -= addedGoals;
                  System.out.println("Added goals to loss: " + addedGoals);
                  lostGame.setNumPlayerGoals(playerGoals + addedGoals);
             }
        }     
        reallocatedPlayerGames.addAll(wonGames);
        reallocatedPlayerGames.addAll(drawnGames);
        reallocatedPlayerGames.addAll(lostGames);

        if(checkConstraintsSatisfied(playerGames, reallocatedPlayerGames))
             return reallocatedPlayerGames;
        System.out.println("Does NOT satisfy constraints");
        return playerGames;
     }

     List<Game> strategy2(List<Game> wonGames, List<Game> lostGames, List<Game> drawnGames, List<Game> reallocatedPlayerGames, List<Game> playerGames) {
        wonGames.sort(
            (Game g1, Game g2) -> (g2.getNumPlayerGoals() - g2.getNumOpponentGoals() 
            - (g1.getNumPlayerGoals() - g1.getNumOpponentGoals()))
       );

       // draws: highest points --> lowest points 
       drawnGames.sort(
            (Game g1, Game g2) -> (g2.getNumPlayerGoals() - g1.getNumOpponentGoals())
       );

       // losses: lowest margin --> highest margin 
       lostGames.sort(
            (Game g1, Game g2) -> (g2.getNumPlayerGoals() - g2.getNumOpponentGoals() 
            - (g1.getNumPlayerGoals() - g1.getNumOpponentGoals()))
       );

       // as per g5's previous approach, we will reallocate goals from wins to draws in order to maximize the number of wins 
       int excessGoals = 0;
       for (Game winningGame : wonGames) {
            int playerGoals = winningGame.getNumPlayerGoals();
            int margin = playerGoals - winningGame.getNumOpponentGoals();
            // randomize whether we are leaving a margin of 1 or 2 on the win
            int subtractedGoals = Math.min(this.random.nextInt(2) + margin - 2, winningGame.getHalfNumPlayerGoals());
            excessGoals += subtractedGoals;
            System.out.println("subtractedGoals is " + subtractedGoals);
            winningGame.setNumPlayerGoals(playerGoals - subtractedGoals);
       }

       System.out.println("excess goals1 is " + excessGoals);
       
       // give up on draws
       for (Game drawnGame : drawnGames) {
            int playerGoals = drawnGame.getNumPlayerGoals();
            
            int half = drawnGame.getHalfNumPlayerGoals();
            // System.out.println("half is " + half);
            System.out.println("Taken goals from drawn games: " + half);
            excessGoals+= half;
            drawnGame.setNumPlayerGoals(playerGoals-half);
       }

       System.out.println("excess goals2 is " + excessGoals);
     
       // reallocate to losses if there are any left 
       for (Game lostGame : lostGames) {
            int playerGoals = lostGame.getNumPlayerGoals();
            // int addedGoals = 1;
            int addedGoals = Math.min(excessGoals, Math.min(lostGame.getNumOpponentGoals() - playerGoals + 1, 8 - playerGoals));
            System.out.println("added goals is " + addedGoals);
            // int addedGoals = Math.min(excessGoals, lostGame.getNumOpponentGoals() - playerGoals + 1);
            // distrubute goals one-by-one? 
            
            // distribute goals once for many losses, randomizing the amount 
            if (excessGoals > 0 && playerGoals < 8) {
                 excessGoals += addedGoals;
                   System.out.println("Added goals to loss: " + addedGoals);
                 lostGame.setNumPlayerGoals(playerGoals + addedGoals);
            }
       } 

               // now that we have the hopeful max # of points from the wins, we reallocate to draws 
        for (Game drawnGame : drawnGames) {
             int playerGoals = drawnGame.getNumPlayerGoals();
             int addedGoals = this.random.nextInt(2);
             // distribute goals once for many draws, randomizing the amount 
             if (excessGoals > 0 && playerGoals < 8) {
                  if ((playerGoals + addedGoals) > 8) addedGoals = 1;
                  excessGoals -= addedGoals;
                  System.out.println("Added goals to draw: " + addedGoals);
                  drawnGame.setNumPlayerGoals(playerGoals + addedGoals);
             }
        }

        System.out.println(excessGoals + " excess goals");
        // reallocate to losses if there are any left 
        for (Game lostGame : lostGames) {
             int playerGoals = lostGame.getNumPlayerGoals();
             // int addedGoals = 1;
             int addedGoals = Math.min(excessGoals, Math.min(lostGame.getNumOpponentGoals() - playerGoals + 1, 8 - playerGoals));
             // int addedGoals = Math.min(excessGoals, lostGame.getNumOpponentGoals() - playerGoals + 1);
             
             // distribute goals once for many losses, randomizing the amount 
             if (excessGoals > 0 && playerGoals < 8 && addedGoals > 0) {
                  excessGoals -= addedGoals;
                  System.out.println("Added goals to loss: " + addedGoals);
                  lostGame.setNumPlayerGoals(playerGoals + addedGoals);
             }
        }  
       
       System.out.println("excess goals3 is " + excessGoals);
       reallocatedPlayerGames.addAll(wonGames);
       reallocatedPlayerGames.addAll(drawnGames);
       reallocatedPlayerGames.addAll(lostGames);

       if(checkConstraintsSatisfied(playerGames, reallocatedPlayerGames)) {
            System.out.println("Did NOT satisfy constraints");
            return reallocatedPlayerGames;
       }
       return playerGames;
     }

     // helper methods stolen from random >:D
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
}