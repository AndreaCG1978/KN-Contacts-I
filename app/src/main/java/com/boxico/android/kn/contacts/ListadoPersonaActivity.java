package com.boxico.android.kn.contacts;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.boxico.android.kn.contacts.persistencia.DataBaseManager;
import com.boxico.android.kn.contacts.persistencia.dtos.CategoriaDTO;
import com.boxico.android.kn.contacts.persistencia.dtos.ConfigDTO;
import com.boxico.android.kn.contacts.persistencia.dtos.PersonaDTO;
import com.boxico.android.kn.contacts.util.ConstantsAdmin;
import com.boxico.android.kn.contacts.util.ExpandableListFragment;
import com.boxico.android.kn.contacts.util.KNSimpleCursorAdapter;
import com.boxico.android.kn.contacts.util.MultiSpinner;
import com.boxico.android.kn.contacts.util.MultiSpinner.MultiSpinnerListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.boxico.android.kn.contacts.util.ConstantsAdmin.obtenerPathImagen;
import static com.boxico.android.kn.contacts.util.ConstantsAdmin.personaSeleccionada;


public class ListadoPersonaActivity extends ExpandableListFragment implements MultiSpinnerListener, LoaderManager.LoaderCallbacks<Cursor> {
	private String mEntryBusquedaNombre = null;
	private Map<String, List<PersonaDTO>> personasMap = null;
	private long mPersonaSelect = -1;
	private LayoutInflater layoutInflater = null;
	//   private ArrayList<Cursor> allMyCursors = null;
	private EditText entryBusqueda = null;
	private MultiSpinner spinnerCategorias = null;
	private ImageButton switchOrganizacion = null;
	private ImageButton expandContractAll = null;
	private ImageButton protegerCategorias = null;
	//	private ImageButton masOMenosDesc = null;
	private static ListadoPersonaActivity me = null;
	private String separadorExcel = null;
	private int mGroupSelected = -1;
	private int mChildSelected = -1;


	private ImageButton preferidos = null;
	private static final String CLAVE = "CLAVE";
	private static final String APELLIDO = "APELLIDO";
	private static final String NOMBRE = "NOMBRE";

	private TextView cantReg = null;

	private List<String> categoriasSeleccionadas = new ArrayList<>();
	private List<CategoriaDTO> todasLasCategorias = new ArrayList<>();
	private List<String> todasLasCategString = new ArrayList<>();

//	private TextView catSelectTextView = null;

	private List<String> mySortedByElements = null;


	private ListView listaEspecial;
	private MenuItem menuItemExportarContactosEstetico;
	private MenuItem menuItemExportarContactos;

	//	private ImageView imgPrefLeft = null;
	private ImageView imgPrefRight = null;

	private static final String LIST_STATE_KEY = "listState";
	private static final String LIST_POSITION_KEY = "listPosition";
	private static final String ITEM_POSITION_KEY = "itemPosition";

	private Parcelable mListState = null;
	private int mListPosition = 0;
	private int mItemPosition = 0;
	private final int CATEGORIAS_CURSOR = 1;
	private final int CATEGORIAS_PERSONALES_CURSOR = 2;
	private final int CONTRASENIA_CURSOR = 3;
	private final int CATEGORIAS_PROTEGIDAS_CURSOR = 4;
	private final int PERSONAS_CURSOR = 5;
	private final int PREFERIDOS_CURSOR = 6;
	private final int CATEGORIAS_ACTIVAS_CURSOR = 7;
	private final int CATEGORIAS_PERSONALES_ACTIVAS_CURSOR = 8;
	private final int CONFIGURACION_CURSOR = 9;

	private final int PERMISSIONS_RESTORE_BACKUP = 101;
	private final int PERMISSIONS_READ_CONTACTS = 102;


	private class ImportCSVTask extends AsyncTask<Long, Integer, Integer> {
		ProgressDialog dialog = null;

		@Override
		protected Integer doInBackground(Long... params) {

			try {
				publishProgress(1);
				DataBaseManager mDBManager = DataBaseManager.getInstance(me);
				Long val = params[1];
				boolean recuperarLite = val == 1L;
				ConstantsAdmin.importarCSV(me, mDBManager, recuperarLite);
				//	ConstantsAdmin.contrasenia.setActiva(false);

				//   ConstantsAdmin.procesarStringDatos(body, me);


			} catch (Exception e) {
				ConstantsAdmin.mensaje = me.getString(R.string.error_importar_csv);
			}
			return 0;

		}

		protected void onProgressUpdate(Integer... progress) {
			dialog = ProgressDialog.show(me, "",
					me.getString(R.string.mensaje_importando_contactos), false);
		}

		@Override
		protected void onPostExecute(Integer result) {
			try {
				dialog.cancel();
			} catch (Exception e) {
				// TODO: handle exception
			}
			ConstantsAdmin.mostrarMensajeDialog(me, ConstantsAdmin.mensaje);
			ConstantsAdmin.mensaje = null;
			if (ConstantsAdmin.contrasenia.isActiva()) {
				protegerCategorias.setBackgroundResource(R.drawable.candado_abierto);
			} else {
				protegerCategorias.setBackgroundResource(R.drawable.candado_cerrado);
			}

			configurarSpinner();
			ConstantsAdmin.resetPersonasOrganizadas();
			mostrarTodosLosContactos();

		}
	}

