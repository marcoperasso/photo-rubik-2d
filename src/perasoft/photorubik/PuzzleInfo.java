package perasoft.photorubik;

import java.io.Serializable;

public class PuzzleInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2077889593947243299L;
	private String file;
	private Point[] tilePositions;
	private int moveCount = 0;
	private long elapsed = 0;
	
	void setFile(String file) {
		this.file = file;
	}

	String getFile() {
		return file;
	}

	void setTilePositions(Point[] tilePositions) {
		this.tilePositions = tilePositions;
	}

	Point[] getTilePositions() {
		return tilePositions;
	}

	public void setMoveCount(int moveCount) {
		this.moveCount = moveCount;
	}

	public int getMoveCount() {
		return moveCount;
	}

	public void increaseMoveCount() {
		moveCount++;
	}

	public void addElepsedTime(long l) {
		elapsed += l;
		
	}

	public void setElapsed(long elapsed) {
		this.elapsed = elapsed;
	}

	public long getElapsed() {
		return elapsed;
	}

	
}
