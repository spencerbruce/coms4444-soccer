package sim;

import java.io.Serializable;

public class PlayerPoints implements Comparable<PlayerPoints>, Serializable {
	
	private Integer numPoints;
	
	private static final long serialVersionUID = 1L;
	
	private static final transient Integer WIN_POINTS = 3;
	private static final transient Integer DRAW_POINTS = 1;
	private static final transient Integer LOSS_POINTS = 0;
	
	public PlayerPoints() {
		this.numPoints = 0;
	}
	
	public PlayerPoints(Integer numPoints) {
		this.numPoints = numPoints;
	}
	
	public Integer getTotalPoints() {
		return numPoints;
	}

	public void setTotalPoints(Integer numPoints) {
		this.numPoints = numPoints;
	}
	
	public void addWinPoints() {
		numPoints += WIN_POINTS;
	}

	public void subtractWinPoints() {
		numPoints -= WIN_POINTS;
	}
	
	public void addDrawPoints() {
		numPoints += DRAW_POINTS;
	}

	public void subtractDrawPoints() {
		numPoints -= DRAW_POINTS;
	}

	public void addLossPoints() {
		numPoints += LOSS_POINTS;
	}
	
	public void subtractLossPoints() {
		numPoints -= LOSS_POINTS;
	}
	
	public static Integer getWinPointValue() {
		return WIN_POINTS;
	}
	
	public static Integer getDrawPointValue() {
		return DRAW_POINTS;
	}
	
	public static Integer getLossPointValue() {
		return LOSS_POINTS;
	}

	@Override
	public String toString() {
		return Integer.toString(numPoints);
	}
	
	@Override
	public int compareTo(PlayerPoints playerPoints) {
		return (int) this.numPoints - playerPoints.getTotalPoints();
	}
}