	private void configurarBotonExpandContractAll() {
		expandContractAll = this.findViewById(R.id.buttonExpandContractAll);
		//	Drawable dexpandir = getResources().getDrawable(R.drawable.expandir_icon);
		//	Drawable dcontraer = getResources().getDrawable(R.drawable.contraer_icon);
		expandContractAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				expandContractAll();
			}
		});

	}


	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions,
										   int[] grantResults) {
		if (requestCode == PERMISSIONS_RESTORE_BACKUP) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				this.importContactosCSV();
			}
		}
		if (requestCode == PERMISSIONS_READ_CONTACTS) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				this.openVerDetallePersona(personaSeleccionada.getId());
			}
		}
	}


	private void askForReadContactsPermission(long idPer) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
					!= PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.READ_CONTACTS},
						PERMISSIONS_READ_CONTACTS);


			} else {//Ya tiene el permiso...
				this.openVerDetallePersona(idPer);
			}
		} else {
			this.openVerDetallePersona(idPer);
		}


	}


	private void askForReadStoragePermission() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
					!= PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
						PERMISSIONS_RESTORE_BACKUP);


			} else {//Ya tiene el permiso...
				this.importContactosCSV();
			}
		} else {
			this.importContactosCSV();
		}


	}


	@Override
	public void onSaveInstanceState(Bundle state) {

		super.onSaveInstanceState(state);

		// Save list state
		mListState = getExpandableListView().onSaveInstanceState();
		state.putParcelable(LIST_STATE_KEY, mListState);

		// Save position of first visible item
		mListPosition = getExpandableListView().getFirstVisiblePosition();
		state.putInt(LIST_POSITION_KEY, mListPosition);

		// Save scroll position of item
		View itemView = getExpandableListView().getChildAt(0);
		mItemPosition = itemView == null ? 0 : itemView.getTop();
		state.putInt(ITEM_POSITION_KEY, mItemPosition);
	}


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		me = this;
		try {
			//	allMyCursors = new ArrayList<>();
			ConstantsAdmin.mainActivity = me;
			this.setContentView(R.layout.list_personas);
			this.setTitle(R.string.app_name);
			this.registerForContextMenu(getExpandableListView());
			this.cargarLoaders();
			this.configurarSpinner();
			this.configurarEntryBusqueda();
			this.configurarListView();
			this.configurarExpandableList();
			this.configurarBotonIrACategoriaTodas();
			this.configurarBotonProtegerCategorias();
			this.configurarVerPreferidos();
			this.configurarAddContact();
			this.configurarBotonSwitchOrganizacion();
			this.configurarBotonExpandContractAll();
			//	this.configurarBotonMasOMenosDesc();
			this.recuperarConfiguracion();
			verBusqueda();
			layoutInflater = this.getLayoutInflater();
//			this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

			this.redirigirImportarContactos();
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

		} catch (Exception e) {
			ConstantsAdmin.mostrarMensaje(this, getString(R.string.errorInicioAplicacion));
		}
	}

	private void redirigirImportarContactos() {
		if ((personasMap == null || personasMap.size() == 0) && (!ConstantsAdmin.config.isMuestraPreferidos())) {
			boolean existsBU = false;
			int size;
			if (ConstantsAdmin.existsBackupFile()) {
				existsBU = true;
				size = 3;
			} else {
				size = 2;
			}
			final CharSequence[] charSequence = new CharSequence[size];

			charSequence[0] = this.getString(R.string.menu_agregar_persona);
			charSequence[1] = this.getString(R.string.menu_importar_contactos);
			if (existsBU) {
				charSequence[2] = this.getString(R.string.menu_importar_contactos_csv);
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(this.getString(R.string.mensaje_sin_contactos))
					//.setMessage("You can buy our products without registration too. Enjoy the shopping")
					.setSingleChoiceItems(charSequence, -1, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
								case 0:
									openAltaPersona();
									break; // optional
								case 1:
									mostrarDialogoImportarContactos();
									//ConstantsAdmin.cursorCategoriasPersonales = cl;
									break; // optional
								case 2:
									askForReadStoragePermission();
									//ConstantsAdmin.cursorCategoriasPersonales = cl;
									break; // optional

								default: // Optional
									// Statements
							}
							dialog.cancel();

						}
					});
			builder.create().show();
		}

		/*if((personasMap == null || personasMap.size() == 0)&&(!ConstantsAdmin.config.isMuestraPreferidos())){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Aun no se han cargado contactos, desea importarlos de la Agenda?")
					.setCancelable(false)
					.setPositiveButton(R.string.label_si, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							mostrarDialogoImportarContactos();
						}
					})
					.setNegativeButton(R.string.label_no, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			builder.show();
		}*/

	}

	private void cargarLoaders() {
		this.getSupportLoaderManager().initLoader(CATEGORIAS_CURSOR, null, this);
		this.getSupportLoaderManager().initLoader(CATEGORIAS_PERSONALES_CURSOR, null, this);
		this.getSupportLoaderManager().initLoader(CONTRASENIA_CURSOR, null, this);
		this.getSupportLoaderManager().initLoader(CATEGORIAS_PROTEGIDAS_CURSOR, null, this);
		this.getSupportLoaderManager().initLoader(PERSONAS_CURSOR, null, this);
		this.getSupportLoaderManager().initLoader(PREFERIDOS_CURSOR, null, this);
		this.getSupportLoaderManager().initLoader(CATEGORIAS_ACTIVAS_CURSOR, null, this);
		this.getSupportLoaderManager().initLoader(CATEGORIAS_PERSONALES_ACTIVAS_CURSOR, null, this);
		this.getSupportLoaderManager().initLoader(CONFIGURACION_CURSOR, null, this);

	}

	private void recuperarConfiguracion() {
		ConfigDTO config;
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		config = ConstantsAdmin.obtenerConfiguracion(me, mDBManager);
		ConstantsAdmin.config = config;

		if (ConstantsAdmin.config.isOrdenadoPorCategoria()) {
			switchOrganizacion.setBackground(getResources().getDrawable(R.drawable.organizar_nombre));
		} else {
			switchOrganizacion.setBackground(getResources().getDrawable(R.drawable.organizar_categoria));
		}
		// TODO Auto-generated method stub

	}


	private void configurarExpandableList() {
		this.getExpandableListView().setDividerHeight(20);
	}

	private void recargarLista() {
		listaEspecial.setVisibility(View.GONE);
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		ConstantsAdmin.inicializarBD(mDBManager);
		ConstantsAdmin.cargarContrasenia(this, mDBManager);
		ConstantsAdmin.cargarCategoriasProtegidas(this, mDBManager);
		ConstantsAdmin.finalizarBD(mDBManager);
		this.getExpandableListView().setVisibility(View.VISIBLE);
		List<Map<String, String>> groupData = new ArrayList<>();
		List<List<Map<String, String>>> childData = new ArrayList<>();

		List<PersonaDTO> personas;
		if (ConstantsAdmin.isResetPersonasOrganizadas()) {
			mGroupSelected = -1;
			mChildSelected = -1;
		}


		if (!ConstantsAdmin.config.isOrdenadoPorCategoria()) {
			personasMap = ConstantsAdmin.obtenerOrganizadosAlfabeticamente(this, mDBManager);
		} else {
			personasMap = ConstantsAdmin.obtenerOrganizadosPorCategoria(this, mDBManager);
		}
		mySortedByElements = new ArrayList<>();
		mySortedByElements.addAll(personasMap.keySet());
		Collections.sort(mySortedByElements);
		Map<String, String> curGroupMap;
		List<Map<String, String>> children;
		Map<String, String> curChildMap;
		Iterator<PersonaDTO> itPers;
		PersonaDTO per;
		int cantidadTotal = 0;

		for (String key : mySortedByElements) {
			personas = personasMap.get(key);
			curGroupMap = new HashMap<>();
			groupData.add(curGroupMap);
			curGroupMap.put(CLAVE, key);

			children = new ArrayList<>();
			itPers = personas.iterator();
			while (itPers.hasNext()) {
				per = itPers.next();
				curChildMap = new HashMap<>();
				children.add(curChildMap);
				if (per.getApellido() != null) {
					curChildMap.put(APELLIDO, per.getApellido());
				} else {
					curChildMap.put(APELLIDO, "");
				}
				if (per.getNombres() != null) {
					curChildMap.put(NOMBRE, per.getNombres());
				} else {
					curChildMap.put(NOMBRE, "");
				}
				//curChildMap.put(NOMBRE, per.getNombres());
				cantidadTotal++;
			}

			childData.add(children);
		}
		this.habilitarDeshabilitarItemMenu();
		cantReg.setText("(" + cantidadTotal + ")");


		setListAdapter(new SimpleExpandableListAdapter(
							   this,
							   groupData,
							   0,
							   null,
							   new int[]{},
							   childData,
							   0,
							   null,
							   new int[]{}
					   ) {
						   @Override
						   public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
							   String clave = mySortedByElements.get(groupPosition);
							   final PersonaDTO per = (PersonaDTO) personasMap.get(clave).toArray()[childPosition];
							   personaSeleccionada = per;
							   final View v = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
							   TextView textApe = v.findViewById(R.id.rowApellido);
							   TextView textNom = v.findViewById(R.id.rowNombres);
							   TextView textFoto = v.findViewById(R.id.rowFoto);
							   textNom.setText(per.getNombres());
							   TextView text;
							   textApe.setPadding(25, 12, 2, 12);
							   textNom.setPadding(3, 12, 10, 12);
							   textApe.setTextSize(16);
							   textNom.setTextSize(16);
							   text = v.findViewById(R.id.rowDatoRelevante);
							   String texto;
							   if (!ConstantsAdmin.config.isOrdenadoPorCategoria()) {
								   //text.setText(per.getCategoriaNombreRelativo());
								   texto = per.getCategoriaNombreRelativo();
							   } else {
								   //text.setText(per.getDatoExtra());
								   texto = per.getDatoExtra();
							   }

							   textApe.setPadding(25, 5, 2, 5);
							   textNom.setPadding(3, 5, 5, 5);
							   textApe.setTextSize(14);
							   textNom.setTextSize(14);
							   if (texto != null && !texto.equals("")) {
								   if (per.getDescripcion() != null && !per.getDescripcion().equals("")) {
									   texto = texto + " (" + per.getDescripcion() + ")";

								   }
							   } else {
								   texto = per.getCategoriaNombreRelativo();
							   }
							   text.setText(texto);
							   text.setTextSize(13);
							   text.setVisibility(View.VISIBLE);

							   //	text = v.findViewById(R.id.rowDatoRelevante2);
							   //	text.setVisibility(View.GONE);

							   textFoto.setVisibility(View.VISIBLE);
							   boolean muestraFoto = mostrarFoto(textFoto, per.getId());
							   if (!muestraFoto) {
								   textFoto.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
								   //textFoto.setText("░");
								   textFoto.setVisibility(View.GONE);
								   //textFoto.setVisibility(View.GONE);
							   } else {
								   textFoto.setText("");
							   }

							   //      ImageView photo = v.findViewById(R.id.photo);
							   if (per.getApellido() != null) {
								   textApe.setText(per.getApellido().toUpperCase());
							   } else {
								   textApe.setText("");
							   }

							   if (per.getNombres() != null) {
								   textNom.setText(per.getNombres().toUpperCase());
							   } else {
								   textNom.setText("");
							   }
							   final int gp = groupPosition;
							   final int cp = childPosition;

							   v.setOnClickListener(new View.OnClickListener() {
								   public void onClick(View v) {
									   //openVerDetallePersona(per.getId());
									   askForReadContactsPermission(per.getId());
								   }
							   });
							   v.setOnLongClickListener(new View.OnLongClickListener() {
								   public boolean onLongClick(View v) {
									   openVerMenuPersona(per.getId(), gp, cp);
									   return false;
								   }
							   });
							   return v;
						   }

						   @Override
						   public View newChildView(boolean isLastChild, ViewGroup parent) {
							   if (layoutInflater == null) {
								   layoutInflater = getLayoutInflater();
							   }
							   return layoutInflater.inflate(R.layout.row_personas, parent, false);
						   }

						   public View newGroupView(boolean isExpanded, ViewGroup parent) {
							   if (layoutInflater == null) {
								   layoutInflater = getLayoutInflater();
							   }

							   return layoutInflater.inflate(R.layout.personas_row_label, parent, false);
						   }

						   @Override
						   public void onGroupExpanded(int groupPosition) {
							   super.onGroupExpanded(groupPosition);
							   mGroupSelected = groupPosition;
						   }

						   public void onGroupCollapsed(int groupPosition) {
							   super.onGroupCollapsed(groupPosition);
							   mGroupSelected = -1;
						   }

						   public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
							   final View v = super.getGroupView(groupPosition, isExpanded, convertView, parent);
							   TextView textName = v.findViewById(R.id.textName);
							   TextView textCantidad = v.findViewById(R.id.textCantidad);


							   String temp;
							   String label;
							   temp = mySortedByElements.get(groupPosition);
							   label = temp.toUpperCase();
							   textName.setText(label);
							   if (((ExpandableListView) parent).isGroupExpanded(groupPosition)) {
								   textName.setTextColor(Color.CYAN);
							   } else {
								   textName.setTextColor(Color.LTGRAY);

							   }
							   //	textName.setTextColor(getResources().getColor(R.color.color_negro));
							   textName.setTypeface(Typeface.SANS_SERIF);
							   textCantidad.setTextColor(getResources().getColor(R.color.color_gris_claro));
							   textCantidad.setTypeface(Typeface.SANS_SERIF);
							   textCantidad.setText(String.valueOf(personasMap.get(temp).size()));
							   return v;
						   }
					   }
		);


		if (mGroupSelected != -1 && mGroupSelected < this.getExpandableListAdapter().getGroupCount()) {
			this.getExpandableListView().expandGroup(mGroupSelected);
			this.getExpandableListView().setSelectedGroup(mGroupSelected);
			if (mChildSelected != -1) {
				this.getExpandableListView().setSelectedChild(mGroupSelected, mChildSelected, true);
			}

		}


	}

	private void openVerDetallePersona(long id) {
		Intent i = new Intent(this, DetallePersonaActivity.class);
		i.putExtra(ConstantsAdmin.PERSONA_SELECCIONADA, String.valueOf(id));
		this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_DETALLE_PERSONA);

	}


	private boolean mostrarFoto(final TextView tv, long idPer) {
		boolean muestraFoto = false;
		try {
		/*	Asociacion puedeCargar = ConstantsAdmin.comprobarSDCard(me);
			boolean puede = (Boolean) puedeCargar.getKey();

			if(puede){*/
			String path = obtenerPathImagen() + "." + String.valueOf(idPer) + ".jpg";
			if (ConstantsAdmin.existeArchivo(path)) {
				Bitmap b = BitmapFactory.decodeFile(path);
				Bitmap small = null;
				if (b != null) {
					small = Bitmap.createScaledBitmap(b, 59, 67, true);
					final Drawable icon = new BitmapDrawable(getResources(), small);
					//final Drawable icon = Drawable.createFromPath(ConstantsAdmin.obtenerPathImagen() + String.valueOf(idPer)  + ".jpg");
					tv.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
					//tv.setCompoundDrawablePadding(3);
					//	Bitmap big = Bitmap.createScaledBitmap(b, 300, 350, true);
					final Drawable iconBig = new BitmapDrawable(getResources(), b);
					muestraFoto = true;
					tv.setOnTouchListener(new View.OnTouchListener() {
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							if (event.getAction() == MotionEvent.ACTION_UP) {
								ConstantsAdmin.showFotoPopUp(iconBig, me);
								return true;
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


	private void openVerMenuPersona(long id, int groupPosition, int childPosition) {
		//Intent i = new Intent(this, MenuPersonaActivity.class);
		mGroupSelected = groupPosition;
		mChildSelected = childPosition;

		openVerMenuPersonaPrivado(id, this);
		/*i.putExtra(ConstantsAdmin.PERSONA_SELECCIONADA, String.valueOf(id));
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		boolean esPreferido = ConstantsAdmin.esPreferido(id, mDBManager, this);
		i.putExtra(ConstantsAdmin.KEY_MUESTRA_PREFERIDOS, esPreferido);
		this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_MENU_PERSONA);*/

	}


	private void openVerMenuPersonaPrivado(long id, Activity ctx) {
		Intent i = new Intent(ctx, MenuPersonaActivity.class);
		i.putExtra(ConstantsAdmin.PERSONA_SELECCIONADA, String.valueOf(id));
		DataBaseManager mDBManager = DataBaseManager.getInstance(ctx);
		boolean esPreferido = ConstantsAdmin.esPreferido(id, mDBManager, ctx);
		i.putExtra(ConstantsAdmin.KEY_MUESTRA_PREFERIDOS, esPreferido);
		this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_MENU_PERSONA);

	}


	private void configurarVerPreferidos() {
		preferidos = this.findViewById(R.id.imagenPreferidos);
		//	Drawable dbnPref = getResources().getDrawable(R.drawable.pref_icon_bw);
		preferidos.setBackgroundResource(R.drawable.pref_icon_bw);
		//imgPrefLeft = this.findViewById(R.id.imgPrefLeft);
		imgPrefRight = this.findViewById(R.id.imgPrefRight);
		preferidos.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				verPreferidos();
			}
		});

	}
    /*

    private void configurarSearch(){
    	searchBoton = (ImageView) this.findViewById(R.id.imagenSearch);
    	dcolorSearch = getResources().getDrawable(R.drawable.search_color_icon);
		dbnSearch = getResources().getDrawable(R.drawable.search_icon);
		searchBoton.setBackgroundDrawable(dbnSearch);
    	searchBoton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				verBusqueda();
			}
		});

    }*/
/*
    private void configurarImport(){
    	ImageView importBoton = (ImageView) this.findViewById(R.id.imagenImport);
    	importBoton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mostrarDialogoImportarContactos();
			}
		});

    }
  */
   /* private void configurarCategorias(){
    	ImageView categorias = (ImageView) this.findViewById(R.id.imagenCategorias);
    	categorias.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mostrarEleccionCategoria();

			}
		});

    }    */

	private void configurarAddContact() {
		ImageButton addContact = this.findViewById(R.id.imagenAddContacto);
		addContact.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openAltaPersona();
			}
		});

	}

	private void verBusqueda() {
		mEntryBusquedaNombre = null;
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		if (ConstantsAdmin.contrasenia == null) {
			ConstantsAdmin.cargarContrasenia(this, mDBManager);
		}
		if (ConstantsAdmin.contrasenia.getId() == -1) {
			protegerCategorias.setBackgroundResource(R.drawable.candado_abierto);
		} else if (ConstantsAdmin.contrasenia.isActiva()) {
			protegerCategorias.setBackgroundResource(R.drawable.candado_abierto);
		} else {
			protegerCategorias.setBackgroundResource(R.drawable.candado_cerrado);
		}

		if (!ConstantsAdmin.config.isMuestraPreferidos()) {
			this.mostrarTodosLosContactos();
		}

	}

	private void verPreferidos() {
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		long prefSize = ConstantsAdmin.tablaPreferidosSize(mDBManager);
		if (prefSize == 0) {
			ConstantsAdmin.mostrarMensaje(this, getString(R.string.error_sin_preferidos));
			this.mostrarTodosLosContactos();
		} else {
			if (!ConstantsAdmin.config.isMuestraPreferidos()) {
				this.mostrarSoloPreferidos();
				//	catSelectTextView.setVisibility(View.VISIBLE);
				//		catSelectTextView.setText(getResources().getString(R.string.label_preferidos));
				preferidos.setBackgroundResource(R.drawable.pref_icon);
				spinnerCategorias.setItems(todasLasCategString, this);
				//	imgPrefLeft.setVisibility(View.VISIBLE);
				imgPrefRight.setVisibility(View.VISIBLE);
			} else {
				ConstantsAdmin.config.setMuestraPreferidos(false);
				//	catSelectTextView.setVisibility(View.GONE);
				//	catSelectTextView.setText(getResources().getString(R.string.title_acerca_de));
				preferidos.setBackground(getResources().getDrawable(R.drawable.pref_icon_bw));
				//	imgPrefLeft.setVisibility(View.GONE);
				imgPrefRight.setVisibility(View.GONE);

				this.mostrarTodosLosContactos();

			}
			preferidos.refreshDrawableState();
		}

	}


	private void mostrarSoloPreferidos() {
		CursorLoader prefCursorLoader;
		Cursor prefCursor;
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		ConstantsAdmin.inicializarBD(mDBManager);
		prefCursorLoader = mDBManager.cursorLoaderPreferidos(!ConstantsAdmin.contrasenia.isActiva(), ConstantsAdmin.categoriasProtegidas, this);
		//prefCursorLoader = mDBManager.fetchAllPreferidos(ConstantsAdmin.categoriasProtegidas);
		prefCursor = prefCursorLoader.loadInBackground();
		if (prefCursor != null) {
			ConstantsAdmin.config.setMuestraPreferidos(true);
			//  startManagingCursor(prefCursor);
			// Create an array to specify the fields we want to display in the list (only TITLE)
			String[] from;
			int[] to;
//			from = new String[]{ConstantsAdmin.KEY_APELLIDO, ConstantsAdmin.KEY_NOMBRES, ConstantsAdmin.KEY_NOMBRE_CATEGORIA_RELATIVO, ConstantsAdmin.KEY_DATO_EXTRA};
//			to = new int[]{R.id.rowApellido, R.id.rowNombres, R.id.rowDatoRelevante, R.id.rowDatoRelevante2};


			from = new String[]{ConstantsAdmin.KEY_APELLIDO, ConstantsAdmin.KEY_NOMBRES, ConstantsAdmin.KEY_NOMBRE_CATEGORIA_RELATIVO, ConstantsAdmin.KEY_DATO_EXTRA};
			to = new int[]{R.id.rowApellido, R.id.rowNombres, R.id.rowDatoRelevante};

			KNSimpleCursorAdapter personas =
					new KNSimpleCursorAdapter(this, R.layout.row_personas, prefCursor, from, to);
			//PONER UNA LISTA OCULTA PARA LAS BUSQUEDAS ESPECIFICAS

			listaEspecial.setAdapter(personas);
			ConstantsAdmin.finalizarBD(mDBManager);
			listaEspecial.setVisibility(View.VISIBLE);
			this.getExpandableListView().setVisibility(View.GONE);
			cantReg.setText("(" + listaEspecial.getAdapter().getCount() + ")");
		}

	}

	private void configurarBotonIrACategoriaTodas() {
		// --Commented out by Inspection (12/11/2018 12:51):ExpandableListAdapter mAdapter = null;
		ImageButton irTodos = this.findViewById(R.id.buttonTodasLasCategorias);
		irTodos.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ConstantsAdmin.config.setListaExpandida(false);
				ConstantsAdmin.config.setMuestraPreferidos(false);
				expandContractAll.setBackground(getResources().getDrawable(R.drawable.expandir_icon));
				mostrarTodosLosContactos();
			}
		});

	}


	private void configurarBotonSwitchOrganizacion() {
		switchOrganizacion = this.findViewById(R.id.buttonSwitchOrganizacion);
		//	Drawable dorganizarNombre = getResources().getDrawable(R.drawable.organizar_nombre);
		//	Drawable dorganizarCategoria = getResources().getDrawable(R.drawable.organizar_categoria);
		switchOrganizacion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switchOrganizacion();
			}
		});

	}


