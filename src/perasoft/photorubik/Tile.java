package perasoft.photorubik;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

public class Tile {

	private Bitmap mImage;
	private int mTargetX;
	private int mTargetY;
	private int mCurrentX;
	private int mCurrentY;

	private Puzzle mPuzzle;

	public Tile(Point ptCurrent, Point ptTarget, Puzzle puzzle, Bitmap tileImage) {
		mImage = tileImage;
		mCurrentX = ptCurrent.x * mImage.getWidth() + puzzle.getRectangle().left;
		mCurrentY = ptCurrent.y * mImage.getHeight() + puzzle.getRectangle().top;
		mTargetX = ptTarget.x * mImage.getWidth() + puzzle.getRectangle().left;
		mTargetY = ptTarget.y * mImage.getHeight() + puzzle.getRectangle().top;
		mPuzzle = puzzle;
		//mX = x;
		//mY = y;
	}
	public void Draw(Canvas canvas) {
		Rect r = new Rect(getCurrentX(), getCurrentY(), getCurrentX()
				+ mImage.getWidth(), getCurrentY() + mImage.getHeight());
		Rect r1 = new Rect(r);
		r1.intersect(mPuzzle.getRectangle());
		Rect r2 = new Rect(r);
		Rect src1 = new Rect(0, 0, mImage.getWidth(), mImage.getHeight());
		Rect src2 = new Rect(0, 0, mImage.getWidth(), mImage.getHeight());
		if (r2.left < mPuzzle.getRectangle().left)
		{
			//sto muovendo a sinistra
			src1.left = mPuzzle.getRectangle().left - r2.left;
			src2.right = src2.width() - src1.width();
			Log.d("a", src1.toString() + " *** " + src2.toString());
			r2.offsetTo(r2.left + mPuzzle.getWidth(), r2.top);
		}
		else if (r2.right > mPuzzle.getRectangle().right)
		{
			r2.offsetTo(r2.left - mPuzzle.getWidth(), r2.top);
			//sto muovendo a destra
			src2.left = mPuzzle.getRectangle().left - r2.left;
			src1.right = src1.width() - src2.width();
			Log.d("b", src1.toString() + " *** " + src2.toString());
		}
		else if (r2.top < mPuzzle.getRectangle().top)
		{
			//sto muovendo su
			src1.top = mPuzzle.getRectangle().top - r2.top;
			src2.bottom = src2.height() - src1.height();
			r2.offsetTo(r2.left, r2.top + mPuzzle.getHeight());
			
		}
		else if (r2.bottom > mPuzzle.getRectangle().bottom)
		{
			r2.offsetTo(r2.left, r2.top - mPuzzle.getHeight());
			
			//sto muovendo giù
			src2.top =  mPuzzle.getRectangle().top - r2.top;
			src1.bottom = src1.height() - src2.height();
			
		}
		r2.intersect(mPuzzle.getRectangle());
		
		
		draw(canvas, src1, r1);
		draw(canvas, src2, r2);
	}

	private void draw(Canvas canvas, Rect src, Rect r) {
		canvas.drawBitmap(mImage, src, r, null);
		
		r.intersect(mPuzzle.getRectangle());
		canvas.drawRect(r, Paints.getTileBorderPaint());
	}

	public boolean contains(int x, int y) {
		return x >= getCurrentX() && x < getCurrentX() + mImage.getWidth()
				&& y >= getCurrentY() && y < getCurrentY() + mImage.getHeight();
	}

	public boolean isToPosition() {
		return getLogicalCurrentX() % mImage.getWidth() == 0
				&& getLogicalCurrentY() % mImage.getHeight() == 0;
	}

	public int getPositionOffsetX(int signum) {
		return (signum >= 0) ? mImage.getWidth()
				- (getLogicalCurrentX() % mImage.getWidth())
				: -(getLogicalCurrentX() % mImage.getWidth());
	}
	private int getLogicalCurrentX() {
		return (getCurrentX() - mPuzzle.getRectangle().left);
	}

	private int getLogicalCurrentY() {
		return (getCurrentY() - mPuzzle.getRectangle().top);
	}

	public int getPositionOffsetY(int signum) {
		return (signum >= 0) ? mImage.getHeight()
				- (getLogicalCurrentY() % mImage.getHeight())
				: -(getLogicalCurrentY() % mImage.getHeight());
	}
	
	public void moveBy(int deltaX, int deltaY) {
		mCurrentX = (getLogicalCurrentX() + deltaX + mPuzzle.getWidth())
				% mPuzzle.getWidth() + mPuzzle.getRectangle().left;
		mCurrentY = (getLogicalCurrentY() + deltaY + mPuzzle.getHeight())
				% mPuzzle.getHeight() + mPuzzle.getRectangle().top;
	}

	int getCurrentX() {
		return mCurrentX;
	}

	int getCurrentY() {
		return mCurrentY;
	}

	public int getWidth() {
		return mImage.getWidth();
	}

	public int getHeight() {
		return mImage.getHeight();
	}
	public Point getOffset() {
		return new Point(
				getLogicalCurrentX()/getWidth(), 
				getLogicalCurrentY()/getHeight());
	}
	public boolean isToTargetPosition() {
		return getCurrentX() == mTargetX && getCurrentY() == mTargetY;
	}
}
