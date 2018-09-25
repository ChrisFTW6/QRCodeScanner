package akka.technologies.app.scancode;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class ScannerActivity extends AppCompatActivity implements View.OnClickListener {
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView(R.layout.activity_scanner);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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

		final SurfaceView cameraView = findViewById(R.id.camera_view);
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
					String container = barcodeSparseArray.valueAt(0).displayValue;
					final String[] parseArray = container.split(",");
					Log.d("QR decoded", parseArray[0] + " " + parseArray[1] + " " + parseArray[2]);

					FirebaseDatabase database = FirebaseDatabase.getInstance();
					DatabaseReference participant = database.getReference(parseArray[0]);
					final DatabaseReference flagReference = participant.child("Flag");

					participant.addValueEventListener(new ValueEventListener() {
						@Override
						public void onDataChange(DataSnapshot dataSnapshot) {
							// This method is called once with the initial value and again
							// whenever data at this location is updated.

							String firstNameValue = dataSnapshot.child("FirstName").getValue(String.class);
							String lastNameValue = dataSnapshot.child("LastName").getValue(String.class);
							Integer flagValue = dataSnapshot.child("Flag").getValue(Integer.class);
							if (firstNameValue.equals(parseArray[1]) && lastNameValue.equals(parseArray[2])) {
								//If the flag is zero, that means the participant is not scanned yet!
								if (flagValue != null && flagValue == 0) {
									flagReference.setValue(1);
								} else {
									Log.d("is******************", "It's already checked!");
								}
							} else {
								Log.d("FLAG DATABASE: ", "It's not the same");
							}

						}

						@Override
						public void onCancelled(DatabaseError error) {
							// Failed to read value

						}
					});
//					((TextView)findViewById (R.id.textCode)).setText (barcodeSparseArray.valueAt (0).displayValue);
				}else{
//					((TextView)findViewById (R.id.textCode)).setText ("Nothing received yet");
				}
			}
		});


	}
	
	@Override
	public void onClick (View v) {
//		Nothing to do at the moment
	}
}
