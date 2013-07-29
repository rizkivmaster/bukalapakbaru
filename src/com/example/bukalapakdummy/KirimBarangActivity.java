package com.example.bukalapakdummy;

import listener.APIListener;
import model.business.Credential;
import model.business.CredentialEditor;
import model.business.Transaction;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import api.ConfirmShipping;

import com.actionbarsherlock.app.SherlockActivity;
import com.bukalapakdummy.R;

public class KirimBarangActivity extends SherlockActivity {
	TextView lblKirimBarang;
	TextView lblJasaPengiriman;
	TextView lblNoResi;
	EditText inputNomorResi;
	TextView lblKurirLain;
	EditText inputKurirLain;
	TextView ongkosKirimSistem;
	TextView lblOngkosBeda;
	TextView linkResiLengkap;
	TextView notifUnggahResi;
	Button btnSubmit;
	Button btnCancel;
	String transactionID;

	protected Transaction transaksi;
	private Context context;
	private Credential credential;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.transaction_kirimbarang);
		context = this;
		transactionID = getIntent().getStringExtra("transaction_id");
		credential = CredentialEditor.loadCredential(context);

		lblKirimBarang = (TextView) findViewById(R.id.lbl_kirimbarang);
		lblJasaPengiriman = (TextView) findViewById(R.id.lbl_jasapengiriman);
		lblNoResi = (TextView) findViewById(R.id.lbl_nomorresi);
		inputNomorResi = (EditText) findViewById(R.id.txtfield_nomorresi);
		lblKurirLain = (TextView) findViewById(R.id.lbl_namakurir);
		inputKurirLain = (EditText) findViewById(R.id.txtfield_namakurir);
		ongkosKirimSistem = (TextView) findViewById(R.id.lbl_oks);
		lblOngkosBeda = (TextView) findViewById(R.id.lbl_otheroks);
		linkResiLengkap = (TextView) findViewById(R.id.lbl_resilengkap);
		btnSubmit = (Button) findViewById(R.id.btn_submit_kirimbarang);
		btnCancel = (Button) findViewById(R.id.btn_batal_kirimbarang);

		registerForContextMenu(linkResiLengkap);

		linkResiLengkap.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					openContextMenu(linkResiLengkap);
				} catch (ActivityNotFoundException e) {
					// do nothing
				}
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		btnSubmit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				String shippingCode = inputNomorResi.getText().toString();

				String kurirLain = inputKurirLain.getText().toString();
				Transaction transaction = new Transaction();
				transaction.setId(transactionID);

				ConfirmShipping task = new ConfirmShipping(context, credential,
						transaction, shippingCode, kurirLain);

				task.setAPIListener(new APIListener<String>() {
					ProgressDialog pd = new ProgressDialog(context);

					@Override
					public void onSuccess(String res) {
						pd.dismiss();
						Toast.makeText(KirimBarangActivity.this,
								"Konfirmasi dikirim", Toast.LENGTH_SHORT)
								.show();
						finish();

					}

					@Override
					public void onFailure(Exception e) {
						pd.dismiss();
					}

					@Override
					public void onExecute() {
						pd.setTitle("Konfirm Transaksi");
						pd.setMessage("Harap menunggu...");
						pd.setIndeterminate(true);
						pd.setCancelable(false);
						pd.show();
					}
				}).execute();
			}
		});
	}

}
