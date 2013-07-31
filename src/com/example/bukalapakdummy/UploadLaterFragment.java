package com.example.bukalapakdummy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import listener.APIListener;
import listener.ProductUploaderListener;
import model.business.Attribute;
import model.business.CheckableAttribute;
import model.business.Credential;
import model.business.CredentialEditor;
import model.business.DraftedLocalProduct;
import model.business.ProductCondition;
import model.business.SelectableAttribute;
import model.business.StringAttribute;
import tools.LocalImageManager;
import tools.UploadProductEditor;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import api.ProductUploader;
import api.ReadAttributes;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.bukalapakdummy.R;

public class UploadLaterFragment extends SherlockActivity {
	protected static final int PICK_FROM_SOURCE = 0;
	private static final int PICK_CROP = 1;
	File source;
	String productID;
	TextView kategori;
	EditText namaBarang;
	EditText kondisi;
	EditText hargaBarang;
	EditText beratBarang;
	EditText stokBarang;
	EditText deskripsiBarang;
	RadioGroup kondisiGroup;
	RadioButton bekas;
	RadioButton baru;
	String username;
	String password;
	String[] list_kota;
	Button image_select;
	Button unggah;
	Button city_select;
	CheckBox nego;
	CheckBox kurirJNE;
	CheckBox kurirTIKI;
	CheckBox kurirPOS;
	LinearLayout len;
	LinearLayout listImages;
	HashMap<String, Attribute> attribs;
	HashMap<View, Attribute> specs;
	String category_id;
	ImageView imgview;
	// ImageUploadAdapter imageAdapter;
	private Bitmap bitmap;
	// Session Manager Class
	// SessionManager session;
	Credential credential;
	Context context;

	protected CharSequence[] kota;
	protected ArrayList<CharSequence> selectedKota;
	private ArrayList<File> localImagePaths;

	public UploadLaterFragment() {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		credential = CredentialEditor.loadCredential(this);
		attribs = new HashMap<String, Attribute>();
		specs = new HashMap<View, Attribute>();
		setContentView(R.layout.view_product_upload_main);

		len = (LinearLayout) findViewById(R.id.listSpecs);
		listImages = (LinearLayout) findViewById(R.id.listImages);

		// untuk multiplechoice
		localImagePaths = new ArrayList<File>();

		list_kota = getResources().getStringArray(R.array.color_names);
		kota = (CharSequence[]) list_kota;
		selectedKota = new ArrayList<CharSequence>();
		kategori = (TextView) findViewById(R.id.kategori_text);
		namaBarang = (EditText) findViewById(R.id.namabarang_edittext);
		bekas = (RadioButton) findViewById(R.id.radioBekas);
		baru = (RadioButton) findViewById(R.id.radioBaru);
		kondisiGroup = (RadioGroup) findViewById(R.id.radioKondisi);
		hargaBarang = (EditText) findViewById(R.id.hargabarang_edittext);
		nego = (CheckBox) findViewById(R.id.checkBisaNego);
		beratBarang = (EditText) findViewById(R.id.perkiraanberat_edittext);
		stokBarang = (EditText) findViewById(R.id.stok_edittext);
		kurirJNE = (CheckBox) findViewById(R.id.checkJasaKurirJNE);
		kurirTIKI = (CheckBox) findViewById(R.id.checkJasaKurirTIKI);
		kurirPOS = (CheckBox) findViewById(R.id.checkJasaKurirPos);
		deskripsiBarang = (EditText) findViewById(R.id.deskripsi_edittext);
		image_select = (Button) findViewById(R.id.photo_button);
		imgview = (ImageView) findViewById(R.id.photo_image);
		unggah = (Button) findViewById(R.id.product_upload_save_button);
		city_select = (Button) findViewById(R.id.pilihdelivery_button);

		unggah.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				final String[] options = { "Unggah", "Simpan" };
				builder.setTitle("Silakan pilih aksi :");
				builder.setItems(options,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case 0:
									uploadProduct();
									break;
								case 1:
									saveProduct();
									break;
								}
							}

						});
				builder.setPositiveButton("Kembali", null);
				builder.setNegativeButton("Batal", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				builder.show();
			}
		});
		image_select.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showImageSelection();
			}
		});
		registerForContextMenu(image_select);
		registerForContextMenu(kategori);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle("Draft: Jual Barang");

		loadDraftProduct();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

