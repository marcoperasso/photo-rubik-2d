package perasoft.photorubik;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class ViewActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (!Helper.isExternalStorageWritable()) {
			Helper.doMessageDialog(this, getString(R.string.error),
					getString(R.string.storage_unavailable),
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
		} else {
			Uri uri = getIntent().getData();
			StringBuffer messages = new StringBuffer();
			final String importedFile = Helper.importFile(this, messages,
					uri.getPath());
			if (messages.length() > 0) {
				Helper.doMessageDialog(this, getString(R.string.app_title),
						messages.toString(), new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								startPuzzle(importedFile);
							}
						});
			} else {
				startPuzzle(importedFile);
			}
		}
		super.onCreate(savedInstanceState);
	}

	private void startPuzzle(String importedFile) {
		if (importedFile != null) {
			Intent myIntent = new Intent(this, PuzzleActivity.class);
			myIntent.setData(Uri.parse(importedFile));
			startActivity(myIntent);
		}
		finish();
	}
}
