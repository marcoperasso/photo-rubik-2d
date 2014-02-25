package perasoft.photorubik;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class PuzzleActivity extends CommonActivity {

	private Puzzle mPuzzle;
	private DrawView drawView;
	private PuzzleInfo info;
	private MediaPlayer mp;
	long start;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mp = MediaPlayer.create(this, R.raw.tile_click);

		setContentView(R.layout.puzzle);

		int size = Settings.getPuzzleSize(this);

		LinearLayout cnt = (LinearLayout) findViewById(R.id.PuzzleContainer);

		drawView = new DrawView(this, size, mp);
		drawView.setPuzzleCompleted(new PuzzleCompletedCallback() {

			@Override
			public void onPuzzleCompleted() {
				info.addElepsedTime(System.currentTimeMillis() - start);
				Intent myIntent = new Intent(drawView.getContext(),
						CompletedActivity.class);
				myIntent.setData(Uri.parse(info.getFile()));
				myIntent.putExtra(Const.MOVE_COUNT, info.getMoveCount());
				myIntent.putExtra(Const.ELAPSED_TIME, info.getElapsed());
				startActivity(myIntent);
				finish();
			}
		});
		cnt.addView(drawView);
		if (savedInstanceState == null) {
			Uri uri = getIntent().getData();
			String file = uri.getPath();
			if (!loadPuzzleInfos(file, size)) {
				info = new PuzzleInfo();
				info.setFile(file);
			}
			mPuzzle = new Puzzle(Helper.getBitmap(this, info.getFile()), info);
			drawView.setPuzzle(mPuzzle);
		} else {
			info = (PuzzleInfo) savedInstanceState
					.getSerializable(Const.PUZZLE_KEY);
			mPuzzle = new Puzzle(Helper.getBitmap(this, info.getFile()), info);
			drawView.setPuzzle(mPuzzle);
		}

	}

	@Override
	protected void onResume() {
		start = System.currentTimeMillis();
		super.onResume();
	}

	@Override
	protected void onPause() {
		info.addElepsedTime(System.currentTimeMillis() - start);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		mp.stop();
		mp.release();
		super.onDestroy();

		savePuzzleInfos(Settings.getPuzzleSize(this));
	}

	private void savePuzzleInfos(int puzzleSize) {
		info.setTilePositions(mPuzzle.getTilePositions());
		String fileName = Helper.getPuzzleMetaFile(info.getFile(), puzzleSize);
		FileOutputStream fos;
		try {
			fos = openFileOutput(fileName, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(info);
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			Helper.manageException(e, this);
			return;
		} catch (IOException e) {
			Helper.manageException(e, this);
			return;
		}
	}

	@Override
	protected void setPuzzleSize(int i) {
		super.setPuzzleSize(i);
		Intent myIntent = new Intent(this, PuzzleActivity.class);

		myIntent.setData(Uri.parse(info.getFile()));
		startActivity(myIntent);
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.shuffle:
			mPuzzle.shuffle();
			drawView.invalidate();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private Boolean loadPuzzleInfos(String file, int puzzleSize) {
		String fileName = Helper.getPuzzleMetaFile(file, puzzleSize);

		FileInputStream fis;
		try {
			fis = openFileInput(fileName);

			ObjectInputStream ois = new ObjectInputStream(fis);
			info = (PuzzleInfo) ois.readObject();
			ois.close();
			fis.close();
			return true;
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		info.setTilePositions(mPuzzle.getTilePositions());
		outState.putSerializable(Const.PUZZLE_KEY, info);
		super.onSaveInstanceState(outState);
	}
}

class DrawView extends View {
	private Puzzle puzzle;
	Tile mMovingTile = null;
	int mCurrX = 0;
	int mCurrY = 0;
	int mDeltaX;
	int mDeltaY;
	private int puzzleSize;
	private PuzzleCompletedCallback puzzleCompletedCallback;
	private MediaPlayer mp;
	private boolean soundsEnabled;
	private boolean vibrationEnabled;

	public DrawView(Context context, int size, MediaPlayer mp) {
		super(context);
		setFocusable(true); // necessary for getting the touch events
		this.puzzleSize = size;
		this.mp = mp;
		soundsEnabled = Settings.isSoundEnabled(context);
		vibrationEnabled = Settings.isVibrationEnabled(context);
		Settings.setEffectsChanged(new EffectsChangedCallback() {

			@Override
			public void onChangedVibration(boolean enabled) {
				vibrationEnabled = enabled;
			}

			@Override
			public void onChangedSound(boolean enabled) {
				soundsEnabled = enabled;
			}
		});
	}

	public void setPuzzleCompleted(
			PuzzleCompletedCallback puzzleCompletedCallback) {
		this.puzzleCompletedCallback = puzzleCompletedCallback;

	}

	// the method that draws the balls
	@Override
	protected void onDraw(Canvas canvas) {
		puzzle.Draw(canvas);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		puzzle.create(puzzleSize, w - 1, h - 1);
	}

	// events when touching the screen
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int eventaction = event.getAction();

		int x = (int) event.getX();
		int y = (int) event.getY();

		switch (eventaction) {

		case MotionEvent.ACTION_DOWN:
			mMovingTile = getTile(x, y);
			mCurrX = x;
			mCurrY = y;
			mDeltaX = mDeltaY = 0;
			break;

		case MotionEvent.ACTION_MOVE: // touch drag with the ball
			if (mMovingTile != null) {
				mDeltaX = x - mCurrX;
				mDeltaY = y - mCurrY;
				getPuzzle().moveBy(mMovingTile, mDeltaX, mDeltaY);
				mCurrX = x;
				mCurrY = y;
				invalidate();
			}
			break;

		case MotionEvent.ACTION_UP:
			if (mMovingTile != null) {
				if (!mMovingTile.isToPosition())
					getPuzzle().moveToPosition(mMovingTile,
							(int) Math.signum(mDeltaX),
							(int) Math.signum(mDeltaY));

				if (getPuzzle().isMoving()) {
					if (soundsEnabled)
						mp.start();
					if (vibrationEnabled) {
						Vibrator v = (Vibrator) getContext().getSystemService(
								Context.VIBRATOR_SERVICE);
						v.vibrate(100);
					}
					getPuzzle().resetMoving();
				}

				if (getPuzzle().isCompleted())
					puzzleCompletedCallback.onPuzzleCompleted();

				mMovingTile = null;
				invalidate();
			}
			break;
		}

		return true;

	}

	private Tile getTile(int x, int y) {

		return getPuzzle().getTile(x, y);
	}

	void setPuzzle(Puzzle puzzle) {
		this.puzzle = puzzle;
	}

	Puzzle getPuzzle() {
		return puzzle;
	}
}
