package perasoft.photorubik;

import java.io.File;

import com.google.ads.*;
import com.google.ads.AdView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MainMenuActivity extends CommonActivity {
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		context = this;
		setContentView(R.layout.main);
		Button btnNew = (Button) this.findViewById(R.id.ButtonNew);
		btnNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(),
						TakePhotoActivity.class);
				startActivity(myIntent);

			}

		});

		Button btnImport = (Button) this.findViewById(R.id.ButtonOpen);
		btnImport.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!Helper.testVersion(context))
					return;

				Intent intent = new Intent(context, ImportActivity.class);
				startActivityForResult(intent, Const.PICK_REQUEST_CODE);
			}

		});
		ListView lv = (ListView) this.findViewById(R.id.ListView01);

		registerForContextMenu(lv);
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View childView,
					int position, long id) {
				Intent myIntent = new Intent(childView.getContext(),
						PuzzleActivity.class);
				String s = parentView.getItemAtPosition(position).toString();
				myIntent.setData(Uri.parse(s));
				startActivity(myIntent);

			}
		});
		
		 // Look up the AdView as a resource and load a request.
	    AdView adView = (AdView)this.findViewById(R.id.ad);
	    adView.loadAd(new AdRequest());
	    super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == Const.PICK_REQUEST_CODE) {
				String[] files = data.getStringArrayExtra(Const.SELECTED_FILES);
				importFiles(files);

			}
		}
	}

	private void importFiles(String[] files) {
		StringBuffer messages = new StringBuffer();
		for (String file : files) {
			Helper.importFile(this, messages, file);
		}
		if (messages.length() > 0)
			Helper.doMessageDialog(context, getString(R.string.app_title),
					messages.toString());
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean b = super.onCreateOptionsMenu(menu);
		if (b)
			mainMenu.findItem(R.id.shuffle).setVisible(false);
		return b;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.filelist, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.delete:
			askAndDeleteFile(info);
			return true;
		case R.id.send:
			if (!Helper.testVersion(context))
				return true;
			Intent sendIntent = new Intent(Intent.ACTION_SEND);
			String internalFile = getSelectedFile(info);
			sendIntent.putExtra(Intent.EXTRA_SUBJECT,
					getString(R.string.sending_puzzle, internalFile));
			sendIntent.putExtra(Intent.EXTRA_TEXT,
					getString(R.string.try_solving_puzzle));

			File subFolder = Helper.getExternalFolder(context,
					Const.TemporaryFolder);
			if (subFolder == null)
				return true;
			File file = new File(subFolder.getAbsolutePath() + "/"
					+ internalFile);

			if (Helper.saveInternalFileToExternalStorage(context, internalFile,
					file)) {
				String sentFile = "file://" + file.getAbsolutePath();
				sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(sentFile));
				sendIntent.setType("*/*");
				startActivity(Intent.createChooser(sendIntent,
						getString(R.string.send_puzzle)));
				file.deleteOnExit();
			}
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void askAndDeleteFile(final AdapterContextMenuInfo info) {
		final MainMenuActivity activity = this;
		final String file = getSelectedFile(info);

		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(R.string.app_title);
		alertDialog.setMessage(activity.getString(R.string.askDelete, file));
		alertDialog.setButton(activity.getString(R.string.yes),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						Helper.safeDeleteFile(context, file);
						Helper.safeDeleteFile(context,
								Helper.getPuzzleMetaFile(file, 3));
						Helper.safeDeleteFile(context,
								Helper.getPuzzleMetaFile(file, 4));
						Helper.safeDeleteFile(context,
								Helper.getPuzzleMetaFile(file, 5));

						setListAdapter();
					}
				});
		alertDialog.setButton2(activity.getString(R.string.no),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				});

		alertDialog.show();

	}

	private String getSelectedFile(final AdapterContextMenuInfo info) {
		ListView lv = (ListView) findViewById(R.id.ListView01);
		return lv.getItemAtPosition(info.position).toString();
	}

	@Override
	protected void onResume() {
		setListAdapter();

		super.onResume();
	}

	private void setListAdapter() {
		ListView lv = (ListView) this.findViewById(R.id.ListView01);
		ListAdapter adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				Helper.getPuzzleFiles(this));
		lv.setAdapter(adapter);
	}

}
