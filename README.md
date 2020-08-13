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

## Required Installations
Before you can start working with the simulator and implementing your code, you will first need to set up your environment.

### Java
The simulator is implemented in Java, and you will be required to submit your project using Java. If you do not have Java set up, please follow these instructions:

### Git
Version control with Git will be a large aspect of team-oriented development in this course. You will be managing and submitting your projects using Git. Mac and Linux users can access Git from their terminal. For Windows users, it is preferrable to use a common emulator like "Git Bash" to access Git.

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
> **[-r | --rounds]**: number of rounds

> **[-p | --players]**: space-separated players/teams

> **[-s | --seed]**: seed value for random player

> **[-l | --log]**: enable logging and specify the log file

> **[-v | --verbose]**: flag for recording verbose log during games when logging is enabled

> **[-g | --gui]**: enable the GUI

> **[-c | --continuous]**: continuous GUI for simulation when GUI is enabled

> **[-d | --discrete]**: discrete/frame-by-frame GUI for simulation when GUI is enabled

> **[-f | --fps]**: speed (frames per second) of the GUI when continuous GUI is enabled


## Class Design

### Class Diagram

### Description of Important Classes/Methods



## Piazza
If you have any questions about the project, please post them in the [Piazza forum](https://piazza.com/class/kdjd7v2b8925zz?cid=6) for the course, and an instructor will reply to them as soon as possible. Any updates to the project itself will be available on Piazza.


## Disclaimer
This project belongs to Columbia University. It may be freely used for educational purposes.
