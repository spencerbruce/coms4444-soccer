package sim;

import java.io.Serializable;

public class Score implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer numPlayerGoals;
	private Integer numOpponentGoals;
	
	public Score(Integer numPlayerGoals, Integer numOpponentGoals) {
		this.numPlayerGoals = numPlayerGoals;
		this.numOpponentGoals = numOpponentGoals;
	}
	
	@Override
	public int hashCode() {
		return 31 * numPlayerGoals.hashCode() + numOpponentGoals.hashCode();
	}
	
	@Override
	public String toString() {
		return "(" + numPlayerGoals + ", " + numOpponentGoals + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null || getClass() != obj.getClass())
			return false;
		
		Score score = (Score) obj;
		if(!this.numPlayerGoals.equals(score.numPlayerGoals))
			return false;

		return this.numOpponentGoals.equals(score.numOpponentGoals);
	}
	
	public void setNumPlayerGoals(Integer numPlayerGoals) {
		this.numPlayerGoals = numPlayerGoals;
	}
	
	public Integer getNumPlayerGoals() {
		return numPlayerGoals;
	}
	
	public void setNumOpponentGoals(Integer numOpponentGoals) {
		this.numOpponentGoals = numOpponentGoals;
	}
	
	public Integer getNumOpponentGoals() {
		return numOpponentGoals;
	}
	
	public Score cloneScore() {
		return new Score(this.numPlayerGoals, this.numOpponentGoals);
	}
}