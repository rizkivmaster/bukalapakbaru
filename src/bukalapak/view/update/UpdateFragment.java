package bukalapak.view.update;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import listener.APIListener;
import model.business.Attribute;
import model.business.CheckableAttribute;
import model.business.Credential;
import model.business.CredentialEditor;
import model.business.DraftedEditedProduct;
import model.business.OnlineProduct;
import model.business.ProductCondition;
import model.business.RadioAttribute;
import model.business.SelectableAttribute;
import model.business.StringAttribute;
import tools.ImageIDParser;
import tools.LocalImageManager;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
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
import api.CreateImage;
import api.ReadAttributes;
import api.ReadProduct;
import api.UpdateProduct;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.bukalapakdummy.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import dialog.ProgressDialogManager;

public class UpdateFragment extends SherlockActivity {
	protected static final int PICK_FROM_SOURCE = 0;
	private static final int PICK_CROP = 1;
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
	private ArrayList<String> imageIDs;
	private ArrayList<String> removedImageIDs;
	private ArrayList<File> localImagePaths;
	private DisplayImageOptions options;

	public UpdateFragment() {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// TODO Auto-generated method stub
		context = this;
		credential = CredentialEditor.loadCredential(this);
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisc(true).displayer(new RoundedBitmapDisplayer(50))
				.build();
		attribs = new HashMap<String, Attribute>();
		specs = new HashMap<View, Attribute>();
		setContentView(R.layout.view_product_upload_main);

		len = (LinearLayout) findViewById(R.id.listSpecs);
		listImages = (LinearLayout) findViewById(R.id.listImages);

		// untuk multiplechoice
		imageIDs = new ArrayList<String>();
		removedImageIDs = new ArrayList<String>();
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
				if (localImagePaths.size() > 0) {
					imageIDs.clear();
					uploadImage(localImagePaths, 0);
				} else {
					uploadProduct();
				}
			}
		});
		image_select.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listImages.getChildCount() < 5) {
					showImageSelection();
				} else {
					Toast.makeText(context, "Gambar tidak boleh lebih dari 5",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		registerForContextMenu(image_select);
		registerForContextMenu(kategori);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle("Ubah Produk");

		loadEditedProduct();
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
			} else if (attribute instanceof RadioAttribute) {
				final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						context, android.R.layout.simple_spinner_item);
				ArrayList<String> list = ((RadioAttribute) attribute)
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
			} else if (attribute instanceof CheckableAttribute) {
				Set<String> list = ((CheckableAttribute) attribute)
						.getOptions();
				Set<String> values = hashMap.get(attribute.getFieldName());
				for (String string : list) {
					CheckBox c = new CheckBox(context);
					c.setText(string);
					if (values.contains(string))
						c.setChecked(true);
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

	private void addImage(Bitmap b) throws Exception {
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

	private void addImage(final String url) {
		final String imgID = ImageIDParser.parse(url);
		LayoutInflater inflater = LayoutInflater.from(context);
		final View view = inflater.inflate(R.layout.view_product_upload_image,
				null);
		ImageLoader imageLoader = ImageLoader.getInstance();
		ImageSize targetSize = new ImageSize(200, 200); // result Bitmap will be
														// fit to this size
		imageLoader.loadImage(url, targetSize, options,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View vw,
							Bitmap loadedImage) {
						ImageView imgview = (ImageView) view
								.findViewById(R.id.thumb);
						imgview.setImageBitmap(loadedImage);
					}
				});
		final Button btn = (Button) view.findViewById(R.id.controlBtn);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				listImages.removeView(view);
				removedImageIDs.add(imgID);
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

	private DraftedEditedProduct compile() {
		DraftedEditedProduct product = new DraftedEditedProduct();
		product.setId(productID);
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
		product.setImgCodes(imageIDs);
		product.setRemovedImageCodes(removedImageIDs);
		return product;
	}

	private void uploadImage(final List<File> list, final int position) {
		Bitmap b = LocalImageManager.loadImage(list.get(position));
		CreateImage task = new CreateImage(context, credential, b);
		task.setAPIListener(new APIListener<String>() {
			ProgressDialogManager dialog = new ProgressDialogManager(
					"Unggah Gambar", context);

			@Override
			public void onSuccess(String res) {
				dialog.successProgress(null);
				imageIDs.add(res);
				int newPosition = position + 1;
				if (newPosition < list.size()) {
					uploadImage(list, newPosition);
				} else {
					uploadProduct();
				}
			}

			@Override
			public void onFailure(Exception e) {
				dialog.failedProgress(e);
			}

			@Override
			public void onExecute() {
				dialog.startProgress(null);
			}
		});
		task.execute();
	}

	private void uploadProduct() {
		DraftedEditedProduct p = compile();
		final UpdateProduct task = new UpdateProduct(context, credential, p);
		task.setAPIListener(new APIListener<String>() {
			ProgressDialogManager dialog = new ProgressDialogManager(
					"Ubah Produk", context);

			@Override
			public void onSuccess(String res) {
				dialog.successProgress("Perubahan berhasil");
				finish();
			}

			@Override
			public void onFailure(Exception e) {
				dialog.failedProgress(e);
			}

			@Override
			public void onExecute() {
				dialog.startProgress(null);
			}
		});
		task.execute();
	}

	private void loadEditedProduct() {
		productID = getIntent().getStringExtra("id");
		ReadProduct task = new ReadProduct(context, credential, productID);
		task.setAPIListener(new APIListener<OnlineProduct>() {
			ProgressDialogManager dialog1 = new ProgressDialogManager(
					"Ambil Produk", context);

			@Override
			public void onSuccess(final OnlineProduct res) {
				dialog1.successProgress(null);
				for (String url : res.getImagesURL()) {
					addImage(url);
				}
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
				stokBarang.setText(res.getStock() + "");
				deskripsiBarang.setText(res.getDescription());
				unggah = (Button) findViewById(R.id.product_upload_save_button);
				city_select = (Button) findViewById(R.id.pilihdelivery_button);
				ReadAttributes task = new ReadAttributes(context, credential,
						res.getCategoryID());
				task.setAPIListener(new APIListener<ArrayList<Attribute>>() {
					ProgressDialogManager dialog2 = new ProgressDialogManager(
							"Sinkronisasi", context);

					@Override
					public void onSuccess(ArrayList<Attribute> attr) {
						dialog2.successProgress(null);
						createSpecs(attr, res.getSpecs());
					}

					@Override
					public void onFailure(Exception e) {
						dialog2.failedProgress(e);
						finish();
					}

					@Override
					public void onExecute() {
						dialog2.startProgress(null);
					}
				}).execute();
			}

			@Override
			public void onFailure(Exception e) {
				dialog1.failedProgress(e);
				finish();
			}

			@Override
			public void onExecute() {
				dialog1.startProgress(null);
			}
		});
		task.execute();
	}
}
