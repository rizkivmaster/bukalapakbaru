package com.example.bukalapakdummy;

import java.util.ArrayList;

import listener.APIListener;
import model.business.AcceptedNegotiation;
import model.business.Credential;
import model.business.CredentialEditor;
import model.business.Negotiation;
import model.business.RejectedNegotiation;
import model.business.UnflaggedNegotiation;
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

import com.bukalapakdummy.R;

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
		TextView namaProduk = (TextView) convertView.
				findViewById(R.id.title_product_nego);
		Button terimaNego = (Button) convertView.
				findViewById(R.id.btn_aksi_terima);
		Button tolakNego = (Button) convertView.
				findViewById(R.id.btn_aksi_tolak);
		if(negotiation instanceof UnflaggedNegotiation) {
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
		}
		else if(negotiation instanceof AcceptedNegotiation)
		{
			terimaNego.setTextColor(Color.GREEN);
			tolakNego.setActivated(false);
		}
		else if(negotiation instanceof RejectedNegotiation)
		{
			tolakNego.setTextColor(Color.RED);
			terimaNego.setActivated(false);
		}


		jumlahbeli.setText(negotiation.getQuantity()+"");
		buyer.setText(negotiation.getBuyerName()+"");
		hargaasli.setText(negotiation.getNormalPrice()+"");
		harganego.setText(negotiation.getNegoPrice()+"");
		namaProduk.setText(negotiation.getProductName()+"");
		return convertView;
	}
	
	private void acceptNego(Negotiation n)
	{
		AcceptNegotiation task = new AcceptNegotiation(context, credential, (UnflaggedNegotiation) n);
		task.setAPIListener(new APIListener<AcceptedNegotiation>() {
			ProgressDialog pd = new ProgressDialog(context);
			@Override
			public void onSuccess(AcceptedNegotiation res) {
				pd.dismiss();
				Toast.makeText(context, "Berhasil diubah", Toast.LENGTH_SHORT).show();
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
	
	private void rejectNego(Negotiation n)
	{
		RejectNegotiation task = new RejectNegotiation(context, credential, (UnflaggedNegotiation) n);
		task.setAPIListener(new APIListener<RejectedNegotiation>() {
			ProgressDialog pd = new ProgressDialog(context);
			@Override
			public void onSuccess(RejectedNegotiation res) {
				pd.dismiss();
				Toast.makeText(context, "Berhasil diubah", Toast.LENGTH_SHORT).show();
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
