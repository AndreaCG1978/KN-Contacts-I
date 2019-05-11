package com.boxico.android.kn.kncontacts.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.boxico.android.kn.kncontacts.DetallePersonaActivity;
import com.boxico.android.kn.kncontacts.R;

import java.util.ArrayList;
import java.util.HashMap;

public class KNSimpleCustomDetailsAdapter extends SimpleAdapter {

	private DetallePersonaActivity localContext = null;

	public KNSimpleCustomDetailsAdapter(Context context, ArrayList<HashMap<String,Object>> data, int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		localContext = (DetallePersonaActivity) context;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View view = super.getView(position, convertView, parent);
		TextView txt = view.findViewById(R.id.rowValor);
		Drawable icon = null;
		if(localContext.ismMostrarTelefonos()){
			icon = localContext.getResources().getDrawable(R.drawable.go_message_icon);
		}else if(localContext.ismMostrarEmails()){
			icon = localContext.getResources().getDrawable(R.drawable.go_mail_icon);
		}else {
			icon = localContext.getResources().getDrawable(R.drawable.go_address_icon);
		}
		txt.setCompoundDrawablesWithIntrinsicBounds(null, null, icon,null);
		return view;
	}
}
