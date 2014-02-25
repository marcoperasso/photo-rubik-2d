package perasoft.photorubik;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public class Paints {
	private static Paint borderPaint;
	private static Paint tileBorderPaint;

	public static Paint getBorderPaint() {
		if (borderPaint == null) {
			borderPaint = new Paint();
			borderPaint.setStyle(Style.STROKE);
			borderPaint.setColor(Color.RED);
		}
		return borderPaint;
	}

	public static Paint getTileBorderPaint() {
		if (tileBorderPaint == null) {
			tileBorderPaint = new Paint();
			tileBorderPaint.setStyle(Style.STROKE);
			tileBorderPaint.setColor(Color.BLUE);
		}
		return tileBorderPaint;
	}
}