/*
	private void configurarBotonMasOMenosDesc(){
		masOMenosDesc = this.findViewById(R.id.buttonMoreOrLess);
		//	Drawable dlessDesc = getResources().getDrawable(R.drawable.less_desc_contact);
		//	Drawable dmoreDesc = getResources().getDrawable(R.drawable.more_desc_contact);
		masOMenosDesc.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				moreOrLessDesc();
			}
		});

	}
*/

	private void expandContractAll() {

		ConstantsAdmin.config.setListaExpandida(!ConstantsAdmin.config.isListaExpandida());
		int grupoSeleccionado = mGroupSelected;
		this.mostrarTodosLosContactos();
		for (int j = 0; j < this.getExpandableListAdapter().getGroupCount(); j++) {
			if (ConstantsAdmin.config.isListaExpandida()) {
				this.getExpandableListView().expandGroup(j);
			} else {
				this.getExpandableListView().collapseGroup(j);
			}

		}
		if (!ConstantsAdmin.config.isListaExpandida()) {
			expandContractAll.setBackground(getResources().getDrawable(R.drawable.expandir_icon));
		} else {
			expandContractAll.setBackground(getResources().getDrawable(R.drawable.contraer_icon));
		}
		if (grupoSeleccionado != -1) {
			this.getExpandableListView().setSelectedGroup(grupoSeleccionado);
		} else {
			mGroupSelected = -1;
		}

	}

	/*

	private void moreOrLessDesc(){
		int grupoSeleccionado = mGroupSelected;
		mListPosition = getExpandableListView().getFirstVisiblePosition();

		// Save scroll position of item
		View itemView = getExpandableListView().getChildAt(0);
		mItemPosition = itemView == null ? 0 : itemView.getTop();

		//    ConstantsAdmin.config.setEstanDetallados(!ConstantsAdmin.config.isEstanDetallados());
		this.mostrarTodosLosContactos();
		if(ConstantsAdmin.config.isListaExpandida()){
			for (int j = 0; j < this.getExpandableListAdapter().getGroupCount(); j++) {
				this.getExpandableListView().expandGroup(j);
			}
		}
		if(grupoSeleccionado != -1){
			this.getExpandableListView().setSelectedGroup(grupoSeleccionado);
			this.getExpandableListView().expandGroup(grupoSeleccionado);
			mGroupSelected = grupoSeleccionado;
		}else{
			mGroupSelected = -1;
		}

		if (mListState != null){
			getExpandableListView().onRestoreInstanceState(mListState);
		}
		getExpandableListView().setSelectionFromTop(mListPosition, mItemPosition);
	}
*/

	private void switchOrganizacion() {
		ConstantsAdmin.config.setOrdenadoPorCategoria(!ConstantsAdmin.config.isOrdenadoPorCategoria());
		this.mostrarTodosLosContactos();
		ConstantsAdmin.config.setListaExpandida(false);
		for (int j = 0; j < this.getExpandableListAdapter().getGroupCount(); j++) {
			this.getExpandableListView().collapseGroup(j);
		}
		expandContractAll.setBackground(getResources().getDrawable(R.drawable.expandir_icon));
		this.getExpandableListView().setSelectedGroup(0);
		if (ConstantsAdmin.config.isOrdenadoPorCategoria()) {
			switchOrganizacion.setBackground(getResources().getDrawable(R.drawable.organizar_nombre));
		} else {
			switchOrganizacion.setBackground(getResources().getDrawable(R.drawable.organizar_categoria));
		}


	}

	private void mostrarTodosLosContactos() {
		mEntryBusquedaNombre = null;
		categoriasSeleccionadas = null;
		preferidos.setBackground(getResources().getDrawable(R.drawable.pref_icon_bw));
		imgPrefRight.setVisibility(View.GONE);
		spinnerCategorias.setItems(todasLasCategString, this);
		mGroupSelected = -1;
		entryBusqueda.setText("");
	//	this.recargarLista();
	}

	private void configurarListView() {
		listaEspecial = this.findViewById(R.id.listaEspecial);
		listaEspecial.setFastScrollEnabled(true);
		listaEspecial.setVisibility(View.VISIBLE);
		listaEspecial.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				// TODO Auto-generated method stub

				Cursor per;
				DataBaseManager mDBManager = DataBaseManager.getInstance(me);
				//	Intent i = new Intent(me, DetallePersonaActivity.class);
				per = (Cursor) listaEspecial.getAdapter().getItem(arg2);
				long id = per.getLong(0);
				personaSeleccionada = ConstantsAdmin.obtenerPersonaId(id, mDBManager);

				askForReadContactsPermission(id);
				/*
				i.putExtra(ConstantsAdmin.PERSONA_SELECCIONADA, String.valueOf(id));
				me.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_DETALLE_PERSONA);*/

			}
		});

		listaEspecial.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
										   int arg2, long arg3) {
				Cursor per;
				//Intent i = new Intent(me, MenuPersonaActivity.class);
				per = (Cursor) listaEspecial.getAdapter().getItem(arg2);
				long id = per.getLong(0);
				//DataBaseManager mDBManager = DataBaseManager.getInstance(me);
				//boolean esPreferido = ConstantsAdmin.esPreferido(id, mDBManager, me);
				//i.putExtra(ConstantsAdmin.KEY_MUESTRA_PREFERIDOS, esPreferido);
				//i.putExtra(ConstantsAdmin.PERSONA_SELECCIONADA, String.valueOf(id));
				//me.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_MENU_PERSONA);
				openVerMenuPersonaPrivado(id, me);
				return true;
			}
		});


		cantReg = this.findViewById(R.id.cantRegistros);
		//	catSelectTextView = this.findViewById(R.id.categoriasSeleccionadasId);
	}

