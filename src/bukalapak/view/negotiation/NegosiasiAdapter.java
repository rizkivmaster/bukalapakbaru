package bukalapak.view.negotiation;

import java.util.ArrayList;

import pagination.UpdateableAdapter;

import listener.APIListener;
import model.business.AvailableNegotiation;
import model.business.BrokenNegotiation;
import model.business.Credential;
import model.business.CredentialEditor;
import model.business.Negotiation;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import api.AcceptNegotiation;
import api.RejectNegotiation;

import com.bukalapakdummy.R;

public class NegosiasiAdapter extends BaseAdapter implements Filterable,
		UpdateableAdapter<Negotiation> {

	private Context context;
	private Credential credential;

	private ArrayList<Negotiation> negotiations;
	private ArrayList<Negotiation> showedData;

	private LayoutInflater inflater;

	public NegosiasiAdapter(Context c) {
		negotiations = new ArrayList<Negotiation>();
		showedData = new ArrayList<Negotiation>();
		context = c;
		credential = CredentialEditor.loadCredential(context);
		inflater = LayoutInflater.from(c);
	}

	@Override
	public int getCount() {
		return showedData.size();
	}

	@Override
	public Object getItem(int position) {
		return showedData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Negotiation negotiation = (Negotiation) getItem(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.nego_list_item, null);
		}
		ImageView img = (ImageView) convertView
				.findViewById(R.id.image_product_nego);
		TextView jumlahbeli = (TextView) convertView
				.findViewById(R.id.val_stocknego);
		TextView buyer = (TextView) convertView.findViewById(R.id.val_pembeli);
		TextView hargaasli = (TextView) convertView
				.findViewById(R.id.val_hargaasli);
		TextView harganego = (TextView) convertView
				.findViewById(R.id.val_harganego);
		TextView namaProduk = (TextView) convertView
				.findViewById(R.id.title_product_nego);
		Button terimaNego = (Button) convertView
				.findViewById(R.id.btn_aksi_terima);
		Button tolakNego = (Button) convertView
				.findViewById(R.id.btn_aksi_tolak);
		switch (negotiation.getStatus()) {
		case ACCEPTED:
			terimaNego.setActivated(false);
			terimaNego.setEnabled(false);
			terimaNego.setTextColor(Color.GREEN);
			tolakNego.setVisibility(Button.INVISIBLE);
			break;
		case REJECTED:
			tolakNego.setActivated(false);
			tolakNego.setTextColor(Color.RED);
			tolakNego.setEnabled(false);
			terimaNego.setVisibility(Button.INVISIBLE);
			break;
		case WAITING:
			terimaNego.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					acceptNego(negotiation);
				}
			});
			tolakNego.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					rejectNego(negotiation);
				}
			});
			break;
		}
		if (negotiation instanceof AvailableNegotiation) {
			hargaasli.setVisibility(Button.VISIBLE);
			hargaasli.setText(((AvailableNegotiation) negotiation)
					.getNormalPrice() + "");
			namaProduk.setText(((AvailableNegotiation) negotiation)
					.getProductName() + "");
			namaProduk.setTextColor(Color.BLACK);
		} else if (negotiation instanceof BrokenNegotiation) {
			hargaasli.setVisibility(Button.INVISIBLE);
			namaProduk.setText("Barang telah dihapus");
			namaProduk.setTextColor(Color.RED);
		}
		jumlahbeli.setText(negotiation.getQuantity() + "");
		buyer.setText(negotiation.getBuyerName() + "");
		harganego.setText(negotiation.getNegoPrice() + "");
		return convertView;
	}

	private void acceptNego(Negotiation n) {
		AcceptNegotiation task = new AcceptNegotiation(context, credential, n);
		task.setAPIListener(new APIListener<Negotiation>() {
			ProgressDialog pd = new ProgressDialog(context);

			@Override
			public void onSuccess(Negotiation res) {
				pd.dismiss();
				Toast.makeText(context, "Berhasil diubah", Toast.LENGTH_SHORT)
						.show();
			}

			@Override
			public void onFailure(Exception e) {
				pd.dismiss();
			}

			@Override
			public void onExecute() {
				pd.setTitle("Terima Negosiasi");
				pd.setMessage("Harap menunggu...");
				pd.setIndeterminate(true);
				pd.setCancelable(false);
				pd.show();
			}
		});
		task.execute();
	}

	private void rejectNego(Negotiation n) {
		RejectNegotiation task = new RejectNegotiation(context, credential, n);
		task.setAPIListener(new APIListener<Negotiation>() {
			ProgressDialog pd = new ProgressDialog(context);

			@Override
			public void onSuccess(Negotiation res) {
				pd.dismiss();
				Toast.makeText(context, "Berhasil diubah", Toast.LENGTH_SHORT)
						.show();
			}

			@Override
			public void onFailure(Exception e) {
				pd.dismiss();
			}

			@Override
			public void onExecute() {
				pd.setTitle("Tolak Negosiasi");
				pd.setMessage("Harap menunggu...");
				pd.setIndeterminate(true);
				pd.setCancelable(false);
				pd.show();
			}
		});
		task.execute();
	}

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		return new Filter() {

			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				showedData.clear();
				ArrayList<Negotiation> result = (ArrayList<Negotiation>) results.values;
				showedData.addAll(result);
				notifyDataSetChanged();
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();
				if (constraint == null || constraint.length() <= 0) {
					results.values = negotiations;
					results.count = negotiations.size();
				} else {

					ArrayList<Negotiation> result = new ArrayList<Negotiation>();
					for (int ii = 0; ii < negotiations.size(); ii++) {
						if (negotiations.get(ii).getBuyerName()
								.contains(constraint)
								|| negotiations.get(ii).getBuyerUsername()
										.contains(constraint)
								|| (negotiations.get(ii) instanceof AvailableNegotiation && ((AvailableNegotiation) negotiations
										.get(ii)).getProductName().contains(
										constraint))) {
							result.add(negotiations.get(ii));
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
	public void setElements(ArrayList<Negotiation> list) {
		negotiations.clear();
		negotiations.addAll(list);
		getFilter().filter("");
	}

	@Override
	public ArrayList<Negotiation> getElements() {
		return negotiations;
	}
}
