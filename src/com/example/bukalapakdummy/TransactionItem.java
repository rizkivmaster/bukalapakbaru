package com.example.bukalapakdummy;

import java.util.ArrayList;

import model.business.Transaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bukalapakdummy.R;


public class TransactionItem extends BaseAdapter {
	private ArrayList<Transaction> transactions;
	private Context context;
	private LayoutInflater inflater;

	// private static int ICON_OFF_PENDING = R.drawable.payment_statuses_006;
	private static int ICON_OFF_PAID = R.drawable.payment_statuses_008;
	private static int ICON_OFF_DELIVERED = R.drawable.payment_statuses_009;
	private static int ICON_OFF_RECEIVED = R.drawable.payment_statuses_004;
	private static int ICON_OFF_REMITTED = R.drawable.payment_statuses_005;

	private static int ICON_ON_PENDING = R.drawable.payment_statuses_016;
	private static int ICON_ON_PAID = R.drawable.payment_statuses_018;
	private static int ICON_ON_DELIVERED = R.drawable.payment_statuses_019;
	private static int ICON_ON_RECEIVED = R.drawable.payment_statuses_014;
	private static int ICON_ON_REMITTED = R.drawable.payment_statuses_015;

	private static int ICON_ON_REFUNDED = R.drawable.payment_statuses_020;

	public TransactionItem(Context c) {
		this.context = c;
		transactions = new ArrayList<Transaction>();
		inflater = LayoutInflater.from(c);
	}

	public ArrayList<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(ArrayList<Transaction> transactions) {
		this.transactions = transactions;
	}

	@Override
	public int getCount() {
		return this.transactions.size();
	}

	@Override
	public Object getItem(int arg0) {
		return this.transactions.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View arg1, ViewGroup arg2) {
		final Transaction t = transactions.get(position);
		if (arg1 == null) {
			arg1 = inflater.inflate(R.layout.transaction_item, null);
		}
		TextView tProduct = (TextView) arg1.findViewById(R.id.product_trans);
		TextView tID = (TextView) arg1.findViewById(R.id.trans_id);
		TextView tAmount = (TextView) arg1.findViewById(R.id.price_trans);
		TextView tLblStatus = (TextView) arg1.findViewById(R.id.trans_status);

		ImageView icon_Pending = (ImageView) arg1
				.findViewById(R.id.trans_pending);
		ImageView icon_Paid = (ImageView) arg1.findViewById(R.id.trans_paid);
		ImageView icon_Delivered = (ImageView) arg1
				.findViewById(R.id.trans_delivered);
		ImageView icon_Received = (ImageView) arg1
				.findViewById(R.id.trans_received);
		ImageView icon_Remitted = (ImageView) arg1
				.findViewById(R.id.trans_remitted);
		Button btnSend = (Button) arg1.findViewById(R.id.trans_btnsend);

		btnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				Intent intent = new Intent(context, KirimBarangActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("transaction_id", t.getTransactionID());
				context.startActivity(intent);

			}
		});

		tProduct.setText(t.getProductName());
		tID.setText(t.getTransactionID());
		String totalAmount = CurrencyFormatter.format(t.getTotalAmount());
		tAmount.setText("Rp " + totalAmount);
		switch (t.getState()) {
		case REFUNDED:
			icon_Pending.setImageResource(ICON_ON_REFUNDED);
			icon_Paid.setVisibility(View.INVISIBLE);
			icon_Delivered.setVisibility(View.INVISIBLE);
			icon_Received.setVisibility(View.INVISIBLE);
			icon_Remitted.setVisibility(View.INVISIBLE);
			btnSend.setVisibility(View.INVISIBLE);
			break;
		case PENDING:
			icon_Pending.setVisibility(View.VISIBLE);
			icon_Pending.setImageResource(ICON_ON_PENDING);

			icon_Paid.setVisibility(View.VISIBLE);
			icon_Paid.setImageResource(ICON_OFF_PAID);

			icon_Delivered.setVisibility(View.VISIBLE);
			icon_Delivered.setImageResource(ICON_OFF_DELIVERED);

			icon_Received.setVisibility(View.VISIBLE);
			icon_Received.setImageResource(ICON_OFF_RECEIVED);

			icon_Remitted.setVisibility(View.VISIBLE);
			icon_Remitted.setImageResource(ICON_OFF_REMITTED);
			break;
		case PAID:
			icon_Pending.setVisibility(View.VISIBLE);
			icon_Paid.setVisibility(View.VISIBLE);
			icon_Delivered.setVisibility(View.VISIBLE);
			icon_Received.setVisibility(View.VISIBLE);
			icon_Remitted.setVisibility(View.VISIBLE);

			icon_Pending.setImageResource(ICON_ON_PENDING);
			icon_Paid.setImageResource(ICON_ON_PAID);
			icon_Delivered.setImageResource(ICON_OFF_DELIVERED);
			icon_Received.setImageResource(ICON_OFF_RECEIVED);
			icon_Remitted.setImageResource(ICON_OFF_REMITTED);
			break;
		case DELIVERED:
			icon_Pending.setVisibility(View.VISIBLE);
			icon_Paid.setVisibility(View.VISIBLE);
			icon_Delivered.setVisibility(View.VISIBLE);
			icon_Received.setVisibility(View.VISIBLE);
			icon_Remitted.setVisibility(View.VISIBLE);

			icon_Pending.setImageResource(ICON_ON_PENDING);
			icon_Paid.setImageResource(ICON_ON_PAID);
			icon_Delivered.setImageResource(ICON_ON_DELIVERED);
			icon_Received.setImageResource(ICON_OFF_RECEIVED);
			icon_Remitted.setImageResource(ICON_OFF_REMITTED);
			btnSend.setVisibility(View.INVISIBLE);
			break;
		case RECEIVED:
			icon_Pending.setVisibility(View.VISIBLE);
			icon_Paid.setVisibility(View.VISIBLE);
			icon_Delivered.setVisibility(View.VISIBLE);
			icon_Received.setVisibility(View.VISIBLE);
			icon_Remitted.setVisibility(View.VISIBLE);

			icon_Pending.setImageResource(ICON_ON_PENDING);
			icon_Paid.setImageResource(ICON_ON_PAID);
			icon_Delivered.setImageResource(ICON_ON_DELIVERED);
			icon_Received.setImageResource(ICON_ON_RECEIVED);
			icon_Remitted.setImageResource(ICON_OFF_REMITTED);
			btnSend.setVisibility(View.INVISIBLE);
			break;
		case REMITTED:
			icon_Pending.setVisibility(View.VISIBLE);
			icon_Paid.setVisibility(View.VISIBLE);
			icon_Delivered.setVisibility(View.VISIBLE);
			icon_Received.setVisibility(View.VISIBLE);
			icon_Remitted.setVisibility(View.VISIBLE);

			icon_Pending.setImageResource(ICON_ON_PENDING);
			icon_Paid.setImageResource(ICON_ON_PAID);
			icon_Delivered.setImageResource(ICON_ON_DELIVERED);
			icon_Received.setImageResource(ICON_ON_RECEIVED);
			icon_Remitted.setImageResource(ICON_ON_REMITTED);
			btnSend.setVisibility(View.INVISIBLE);
			break;
		}

		for (int ii = 0; ii < t.getPossibleActions().size(); ii++) {
			String action = t.getPossibleActions().get(ii);
			if (action.equals("deliver")) {
				btnSend.setVisibility(View.VISIBLE);
				btnSend.setTextColor(Color.BLACK);
				btnSend.setText("Kirim Barang");
			}
		}

		return arg1;

	}

}
