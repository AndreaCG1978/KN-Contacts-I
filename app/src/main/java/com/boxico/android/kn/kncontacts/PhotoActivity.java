package com.boxico.android.kn.kncontacts;

import java.io.File;
import java.io.InputStream;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;

import com.boxico.android.kn.kncontacts.util.ConstantsAdmin;

import static android.os.Build.VERSION_CODES.M;

public class PhotoActivity extends Activity {

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;
	private int mPersonaSeleccionadaId = -1;

	private boolean capturoConCamara = false;
	private final int PERMISSIONS_READ_WRITE_STORAGE = 1001;
	private final int PERMISSIONS_CAMERA = 1002;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		try {

			super.onCreate(savedInstanceState);
			mPersonaSeleccionadaId = Integer.valueOf((String)getIntent().getExtras().get(ConstantsAdmin.PERSONA_SELECCIONADA));
			setContentView(R.layout.main);

			startDialog();

		} catch (Exception e) {
			finish();

		}

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions,
										   int[] grantResults) {
		if (requestCode == PERMISSIONS_READ_WRITE_STORAGE) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
					return;
				}
				capturarImagenDesdeAlmacenamiento();
			}
		}
		if (requestCode == PERMISSIONS_CAMERA) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
						&& ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
						&& ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
					return;
				}
				capturarImagenDesdeCamara();
			}
		}


	}

	private void startDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.title_obtener_imagen)
				.setCancelable(true)
				.setPositiveButton(R.string.title_capturar_archivo, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						startFromFileActivity();
					}
				})
				.setNegativeButton(R.string.title_capturar_foto, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						startCameraActivity();
					}
				});
		builder.show();

	}


	private void startFromFileActivity() {
		if (Build.VERSION.SDK_INT >= M) {
			if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
					checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling

				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
						PERMISSIONS_READ_WRITE_STORAGE);
			}else{
				capturarImagenDesdeAlmacenamiento();
			}
		}else{
			capturarImagenDesdeAlmacenamiento();
		}

	}

	private void capturarImagenDesdeAlmacenamiento(){
		capturoConCamara = false;
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	private void capturarImagenDesdeCamara(){

		String folderName = "temp";
		// --Commented out by Inspection (12/11/2018 12:36):protected boolean _taken = true;
		String fileTemp = "temp.jpg";
		File sdImageMainDirectory = ConstantsAdmin.obtenerFile(folderName, fileTemp);


		capturoConCamara = true;
		Uri outputFileUri = null;
		Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		if (Build.VERSION.SDK_INT > M){
			outputFileUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", sdImageMainDirectory);
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		}else{
			outputFileUri = Uri.fromFile(sdImageMainDirectory);
		}
	//	intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

	}

	private void startCameraActivity() {

		if (Build.VERSION.SDK_INT >= M) {
			if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
					|| checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
					|| checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling

				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
						PERMISSIONS_CAMERA);
			}else{
				capturarImagenDesdeCamara();
			}
		}else{
			capturarImagenDesdeCamara();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
			{

				try{
					Bitmap b;
					if(capturoConCamara){
						b  = (Bitmap) data.getExtras().get("data");
						//b = BitmapFactory.decodeFile(ConstantsAdmin.obtenerPath() + File.separator +  "temp" + File.separator + fileTemp, null);
					}else{
						Uri selectedImage = data.getData();
						InputStream imageStream = getContentResolver().openInputStream(selectedImage);
						Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
						double w = bitmap.getWidth();
						double h = bitmap.getHeight();
						if(w > 1500 && h > 1500){
							w = w * 0.15;
							h = h * 0.15;
						}
						int wint = (int)w;
						int hint = (int)h;
						b = Bitmap.createScaledBitmap(bitmap, wint, hint, true);
						/*ByteArrayOutputStream bytes = new ByteArrayOutputStream();
						b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);*/

					}

					ConstantsAdmin.almacenarImagen(this, ConstantsAdmin.folderCSV + File.separator + ConstantsAdmin.imageFolder, "." + String.valueOf(mPersonaSeleccionadaId) + ".jpg", b);
				}
				catch (Exception e) {
					finish();
				}

			}
		}
		finish();
	}



	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
	/*    if (savedInstanceState.getBoolean(CameraCapture.PHOTO_TAKEN)) {
	        _taken = true;
	    }*/
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		//  outState.putBoolean(CameraCapture.PHOTO_TAKEN, _taken);
	}
}
