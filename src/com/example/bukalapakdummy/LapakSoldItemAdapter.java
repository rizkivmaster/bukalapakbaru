package com.example.bukalapakdummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import listener.APIListener;
import model.business.AvailableProduct;
import model.business.Credential;
import model.business.CredentialEditor;
import model.business.OnlineProduct;
import model.business.SoldProduct;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;

import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import api.DeleteProduct;
import api.ListLapakTidakDijual;
import api.RelistProduct;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.bukalapakdummy.R;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class LapakSoldItemAdapter extends BaseAdapter {
	Context context;
	Credential credential;
	private ArrayList<SoldProduct> productList;
	HashMap<String, Bitmap> bitmaps;
	HashMap<String, Boolean> checks;
	LayoutInflater inflater;
	DisplayImageOptions options;
	ImageLoaderConfiguration config;
	private boolean showCheckboxes;
	private boolean[] checked;
	private SlidingFragmentActivity ra;
	private Fragment parent;

	public LapakSoldItemAdapter(Context c, Fragment r) {
		parent = r;
		context = c;
		credential = CredentialEditor.loadCredential(context);
		ra = (SlidingFragmentActivity) parent.getActivity();
		inflater = LayoutInflater.from(context);
		showCheckboxes = false;
		productList = new ArrayList<SoldProduct>();
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisc(true).displayer(new RoundedBitmapDisplayer(50))
				.build();
	}

	public void setList(ArrayList<SoldProduct> list) {
		productList = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return productList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return productList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		final OnlineProduct p = productList.get(arg0);
		if (arg1 == null) {
			arg1 = inflater.inflate(R.layout.view_product_list_grid_item, null);
		}

		final ImageView prodIcon = (ImageView) arg1
				.findViewById(R.id.item_image_lapak);

		ImageLoader imageLoader = ImageLoader.getInstance();

		ImageSize targetSize = new ImageSize(200, 200); // result Bitmap will be
														// fit to this size
		imageLoader.loadImage(p.getImagesURL().get(0), targetSize, options,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						prodIcon.setImageBitmap(loadedImage);
					}
				});
		TextView prodName = (TextView) arg1.findViewById(R.id.title);
		prodName.setText(p.getName());

		TextView prodPrice = (TextView) arg1.findViewById(R.id.price);
		prodPrice.setText("Rp " + p.getPrice());

		TextView prodStock = (TextView) arg1.findViewById(R.id.stock);
		prodStock.setText(p.getStock() + " pcs");

		// TextView prodStatus = (TextView) arg1.findViewById(R.id.status);
		// if (p instanceof AvailableProduct) {
		// prodStatus.setText("dijual");
		// prodStatus.setBackgroundColor(Color.GREEN);
		// } else {
		// prodStatus.setText("terjual");
		// prodStatus.setBackgroundColor(Color.RED);
		// }

		final CheckBox check = (CheckBox) arg1.findViewById(R.id.checkBox1);
		check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
				if (isChecked) {
					checked[arg0] = true;
				} else {
					checked[arg0] = false;
				}
			}
		});
		if (showCheckboxes) {
			check.setVisibility(CheckBox.VISIBLE);
			arg1.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					checked[arg0] = !checked[arg0];
					check.setChecked(checked[arg0]);
					notifyDataSetChanged();
				}
			});
		} else {
			check.setVisibility(CheckBox.INVISIBLE);
			arg1.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent i = new Intent(context, UpdateFragment.class);
					i.putExtra("id", p.getId());
					context.startActivity(i);
				}
			});

			arg1.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					showCheckboxes();
					checked[arg0] = !checked[arg0];
					check.setChecked(checked[arg0]);
					notifyDataSetChanged();
					ra.startActionMode(((LapakTidakDijualFragment)parent).getCallback());
					return false;
				}
			});

		}


		return arg1;
	}

	public void showCheckboxes() {
		this.showCheckboxes = true;
		checked = new boolean[productList.size()];
		notifyDataSetChanged();
		notifyDataSetInvalidated();
	}

	public void dismissCheckbox() {
		this.showCheckboxes = false;
		if (checked != null)
			checked = new boolean[checked.length];
		notifyDataSetChanged();
		notifyDataSetInvalidated();
	}

	public void deleteProduct() {
		final ArrayList<OnlineProduct> list = new ArrayList<OnlineProduct>();
		for (int ii = 0; ii < checked.length; ii++) {
			if (checked[ii])
				list.add(productList.get(ii));
		}
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle("Hapus");
		alert.setMessage("Apakah Anda yakin ingin menghapus " + list.size()
				+ " barang?");
		alert.setPositiveButton("Ya", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (productList.size() > 0) {
					deleteProduct(list, 0);
				} else {
					Toast.makeText(context, "Tidak ada produk yang dipilih",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		alert.setNegativeButton("Tidak", null);
		alert.show();
	}

	private void deleteProduct(final List<OnlineProduct> onList,
			final int position) {
		OnlineProduct product = onList.get(position);
		final DeleteProduct task = new DeleteProduct(context, credential,
				product);
		task.setAPIListener(new APIListener<String>() {
			ProgressDialog pd = new ProgressDialog(context);

			@Override
			public void onSuccess(String res) {
				if (position + 1 < onList.size()) {
					deleteProduct(onList, position + 1);
				} else {
					pd.dismiss();
					dismissCheckbox();
					refreshView();
					Toast.makeText(context, "Barang berhasil dihapus",
							Toast.LENGTH_SHORT).show();

				}
			}

			@Override
			public void onFailure(Exception e) {
				pd.dismiss();
				Toast.makeText(context, "Barang gagal dihapus",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onExecute() {
				pd.setTitle("Hapus Barang");
				pd.setMessage("Sedang menghapus. Tunggu sebentar...");
				pd.setIndeterminate(false);
				pd.setCancelable(true);
				pd.show();
			}
		});
		task.execute();

	}

	public void relistProduct() {
		final ArrayList<OnlineProduct> list = new ArrayList<OnlineProduct>();
		for (int ii = 0; ii < checked.length; ii++) {
			if (checked[ii])
				list.add(productList.get(ii));
		}
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle("Relist");
		alert.setMessage("Apakah Anda yakin ingin merelist " + list.size()
				+ " barang ?");
		alert.setPositiveButton("Ya", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (productList.size() > 0) {
					relistProduct(list, 0);
				} else {
					Toast.makeText(context, "Tidak ada produk yang dipilih",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		alert.setNegativeButton("Tidak", null);
		alert.show();
	}

	private void relistProduct(final List<OnlineProduct> onList,
			final int position) {
		OnlineProduct product = onList.get(position);
		final RelistProduct task = new RelistProduct(context, credential,
				(SoldProduct) product);
		task.setAPIListener(new APIListener<String>() {
			ProgressDialog pd = new ProgressDialog(context);

			@Override
			public void onSuccess(String res) {
				if (position + 1 < onList.size()) {
					relistProduct(onList, position + 1);
				} else {
					pd.dismiss();
					dismissCheckbox();
					refreshView();
					Toast.makeText(context, "Barang berhasil direlist",
							Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(Exception e) {
				pd.dismiss();
				Toast.makeText(context, "Barang gagal direlist",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onExecute() {
				pd.setTitle("Hapus Barang");
				pd.setMessage("Harap menunggu...");
				pd.setIndeterminate(false);
				pd.setCancelable(true);
				pd.show();
			}
		});
		task.execute();

	}

	public void refreshView() {
		final ListLapakTidakDijual task = new ListLapakTidakDijual(context,
				credential);
		task.setAPIListener(new APIListener<ArrayList<SoldProduct>>() {
			ProgressDialog pd;

			@Override
			public void onFailure(Exception e) {
				pd.cancel();
			}

			@Override
			public void onExecute() {
				pd = new ProgressDialog(context);
				pd.setTitle("Lapak Tidak Dijual");
				pd.setMessage("Harap menunggu...");
				pd.setCancelable(false);
				pd.setIndeterminate(true);
				pd.show();
			}

			@Override
			public void onSuccess(ArrayList<SoldProduct> res) {
				pd.dismiss();
				setList(res);
				notifyDataSetChanged();
			}
		});
		task.execute();
	}

}
