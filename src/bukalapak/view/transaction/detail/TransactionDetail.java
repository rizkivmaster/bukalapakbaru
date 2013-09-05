package bukalapak.view.transaction.detail;

import listener.APIListener;
import model.business.Credential;
import model.business.CredentialEditor;
import model.business.Transaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import api.ReadTransaction;
import bukalapak.view.CurrencyFormatter;
import bukalapak.view.transaction.detail.KirimBarangActivity;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.bukalapakdummy.R;

import dialog.ProgressDialogManager;

public class TransactionDetail extends SherlockActivity {
	private static int ICON_ON_PENDING = R.drawable.payment_statuses_016;
	private static int ICON_ON_PAID = R.drawable.payment_statuses_018;
	private static int ICON_ON_DELIVERED = R.drawable.payment_statuses_019;
	private static int ICON_ON_RECEIVED = R.drawable.payment_statuses_014;
	private static int ICON_ON_REMITTED = R.drawable.payment_statuses_015;
	private static int ICON_ON_REFUNDED = R.drawable.payment_statuses_020;
	Context context;
	Credential credential;
	String id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;
		credential = CredentialEditor.loadCredential(context);
		id = getIntent().getStringExtra("transaction_id");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (id != null && !id.isEmpty()) {
			retrieveTransaction(id);
		}
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

	private void viewTransaction(final Transaction transaction) {

		setContentView(R.layout.transaction_detail);
		TextView transactionID = (TextView) findViewById(R.id.val_transactionid);
		TextView status = (TextView) findViewById(R.id.status_desc);
		TextView hargaPesanan = (TextView) findViewById(R.id.val_hargapesanan);
		TextView biayaKirim = (TextView) findViewById(R.id.val_biayakirim);
		TextView hargaTotal = (TextView) findViewById(R.id.val_hargatotal);
		TextView namaProduk = (TextView) findViewById(R.id.val_namaproduk);
		TextView linkProduk = (TextView) findViewById(R.id.val_linkproduk);
		TextView namaPembeli = (TextView) findViewById(R.id.val_namapembeli);
		TextView usernamePembeli = (TextView) findViewById(R.id.val_usernamepembeli);
		Button kirimBarang = (Button) findViewById(R.id.trans_btnsend);
		kirimBarang.setVisibility(Button.INVISIBLE);
		ImageView statusIcon = (ImageView) findViewById(R.id.status_img);

		transactionID.setText(transaction.getTransactionID());
		switch (transaction.getState()) {
		case DELIVERED:
			statusIcon.setImageResource(ICON_ON_DELIVERED);
			status.setText("DELIVERED");
			break;
		case PAID:
			statusIcon.setImageResource(ICON_ON_PAID);
			status.setText("PAID");
			break;
		case PENDING:
			statusIcon.setImageResource(ICON_ON_PENDING);
			status.setText("PENDING");
			break;
		case RECEIVED:
			statusIcon.setImageResource(ICON_ON_RECEIVED);
			status.setText("RECEIVED");
			break;
		case REFUNDED:
			statusIcon.setImageResource(ICON_ON_REFUNDED);
			status.setText("REFUNDED");
			break;
		case REMITTED:
			statusIcon.setImageResource(ICON_ON_REMITTED);
			status.setText("REMITTED");
			break;
		}
		hargaPesanan.setText(CurrencyFormatter.format(transaction.getAmount()));
		biayaKirim.setText(CurrencyFormatter.format(transaction
				.getShippingFee()));
		hargaTotal.setText(CurrencyFormatter.format(transaction
				.getTotalAmount()));
		namaProduk.setText(transaction.getProductName());
		linkProduk.setText(transaction.getProductURL());
		namaPembeli.setText(transaction.getBuyerName());
		usernamePembeli.setText(transaction.getBuyerUsername());
		for (int ii = 0; ii < transaction.getPossibleActions().size(); ii++) {
			String action = transaction.getPossibleActions().get(ii);
			if (action.equals("deliver")) {
				kirimBarang.setVisibility(View.VISIBLE);
				kirimBarang.setTextColor(Color.BLACK);
				kirimBarang.setText("Kirim Barang");
				kirimBarang.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(context,
								KirimBarangActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("transaction_id", transaction.getId());
						context.startActivity(intent);
					}
				});
			}
		}

	}

	private void retrieveTransaction(String transID) {
		final ReadTransaction task = new ReadTransaction(context, credential, transID);
		task.setAPIListener(new APIListener<Transaction>() {
			ProgressDialogManager manager = new ProgressDialogManager(
					"Ambil Transaksi", context);

			@Override
			public void onSuccess(Transaction transaction) {
				manager.successProgress("");
				viewTransaction(transaction);
			}

			@Override
			public void onFailure(Exception e) {
				manager.failedProgress(e);
			}

			@Override
			public void onExecute() {
				manager.startProgress(task);
			}
		});
		task.execute();
	}

}
