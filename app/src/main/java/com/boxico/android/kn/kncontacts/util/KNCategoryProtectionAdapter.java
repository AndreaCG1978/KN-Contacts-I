package com.boxico.android.kn.kncontacts.util;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.boxico.android.kn.kncontacts.ProteccionCategoriaActivity;
import com.boxico.android.kn.kncontacts.R;
import com.boxico.android.kn.kncontacts.persistencia.dtos.CategoriaDTO;

import java.util.List;

public class KNCategoryProtectionAdapter extends ArrayAdapter<CategoriaDTO> {

	private ProteccionCategoriaActivity activity = null;
	//private boolean paraProteccion;


	public KNCategoryProtectionAdapter(Context context, int resourceId, int textViewResourceId,
                                       List<CategoriaDTO> objects, boolean paraProtec) {
		// TODO Auto-generated constructor stub
		super(context, resourceId, textViewResourceId, objects);
        activity = (ProteccionCategoriaActivity) context;
	}

	@NonNull
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = super.getView(position, convertView, parent);
		ListView lv = (ListView)parent;
		final CategoriaDTO cat = (CategoriaDTO) lv.getAdapter().getItem(position);
		LinearLayout ll;
		ll = (LinearLayout)v;
		ImageButton btnR = ll.findViewById(R.id.removeButton);
		ImageButton btnE = ll.findViewById(R.id.editButton);
		btnR.setVisibility(View.GONE);
		btnE.setVisibility(View.GONE);
		final TextView txt = ll.findViewById(R.id.text1);


		final CheckBox cb = ll.findViewById(R.id.checkActivada);
		//	final ListadoCategoriaActivity act = (ListadoCategoriaActivity) activity;

		cb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//activity.activarODesactivarCategoria(pos);
				activity.activarODesactivarCategoria(cat);
				if(cb.isChecked()){
					txt.setTextColor(Color.WHITE);
				}else{
					txt.setTextColor(Color.LTGRAY);
				}
			}
		});

		if(ConstantsAdmin.estaProtegidaCategoria(cat.getNombreReal())){
			cb.setChecked(true);
			txt.setTextColor(Color.WHITE);
		}else{
			cb.setChecked(false);
			txt.setTextColor(Color.LTGRAY);
		}

		return ll;
	}


	
	

}
