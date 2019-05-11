package com.boxico.android.kn.kncontacts.util;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.AttributeSet;
import android.widget.Spinner;

import com.boxico.android.kn.kncontacts.R;

public class MultiSpinner extends Spinner implements
		OnMultiChoiceClickListener, OnCancelListener {
	 
	private List<String> items;
	private boolean[] selected;
	private MultiSpinnerListener listener;

		public MultiSpinner(Context context) {
	        super(context);
	    }

	    public MultiSpinner(Context arg0, AttributeSet arg1) {
	        super(arg0, arg1);
	    }

	    public MultiSpinner(Context arg0, AttributeSet arg1, int arg2) {
	        super(arg0, arg1, arg2);
	    }

	    @Override
	    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
			selected[which] = isChecked;
	    }

	    @Override
	    public void onCancel(DialogInterface dialog) {

	      /*  ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
					android.R.layout.simple_spinner_item,
					new String[]{""});
	        setAdapter(adapter);*/

	        listener.onItemsSelected(selected);
	    }

	    @Override
	    public boolean performClick() {
		    super.performClick();
	        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
	        builder.setMultiChoiceItems(
	                items.toArray(new CharSequence[0]), selected, this);
	        builder.setPositiveButton(R.string.label_ok,
	                new DialogInterface.OnClickListener() {

	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                        dialog.cancel();
	                    }
	                });
	        builder.setOnCancelListener(this);
	        builder.show();

	        return false;
	    }

	    public void setItems(List<String> items,
	            MultiSpinnerListener listener) {
	        this.items = items;
			this.listener = listener;
	        selected = new boolean[items.size()];
/*
	        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, new String[]{""});
	        setAdapter(adapter);*/
	    }

	    public interface MultiSpinnerListener {
	        void onItemsSelected(boolean[] selected);
	    }


}