/*	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_ELIMINAR_CONTACTO, 0, R.string.menu_eliminar);

		// TODO: fill in rest of method
	}
*/






	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case ConstantsAdmin.ACTIVITY_EJECUTAR_ELIMINAR_CONTACTO:
				AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
				mPersonaSelect = info.id;
			//	eliminarPersonaDialog();
				return true;
		}
		return super.onContextItemSelected(item);

		// TODO: fill in rest of method
	}

	/*
	private void eliminarPersonaDialog(){
		try {
			DataBaseManager mDBManager = DataBaseManager.getInstance(this);
			PersonaDTO per = ConstantsAdmin.obtenerPersonaId(mPersonaSelect, mDBManager);
			String contacto = per.getApellido();
			if(contacto == null){
				contacto = "";
			}else{
				contacto = contacto + ", " ;
			}
			if(per.getNombres() != null && !per.getNombres().equals("")){
				contacto = contacto + per.getNombres();
			}
			contacto = contacto + ": ";
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(contacto + getString(R.string.mensaje_borrar_persona))
					.setCancelable(false)
					.setPositiveButton(R.string.label_si, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							eliminarPersonaSeleccionada();
							recargarLista();
						}
					})
					.setNegativeButton(R.string.label_no, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			builder.show();

		} catch (Exception e) {
			ConstantsAdmin.mostrarMensaje(this, getString(R.string.errorEliminacionContacto));
		}
	}
*/

	private void eliminarPersonaSeleccionada(){
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		ConstantsAdmin.inicializarBD(mDBManager);
		mDBManager.eliminarPersona(mPersonaSelect);
		ConstantsAdmin.finalizarBD(mDBManager);

	}

	/*
    private void importarContactos(){
    	try {
        	PersonaDTO per;
        	List<PersonaDTO> contactosImportados = this.importarContactosPrivado();
        	Iterator<PersonaDTO> it = contactosImportados.iterator();
        	ConstantsAdmin.mensaje = getString(R.string.mensaje_exito_importar_csv);
			DataBaseManager mDBManager = DataBaseManager.getInstance(this);
        	if(it.hasNext()){
	        	ConstantsAdmin.inicializarBD(mDBManager);
	    		while(it.hasNext()){
	    			per = it.next();
	    			if(per.getNombres() != null && per.getApellido() == null){
	    				per.setApellido(per.getNombres());
	    				per.setNombres(null);
	    			}
	    			mDBManager.createPersona(per, false);
	    		}
	    		ConstantsAdmin.finalizarBD(mDBManager);
        	}else{
        		ConstantsAdmin.mensaje = getString(R.string.errorSinContactosPorImportar);
        	}

		} catch (Exception e) {
			// TODO: handle exception
			ConstantsAdmin.mensaje = getString(R.string.errorImportarContacto);
		}
    }*/

 /*
    private CategoriaDTO obtenerCategoriaSeleccionada(){
    	CategoriaDTO result = null;
    	if(this.mSpinnerPos != -1){
    		result = (CategoriaDTO) this.mSpinnerAdapt.getItem(this.mSpinnerPos);
    	}
    	return result;
    }
  */

 /*
    private List<PersonaDTO> importarContactosPrivado(){
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
    	ArrayList<PersonaDTO> result = new ArrayList<>();
    	ConstantsAdmin.mailsARegistrar = new ArrayList<>();
    	CategoriaDTO cat;
    	cat = todasLasCategorias.get(0);
    	PersonaDTO per = null;
    	ConstantsAdmin.inicializarBD(mDBManager);
    	Cursor people = this.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
    	Asociacion asoc;
    	if(people != null){

	    	while(people.moveToNext()) {
	    		try {
	    	       String contactId = people.getString(people.getColumnIndex(ContactsContract.Contacts._ID));
		   	       asoc = this.obtenerNombreYApellidoDeContactoDeAgenda(contactId);
		   	       per = this.obtenerContactoCapturado(asoc, people, cat, contactId);
				} catch (Exception e) {
					// TODO: handle exception
				} finally{
					if(per != null){
						result.add(per);
					}
					per = null;
				}


	    	}

    	}
    	ConstantsAdmin.finalizarBD(mDBManager);
      	return result;

    }
*/

