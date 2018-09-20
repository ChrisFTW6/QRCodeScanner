package akka.technologies.app.scancode;

import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_main);
		
		//We create the barcode detector
		final BarcodeDetector barcodeDetector = new BarcodeDetector
				.Builder (getApplicationContext ())
				.build ();
		
		//We create the object where we are going to receive the picture of the camera
		final CameraSource cameraSource = new CameraSource
				.Builder (getApplicationContext (),barcodeDetector)
				.setAutoFocusEnabled (true)
				.setRequestedPreviewSize (480,480)
				.build ();
		
		final SurfaceView cameraView = (SurfaceView)findViewById (R.id.camera_view);
		cameraView.getHolder ().addCallback (new SurfaceHolder.Callback () {
			@Override
			public void surfaceCreated (SurfaceHolder holder) {
				if (ContextCompat.checkSelfPermission(getApplicationContext (), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
					try {
						cameraSource.start(cameraView.getHolder());
						Log.d ("CAMERA WORKING?", "Yes, it seems that the camera is working");
					} catch (IOException ie) {
						Log.e("CAMERA SOURCE", ie.getMessage());
					}
				} else {
					Toast.makeText(getApplicationContext (), getResources().getString(R.string.error_camera), Toast.LENGTH_SHORT).show();
				}
			}
			
			@Override
			public void surfaceChanged (SurfaceHolder holder, int format, int width, int height) {
				//Nothing to do with that at the moment
			}
			
			@Override
			public void surfaceDestroyed (SurfaceHolder holder) {
				cameraSource.stop ();
			}
		});
		
		barcodeDetector.setProcessor (new Detector.Processor<Barcode> () {
			@Override
			public void release () {
			
			}
			
			@Override
			public void receiveDetections (Detector.Detections<Barcode> detections) {
				final SparseArray<Barcode> barcodeSparseArray = detections.getDetectedItems ();
				if(barcodeSparseArray.size () != 0){
					Log.d ("QR decoded", barcodeSparseArray.valueAt (0).displayValue);
//					((TextView)findViewById (R.id.textCode)).setText (barcodeSparseArray.valueAt (0).displayValue);
				}else{
//					((TextView)findViewById (R.id.textCode)).setText ("Nothing received yet");
				}
			}
		});

		FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference myRef = database.getReference("message");

		myRef.setValue("Hello, World!");
	}
	
	@Override
	public void onClick (View v) {
//		Nothing to do at the moment
	}
}
