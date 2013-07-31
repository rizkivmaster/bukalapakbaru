package com.example.bukalapakdummy;

import java.util.ArrayList;

import listener.APIListener;
import model.business.AvailableNegotiation;
import model.business.BrokenNegotiation;
import model.business.Credential;
import model.business.CredentialEditor;
import model.business.Negotiation;
import com.bukalapakdummy.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import api.AcceptNegotiation;
import api.RejectNegotiation;

public class NegosiasiAdapter extends BaseAdapter {

	private Context context;
	private Credential credential;

	private ArrayList<Negotiation> negotiations;

	private LayoutInflater inflater;

	public NegosiasiAdapter(ArrayList<Negotiation> n, Context c) {
		negotiations = n;
		context = c;
		credential = CredentialEditor.loadCredential(context);
		inflater = LayoutInflater.from(c);
	}

	@Override
	public int getCount() {
		return negotiations.size();
	}

	@Override
	public Object getItem(int position) {
		return negotiations.get(position);
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
			terimaNego.setTextColor(Color.GREEN);
			tolakNego.setActivated(false);
			tolakNego.setClickable(false);
			break;
		case REJECTED:
			tolakNego.setTextColor(Color.RED);
			terimaNego.setActivated(false);
			terimaNego.setClickable(false);
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
		if(negotiation instanceof AvailableNegotiation)
		{
			hargaasli.setText(((AvailableNegotiation)negotiation).getNormalPrice() + "");
			namaProduk.setText(((AvailableNegotiation)negotiation).getProductName() + "");
		}
		else if(negotiation instanceof BrokenNegotiation)
		{
			hargaasli.setText("Barang telah dihapus");
			hargaasli.setTextColor(Color.RED);
			namaProduk.setText("Barang telah dihapus");
			namaProduk.setTextColor(Color.RED);
		}
		jumlahbeli.setText(negotiation.getQuantity() + "");
		buyer.setText(negotiation.getBuyerName() + "");
		harganego.setText(negotiation.getNegoPrice() + "");
		return convertView;
	}

	private void acceptNego(Negotiation n) {
		AcceptNegotiation task = new AcceptNegotiation(context, credential,n);
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
		RejectNegotiation task = new RejectNegotiation(context, credential,n);
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

}
