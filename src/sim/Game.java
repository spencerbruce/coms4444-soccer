package sim;

public class Game {

	private Integer gameID;
	private Score score;
	
	public Integer getID() {
		return gameID;
	}

	public Score getScore() {
		return score;
	}
	
	public void setScore(Score score) {
		this.score = score;
	}
	
	public Integer getNumPlayerGoals() {
		return score.getNumPlayerGoals();
	}
	
	public void setNumPlayerGoals(Integer numPlayerGoals) {
		score.setNumPlayerGoals(numPlayerGoals);
	}
	
	public Integer getNumOpponentGoals() {
		return score.getNumOpponentGoals();
	}

	public void setNumOpponentGoals(Integer numOpponentGoals) {
		score.setNumOpponentGoals(numOpponentGoals);
	}
}