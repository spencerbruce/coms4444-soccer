
# Project 1: Retroactive Soccer

## Course Summary

Course: COMS 4444 Programming and Problem Solving (Fall 2020)  
Website: http://www.cs.columbia.edu/~kar/4444f20  
University: Columbia University  
Instructor: Prof. Kenneth Ross  
TAs: Aditya Sridhar, *<TA 2>*

## Project Description

<p align="center">
     <img src="statics/soccerBall.png"
          alt="Soccer Ball" />
</p>

The project involves a retroactive soccer league. The league has 10 teams, each competing to rank as highly as possible after playing once against every other team. Following conventional soccer leagues, a win counts for 3 points, a draw is 1 point, and a loss earns 0 points. The first round of the league is randomized, and you have no control over the outcome. The simulator will look at each of the 45 games and assign a random number of goals to each team in each game. The number of goals scored comes from the set {0, 1, 2, 3, 4, 5, 6, 7, 8}, where each of the nine games for a team uses exactly one element of this set. The outcome of all games can be computed based on the goals for each team, and the teams can then have their point totals calculated. Teams are then ranked from 1 to 10 based on the point totals. At this point, you can reallocate your goals subject to a set of constraints (see the project on the course website for these constraints). This reallocation is iterated over *r* rounds, where *r* is relatively large. Your team’s point score and rank are computed at the end of each round, and the average rank over all rounds will be called the **final rank**. Your goal for this project is to get as high a final rank as possible.

### Rank Calculation

*Round ranks* or *round rankings* for a round *r* are determined by the total number of points earned in round *r*. For example, if team A scores 17 points between wins and draws, team B scores 14 points, and team C scores 17 points in round *r*, then team A would be ranked higher than team B, while teams A and C would have the same rank.

*Average ranks* at the end of a round *r* are computed as the average of the round ranks over all rounds up to and including round *r*. Note that they are NOT determined by the cumulative point tallies until round *r*, although there might often be a correlation between cumulative points and average rank in simulations.

The first round of the league, round 0, is randomized and not included in the computation of round and average ranks.

## Required Installations
Before you can start working with the simulator and implementing your code, you will first need to set up your environment.

### Java
The simulator is implemented in Java, and you will be required to submit Java code for your project. To check if you have Java already installed, run `javac -version` and `java -version` for the versions of the Java Development Kit (JDK) and Java Runtime Environment (JRE), respectively.

