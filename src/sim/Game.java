package sim;

import java.io.Serializable;

public class Game implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer gameID;
	private Score score;
	
	private static final transient Integer MAX_GOAL_THRESHOLD = 8;
	
	public Game(Integer gameID, Score score) {
		this.gameID = gameID;
		this.score = score;
	}
	
	public Integer getID() {
		return gameID;
	}

	public Score getScore() {
		return score;
	}
	
	public String getScoreAsString() {
		return score.toString();
	}
	
	public void setScore(Score score) {
		this.score = score;
	}
	
	public Integer getNumPlayerGoals() {
		return score.getNumPlayerGoals();
	}

	public Integer getHalfNumPlayerGoals() {
		return (int) Math.ceil(((double) getNumPlayerGoals()) / 2);
	}
	
	public void setNumPlayerGoals(Integer numPlayerGoals) {
		score.setNumPlayerGoals(numPlayerGoals);
	}
	
	public Integer getNumOpponentGoals() {
		return score.getNumOpponentGoals();
	}

	public Integer getHalfNumOpponentGoals() {
		return (int) Math.ceil(((double) getNumOpponentGoals()) / 2);
	}
	
	public void setNumOpponentGoals(Integer numOpponentGoals) {
		score.setNumOpponentGoals(numOpponentGoals);
	}
	
	public boolean maxPlayerGoalsReached() {
		return getNumPlayerGoals() >= MAX_GOAL_THRESHOLD;
	}
	
	public boolean maxOpponentGoalsReached() {
		return getNumOpponentGoals() >= MAX_GOAL_THRESHOLD;
	}
	
	public static Integer getMaxGoalThreshold() {
		return MAX_GOAL_THRESHOLD;
	}
	
	public Game cloneGame() {
		return new Game(this.gameID, this.score.cloneScore());
	}	
}