// --Commented out by Inspection START (26/12/2018 09:04):
//    private PersonaDTO obtenerContactoCapturado(Asociacion asoc, Cursor people, CategoriaDTO cat, String contactId){
//    	String given;
//    	String family;
//    	PersonaDTO per = null;
//	//	DataBaseManager mDBManager = DataBaseManager.getInstance(this);
//    	String[] projectionPhone = new String[]{
//    			Phone.NUMBER,
//    			Phone.TYPE
//    	};
//
//    	String[] projectionMail = new String[]{
//    			Email.DATA,
//    			Email.TYPE
//    	};
//
//    	try{
// 		   String hasPhone = people.getString(people.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
//	       given = (String)asoc.getKey();
//   	       family = (String)asoc.getValue();
//   	       if(family != null && !family.equals("") || given != null && !given.equals("")){
//	        	   per = ConstantsAdmin.obtenerPersonaConNombreYApellido(given,family,this);
//	        	   if(per == null){
//	        		   per = new PersonaDTO();
//	        	   }
//			       if ( hasPhone.equalsIgnoreCase("1")){
//			    	   hasPhone = "true";
//			       }else{
//			           hasPhone = "false";
//			       }
//
//			       per.setApellido(family);
//			       per.setNombres(given);
//			       if(per.getId()== -1){
//			    	   per.setCategoriaId(cat.getId());
//				       per.setCategoriaNombre(cat.getNombreReal());
//				       per.setCategoriaNombreRelativo(cat.getNombreRelativo());
//			       }
//
//
//			       if (Boolean.parseBoolean(hasPhone))
//			       {
//			    	   this.importarTelDeContacto(projectionPhone, per, contactId);
//			       }
//			       this.importarMailDeContacto(projectionMail, per, contactId);
//           }
//
//		} catch (Exception e) {
//			e.getMessage();
//		}
//		return per;
//
//    }
// --Commented out by Inspection STOP (26/12/2018 09:04)

/*
	private Asociacion obtenerNombreYApellidoDeContactoDeAgenda(String contactId){
		Asociacion asoc = null;
		Cursor nameCur;
		String given;
		String family;
		String whereName = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + "=" + contactId;
		String[] whereNameParams = new String[] { ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE };
		nameCur = getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, whereName, whereNameParams, null);
		if(nameCur!=null){
			//startManagingCursor(nameCur);
			if (nameCur.moveToNext()) {
				given = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
				family = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
				asoc = new Asociacion(given, family);
				//  stopManagingCursor(nameCur);
			}
			nameCur.close();
		}

		return asoc;
	}*/

	/*
	private void importarMailDeContacto(String[] projectionMail, PersonaDTO per, String contactId){
		String emailAddress;
		int emailType;
		try {
			Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projectionMail, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
			if(emails != null){
				//   startManagingCursor(emails);
				while (emails.moveToNext()) {
					// Tis would allow you get several email addresses
					emailAddress = emails.getString(emails.getColumnIndex(Email.DATA));
					emailType = emails.getInt(emails.getColumnIndex(Email.TYPE));

					if(!emailAddress.equals("")){
						switch (emailType) {
							case Email.TYPE_HOME :
								per.setEmailParticular(emailAddress);
								break;
							case Email.TYPE_WORK:
								per.setEmailLaboral(emailAddress);
								break;
							case Email.TYPE_OTHER :
								per.setEmailOtro(emailAddress);
								break;
							default:
								break;
						}
					}
				}
				emails.close();
			}

		} catch (Exception e) {
			// TODO: handle exception
		}


	}

*/

/*
	private void importarTelDeContacto(String[] projectionPhone, PersonaDTO per, String contactId){
		String phoneNumber;
		int phoneType;
		try {
			Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projectionPhone ,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
			if(phones != null){
				//  startManagingCursor(phones);
				while (phones.moveToNext())
				{
					phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					phoneType = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

					if(!phoneNumber.equals("")){
						switch (phoneType) {
							case Phone.TYPE_HOME :
								per.setTelParticular(phoneNumber);
								break;
							case Phone.TYPE_MOBILE :
								per.setCelular(phoneNumber);
								break;
							case Phone.TYPE_WORK :
								per.setTelLaboral(phoneNumber);
								break;
							case Phone.TYPE_WORK_MOBILE :
								per.setTelLaboral(phoneNumber);
								break;
							default:
								break;
						}

					}
				}
				phones.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}


	}

	*/

	private void configurarEntryBusqueda(){
		entryBusqueda = findViewById(R.id.entryBusquedaPersona);
		entryBusqueda.addTextChangedListener(new TextWatcher() {

				 @Override
				 public void onTextChanged(CharSequence s, int start, int before, int count) {
					 mEntryBusquedaNombre = s.toString();
					 if(mEntryBusquedaNombre.equals("") && (categoriasSeleccionadas == null || categoriasSeleccionadas.size() == 0)){
						 recargarLista();
					 }else{
						 cargarPersonasPorApellidoONombreMultipleSeleccion();
					 }


				 }

				 @Override
				 public void beforeTextChanged(CharSequence s, int start, int count,
											   int after) {
					 // TODO Auto-generated method stub
				 }

				 @Override
				 public void afterTextChanged(Editable s) {
					 // TODO Auto-generated method stub
				 }
			 }
		);

	}

	private void configurarSpinner(){
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		spinnerCategorias = this.findViewById(R.id.multi_spinner);
		CursorLoader cursorLoader;
		CategoriaDTO cat;
		List<CategoriaDTO> categorias = new ArrayList<>();
		List<CategoriaDTO> categoriasPersonales = null;
		Iterator<CategoriaDTO> it;
		//    CategoriaDTO cat = null;
		ConstantsAdmin.inicializarBD( mDBManager);
		cursorLoader = mDBManager.cursorLoaderCategoriasActivasPorNombre(null, this);
		//		cursor = mDBManager.fetchCategoriasActivasPorNombre(null);
		if(cursorLoader != null){
			//	startManagingCursor(cursor);
			categorias = ConstantsAdmin.categoriasCursorToDtos(cursorLoader.loadInBackground());
		}

		cursorLoader = mDBManager.cursorLoaderCategoriasPersonalesActivasPorNombre(null, this);
		if(cursorLoader != null){
			// 	startManagingCursor(cursor);
			categoriasPersonales = ConstantsAdmin.categoriasCursorToDtos(cursorLoader.loadInBackground());
		}

		ConstantsAdmin.finalizarBD(mDBManager);
		//   cat = new CategoriaDTO(0,ConstantsAdmin.CATEGORIA_TODOS, ConstantsAdmin.CATEGORIA_TODOS, 1, "");
		categorias.addAll(categoriasPersonales);

		if(ConstantsAdmin.contrasenia != null && !ConstantsAdmin.contrasenia.isActiva()){
			it = categorias.iterator();
			while(it.hasNext()){
				cat = it.next();
				if(ConstantsAdmin.estaProtegidaCategoria(cat.getNombreReal())){
					it.remove();
				}
			}

		}


		this.cambiarNombreCategorias(categorias);

		Collections.sort(categorias);
		//   categorias.add(0,cat);


		Iterator<CategoriaDTO> itCat = categorias.iterator();
		todasLasCategString = new ArrayList<>();
		todasLasCategorias = new ArrayList<>();
		CategoriaDTO cattemp;
		while(itCat.hasNext()){
			cattemp = itCat.next();
			todasLasCategorias.add(cattemp);
			if(ConstantsAdmin.estaProtegidaCategoria(cattemp.getNombreReal())){
				todasLasCategString.add("**" + cattemp.getNombreRelativo());
			}else{
				todasLasCategString.add(cattemp.getNombreRelativo());
			}

		}

		spinnerCategorias.setItems(todasLasCategString, this);


				//    this.crearSpinnerCategorias(R.id.spinnerCategorias_list_personas, categorias);
	}

	private void cambiarNombreCategorias(List<CategoriaDTO> categorias){
		Iterator<CategoriaDTO> it = categorias.iterator();
		CategoriaDTO catTemp;
		String nombreRelativo;
		while(it.hasNext()){
			catTemp = it.next();
			nombreRelativo = ConstantsAdmin.obtenerNombreCategoria(catTemp.getNombreReal(), this);
			if(nombreRelativo == null){
				nombreRelativo = catTemp.getNombreReal();
			}
			catTemp.setNombreRelativo(nombreRelativo);
		}
	}