If you do not have Java set up, you will first need to install a JDK, which provides everything that allows you to write and execute Java code inside of a runtime environment. Please download the latest release of Java JDK for your OS [here](https://www.oracle.com/java/technologies/javase-downloads.html) (currently 14.0.2).
* Under Oracle Java SE 14 > Oracle JDK, click on *JDK Download*.
* Click on the installer link corresponding to your OS.
* Check the box to accept the license agreement, and click the download button.
* Once the installer has been downloaded, start the installer and complete the steps.
* Depending on your OS, you might need to set up some environment variables to run Java. This is especially true for Windows and Linux. As a recommendation, follow the instructions [here]([https://www3.ntu.edu.sg/home/ehchua/programming/howto/JDK_Howto.html](https://www3.ntu.edu.sg/home/ehchua/programming/howto/JDK_Howto.html)) to finish the Java environment setup for your OS (note that the website also has full step-by-step instructions that you can follow to install the JDK for your OS).
* Verify now that you have your JDK and JRE set up by rerunning `javac -version` and `java -version`.

You are now ready to start writing Java code!

It is also preferable to develop your Java code in an integrated development environment (IDE) such as [Eclipse](https://www.eclipse.org/downloads/) or [IntelliJ IDEA]().

### Git
Version control with Git will be a large aspect of team-oriented development in this course. You will be managing and submitting your projects using Git. Mac and Linux users can access Git from their terminal. For Windows users, it is preferable to use a common emulator like "Git Bash" to access Git.

Please follow these instructions for installing Git and forking repositories:

1.  Make sure you have Git installed. Instructions on installing Git for your OS can be found [here](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git).
2.  You will need to set up SSH keys for each machine using Git, if you haven't done so. To set up SSH keys, please refer to this [page](https://docs.github.com/en/enterprise/2.20/user/github/authenticating-to-github/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent). Note that you only need to complete the subsection titled _Generating a new SSH key_ for your OS.
3.  Add your newly-generated SSH keys to the GitHub account, as done [here](https://docs.github.com/en/enterprise/2.20/user/github/authenticating-to-github/adding-a-new-ssh-key-to-your-github-account).
4.  Fork any repository (to fork, click on "Fork" in the top-right of the page), and clone the forked repository in your local machine inside the parent directory that will house the repository (to copy the remote URL for cloning, click on the "Clone" button, make sure that "Clone with SSH" is visible - the remote URL should start with `git@github.com` - and copy the URL to the clipboard). You should now be able to stage, commit, push, and pull changes using Git.

## Implementation

You will be creating your own player that extends the simulator's abstract player. Please follow these steps to begin your implementation:
1.  Enter the `coms4444-soccer/src` source directory, and create a folder called "g*x*" (where *x* is the number of your team). For example, if you are team "g5," please create a folder called "g5" in the `src` directory.
2.  Create a class called `Player` inside your newly-created folder, and copy the following code into `Player` (the TODOs indicate all changes you need to make):

```
package gx; // TODO modify the package name to reflect your team

import java.util.List;
import java.util.Map;

import sim.Game;
import sim.GameHistory;

public class Player extends sim.Player {

     /**
      * Player constructor
      *
      * @param teamID  team ID
      * @param rounds  number of rounds
      * @param seed    random seed
      *
      */
     public Player(Integer teamID, Integer rounds, Integer seed) {
          super(teamID, rounds, seed);
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
          // TODO add your code here to reallocate player goals
          
          return null; // TODO modify the return statement to return your list of reallocated player games
     }
}
```

When reallocating games, please make sure to preserve the game IDs (indicated by the `gameID` field in `Game`), even if you create new `Game` instances. The game IDs are used to verify whether or not the reallocation constraints have been satisfied. If you do not preserve the game IDs while returning the list of reallocated games, your player may not have the intended behavior during the simulation.


## Submission
You will be submitting your created team folder, which includes the implemented `Player` class and any other helper classes you create. We ask that you please do not modify any code in the `sim` or `random` directories, especially the simulator, when you submit your code. This makes it easier for us to merge in your code.

To submit your code for each class and for the final deliverable of the project, you will create a pull request to merge your forked repository's *master* branch into the TA's base repository's *master* branch. The TA will merge the commits from the pull request after the deliverable deadline has passed. The base repository will be updated before the start of the next class meeting.

Additionally, please comment out or remove any print statements you may have included, as flooding the console with print statements during the simulation will weaken performance and readability.

## Simulator

#### Steps to run the simulator:
1.  On your command line, *fork* the Git repository, and then clone the forked version. Do NOT clone the original repository.
2.  Enter `cd coms4444-soccer/src` to enter the source folder of the repository.
3.  Run `make compile` to generate the make file.
4.  Update the make file with the set of teams participating in the game.
5.  Run one of the following:
    * `make run`: view results/rankings from the command line
    * `make gui`: view results/rankings from the GUI

#### Simulator arguments:
> **[-r | --rounds]**: number of rounds (default = 10)

> **[-p | --players]**: space-separated players/teams

> **[-s | --seed]**: seed value for random player (default = 10)

> **[-l PATH | --log PATH]**: enable logging and output log to both console and log file

> **[-v | --verbose]**: record verbose log when logging is enabled (default = false)

> **[-e PATH | --export PATH]**: export all game information to CSV file

> **[-g | --gui]**: enable GUI (default = false)

> **[-c | --continuous]**: enable continuous GUI for simulation when GUI is enabled (default = true)

> **[-d | --discrete]**: enable discrete/frame-by-frame GUI for simulation when GUI is enabled (default = false)

> **[-f | --fpm]**: speed (frames per minute) of GUI when continuous GUI is enabled (default = 15)


## GUI Features

### Modes
There are two modes available for the GUI:  *discrete* and *continuous*.
* __Discrete Mode__: this mode allows for running rounds manually on a frame-by-frame basis; this is preferable for visual analysis. To run discrete mode, add the `-d` or `--discrete` flag to the `make gui` command.
* __Continuous Mode__: this mode automatically runs all rounds at a certain speed (frames per minute), which can be specified by passing in the `-f` or `--fpm` flag. To run continuous mode, add the `-c` or `--continuous` flag to the `make gui` command. This is the default mode if no flag is specified.

### Cumulative Results
The cumulative results section of the GUI contains three primary entities: *average rank table*, *cumulative points graph*, and *cumulative win-loss record graph*.
* __Average Rank Table__: this table shows the average rank for all teams. On the final round, the average rank becomes the final rank. Teams are awarded gold for having the best final rank, silver for the second best, and bronze for the third best (illuminated by highlighted rows). If there are ties, teams will share the award for the respective top-three final rank.
* __Cumulative Points Graph__: this graph shows the cumulative points for each team. You can view additional cumulative statistics in the tooltip for each team bar (hover over the bar), including cumulative team points, average rank, total team goals (GF or cumulative goals for), and total opponent goals (GA or cumulative goals against).
* __Cumulative Win-Loss Record Graph__: this graph shows the cumulative wins, losses, and draws for each team. Like with the cumulative points graph, you can access the tooltip for each bar to view the same information. Additionally, you have the ability to enable or disable legend elements to view only a subset of the data (check and uncheck the legend elements); this is especially helpful for visual analysis in discrete mode.

### Round Results

The round results section of the GUI contains two primary entities: *game grid* and *round summary table*.
* __Game Grid__: this grid shows a visual representation of all games played that round. The entries in the grid correspond to the number of goals scored by a team (indicated by the row header) against an opponent (indicated by the column header). For example, if the value in the cell corresponding to the row for team A and the column for team B is 8, then team A scored 8 goals against team B that round. Further, the coloration in the grid indicates margins of victory (lighter green shades for lower margins of victory and darker green shades for higher margins of victory), margins of loss (lighter red shades for lower margins of loss and darker red shades for higher margins of loss), or draws (orange).
* __Round Summary Table__: this table shows the round summary for all teams (ordered by round ranking), including round ranking, total points earned, games played, games won, games lost, and games drawn.


## API Description

The following provides the API available for students to use:
1. `Player`: the player abstraction that should be extended by implemented players.
	*	`reallocate`: an abstract method to reallocate player goals (the method students must implement). A list of player games with reallocated player goals is returned.
	*	`checkConstraintsSatisfied`: checks whether the reallocation is valid subject to the constraints in the project. The list of constraints is provided in the Javadoc comments for the method.
	*	`getID`: returns the unique team ID.
	*	`getNumRounds`: returns the total number of rounds in the simulation
	*	`getSeed`: returns the random seed used in the simulation.
	*	`getRandomGenerator`: returns the random generator constructed from the random seed.
2. `Game`: a wrapper to represent a game instance.
	* `getID`: returns the unique game ID.
	* `getScore`: returns a score for the game as a `Score` type.
	* `getScoreAsString`: returns a score for the game as a `String` type.
	* `setScore`: assigns the score for the game.
	* `getNumPlayerGoals`: returns the number of goals scored by the team (GF/goals for).
	* `getHalfNumPlayerGoals`: returns the ceiling of half the number of goals scored by the team. This method is useful for reallocating goals from winning games.
	* `setNumPlayerGoals`: assigns the number of goals scored by the team.
	* `getNumOpponentGoals`: returns the number of goals scored by the opponent (GA/goals against).
	* `setNumOpponentGoals`: assigns the number of goals scored by the opponent. This method should not be called to reallocate opponent goals, but it may be invoked for setting the number of opponent goals in a new `Game` instance.
	* `maxPlayerGoalsReached`: checks if the number of team goals has reached the maximum threshold (default is 8). This method is helpful for goal reallocation to ensure constraints are satisfied.
	* `maxOpponentGoalsReached`: checks if the number of opponent goals has reached the maximum threshold (default is 8). This method does not need to be called, since only player goals should be reallocated.
	* `getMaxGoalThreshold`: returns the maximum goal threshold (default is 8).
	* `cloneGame`: returns a cloned version of the current `Game` instance.
3. `Score`: a wrapper to represent a score instance.
	* `setNumPlayerGoals`: assigns the number of goals scored by the team.
	* `getNumPlayerGoals`: returns the number of goals scored by the team (GF/goals for).
	* `setNumOpponentGoals`: assigns the number of goals scored by the opponent. This method should not be called to reallocate opponent goals, but it may be invoked for setting the number of opponent goals in a new `Score` instance.
	* `getNumOpponentGoals`: returns the number of goals scored by the opponent (GA/goals against).
	* `toString`: returns a coordinate representation of the score, i.e., a score of (*x*, *y*), which indicates that a team scored *x* points against an opponent that scored *y* points. 
	* `cloneScore`: returns a cloned version of the current `Score` instance.
	* `equals`: checks if two scores are equal.
	* `hashCode`: returns a unique hash for the `Score` instance.
4. `GameHistory`: a running history of all games, points, and rankings.
	*	`getAllGamesMap`: returns all games across all completed rounds.
	*	`addRoundGames`: adds round-specific games for each team to the games map.
	*	`getAllRoundPointsMap`: returns all round point totals across all completed rounds.
	*	`addRoundPoints`: adds round-specific point totals for each team to the round points map.
	*	`getAllCumulativePointsMap`: returns all cumulative point totals across all completed rounds.
	*	`addRoundCumulativePoints`: adds cumulative point totals (up to a specific round) for each team to the cumulative points map.
	*	`getAllRoundRankingsMap`: returns all round rankings across all completed rounds.
	*	`addRoundRankings`: adds round-specific rankings for each team to the round rankings map.
	*	`getAllAverageRankingsMap`: returns all average rankings across all completed rounds.
	*	`addRoundAverageRankings`: adds average rankings (up to a specific round) for each team to the average rankings map.
5. `PlayerPoints`: manages a team's point tally and provides methods to update and retrieve the point tally.
	* `getTotalPoints`: returns the point total.
	* `setTotalPoints`: assigns the point total.
	* `addWinPoints`: adds win points to the total.
	* `subtractWinPoints`: removes win points from the total.
	* `addDrawPoints`: adds draw points to the total.
	* `subtractDrawPoints`: removes draw points from the total.
	* `addLossPoints`: adds loss points to the total (default is 0).
	* `subtractLossPoints`: removes loss points from the total.
	* `getWinPointValue`: returns the win point value (default is 3).
	* `getDrawPointValue`: returns the draw point value (default is 1).
	* `getLossPointValue`: returns the loss point value (default is 0).
	* `toString`: returns the total points as a `String` object.
	* `compareTo`: compares one `PlayerPoints` object to a second and returns whether the point total of the first is higher than, equal to, or lower than that of the second.

Classes that are used by the simulator include:
1. `Simulator`: the simulator and entry point for the project; manages the game history, wrappers for individual players, logging, server, and GUI state.
2. `HTTPServer`: a lightweight web server for the simulator.
3. `PlayerWrapper`: a player wrapper that enforces appropriate timeouts on player goal reallocations.
4. `Timer`: basic functionality for imposing timeouts.
5. `Log`: basic functionality to log results, with the option to enable verbose logging.

## Piazza
If you have any questions about the project, please post them in the [Piazza forum](https://piazza.com/class/kdjd7v2b8925zz?cid=6) for the course, and an instructor will reply to them as soon as possible. Any updates to the project itself will be available on Piazza.


## Disclaimer
This project belongs to Columbia University. It may be freely used for educational purposes.