//	private void createSpecs(ArrayList<Attribute> attr,
//			HashMap<String, String> spec) {
//		for (Attribute attribute : attr) {
//			String field;
//			String disp;
//			field = attribute.getFieldName();
//			disp = attribute.getDisplayName();
//			attribs.put(field, disp);
//			LayoutInflater inflater = LayoutInflater.from(context);
//			View view = inflater.inflate(R.layout.view_product_upload_field,
//					null);
//			TextView tx = (TextView) view.findViewById(R.id.field_text);
//			tx.setText(disp);
//			View vw = null;
//			if (attribute instanceof StringAttribute) {
//				EditText et = (EditText) view.findViewById(R.id.field_edittext);
//				et.setVisibility(EditText.VISIBLE);
//				et.setText(spec.get(attribute.getFieldName()));
//				vw = et;
//			} else if (attribute instanceof SelectableAttribute) {
//
//				final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//						context, android.R.layout.simple_spinner_item);
//				ArrayList<String> list = ((SelectableAttribute) attribute)
//						.getOptions();
//				for (int ii = 0; ii < list.size(); ii++) {
//					adapter.add(list.get(ii));
//				}
//				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//				Spinner spin = (Spinner) view.findViewById(R.id.field_spinner);
//				spin.setAdapter(adapter);
//				spin.setSelection(list.indexOf(spec.get(attribute
//						.getFieldName())));
//				spin.setVisibility(Spinner.VISIBLE);
//				vw = spin;
//			}
//			tx.setTextColor(Color.BLACK);
//			len.addView(view);
//			specs.put(field, vw);
//		}
//		len.requestLayout();
//	}
	private void createSpecs(ArrayList<Attribute> attr,
			HashMap<String, Set<String>> hashMap) {
		for (Attribute attribute : attr) {
			String field;
			String disp;
			field = attribute.getFieldName();
			disp = attribute.getDisplayName();
			attribs.put(field, attribute);
			LayoutInflater inflater = LayoutInflater.from(context);
			View view = inflater.inflate(R.layout.view_product_upload_field,
					null);
			TextView tx = (TextView) view.findViewById(R.id.field_text);
			tx.setText(disp);
			tx.setTextColor(Color.BLACK);
			View vw = null;
			if (attribute instanceof StringAttribute) {
				EditText et = (EditText) view.findViewById(R.id.field_edittext);
				et.setVisibility(EditText.VISIBLE);
				Set<String> value = hashMap.get(attribute.getFieldName());
				for (String string : value) {
					et.setText(string);	
				}
				specs.put(et, attribute);
			} else if (attribute instanceof SelectableAttribute) {
				final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						context, android.R.layout.simple_spinner_item);
				ArrayList<String> list = ((SelectableAttribute) attribute)
						.getOptions();
				for (int ii = 0; ii < list.size(); ii++) {
					adapter.add(list.get(ii));
				}
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				Spinner spin = (Spinner) view.findViewById(R.id.field_spinner);
				spin.setAdapter(adapter);
				Set<String> value = hashMap.get(attribute.getFieldName());
				for (String string : value) {
					spin.setSelection(list.indexOf(string));
				}
				spin.setVisibility(Spinner.VISIBLE);
				specs.put(spin, attribute);
			}
			else if(attribute instanceof CheckableAttribute)
			{
				Set<String> list = ((CheckableAttribute) attribute)
						.getOptions();
				Set<String> values = hashMap.get(attribute.getFieldName());
				for (String string : list) {
					CheckBox c = new CheckBox(context);
					c.setText(string);
					if(values.contains(string)) c.isChecked();
					specs.put(c, attribute);
					((LinearLayout) view).addView(c);
				}
			}
			
			len.addView(view);
		}
		len.requestLayout();
	}

	private void showImageSelection() {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		final String[] keys = { "Kamera", "Koleksi" };
		builder.setTitle("Pilih sumber gambar :");
		builder.setItems(keys, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra("return-data", true);
					startActivityForResult(intent, PICK_FROM_SOURCE);
					break;
				case 1:
					Intent intent2 = new Intent();
					intent2.setType("image/*");
					intent2.setAction(Intent.ACTION_GET_CONTENT);
					intent2.putExtra("return-data", true);
					startActivityForResult(Intent.createChooser(intent2,
							"Complete action using"), PICK_FROM_SOURCE);
					break;
				}
			}

		});
		builder.show();
	}

	private void addImage(final File f) {
		LayoutInflater inflater = LayoutInflater.from(context);
		final View view = inflater.inflate(R.layout.view_product_upload_image,
				null);
		Bitmap b = LocalImageManager.loadImage(f);
		localImagePaths.add(f);

		ImageView imgview = (ImageView) view.findViewById(R.id.thumb);
		imgview.setImageBitmap(b);
		final Button btn = (Button) view.findViewById(R.id.controlBtn);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				listImages.removeView(view);
				localImagePaths.remove(f);
			}
		});
		listImages.addView(view);
		listImages.requestLayout();
		listImages.invalidate();
	}

	private void addImage(Bitmap b) {
		LayoutInflater inflater = LayoutInflater.from(context);
		final View view = inflater.inflate(R.layout.view_product_upload_image,
				null);
		final File f = LocalImageManager.saveImage(b, credential, context);
		localImagePaths.add(f);

		ImageView imgview = (ImageView) view.findViewById(R.id.thumb);
		imgview.setImageBitmap(b);
		final Button btn = (Button) view.findViewById(R.id.controlBtn);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				listImages.removeView(view);
				localImagePaths.remove(f);
			}
		});
		listImages.addView(view);
		listImages.requestLayout();
		listImages.invalidate();
	}

	//
	private void performCrop(Uri uri) {
		try {
			// call the standard crop action intent (the user device may not
			// support it)
			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			// indicate image type and Uri
			cropIntent.setDataAndType(uri, "image/*");
			// set crop properties
			cropIntent.putExtra("crop", "true");
			// indicate aspect of desired crop
			cropIntent.putExtra("aspectX", 1);
			cropIntent.putExtra("aspectY", 1);
			// indicate output X and Y
			cropIntent.putExtra("outputX", 300);
			cropIntent.putExtra("outputY", 300);
			// retrieve data on return
			cropIntent.putExtra("return-data", true);
			// start the activity - we handle returning in onActivityResult
			startActivityForResult(cropIntent, PICK_CROP);
		} catch (ActivityNotFoundException anfe) {
			// display an error message
			String errorMessage = "Whoops - your device doesn't support the crop action!";
			Toast toast = Toast
					.makeText(this, errorMessage, Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == PICK_FROM_SOURCE) {
			if (data != null) {
				Uri picUri = data.getData();
				performCrop(picUri);
			}
		} else if (requestCode == PICK_CROP) {
			if (data != null) {
				Bundle extras = data.getExtras();
				if (extras != null) {
					bitmap = extras.getParcelable("data");
					if (bitmap.getHeight() < 300 || bitmap.getWidth() < 300)
						Toast.makeText(getApplicationContext(),
								"Gambar kurang dari ukuran minimal",
								Toast.LENGTH_SHORT).show();
					else {
						try {
							addImage(bitmap);
						} catch (Exception e) {

						}
					}
				}
			}
		}
	}

	private DraftedLocalProduct compile() {
		DraftedLocalProduct product = new DraftedLocalProduct();
		product.setCategoryID(category_id);
		product.setName(namaBarang.getText().toString());
		product.setPrice(Integer.parseInt(hargaBarang.getText().toString()));
		product.setWeight(Integer.parseInt(beratBarang.getText().toString()));
		product.setStock(Integer.parseInt(stokBarang.getText().toString()));
		product.setDescription(deskripsiBarang.getText().toString());
		if (bekas.isChecked()) {
			product.setCondition(ProductCondition.SECONDHAND);
		} else if (baru.isChecked()) {
			product.setCondition(ProductCondition.NEW);
		}
		product.setNegotiable(nego.isChecked());
		HashMap<String, Set<String>> temp = new HashMap<String, Set<String>>();
		for (View v : specs.keySet()) {
			Attribute a = specs.get(v);
			if (v instanceof EditText) {
				EditText e = (EditText) v;
				String result = e.getText().toString();
				Set<String> value = new HashSet<String>();
				value.add(result);
				temp.put(a.getFieldName(), value);
			} else if (v instanceof Spinner) {
				Spinner s = (Spinner) v;
				String result = s.getSelectedItem().toString();
				Set<String> value = new HashSet<String>();
				value.add(result);
				temp.put(a.getFieldName(), value);
			} else if (v instanceof CheckBox) {
				CheckBox c = (CheckBox) v;
				if (c.isChecked()) {
					String result = c.getText().toString();
					if (temp.containsKey(a.getFieldName())) {
						Set<String> value = temp.get(a.getFieldName());
						value.add(result);
					} else {
						Set<String> value = new HashSet<String>();
						value.add(result);
						temp.put(a.getFieldName(), value);
					}
				}
			}
		}
		product.setSpecs(temp);
		product.setImgDirs(localImagePaths);
		return product;
	}

	private void loadDraftProduct() {
		String path = getIntent().getStringExtra("path");
		source = new File(path);
		final DraftedLocalProduct res = UploadProductEditor
				.loadDraftedProduct(source);
		for (File f : res.getImgDirs()) {
			addImage(f);
		}
		category_id = res.getCategoryID();
		kategori.setText(res.getCategory());
		namaBarang.setText(res.getName());
		switch (res.getCondition()) {
		case NEW:
			baru.setChecked(true);
			break;
		case SECONDHAND:
			bekas.setChecked(true);
			break;
		}
		hargaBarang.setText(res.getPrice() + "");
		nego.setChecked(res.isNegotiable());
		beratBarang.setText(res.getWeight() + "");
		stokBarang.setText(res.getStock() + "");
		deskripsiBarang.setText(res.getDescription());

		ReadAttributes task = new ReadAttributes(context, credential,
				res.getCategoryID());
		task.setAPIListener(new APIListener<ArrayList<Attribute>>() {
			ProgressDialog pd;

			@Override
			public void onSuccess(ArrayList<Attribute> attr) {
				pd.dismiss();
				createSpecs(attr, res.getSpecs());
			}

			@Override
			public void onFailure(Exception e) {
				pd.dismiss();
			}

			@Override
			public void onExecute() {
				pd = new ProgressDialog(context);
				pd.setTitle("Edit Produk...");
				pd.setMessage("Sedang sinkronisasi data produk...");
				pd.setIndeterminate(true);
				pd.setCancelable(true);
				pd.show();
			}
		}).execute();

	}

	private void uploadProduct() {
		DraftedLocalProduct p = compile();
		final ProductUploader task = new ProductUploader(context, credential, p);
		task.setListener(new ProductUploaderListener() {
			ProgressDialog pd = new ProgressDialog(context);

			@Override
			public void onUploadingProduct() {
				pd.setMessage("Tunggu sebentar, sedang mengunggah data produk");
			}

			@Override
			public void onUploadingImage(int imgpos) {
				pd = new ProgressDialog(context);
				pd.setTitle("Unggah Produk");
				pd.setMessage("Tunggu sebentar, sedang mengunggah gambar ke "
						+ imgpos);
				pd.setCancelable(true);
				pd.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						task.cancel();
					}
				});

				pd.setIndeterminate(false);
				pd.show();
			}

			@Override
			public void onSuccess(String id) {
				pd.dismiss();
				UploadProductEditor.deleteDraftedProduct(source);
				Toast.makeText(context, "Produk berhasil diunggah",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(Exception ex) {
				pd.dismiss();
				Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT)
						.show();
			}
		});
		task.execute();
	}

	private void saveProduct() {
		DraftedLocalProduct p = compile();
		UploadProductEditor.saveDraftedProduct(p, source);
		Toast.makeText(context, "Draft disimpan", Toast.LENGTH_SHORT).show();
	}
}