/*
    protected Spinner crearSpinnerCategorias(int nombreSpinner, List categorias){

	    spinnerCategorias = (Spinner) findViewById(nombreSpinner);
	    mSpinnerAdapt = new ArrayAdapter<CategoriaDTO>(this, android.R.layout.simple_spinner_dropdown_item, categorias);

	    spinnerCategorias.setAdapter(mSpinnerAdapt);
	    OnItemSelectedListener spinnerListener = new seleccionSpinnerListener();
	    spinnerCategorias.setOnItemSelectedListener(spinnerListener);
	    return spinnerCategorias;
    }
 */
 /*   public class seleccionSpinnerListener implements OnItemSelectedListener {


        public void onItemSelected(AdapterView<?> parent, View v, int pos, long row) {
        	mCategoriaSeleccionada = (CategoriaDTO) parent.getSelectedItem();
        	TextView t = (TextView) v;
        	if(v != null){
        		t.setText(mCategoriaSeleccionada.getNombreRelativo());
        	}


        	parent.setSelection(pos);
        	mSpinnerPos = pos;
        	cargarPersonasPorApellidoONombre();
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

  */

   /* protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, DetallePersonaActivity.class);
        i.putExtra(ConstantsAdmin.PERSONA_SELECCIONADA, String.valueOf(id));
        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_DETALLE_PERSONA);
    }
*/

    /*
    private void cargarPersonasPorApellidoONombre(){
        // Get all of the rows from the database and create the item list
     	preferidos.setVisibility(View.VISIBLE);
     	preferidos.setBackground(getResources().getDrawable(R.drawable.pref_icon_bw));
    	Cursor personasCursor = null;
    	mMostrandoPreferidos = false;
    	try{
    		ConstantsAdmin.inicializarBD(this);

	    	personasCursor = ConstantsAdmin.mDBManager.fetchAllPersonaPorApellidoONombreODatosCategoria(mEntryBusquedaNombre, mCategoriaSeleccionada);
	    	if(personasCursor != null){
	    		startManagingCursor(personasCursor);
		        // Create an array to specify the fields we want to display in the list (only TITLE)
		        String[] from = null;
		        int[] to = null;
		        if(mCategoriaSeleccionada != null){
		        	if(mCategoriaSeleccionada.getNombreReal().equals(ConstantsAdmin.CATEGORIA_TODOS)){
						from = new String[]{ConstantsAdmin.KEY_APELLIDO, ConstantsAdmin.KEY_NOMBRES, ConstantsAdmin.KEY_NOMBRE_CATEGORIA_RELATIVO, ConstantsAdmin.KEY_DATO_EXTRA};
					}else{
						from = new String[]{ConstantsAdmin.KEY_APELLIDO, ConstantsAdmin.KEY_NOMBRES, ConstantsAdmin.KEY_DATO_EXTRA, ConstantsAdmin.KEY_DESCRIPCION};
					}
					to = new int[]{R.id.rowApellido, R.id.rowNombres, R.id.rowDatoRelevante, R.id.rowDatoRelevante2};
		        }else{
					from = new String[]{ConstantsAdmin.KEY_APELLIDO, ConstantsAdmin.KEY_NOMBRES, ConstantsAdmin.KEY_NOMBRE_CATEGORIA_RELATIVO, ConstantsAdmin.KEY_DATO_EXTRA};
					to = new int[]{R.id.rowApellido, R.id.rowNombres, R.id.rowDatoRelevante, R.id.rowDatoRelevante2};
		        }


		        KNSimpleCursorAdapter personas =
		            new KNSimpleCursorAdapter(this, R.layout.row_personas, personasCursor, from, to);
		        setListAdapter(personas);
		        cantReg.setText("(" + this.getListAdapter().getCount() + ")");
	    	}
    	}catch (Exception e) {
			e.getMessage();
		}
    }
   */
