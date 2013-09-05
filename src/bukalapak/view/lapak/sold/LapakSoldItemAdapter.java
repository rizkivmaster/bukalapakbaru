package bukalapak.view.lapak.sold;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import listener.APIListener;
import model.business.Credential;
import model.business.CredentialEditor;
import model.business.OnlineProduct;
import model.business.SoldProduct;
import pagination.PageLoaderListener;
import pagination.UpdateableAdapter;
import pagination.pageloader.LapakTidakDijualLoader;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import api.DeleteProduct;
import api.RelistProduct;
import bukalapak.view.MainWindowActivity;
import bukalapak.view.lapak.available.LapakDijualFragment;
import bukalapak.view.product.ProductDetail;

import com.bukalapakdummy.R;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class LapakSoldItemAdapter extends BaseAdapter implements Filterable,
		UpdateableAdapter<SoldProduct> {
	Context context;
	Credential credential;
	private ArrayList<SoldProduct> productList;
	private ArrayList<SoldProduct> showedData;
	HashMap<String, Bitmap> bitmaps;
	HashMap<String, Boolean> checked;
	LayoutInflater inflater;
	DisplayImageOptions options;
	ImageLoaderConfiguration config;
	private boolean showCheckboxes;
	private SlidingFragmentActivity ra;
	private Fragment fragment;
	private LapakTidakDijualLoader loader;

	public LapakSoldItemAdapter(Context c, Fragment r) {
		fragment = r;
		context = c;
		credential = CredentialEditor.loadCredential(context);
		ra = (SlidingFragmentActivity) fragment.getActivity();
		inflater = LayoutInflater.from(context);
		showCheckboxes = false;
		productList = new ArrayList<SoldProduct>();
		showedData = new ArrayList<SoldProduct>();
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisc(true).displayer(new RoundedBitmapDisplayer(50))
				.build();

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return showedData.size();
	}

	@Override
	public Object getItem(int arg0) {
		return showedData.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return showedData.size();
	}

	@Override
	public View getView(final int position, View v, ViewGroup parent) {
		// TODO Auto-generated method stub
		final LapakViewHolder holder;
		// final MessageLine message = (MessageLine) getItem(arg0);
		if (v == null) {
			LayoutInflater li = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.view_product_list_grid_item, parent, false);
			holder = new LapakViewHolder();
			holder.prodIcon = (ImageView) v.findViewById(R.id.item_image_lapak);
			holder.prodName = (TextView) v.findViewById(R.id.title);
			holder.prodPrice = (TextView) v.findViewById(R.id.price);
			holder.prodStock = (TextView) v.findViewById(R.id.stock);
			holder.check = (CheckBox) v.findViewById(R.id.checkBox1);

			v.setTag(holder);
		} else {
			holder = (LapakViewHolder) v.getTag();
		}

		final SoldProduct p = (SoldProduct) getItem(position);
		ImageLoader imageLoader = ImageLoader.getInstance();

		ImageSize targetSize = new ImageSize(200, 200); // result Bitmap will be
														// fit to this size
		imageLoader.loadImage(p.getImagesURL().get(0), targetSize, options,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						holder.prodIcon.setImageBitmap(loadedImage);
					}
				});
		holder.prodName.setText(p.getName());
		holder.prodPrice.setText("Rp " + p.getPrice());
		holder.prodStock.setText(p.getStock() + " pcs");
		holder.check.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				checked.put(p.getId(), ((CheckBox) arg0).isChecked());
			}
		});
		if (showCheckboxes) {
			holder.check.setVisibility(CheckBox.VISIBLE);
			if (checked.containsKey(p.getId())) {
				holder.check.setChecked(checked.get(p.getId()));
			} else {
				holder.check.setChecked(false);
			}
			v.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					holder.check.setChecked(!holder.check.isChecked());
					checked.put(p.getId(), holder.check.isChecked());
				}
			});

			v.setOnLongClickListener(null);
		} else {
			holder.check.setVisibility(CheckBox.INVISIBLE);
			v.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(context, ProductDetail.class);
					i.putExtra("id", p.getId());
					context.startActivity(i);
				}
			});

			v.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					showCheckboxes();
					boolean isChecked = false;
					if (checked.containsKey(p.getId())) {
						isChecked = checked.get(p.getId());
					}
					holder.check.setChecked(isChecked);
					notifyDataSetChanged();
					ra.startActionMode(((LapakTidakDijualFragment) fragment)
							.getCallback());
					return false;
				}
			});

		}

		return v;
	}

	public void showCheckboxes() {
		this.showCheckboxes = true;
		checked = new HashMap<String, Boolean>();
		notifyDataSetChanged();
	}

	public void dismissCheckbox() {
		this.showCheckboxes = false;
		if (checked != null)
			checked = new HashMap<String, Boolean>();
		notifyDataSetChanged();
	}

	public void deleteProduct() {
		final ArrayList<OnlineProduct> list = new ArrayList<OnlineProduct>();
		for (int ii = 0; ii < productList.size(); ii++) {
			if (checked.containsKey(productList.get(ii).getId())
					&& checked.get(productList.get(ii).getId()))
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
				pd.dismiss();
				if (position + 1 < onList.size()) {
					deleteProduct(onList, position + 1);
				} else {
					dismissCheckbox();
					((MainWindowActivity) fragment.getActivity()).refreshPage();
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
		for (int ii = 0; ii < productList.size(); ii++) {
			if (checked.containsKey(productList.get(ii).getId())
					&& checked.get(productList.get(ii).getId()))
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
				pd.dismiss();
				if (position + 1 < onList.size()) {
					relistProduct(onList, position + 1);
				} else {
					dismissCheckbox();
					((MainWindowActivity) fragment.getActivity()).refreshPage();
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
				pd.setTitle("Relist Barang");
				pd.setMessage("Harap menunggu...");
				pd.setIndeterminate(false);
				pd.setCancelable(true);
				pd.show();
			}
		});
		task.execute();

	}

	

	@Override
	public Filter getFilter() {
		return new Filter() {

			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				showedData.clear();
				ArrayList<SoldProduct> result = (ArrayList<SoldProduct>) results.values;
				showedData.addAll(result);
				notifyDataSetChanged();
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();
				if (constraint == null || constraint.length() <= 0) {
					results.values = productList;
					results.count = productList.size();
				} else {

					ArrayList<SoldProduct> result = new ArrayList<SoldProduct>();
					for (int ii = 0; ii < productList.size(); ii++) {
						if (productList.get(ii).getName().contains(constraint)) {
							result.add(productList.get(ii));
						}
					}
					results.values = result;
					results.count = result.size();
				}
				return results;
			}
		};
	}

	@Override
	public void setElements(ArrayList<SoldProduct> list) {
		productList.clear();
		productList.addAll(list);
		getFilter().filter("");
	}

	@Override
	public ArrayList<SoldProduct> getElements() {
		return productList;
	}

	static class LapakViewHolder {
		ImageView prodIcon;
		TextView prodName;
		TextView prodPrice;
		TextView prodStock;
		CheckBox check;
	}

}
