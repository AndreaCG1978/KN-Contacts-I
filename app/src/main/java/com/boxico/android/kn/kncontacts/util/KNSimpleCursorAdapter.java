package com.boxico.android.kn.kncontacts.util;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.boxico.android.kn.kncontacts.R;

public class KNSimpleCursorAdapter extends SimpleCursorAdapter {

	private final Activity localContext;


	public KNSimpleCursorAdapter(Activity context, int layout, Cursor c,
								 String[] from, int[] to) {
		super(context, layout, c, from, to, 0);
		localContext = context;
		// TODO Auto-generated constructor stub
	}


	private boolean mostrarFoto(final TextView tv, long idPer){
		boolean muestraFoto = false;
		try {
		/*	Asociacion puedeCargar = ConstantsAdmin.comprobarSDCard(localContext);
			boolean puede = (Boolean) puedeCargar.getKey();

			if(puede){*/
				String path = ConstantsAdmin.obtenerPathImagen() + "." +String.valueOf(idPer)  + ".jpg";
				if(ConstantsAdmin.existeArchivo(path)){


					Bitmap b = BitmapFactory.decodeFile(path);
					Bitmap small = null;
					small = Bitmap.createScaledBitmap(b, 55, 65, true);
					final Drawable icon = new BitmapDrawable(localContext.getResources(), small);
					//final Drawable icon = Drawable.createFromPath(ConstantsAdmin.obtenerPathImagen() + String.valueOf(idPer)  + ".jpg");
					if(icon != null){
						tv.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
						tv.setCompoundDrawablePadding(1);
						//	Bitmap big = Bitmap.createScaledBitmap(b, 300, 350, true);
						Bitmap big = b;
						final Drawable iconBig = new BitmapDrawable(localContext.getResources(), big);
						muestraFoto = true;
						tv.setOnTouchListener(new View.OnTouchListener() {
							@Override
							public boolean onTouch(View v, MotionEvent event) {
								if(event.getAction() == MotionEvent.ACTION_DOWN) {
								//	if(event.getRawX() <= tv.getTotalPaddingLeft()) {
										// your action for drawable click event
									ConstantsAdmin.showFotoPopUp(iconBig, localContext);
									return true;
								//	}
								}
								return true;
							}



						});

					}
			//	}
			}
    		/*


    		if(puede){
            	Bitmap b = BitmapFactory.decodeFile(ConstantsAdmin.obtenerPathImagen() + String.valueOf(idPer)  + ".jpg");
            	if(b != null){
            		photo.setVisibility(View.VISIBLE);
            		photo.setImageBitmap(b);
            	}else{
            		photo.setVisibility(View.GONE);
            	}
    		}else{
    			photo.setVisibility(View.GONE);
    		}*/

		} catch (Exception e) {
			//photo.setVisibility(View.GONE);
		}
		return muestraFoto;


	}


	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		super.bindView(view, context, cursor);
	//	boolean miniFoto = false;
		TextView textApe = view.findViewById(R.id.rowApellido);
		TextView textNom = view.findViewById(R.id.rowNombres);
		TextView textDR	= view.findViewById(R.id.rowDatoRelevante);
		TextView textFoto = view.findViewById(R.id.rowFoto);
	//	textApe.setPadding(5, 5, 5, 5);
	//	textNom.setPadding(5, 5, 5, 5);
		textApe.setTextSize(15);
		textNom.setTextSize(15);
		textDR.setTextSize(13);
		textApe.setPadding(25, 5, 2, 5);
		textNom.setPadding(3, 5, 5, 5);

		textDR.setVisibility(View.VISIBLE);

	//	view.findViewById(R.id.rowDatoRelevante2).setVisibility(View.GONE);

		long id = cursor.getLong(cursor.getColumnIndex(ConstantsAdmin.KEY_ROWID));
		textFoto.setVisibility(View.VISIBLE);
		boolean muestraFoto = mostrarFoto(textFoto, id);
		if(!muestraFoto){
			textFoto.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			textFoto.setVisibility(View.GONE);
		}

		String dato1 = cursor.getString(cursor.getColumnIndex(ConstantsAdmin.KEY_DATO_EXTRA));
		String dato2 = cursor.getString(cursor.getColumnIndex(ConstantsAdmin.KEY_DESCRIPCION));
		String texto = textDR.getText().toString();

		if(dato1 != null && !dato1.equals("") && dato2 != null && !dato2.equals("")){
			texto = texto + "(" + dato1 + "-" + dato2 + ")";
		}else if(dato1!= null && !dato1.equals("")){
			texto = texto + "(" + dato1 + ")";
		}else if(dato2!= null && !dato2.equals("")){
			texto = texto + "(" + dato2 + ")";
		}
		textDR.setText(texto);


		//      ImageView photo = v.findViewById(R.id.photo);




	}

}
