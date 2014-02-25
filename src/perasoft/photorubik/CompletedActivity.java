package perasoft.photorubik;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CompletedActivity extends Activity {
	private CompletedActivity context;
	private boolean isDialogToShow = true;
	private MediaPlayer mp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		context = this;
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null)
			isDialogToShow = savedInstanceState.getBoolean(Const.SHOW_DIALOG);
		else {
			mp = MediaPlayer.create(this, R.raw.fanfare);
			if (mp != null)
				mp.start();
		}
		setContentView(R.layout.completed);

		int moveCount = (Integer) getIntent().getExtras().get(Const.MOVE_COUNT);
		Long elapsedTime = (Long) getIntent().getExtras().get(
				Const.ELAPSED_TIME);

		ImageView iv = (ImageView) findViewById(R.id.ImageViewResult);

		iv.setImageBitmap(Helper.getBitmap(this, getIntent().getData()
				.toString()));

		if (isDialogToShow)
			showDialog(moveCount, elapsedTime);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(Const.SHOW_DIALOG, isDialogToShow);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		if (mp != null) {
			mp.stop();
			mp.release();
		}
		super.onDestroy();
	}

	private void showDialog(int moveCount, Long elapsedTime) {
		final Dialog dialog = new Dialog(this);

		dialog.setContentView(R.layout.completed_summary);
		dialog.setTitle(R.string.level_completed);

		TextView text = (TextView) dialog
				.findViewById(R.id.TextViewTotalMovesCount);
		text.setText(Integer.toString(moveCount));

		text = (TextView) dialog.findViewById(R.id.TextViewTotalTimeCount);
		text.setText(Helper.getElapsedTime(elapsedTime));

		Button btn = (Button) dialog.findViewById(R.id.ButtonClose);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
				isDialogToShow = false;
			}
		});
		Button btnReplay = (Button) dialog.findViewById(R.id.ButtonReplay);
		btnReplay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

				Intent myIntent = new Intent(context, PuzzleActivity.class);

				Uri file = getIntent().getData();
				myIntent.setData(file);
				// cancello il file con le posizioni per forzare un
				// rimescolamento
				Helper.safeDeleteFile(
						context,
						Helper.getPuzzleMetaFile(file.toString(),
								Settings.getPuzzleSize(context)));
				startActivity(myIntent);
			}
		});
		Button btnSave = (Button) dialog.findViewById(R.id.ButtonSave);
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				File subFolder = Helper.getExternalFolder(context, "Pictures");
				if (subFolder == null)
					return;
				File file = getNewFile(subFolder);

				if (Helper.saveInternalFileToExternalStorage(context,
						getIntent().getData().toString(), file)) {
					context.sendBroadcast(new Intent(
							Intent.ACTION_MEDIA_MOUNTED,
							Uri.parse("file://"
									+ Environment.getExternalStorageDirectory())));

					Helper.doMessageDialog(context,
							getString(R.string.app_title),
							getString(R.string.file_saved, file.toString()));
				}
				dialog.cancel();
				isDialogToShow = false;

			}

			private File getNewFile(File subFolder) {
				int idx = 1;
				File file;
				do {
					file = new File(subFolder.getAbsolutePath()
							+ String.format("/Puzzle_%d.jpg", idx++));
				} while (file.exists());
				return file;
			};
		});
		dialog.show();
	}
}