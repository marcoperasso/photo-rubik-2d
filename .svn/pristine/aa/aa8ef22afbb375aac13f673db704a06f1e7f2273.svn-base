package perasoft.photorubik;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ImportActivity extends Activity {

	private String[] files;
	private Context context;

	Handler hRefresh = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Const.REFRESH:
				View pv = findViewById(R.id.LinearLayoutWait);
				View lv = findViewById(R.id.ListViewFiles);
				pv.setVisibility(View.INVISIBLE);
				pv.setMinimumHeight(0);
				lv.setVisibility(View.VISIBLE);
				setListAdapter(files);

				Button btn = (Button) findViewById(R.id.ButtonStartImport);
				btn.setVisibility(View.VISIBLE);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		context = this;
		setContentView(R.layout.import_puzzle);
		ListView lv = (ListView) this.findViewById(R.id.ListViewFiles);
		lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		Button btn = (Button) findViewById(R.id.ButtonStartImport);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent retData = new Intent();
				retData.putExtra(Const.SELECTED_FILES, getSelectedFiles());
				setResult(RESULT_OK, retData);
				finish();

			}

		});
		if (savedInstanceState != null)
			files = savedInstanceState.getStringArray(Const.SELECTED_FILES);

		super.onCreate(savedInstanceState);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putStringArray(Const.SELECTED_FILES, files);

		super.onSaveInstanceState(outState);
	}

	private String[] getSelectedFiles() {
		Vector<String> list = new Vector<String>();
		ListView lv = (ListView) this.findViewById(R.id.ListViewFiles);
		for (long l : lv.getCheckItemIds())
			list.add(lv.getAdapter().getItem((int) l).toString());

		return list.toArray(new String[list.size()]);
	}

	@Override
	protected void onResume() {
		// se non ho la lista, nascono gli elementi di interazione,
		// faccio vedere la progress e lancio il thread di ricerca
		// al cui termine ripristinerà lo stato originario di visualizzazione
		if (files == null) {
			if (!Helper.isExternalStorageWritable()) {
				Helper.doMessageDialog(context,
						context.getString(R.string.error),
						context.getString(R.string.storage_unavailable),
				new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();						
					}
				});
				files = new String[0];
			} else {
				View pv = findViewById(R.id.LinearLayoutWait);
				View lv = findViewById(R.id.ListViewFiles);
				pv.setVisibility(View.VISIBLE);
				lv.setVisibility(View.INVISIBLE);
				Button btn = (Button) findViewById(R.id.ButtonStartImport);
				btn.setVisibility(View.INVISIBLE);

				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						files = Helper.getExternalPuzzles(context);
						hRefresh.sendEmptyMessage(Const.REFRESH);
					}
				});
				t.start();
			}
		} else
		// altrimenti popolo la listview
		{
			setListAdapter(files);
		}
		super.onResume();
	}

	private void setListAdapter(String[] files) {
		View v = findViewById(R.id.TextViewNoPuzzles);
		v.setVisibility((files.length == 0) ? View.VISIBLE : View.GONE);
		TextView tv = (TextView) findViewById(R.id.ButtonStartImport);
		tv.setText((files.length == 0) ? getString(R.string.close)
				: getString(R.string.import_selected));

		ListView lv = (ListView) this.findViewById(R.id.ListViewFiles);
		ListAdapter adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice, files);
		lv.setAdapter(adapter);
	}
}