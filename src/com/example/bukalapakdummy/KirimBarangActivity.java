package com.example.bukalapakdummy;

import listener.APIListener;
import model.business.Credential;
import model.business.CredentialEditor;
import model.business.Transaction;

import org.json.JSONException;
import org.json.JSONObject;

import com.bukalapakdummy.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import api.ConfirmShipping;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

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
Context context;
Credential credential;

protected void onCreate(Bundle savedInstanceState) {
	
	super.onCreate(savedInstanceState);
	setContentView(R.layout.transaction_kirimbarang);
	transactionID = getIntent().getStringExtra("transaction_id");
	context = this;
	credential = CredentialEditor.loadCredential(context);
	
	lblKirimBarang = (TextView)findViewById(R.id.lbl_kirimbarang);
	lblJasaPengiriman = (TextView)findViewById (R.id.lbl_jasapengiriman);
	lblNoResi = (TextView)findViewById (R.id.lbl_nomorresi);
	inputNomorResi = (EditText)findViewById(R.id.txtfield_nomorresi);
	lblKurirLain = (TextView)findViewById(R.id.lbl_namakurir);
	inputKurirLain = (EditText)findViewById (R.id.txtfield_namakurir);
	ongkosKirimSistem = (TextView)findViewById (R.id.lbl_oks);
	lblOngkosBeda = (TextView)findViewById (R.id.lbl_otheroks);
	linkResiLengkap = (TextView)findViewById (R.id.lbl_resilengkap);
	btnSubmit = (Button)findViewById (R.id.btn_submit_kirimbarang);
	btnCancel = (Button)findViewById (R.id.btn_batal_kirimbarang);
	
	registerForContextMenu(linkResiLengkap);
	
	linkResiLengkap.setOnClickListener(
			new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
						openContextMenu(linkResiLengkap);
				}
			});
	btnCancel.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			finish();
		}});
	btnSubmit.setOnClickListener(new View.OnClickListener(){
		public void onClick(View arg0){
			String shippingCode = inputNomorResi.getText().toString();
			String kurirLain = inputKurirLain.getText().toString();
			Transaction transaction = new Transaction();
			transaction.setTransactionID(transactionID);
			final ConfirmShipping task = new ConfirmShipping(context, credential, transaction, shippingCode, kurirLain);
			task.setAPIListener(new APIListener<String>() {
				ProgressDialog pd = new ProgressDialog(context);
				@Override
				public void onSuccess(String res) {
					Toast.makeText(KirimBarangActivity.this,
							"Konfirmasi terkirim", Toast.LENGTH_SHORT)
							.show();
					finish();
				}
				
				@Override
				public void onFailure(Exception e) {
					pd.cancel();
				}
				
				@Override
				public void onExecute() {
					pd.setTitle("Konfirmasi Pengiriman");
					pd.setMessage("Harap menunggu...");
					pd.setIndeterminate(true);
					pd.setCancelable(false);
					pd.show();
				}
			});
			AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setTitle("Konfirm pengiriman");
			alert.setMessage("Apakah Anda yakin ingin konfirmasi?");
			alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					task.execute();
				}
			});
			alert.setNegativeButton("Tidak", null);
			alert.show();

		}
	});
	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	
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

private JSONObject compileInfo()throws JSONException {
	JSONObject payment_shipping = new JSONObject();
	payment_shipping.put("shipping_code", inputNomorResi.getText().toString());
	
	return payment_shipping;
	}
}