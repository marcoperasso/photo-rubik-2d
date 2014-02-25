package perasoft.photorubik;

import java.util.Calendar;
import java.util.Random;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Puzzle {

	Vector<Tile> mTiles = new Vector<Tile>();
	private int mWidth;
	private int mHeight;
	private boolean mMovingX = false;
	private boolean mMovingY = false;
	private Bitmap image;
	private Rect rectangle;

	private int size;
	private boolean created = false;
	private PuzzleInfo info;

	public Puzzle(Bitmap bmp, PuzzleInfo info) {
		image = bmp;
		this.info = info;
	}

	public void create(int size, int totalWidth, int totalHeight) {
		this.size = size;
		int tileBmpWidth = image.getWidth() / size;
		int tileBmpHeight = image.getHeight() / size;

		int tileWidth = tileBmpWidth * totalWidth / image.getWidth();
		int tileHeight = tileBmpHeight * tileWidth / tileBmpWidth; // per
																	// mantenere
																	// le
																	// proporzioni
		if (tileHeight * size > totalHeight) {// ho sforato in verticale,
												// ricalcolo tutto partendo
												// dall'altezza
			tileHeight = tileBmpHeight * totalHeight / image.getHeight();
			tileWidth = tileBmpWidth * tileHeight / tileBmpHeight;
		}

		// per tenere conto degli scarti di arrotondamento
		this.mWidth = tileWidth * size;
		this.mHeight = tileHeight * size;
		rectangle = new Rect(0, 0, this.mWidth, this.mHeight);
		rectangle.offset((totalWidth - this.mWidth) / 2,
				(totalHeight - this.mHeight) / 2);

		int prog = 0;
		Point[] tilePositions = info.getTilePositions();
		while (true) {
			try {
				mTiles.removeAllElements();
				for (int x = 0; x < size; x++)
					for (int y = 0; y < size; y++) {
						Bitmap tileImage = Bitmap.createBitmap(image, x
								* tileBmpWidth, y * tileBmpHeight,
								tileBmpWidth, tileBmpHeight);
						tileImage = Bitmap.createScaledBitmap(tileImage,
								tileWidth, tileHeight, true);
						Point targetPt = new Point(x, y);
						Point pt = tilePositions == null ? targetPt
								: tilePositions[prog++];
						Tile t = new Tile(pt, targetPt, this, tileImage);
						mTiles.add(t);

					}
				break;
			} catch (Exception e) {
				// si è verificato un errore a causa delle posizioni salvate?
				// riprovo senza!
				if (tilePositions != null)
					tilePositions = null;
				else
					break;
			}
		}

		// se non ho posizioni iniziali, non sto provenendo da uno sato salvato
		// quindi devo mischiare le mattonelle
		 if (tilePositions == null)
			 shuffle();
		created = true;
	}

	public Point[] getTilePositions() {
		Point[] ppts = new Point[mTiles.size()];
		int idx = 0;
		for (Tile t : mTiles) {
			ppts[idx++] = t.getOffset();
		}
		return ppts;
	}

	public void shuffle() {
		Random rnd = new Random();
		rnd.setSeed(Calendar.getInstance().getTimeInMillis());
		int moves = 25 + rnd.nextInt(50);
		for (int i = 0; i < moves; i++) {
			int tileIdx = rnd.nextInt(mTiles.size());
			Tile t = mTiles.get(tileIdx);
			int step = rnd.nextInt(size);
			mMovingX = true;
			moveBy(t, step * mWidth, 0);
			moveToPosition(t, 1, 0);
			mMovingX = false;
			step = rnd.nextInt(size);
			mMovingY = true;
			moveBy(t, 0, step * mHeight);

			moveToPosition(t, 0, 1);
			mMovingY = false;
		}
		info.setMoveCount(0);
	}

	public void Draw(Canvas canvas) {
		for (Tile t : mTiles) {
			t.Draw(canvas);
		}
		canvas.drawRect(getRectangle(), Paints.getBorderPaint());

	}

	public Tile getTile(int pointX, int pointY) {
		for (Tile t : mTiles) {
			if (t.contains(pointX, pointY))
				return t;
		}

		return null;
	}

	public void moveBy(Tile movingTile, int deltaX, int deltaY) {
		if (!mMovingX && !mMovingY) {
			if (deltaX == 0 && deltaY == 0)
				return;
			mMovingX = Math.abs(deltaX) > Math.abs(deltaY);
			mMovingY = !mMovingX;
		}
		if (mMovingX) {
			if (deltaX == 0)
				deltaX = 1;
			for (Tile t : mTiles) {
				if (t.getCurrentY() == movingTile.getCurrentY())
					t.moveBy(deltaX, 0);
			}
		}
		if (mMovingY) {
			if (deltaY == 0)
				deltaY = 1;
			for (Tile t : mTiles) {
				if (t.getCurrentX() == movingTile.getCurrentX())
					t.moveBy(0, deltaY);
			}
		}

	}

	int getWidth() {
		return mWidth;
	}

	int getHeight() {
		return mHeight;
	}

	public void resetMoving() {
		mMovingX = mMovingY = false;

	}

	public boolean isMoving() {
		return mMovingX || mMovingY;
	}

	boolean getMovingX() {
		return mMovingX;
	}

	boolean getMovingY() {
		return mMovingY;
	}

	public void moveToPosition(Tile floatingTile, int signumX, int signumY) {
		if (mMovingX)
			moveBy(floatingTile, floatingTile.getPositionOffsetX(signumX), 0);
		else if (mMovingY)
			moveBy(floatingTile, 0, floatingTile.getPositionOffsetY(signumY));

		if (created)
			info.increaseMoveCount();
	}

	public Rect getRectangle() {
		return rectangle;
	}

	public boolean isCreated() {
		return created;
	}

	public boolean isCompleted() {
		for (Tile t : mTiles) {
			if (!t.isToTargetPosition())
				return false;
		}
		return true;
	}

	public int getMoveCount() {

		return info.getMoveCount();
	}

}