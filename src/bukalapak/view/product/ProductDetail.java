package bukalapak.view.product;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import listener.APIListener;
import model.business.Attribute;
import model.business.AvailableProduct;
import model.business.CheckableAttribute;
import model.business.Credential;
import model.business.CredentialEditor;
import model.business.OnlineProduct;
import model.business.Product;
import model.business.ProductCondition;
import model.business.RadioAttribute;
import model.business.SelectableAttribute;
import model.business.SoldProduct;
import model.business.StringAttribute;
import urlshortener.URLShortener;
import urlshortener.URLShortenerListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import api.ReadAttributes;
import api.ReadProduct;
import api.RelistProduct;
import api.SetSoldProduct;
import bukalapak.view.update.UpdateFragment;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.bukalapakdummy.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class ProductDetail extends SherlockActivity {

	private Credential credential;
	private Context context;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private String id;
	

	public ProductDetail() {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// TODO Auto-generated method stub
		context = this;
		credential = CredentialEditor.loadCredential(context);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle("Detail Produk");
		id = getIntent().getStringExtra("id");
		if (options == null) {
			options = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.ic_stub)
					.showImageForEmptyUri(R.drawable.ic_empty)
					.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
					.cacheOnDisc(true)
					.displayer(new RoundedBitmapDisplayer(50)).build();
		}
		if (imageLoader == null)
			imageLoader = ImageLoader.getInstance();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(id!=null)
		{
			retrieveProduct(id);
		}
	}

	private void retrieveProduct(String id) {
		ReadProduct task = new ReadProduct(context, credential, id);
		APIListener<OnlineProduct> listener = new APIListener<OnlineProduct>() {
			ProgressDialog pd;

			@Override
			public void onSuccess(OnlineProduct res) {
				pd.dismiss();
				viewProduct(res);
			}

			@Override
			public void onFailure(Exception e) {
				pd.dismiss();
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT)
						.show();
				finish();
			}

			@Override
			public void onExecute() {
				pd = new ProgressDialog(context);
				pd.setTitle("Mengambil Produk");
				pd.setMessage("Harap menunggu");
				pd.setIndeterminate(true);
				pd.setCancelable(false);
				pd.show();
			}
		};
		task.setAPIListener(listener);
		task.execute();
	}

	private void revealAttributes(final Product product) {
		ReadAttributes task = new ReadAttributes(context, credential,
				product.getCategoryID());
		task.setAPIListener(new APIListener<ArrayList<Attribute>>() {
			ProgressDialog pd;

			@Override
			public void onSuccess(ArrayList<Attribute> attr) {
				pd.dismiss();
				createSpecs(attr, product.getSpecs());
			}

			@Override
			public void onFailure(Exception e) {
				pd.dismiss();
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT)
						.show();
				finish();
			}

			@Override
			public void onExecute() {
				pd = new ProgressDialog(context);
				pd.setTitle("Edit Produk");
				pd.setMessage("Sedang sinkronisasi data produk...");
				pd.setIndeterminate(true);
				pd.setCancelable(true);
				pd.show();
			}
		}).execute();
	}

	private void viewProduct(final OnlineProduct product) {
		setContentView(R.layout.view_product_list_detail);
		final ImageView imageView = (ImageView) findViewById(R.id.desc_img);
		final Button shareButton = (Button) findViewById(R.id.btn_share);
		final Button editButton = (Button) findViewById(R.id.btn_editbarang);
		final Button setStatus = (Button) findViewById(R.id.btn_setterjual);
		TextView productName = (TextView) findViewById(R.id.desc_namaproduk);
		TextView productPrice = (TextView) findViewById(R.id.desc_price);
		TextView productCategory = (TextView) findViewById(R.id.val_desckategori);
		TextView productCondition = (TextView) findViewById(R.id.val_desckondisi);
		TextView productDesc = (TextView) findViewById(R.id.desc_deskripsibarang);

		editButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(ProductDetail.this,
						UpdateFragment.class);
				intent.putExtra("id", product.getId());
				startActivity(intent);

			}
		});

		ImageSize targetSize = new ImageSize(500, 500); // result Bitmap will be
														// fit to this size
		imageLoader.loadImage(product.getImagesURL().get(0), targetSize,
				options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(final String imageUri,
							View view, final Bitmap loadedImage) {
						imageView.setImageBitmap(loadedImage);
						shareButton
								.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View arg0) {
										URLShortener task = new URLShortener(
												context, credential, product
														.getURL());
										task.setListener(new URLShortenerListener() {
											ProgressDialog pd = null;

											@Override
											public void onSuccess(String url) {
												pd.dismiss();
												Uri uri = getImageUri(context,
														loadedImage);
												goToShareClient(product, url,
														uri);
											}

											@Override
											public void onFailure(Exception e) {
												pd.dismiss();
											}

											@Override
											public void onExecute() {
												pd = new ProgressDialog(context);
												pd.setTitle("Mendapatkan Tautan");
												pd.setMessage("Harap menunggu");
												pd.setIndeterminate(true);
												pd.setCancelable(false);
												pd.show();
											}
										});
										// //
										// // class GetTinyURL
										// // extends
										// // AsyncTask<String, String, String>
										// {
										// // ProgressDialog pd = null;
										// //
										// // @Override
										// // protected void onPreExecute() {
										// // super.onPreExecute();
										// // pd = new ProgressDialog(context);
										// // pd.setTitle("Mendapatkan Tautan");
										// // pd.setMessage("Harap menunggu");
										// // pd.setIndeterminate(true);
										// // pd.setCancelable(false);
										// // pd.show();
										// // }
										// //
										// // @Override
										// // protected String doInBackground(
										// // String... arg0) {
										// // String tinyUrl = "";
										// // String urlString = T_URL
										// // + arg0[0];
										// //
										// // try {
										// // URL url = new URL(urlString);
										// //
										// // BufferedReader in = new
										// BufferedReader(
										// // new InputStreamReader(
										// // url.openStream()));
										// // String str;
										// //
										// // while ((str = in.readLine()) !=
										// null) {
										// // tinyUrl += str;
										// // }
										// // in.close();
										// // } catch (Exception e) {
										// // Log.e(LOG_TAG,
										// // "Can not create an tinyurl link",
										// // e);
										// // }
										// // return tinyUrl;
										// // }
										// //
										// // @Override
										// // protected void onPostExecute(
										// // String result) {
										// // super.onPostExecute(result);
										// // pd.dismiss();
										// // Intent shareIntent =
										// findTwitterClient();
										// // shareIntent
										// // .putExtra(
										// // Intent.EXTRA_TEXT,
										// //
										// "Silakan cek jualan terbaru saya, "
										// // + product
										// // .getName()
										// // + " di "
										// // + result);
										// // Uri uri = getImageUri(context,
										// // loadedImage);
										// // shareIntent.putExtra(
										// // Intent.EXTRA_STREAM,
										// // uri);
										// // startActivity(Intent
										// // .createChooser(
										// // shareIntent,
										// // "Share"));
										// // }
										// // }
										// // ;
										// new GetTinyURL().execute(product
										// .getURL());
										task.execute();
									}
								});
					}
				});
		productName.setText(product.getName());
		productPrice.setText("Rp " + product.getPrice() + ",-");
		productCategory.setText(product.getCategory());
		if (product.getCondition() == ProductCondition.NEW) {
			productCondition.setText("Baru");
		} else if (product.getCondition() == ProductCondition.SECONDHAND) {
			productCondition.setText("Bekas");
		}
		if (product instanceof AvailableProduct) {
			setStatus.setText("Set Terjual");
			setStatus.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					setSold((AvailableProduct) product);
				}
			});
		} else if (product instanceof SoldProduct) {
			setStatus.setText("Set Dijual");
			setStatus.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					setAvailable((SoldProduct) product);
				}
			});
		}
		productDesc.setText(product.getDescription());

		revealAttributes(product);

	}

	private void createSpecs(ArrayList<Attribute> attr,
			HashMap<String, Set<String>> hashMap) {

		LinearLayout len = (LinearLayout) findViewById(R.id.listSpecs);
		for (Attribute attribute : attr) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View view = inflater.inflate(
					R.layout.view_product_list_detail_field_spec, null);
			TextView fieldText = (TextView) view
					.findViewById(R.id.lbl_desckategori);
			TextView valueText = (TextView) view
					.findViewById(R.id.val_desckategori);
			fieldText.setText(attribute.getDisplayName());
			if (attribute instanceof StringAttribute) {
				Set<String> value = hashMap.get(attribute.getFieldName());
				for (String string : value) {
					valueText.setText(string);
				}
			} else if (attribute instanceof SelectableAttribute) {
				Set<String> value = hashMap.get(attribute.getFieldName());
				for (String string : value) {
					valueText.setText(string);
				}

			} else if (attribute instanceof RadioAttribute) {
				Set<String> value = hashMap.get(attribute.getFieldName());
				for (String string : value) {
					valueText.setText(string);
				}

			} else if (attribute instanceof CheckableAttribute) {
				Set<String> values = hashMap.get(attribute.getFieldName());
				String temp = "";
				for (String string : values) {
					if (temp.length() > 0) {
						temp += "," + string;
					} else {
						temp = string;
					}
					valueText.setText(temp);
				}
			}

			len.addView(view);
		}
		len.requestLayout();
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

	// private Intent findTwitterClient() {
	// final String[] twitterApps = {
	// // package // name - nb installs (thousands)
	// "com.twitter.android", // official - 10 000
	// "com.twidroid", // twidroid - 5 000
	// "com.handmark.tweetcaster", // Tweecaster - 5 000
	// "com.thedeck.android" }; // TweetDeck - 5 000 };
	// Intent tweetIntent = new Intent(android.content.Intent.ACTION_SEND);
	// tweetIntent.setType("image/jpeg");
	// final PackageManager packageManager = getPackageManager();
	// List<ResolveInfo> list = packageManager.queryIntentActivities(
	// tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);
	//
	// for (int i = 0; i < twitterApps.length; i++) {
	// for (ResolveInfo resolveInfo : list) {
	// String p = resolveInfo.activityInfo.packageName;
	// if (p != null && p.startsWith(twitterApps[i])) {
	// tweetIntent.setPackage(p);
	// return tweetIntent;
	// }
	// }
	// }
	// return null;
	//
	// }

	private Uri getImageUri(Context inContext, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = Images.Media.insertImage(inContext.getContentResolver(),
				inImage, "Title", null);
		return Uri.parse(path);
	}

	private void goToShareClient(Product product, String url, Uri image) {
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		shareIntent.setType("image/jpeg");
		shareIntent.putExtra(Intent.EXTRA_TEXT,
				"Silakan cek jualan terbaru saya, " + product.getName()
						+ " di " + url);
		shareIntent.putExtra(Intent.EXTRA_STREAM, image);
		// startActivity(Intent.createChooser(shareIntent, "Share"));
		startActivity(shareIntent);
	}

	private void setSold(AvailableProduct product) {
		SetSoldProduct task = new SetSoldProduct(context, credential,
				(AvailableProduct) product);
		task.setAPIListener(new APIListener<String>() {
			ProgressDialog pd;

			@Override
			public void onSuccess(String res) {
				pd.dismiss();
				Toast.makeText(context, "Perubahan berhasil",
						Toast.LENGTH_SHORT).show();
				retrieveProduct(res);
			}

			@Override
			public void onFailure(Exception e) {
				Toast.makeText(context, "Perubahan gagal", Toast.LENGTH_SHORT)
						.show();
				pd.dismiss();
			}

			@Override
			public void onExecute() {
				pd = new ProgressDialog(context);
				pd.setTitle("Set Terjual");
				pd.setMessage("Harap Menunggu");
				pd.setIndeterminate(true);
				pd.setCancelable(false);
				pd.show();
			}
		});
		task.execute();
	}

	private void setAvailable(SoldProduct product) {
		RelistProduct task = new RelistProduct(context, credential, product);
		task.setAPIListener(new APIListener<String>() {
			ProgressDialog pd;

			@Override
			public void onSuccess(String res) {
				pd.dismiss();
				Toast.makeText(context, "Perubahan berhasil",
						Toast.LENGTH_SHORT).show();
				retrieveProduct(res);
			}

			@Override
			public void onFailure(Exception e) {
				Toast.makeText(context, "Perubahan gagal", Toast.LENGTH_SHORT)
						.show();
				pd.dismiss();
			}

			@Override
			public void onExecute() {
				pd = new ProgressDialog(context);
				pd.setTitle("Set Terjual");
				pd.setMessage("Harap Menunggu");
				pd.setIndeterminate(true);
				pd.setCancelable(false);
				pd.show();
			}
		});
		task.execute();
	}

}
