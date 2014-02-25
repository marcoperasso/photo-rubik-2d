package perasoft.photorubik;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {
	private static EffectsChangedCallback effectsChanged;
	
	public static int getPuzzleSize(Context context) {
		SharedPreferences settings = context.getSharedPreferences(Const.PREFS_NAME, 0);
		int size = settings.getInt(Const.PUZZLE_SIZE, 3);
		return size;
	}
	
	public static void setPuzzleSize(Context context, int size) {
		SharedPreferences settings = context.getSharedPreferences(Const.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(Const.PUZZLE_SIZE, size);
		editor.commit();
	}

	public static boolean isSoundEnabled(Context context) {
		SharedPreferences settings = context.getSharedPreferences(Const.PREFS_NAME, 0);
		return settings.getBoolean(Const.SOUNDS, true);
	}

	public static void setSoundEnabled(Context context, boolean b) {
		SharedPreferences settings = context.getSharedPreferences(Const.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(Const.SOUNDS, b);
		editor.commit();
		if (effectsChanged != null)
			effectsChanged.onChangedSound(b);
	}

	public static boolean isVibrationEnabled(Context context) {
		SharedPreferences settings = context.getSharedPreferences(Const.PREFS_NAME, 0);
		return settings.getBoolean(Const.VIBRATION, true);
	}

	public static void setVibrationEnabled(Context context,
			boolean b) {
		SharedPreferences settings = context.getSharedPreferences(Const.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(Const.VIBRATION, b);
		editor.commit();
		if (effectsChanged != null)
			effectsChanged.onChangedVibration(b);
	}

	public static void setEffectsChanged(EffectsChangedCallback effectsChanged) {
		Settings.effectsChanged = effectsChanged;
	}

	public static EffectsChangedCallback getEffectsChanged() {
		return effectsChanged;
	}
}
