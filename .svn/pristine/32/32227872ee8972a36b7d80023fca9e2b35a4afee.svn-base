package perasoft.photorubik;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class CommonActivity extends Activity{
	protected Menu mainMenu;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		
		this.mainMenu = menu;
		setMenuSizeCheck();
		return true;
	}

	private void setMenuSizeCheck() {
		int size = Settings.getPuzzleSize(this);
		mainMenu.findItem(R.id.itemSize3).setChecked(size == 3);
		mainMenu.findItem(R.id.itemSize4).setChecked(size == 4);
		mainMenu.findItem(R.id.itemSize5).setChecked(size == 5);
		mainMenu.findItem(R.id.itemSound).setChecked(
				Settings.isSoundEnabled(this));
		mainMenu.findItem(R.id.itemVibration).setChecked(
				Settings.isVibrationEnabled(this));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean b;
		switch (item.getItemId()) {
		case R.id.itemSize3:
			setPuzzleSize(3);
			return true;
		case R.id.itemSize4:
			setPuzzleSize(4);
			return true;
		case R.id.itemSize5:
			setPuzzleSize(5);
			return true;
		case R.id.itemSound:
			b = !item.isChecked();
			item.setChecked(b);
			Settings.setSoundEnabled(this, b);
			return true;
		case R.id.itemVibration:
			b = !item.isChecked();
			item.setChecked(b);
			Settings.setVibrationEnabled(this, b);
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void setPuzzleSize(int i) {
		if (!Helper.testVersion(this))
			return;
		Settings.setPuzzleSize(this, i);
		setMenuSizeCheck();
	}

}
