package perasoft.photorubik;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

public class TakePhotoActivity extends Activity {

	// PreviewSurfaceCallback previewSurface;
	Camera mCamera;
	Boolean takingPhoto = false;
	private TakePhotoActivity context;

	public class PreviewSurfaceCallback implements SurfaceHolder.Callback {

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// Now that the size is known, set up the camera parameters and
			// begin
			// the preview.
			if (mCamera != null) {
				try {
					Camera.Parameters parameters = mCamera.getParameters();

					// parameters.setPreviewSize(100, 100);
					mCamera.setParameters(parameters);
					mCamera.startPreview();
				} catch (Exception e) {
					Helper.manageException(e, context);
				}
			}

		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// The Surface has been created, acquire the camera and tell it
			// where
			// to draw.
			try {
				mCamera = Camera.open();
				mCamera.setPreviewDisplay(holder);
				ImageButton btnShoot = (ImageButton) findViewById(R.id.ShootButton);
				btnShoot.requestFocus();
			} catch (Exception e) {
				Helper.manageException(e, context);
				if (mCamera != null) {
					mCamera.release();
					mCamera = null;
				}
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// Surface will be destroyed when we return, so stop the preview.
			// Because the CameraDevice object is not a shared resource, it's
			// very
			// important to release it when the activity is paused.
			if (mCamera != null) {
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
			}

		}
	};

	PreviewSurfaceCallback surfaceCallback = new PreviewSurfaceCallback();

	OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mCamera == null || takingPhoto)
				return;
			takingPhoto = true;
			View iv = findViewById(R.id.ProgressBarPhoto);
			iv.setVisibility(View.VISIBLE);

			mCamera.autoFocus(autoFocusCallback);

		}
	};
	AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {

		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			// if (success) {
			camera.takePicture(null, null, pictureCallback);
			/*
			 * } else { Helper.doMessageDialog(context,
			 * context.getString(R.string.error),
			 * context.getString(R.string.autofocus_failed)); View iv =
			 * findViewById(R.id.ProgressBarPhoto);
			 * iv.setVisibility(View.INVISIBLE); takingPhoto = false; }
			 */

		}

	};

	PictureCallback pictureCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			String fileName = Helper.getPuzzleFileName(context);
			FileOutputStream fos;
			try {
				fos = openFileOutput(fileName, Context.MODE_PRIVATE);
				fos.write(data);
				fos.close();
			} catch (FileNotFoundException e) {
				Helper.manageException(e, context);
				return;
			} catch (IOException e) {
				Helper.manageException(e, context);
				return;
			}
			takingPhoto = false;
			View iv = findViewById(R.id.ProgressBarPhoto);
			iv.setVisibility(View.INVISIBLE);
			SurfaceView sv = (SurfaceView) findViewById(R.id.SurfaceView01);
			Intent myIntent = new Intent(sv.getContext(), PuzzleActivity.class);
			myIntent.setData(Uri.parse(fileName));
			startActivity(myIntent);
			finish();
		}

	};
	private OrientationEventListener myOrientationEventListener;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;

		// remove title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// remove status bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);

		setContentView(R.layout.camera);

		ImageButton btnShoot = (ImageButton) this
				.findViewById(R.id.ShootButton);

		btnShoot.setOnClickListener(onClickListener);
		SurfaceView sv = (SurfaceView) this.findViewById(R.id.SurfaceView01);
		sv.getHolder().addCallback(surfaceCallback);
		sv.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		myOrientationEventListener = new OrientationEventListener(this,
				SensorManager.SENSOR_DELAY_NORMAL) {

			private int mOrientation = -1;

			@Override
			public void onOrientationChanged(int orientation) {
				if (orientation == ORIENTATION_UNKNOWN || mCamera == null
						|| takingPhoto)
					return;
				if (orientation > 315 || orientation <= 45)
					orientation = 0;
				else if (orientation > 45 && orientation <= 135)
					orientation = 90;
				else if (orientation > 135 && orientation <= 225)
					orientation = 180;
				else if (orientation > 225 && orientation <= 315)
					orientation = 270;
				if (mOrientation == orientation)
					return;
				mOrientation = orientation;

				try {
					Parameters p = mCamera.getParameters();
					int rotation = (orientation + 90) % 360;
					p.setRotation(rotation);
					mCamera.setParameters(p);
				} catch (Exception e) {
					Helper.manageException(e, context);
				}
			}
		};
	}

	@Override
	protected void onPause() {
		myOrientationEventListener.disable();
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (myOrientationEventListener.canDetectOrientation())
			myOrientationEventListener.enable();
		super.onResume();
	}
}