/*
	private String recuperarEtiquetaCatSeleccionadas(){
		Iterator<String> it = categoriasSeleccionadas.iterator();
		StringBuilder temp = new StringBuilder();
		while(it.hasNext()){
			temp.append(it.next().toUpperCase()).append("*");
		}
		return temp.substring(0, temp.length() - 1);
	}
*/


	private void cargarPersonasPorApellidoONombreMultipleSeleccion(){
		// Get all of the rows from the database and create the item list
		listaEspecial.setVisibility(View.VISIBLE);
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		this.getExpandableListView().setVisibility(View.GONE);

		preferidos.setVisibility(View.VISIBLE);
		preferidos.setBackground(getResources().getDrawable(R.drawable.pref_icon_bw));
		Cursor personasCursor;

		imgPrefRight.setVisibility(View.GONE);
		ConstantsAdmin.inicializarBD(mDBManager);
/*		if(categoriasSeleccionadas != null && categoriasSeleccionadas.size()>0){
			spinnerCategorias.setItems(todasLasCategString, this);
		}*/
		ConstantsAdmin.config.setMuestraPreferidos(false);
		boolean noActivaContrasenia = !ConstantsAdmin.contrasenia.isActiva();
		personasCursor = mDBManager.fetchAllPersonaPorApellidoONombreODatosCategoriaMultiSeleccion(noActivaContrasenia, mEntryBusquedaNombre, categoriasSeleccionadas, ConstantsAdmin.categoriasProtegidas);

		if(personasCursor != null){
			String[] from;
			int[] to;
//			from = new String[]{ConstantsAdmin.KEY_APELLIDO, ConstantsAdmin.KEY_NOMBRES, ConstantsAdmin.KEY_NOMBRE_CATEGORIA_RELATIVO, ConstantsAdmin.KEY_DATO_EXTRA};
//			to = new int[]{R.id.rowApellido, R.id.rowNombres, R.id.rowDatoRelevante, R.id.rowDatoRelevante2};

			from = new String[]{ConstantsAdmin.KEY_APELLIDO, ConstantsAdmin.KEY_NOMBRES, ConstantsAdmin.KEY_NOMBRE_CATEGORIA_RELATIVO, ConstantsAdmin.KEY_DATO_EXTRA};
			to = new int[]{R.id.rowApellido, R.id.rowNombres, R.id.rowDatoRelevante};


			KNSimpleCursorAdapter personas =
					new KNSimpleCursorAdapter(this, R.layout.row_personas, personasCursor, from, to);
			listaEspecial.setAdapter(personas);
			cantReg.setText("(" + listaEspecial.getAdapter().getCount() + ")");
		//	listaEspecial.requestFocus(0);

			//entryBusqueda.requestFocus();
		//	getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

		}/*
		this.closeContextMenu();
		this.closeOptionsMenu();
		spinnerCategorias.setPressed(false);
		spinnerCategorias.refreshDrawableState();
*/


	}

	private void habilitarDeshabilitarItemMenu(){
		if(menuItemExportarContactos != null){
			if(personasMap != null && personasMap.size()!= 0){
				menuItemExportarContactos.setEnabled(true);
			}else{
				menuItemExportarContactos.setEnabled(false);
			}
		}
		if(menuItemExportarContactosEstetico != null){
			if(personasMap != null && personasMap.size() != 0){
				menuItemExportarContactosEstetico.setEnabled(true);
			}else{
				menuItemExportarContactosEstetico.setEnabled(false);
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item;
		super.onCreateOptionsMenu(menu);

		item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_PERSONA,0, R.string.menu_agregar_persona);
		item.setIcon(R.drawable.person_menu_icon);

		item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_IMPORTAR_CONTACTOS,0, R.string.menu_importar_contactos);
		item.setIcon(R.drawable.import_menu_icon);

		item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_LISTADO_CATEGORIAS,0, R.string.menu_listar_categoria);
		item.setIcon(R.drawable.categoria_menu_icon);

		menuItemExportarContactosEstetico = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_EXPORTAR_CONTACTOS_ESTETICO,0, R.string.menu_exportar_excel);
		menuItemExportarContactosEstetico.setIcon(R.drawable.export_excel);


		menuItemExportarContactos = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_EXPORTAR_CONTACTOS,0, R.string.menu_exportar_contactos);
		menuItemExportarContactos.setIcon(R.drawable.generate_menu);

		item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_IMPORTAR_CONTACTOS_CSV,0, R.string.menu_importar_contactos_csv);
		item.setIcon(R.drawable.restore_menu);

		item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_ABOUT_ME,0, R.string.menu_about_me);
		item.setIcon(R.drawable.about_me_menu);

		this.habilitarDeshabilitarItemMenu();

		return true;
	}

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
			case ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_PERSONA:
				this.openAltaPersona();
				return true;
			case ConstantsAdmin.ACTIVITY_EJECUTAR_LISTADO_CATEGORIAS:
				this.mostrarEleccionCategoria();
				return true;
			case ConstantsAdmin.ACTIVITY_EJECUTAR_IMPORTAR_CONTACTOS:
				this.mostrarDialogoImportarContactos();
				return true;
			case ConstantsAdmin.ACTIVITY_EJECUTAR_ABOUT_ME:
				this.mostrarAboutMe();
				return true;
			case ConstantsAdmin.ACTIVITY_EJECUTAR_EXPORTAR_CONTACTOS:
				this.exportContacts();
				return true;
			case ConstantsAdmin.ACTIVITY_EJECUTAR_IMPORTAR_CONTACTOS_CSV:
				this.askForReadStoragePermission();
				return true;
			case ConstantsAdmin.ACTIVITY_EJECUTAR_EXPORTAR_CONTACTOS_ESTETICO:
				this.exportContactsEstetico();
				return true;
		}
		//return super.onMenuItemSelected(featureId, item);
		return true;
	}

	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		//   ConstantsAdmin.inicializarBD( mDBManager);
		CursorLoader cl = null;
		boolean noActivaContrasenia = ConstantsAdmin.contrasenia != null && !ConstantsAdmin.contrasenia.isActiva();
		switch(id) {
			case CATEGORIAS_CURSOR:
				cl = mDBManager.cursorLoaderCategoriasPorNombre(null, this);
				ConstantsAdmin.cursorCategorias = cl;
				break; // optional
			case CATEGORIAS_PERSONALES_CURSOR:
				cl = mDBManager.cursorLoaderCategoriasPersonalesPorNombre(null, this);
				ConstantsAdmin.cursorCategoriasPersonales = cl;
				break; // optional
			case CONTRASENIA_CURSOR:
				cl = mDBManager.cursorLoaderContrasenia(this);
				break;
			case CATEGORIAS_PROTEGIDAS_CURSOR:
				cl = mDBManager.cursorLoaderCategoriasProtegidas(this);
				break; 				// optional
			case PERSONAS_CURSOR:
				cl = mDBManager.cursorLoaderPersonas(noActivaContrasenia, ConstantsAdmin.categoriasProtegidas, this);
				ConstantsAdmin.cursorPersonas = cl;
				break;
			case PREFERIDOS_CURSOR:
				cl = mDBManager.cursorLoaderPreferidos(noActivaContrasenia, ConstantsAdmin.categoriasProtegidas, this);
				ConstantsAdmin.cursorPreferidos = cl;
				break;
			case CATEGORIAS_ACTIVAS_CURSOR:
				cl = mDBManager.cursorLoaderCategoriasActivasPorNombre(null, this);
				ConstantsAdmin.cursorCategoriasActivas = cl;
				break;
			case CATEGORIAS_PERSONALES_ACTIVAS_CURSOR:
				cl = mDBManager.cursorLoaderCategoriasPersonalesActivasPorNombre(null, this);
				ConstantsAdmin.cursorCategoriasPersonalesActivas = cl;
				break;			// You can have any number of case statements.
			case CONFIGURACION_CURSOR:
				cl = mDBManager.cursorLoaderConfiguracion( this);
				ConstantsAdmin.cursorConfiguracion = cl;
				break;
			default : // Optional
				// Statements
		}

		return cl;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Swap the new cursor in.  (The framework will take care of closing the
		// old cursor once we return.)

	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {

	}

	private static class ExportCSVTask extends AsyncTask<Long, Integer, Integer>{
		ProgressDialog dialog = null;
		@Override
		protected Integer doInBackground(Long... params) {

			try {
				DataBaseManager mDBManager = DataBaseManager.getInstance(me);
				publishProgress(1);
				ConstantsAdmin.exportarCSV(me, mDBManager);


			} catch (Exception e) {
				ConstantsAdmin.mensaje = me.getString(R.string.error_exportar_csv) ;
			}
			return 0;

		}

		protected void onProgressUpdate(Integer... progress) {
			dialog = ProgressDialog.show(me, "",
					me.getString(R.string.mensaje_exportando_contactos), false);
		}

		@Override
		protected void onPostExecute(Integer result) {
			try{
				dialog.cancel();
			}catch (Exception e) {
				// TODO: handle exception
			}
			ConstantsAdmin.mostrarMensajeDialog(me, ConstantsAdmin.mensaje);
			ConstantsAdmin.mensaje = null;

		}
	}

	private class ExportCSVEsteticoTask extends AsyncTask<Long, Integer, Integer>{
		ProgressDialog dialog = null;

		@Override
		protected Integer doInBackground(Long... params) {

			try {
				publishProgress(1);
				DataBaseManager mDBManager = DataBaseManager.getInstance(me);
				ConstantsAdmin.exportarCSVEstetico(me, separadorExcel, ConstantsAdmin.categoriasProtegidas, mDBManager);


			} catch (Exception e) {
				ConstantsAdmin.mensaje = me.getString(R.string.error_exportar_csv) ;
			}
			return 0;

		}

		protected void onProgressUpdate(Integer... progress) {
		//	dialog = ProgressDialog.show(me, "",me.getString(R.string.mensaje_exportando_contactos), false);
		}

		@Override
		protected void onPostExecute(Integer result) {
			try{
				dialog.cancel();
			}catch (Exception e) {
				// TODO: handle exception
			}
	//		ConstantsAdmin.mostrarMensajeDialog(me, ConstantsAdmin.mensaje);
		//	ConstantsAdmin.mensaje = null;

		}
	}

    /*

    private class ImportContactTask extends AsyncTask<Long, Integer, Integer>{
    	ProgressDialog dialog = null;
        @Override
        protected Integer doInBackground(Long... params) {

            try {
                publishProgress(1);
                importarContactos();

            } catch (Exception e) {
            	ConstantsAdmin.mensaje = me.getString(R.string.errorImportarContacto);
            }
            return 0;

        }

        protected void onProgressUpdate(Integer... progress) {
        	dialog = ProgressDialog.show(me, "",
                    me.getString(R.string.mensaje_importando_contactos), false);
        }

        @Override
        protected void onPostExecute(Integer result) {
        	try{
        		dialog.cancel();
        	}catch (Exception e) {
				// TODO: handle exception
			}
       		ConstantsAdmin.mostrarMensajeDialog(me, ConstantsAdmin.mensaje);
    		ConstantsAdmin.mensaje = null;
    		ConstantsAdmin.resetPersonasOrganizadas();
    		recargarLista();
        }
    }
*/


	private void mostrarDialogImportarBackup(){
	/*	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.mensaje_exportar_contactos)
				.setCancelable(false)
				.setPositiveButton(R.string.label_si, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Long[] params = new Long[1];
						params[0] = 1L;
						dialog.cancel();
						new ExportCSVTask().execute(params);    	           }
				})
				.setNegativeButton(R.string.label_no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		builder.show();*/

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.mensaje_importar_contactos_csv)
				.setCancelable(false)
				.setPositiveButton(R.string.label_si, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Long[] params = new Long[2];
						params[0] = 1L;
						params[1] = 0L;
						dialog.cancel();
						new ImportCSVTask().execute(params);
					}
				})
				.setNegativeButton(R.string.label_no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		builder.show();

	}


	private void mostrarDialogImportarBackupLite(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.mensaje_importar_contactos_csv)
				.setCancelable(false)
				.setPositiveButton(R.string.label_si, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Long[] params = new Long[2];
						params[0] = 1L;
						params[1] = 1L;
						dialog.cancel();
						new ImportCSVTask().execute(params);
					}
				})
				.setNegativeButton(R.string.label_no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		builder.show();

	}




	private void exportContacts(){
		if(!personasMap.isEmpty()){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.mensaje_exportar_contactos)
					.setCancelable(false)
					.setPositiveButton(R.string.label_si, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Long[] params = new Long[1];
							params[0] = 1L;
							dialog.cancel();
							new ExportCSVTask().execute(params);    	           }
					})
					.setNegativeButton(R.string.label_no, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			builder.show();
		}else{
			ConstantsAdmin.mostrarMensajeDialog(this, this.getString(R.string.mensaje_no_hay_contactos));
		}

	}

	private void exportContactsEstetico(){
		if(!personasMap.isEmpty()){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.mensaje_exito_exportar_excel)
					.setCancelable(true)
					.setPositiveButton(R.string.coma_separated, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Long[] params = new Long[1];
							params[0] = 1L;
							dialog.cancel();
							separadorExcel = ConstantsAdmin.COMA;
							new ExportCSVEsteticoTask().execute(params);
							Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
							Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT)
									.addCategory(Intent.CATEGORY_OPENABLE)
									.setDataAndType(uri, "text/csv")
									.putExtra(Intent.EXTRA_TITLE, ConstantsAdmin.fileEsteticoCSV);

							startActivityForResult(intent, ConstantsAdmin.ACTIVITY_CHOOSE_FILE);
						}
					})
					.setNegativeButton(R.string.puntocoma_separated, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Long[] params = new Long[1];
							params[0] = 1L;
							dialog.cancel();
							separadorExcel = ConstantsAdmin.PUNTO_COMA;
							new ExportCSVEsteticoTask().execute(params);

							Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
							Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT)
									.addCategory(Intent.CATEGORY_OPENABLE)
									.setDataAndType(uri, "text/csv")
									.putExtra(Intent.EXTRA_TITLE, ConstantsAdmin.fileEsteticoCSV);

							startActivityForResult(intent, ConstantsAdmin.ACTIVITY_CHOOSE_FILE);


						}
					});
			builder.show();
		}else{
			ConstantsAdmin.mostrarMensajeDialog(this, this.getString(R.string.mensaje_no_hay_contactos));
		}

	}


	private void importContactosCSV(){/*
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.mensaje_importar_contactos_csv)
				.setCancelable(false)
				.setPositiveButton(R.string.label_si, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Long[] params = new Long[1];
						params[0] = 1L;
						dialog.cancel();
						new ImportCSVTask().execute(params);
					}
				})
				.setNegativeButton(R.string.label_no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		builder.show();
*/


		if(!ConstantsAdmin.existeBackupKNContactsLite()) {
			this.mostrarDialogImportarBackup();
		}else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.mensaje_seleccionar_origen_backup)
					.setCancelable(false)
					.setPositiveButton(R.string.app_name, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							mostrarDialogImportarBackup();
						}
					})
					.setNegativeButton(R.string.app_lite_name, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							mostrarDialogImportarBackupLite();
						}
					});
			builder.show();
		}



	}

	/*
     @Override
     public void startManagingCursor(Cursor c) {
         allMyCursors.add(c);
         super.startManagingCursor(c);
     }
     */
	private void mostrarDialogoImportarContactos(){

		Intent i = new Intent(this, ImportarContactoActivity.class);
		this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_IMPORTAR_CONTACTOS);
    	/*

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(R.string.mensaje_importar_contactos)
    	       .setCancelable(false)
    	       .setPositiveButton(R.string.label_si, new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
	   	        	   Long[] params = new Long[1];
	   		    	   params[0] = new Long(1);
	   		    	   dialog.cancel();
    	        	   new ImportContactTask().execute(params);
    	           }
    	       })
    	       .setNegativeButton(R.string.label_no, new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                dialog.cancel();
    	           }
    	       });
    	builder.show();
    	*/
	}


	private void mostrarAboutMe(){
		Intent i = new Intent(this, AboutMeActivity.class);
		this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_ABOUT_ME);
	}

	private void openAltaPersona() {
		Intent i = new Intent(this, AltaPersonaActivity.class);
		this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_PERSONA);
	}

	private void openListadoCategoria(){
		Intent i = new Intent(this, ListadoCategoriaActivity.class);
		this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_LISTADO_CATEGORIAS);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		super.onActivityResult(requestCode, resultCode, intent);
		ejecutarOnActivityResult(requestCode, intent);
	}

	private void saveCSVContactsFiles(Intent intent){
		Uri uri = null;
		if(intent != null){
			uri = intent.getData();
			//	String filePath = getRealPathFromURI(uri);
			//String filePath = intent.getData().getPath();
			String fileTempPath = ConstantsAdmin.obtenerPathDeArchivo(ConstantsAdmin.fileEsteticoCSV);
			try {
				ConstantsAdmin.copyFiles(fileTempPath,uri, this.getContentResolver());
			} catch (IOException e) {
				ConstantsAdmin.mostrarMensajeDialog(this, this.getString(R.string.error_exportar_csv));
			}
		}


	}





	private void ejecutarOnActivityResult(int requestCode, Intent intent){
		try {
		//	String path  = "";
			DataBaseManager mDBManager = DataBaseManager.getInstance(this);
			if(requestCode == ConstantsAdmin.ACTIVITY_EJECUTAR_PROTECCION_CATEGORIA){
				categoriasSeleccionadas = null;
				mEntryBusquedaNombre = null;
			}
			if(requestCode == ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_PERSONA || requestCode == ConstantsAdmin.ACTIVITY_EJECUTAR_ELIMINAR_CONTACTO
					|| requestCode == ConstantsAdmin.ACTIVITY_EJECUTAR_ACTIVAR_CONTRASENIA){
				ConstantsAdmin.resetPersonasOrganizadas();

			}

			if(requestCode == ConstantsAdmin.ACTIVITY_CHOOSE_FILE)
			{
				this.saveCSVContactsFiles(intent);

			}
			mySortedByElements = null;
			//	this.resetAllMyCursors();
			this.configurarSpinner();

			if(ConstantsAdmin.contrasenia == null){
				ConstantsAdmin.cargarCategoriasProtegidas(this, mDBManager);
			}
			if(ConstantsAdmin.contrasenia.isActiva()){
				protegerCategorias.setBackgroundResource(R.drawable.candado_abierto);
			}else{
				protegerCategorias.setBackgroundResource(R.drawable.candado_cerrado);
			}
			if((mEntryBusquedaNombre!= null && !mEntryBusquedaNombre.equals("")) || (categoriasSeleccionadas != null && categoriasSeleccionadas.size() > 0)){
				this.cargarPersonasPorApellidoONombreMultipleSeleccion();
			}else if(ConstantsAdmin.config.isMuestraPreferidos()){
				ConstantsAdmin.config.setMuestraPreferidos(false);
				this.verPreferidos();
			}
			else{
				this.mostrarTodosLosContactos();
			}
		} catch (Exception e) {
			ConstantsAdmin.mostrarMensaje(this, e.getMessage());
		}


	}
/*
	public String getRealPathFromURI(Uri contentUri) {
	//	String [] proj = {MediaStore.Images.Media.DATA};
		Cursor cursor = getContentResolver().query( contentUri, null, null, null,null);
		if (cursor == null) {
			return null;
		}
		//int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(0);
	}
*/
	public void onPause() {
		super.onPause();
		this.closeContextMenu();
		this.closeOptionsMenu();
		spinnerCategorias.setPressed(false);
		spinnerCategorias.refreshDrawableState();



	}
    /*
    private void resetAllMyCursors(){
    	Cursor cur;
    	if(allMyCursors != null){
			for (Cursor allMyCursor : allMyCursors) {
				cur = allMyCursor;
				cur.close();
		//		this.stopManagingCursor(cur);
			}

    	}
    	allMyCursors = new ArrayList<>();
    }
    */


	public void onResume() {
		super.onResume();
		if(ConstantsAdmin.cerrarMainActivity){
			ConstantsAdmin.cerrarMainActivity = false;
			this.finish();
		}else{
			//     this.resetAllMyCursors();

			if(listaEspecial == null){
				this.configurarListView();
			}
			if((mEntryBusquedaNombre!= null && !mEntryBusquedaNombre.equals("")) || (categoriasSeleccionadas != null && categoriasSeleccionadas.size() > 0)){
				this.cargarPersonasPorApellidoONombreMultipleSeleccion();
			}else if(ConstantsAdmin.config.isMuestraPreferidos()){
				ConstantsAdmin.config.setMuestraPreferidos(false);
				this.verPreferidos();
			}

		}
		// Restore list state and list/item positions
		if (mListState != null) {
			getExpandableListView().onRestoreInstanceState(mListState);
		}
		getExpandableListView().setSelectionFromTop(mListPosition, mItemPosition);

	}




	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		//this.finish();
		super.onDestroy();
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		ConstantsAdmin.actualizarConfig(mDBManager);
	}


	@Override
	public void onItemsSelected(boolean[] selected) {
		// TODO Auto-generated method stub
		categoriasSeleccionadas = new ArrayList<>();
		Iterator<CategoriaDTO> it;
		CategoriaDTO tempCat;
		it = todasLasCategorias.iterator();
		int i = 0;
		while(it.hasNext()){
			tempCat = it.next();
			if(selected[i]){
				categoriasSeleccionadas.add(tempCat.getNombreRelativo());
			}
			i++;
		}
		if(categoriasSeleccionadas != null && categoriasSeleccionadas.size() > 0){
			cargarPersonasPorApellidoONombreMultipleSeleccion();
		}else{
			this.mostrarTodosLosContactos();

		}
		entryBusqueda.requestFocus();


	}

	// CAMBIOS PARA AGREGAR CONTRASENIA EN LAS CATEGORIAS


	private void mostrarEleccionCategoria(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(this.getString(R.string.mensaje_seleccione_actividad_categoria))
				.setCancelable(true)
				.setPositiveButton(R.string.label_proteger_categoria, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						openProteccionCategoria();
					}
				})
				.setNegativeButton(R.string.label_activar_categoria, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						openListadoCategoria();
					}
				});
		builder.show();

	}

	private void openProteccionCategoria(){
		Intent i = new Intent(this, ProteccionCategoriaActivity.class);
		this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_PROTECCION_CATEGORIA);
	}

	private void configurarBotonProtegerCategorias(){
		protegerCategorias = this.findViewById(R.id.botonProtegerCategorias);
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		if(ConstantsAdmin.contrasenia == null){
			ConstantsAdmin.cargarContrasenia(this, mDBManager);
			ConstantsAdmin.cargarCategoriasProtegidas(this, mDBManager);
		}
		if(ConstantsAdmin.contrasenia.isActiva()){
			protegerCategorias.setBackgroundResource(R.drawable.candado_abierto);
		}else{
			protegerCategorias.setBackgroundResource(R.drawable.candado_cerrado);
		}


		protegerCategorias.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				preferidos.setBackground(getResources().getDrawable(R.drawable.pref_icon_bw));
				DataBaseManager mDBManager = DataBaseManager.getInstance(me);
				// TODO Auto-generated method stub
				if(ConstantsAdmin.categoriasProtegidas.size() == 0 || ConstantsAdmin.contrasenia.getId() == -1){
					openProteccionCategoria();
					if(ConstantsAdmin.categoriasProtegidas.size() == 0){
						ConstantsAdmin.mostrarMensaje(me, me.getString(R.string.mensaje_seleccione_categoria_proteger));
					}else{
						ConstantsAdmin.mostrarMensaje(me, me.getString(R.string.mensaje_registre_contrasenia));
					}


				}else if(!ConstantsAdmin.contrasenia.isActiva()){
					openVerActivarContrasenia();
				}else{
					ConstantsAdmin.contrasenia.setActiva(false);
					ConstantsAdmin.actualizarContrasenia(ConstantsAdmin.contrasenia, mDBManager);
					protegerCategorias.setBackgroundResource(R.drawable.candado_cerrado);
					configurarSpinner();
					ConstantsAdmin.resetPersonasOrganizadas();
					mostrarTodosLosContactos();
				}

			}
		});

	}

	private void openVerActivarContrasenia() {
		Intent i = new Intent(this, ActivarContraseniaActivity.class);
		this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_ACTIVAR_CONTRASENIA);
	}







}
