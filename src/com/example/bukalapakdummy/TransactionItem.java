package com.example.bukalapakdummy;

import java.util.ArrayList;
import java.util.List;

import model.business.Transaction;
import model.business.TransactionState;

import org.json.JSONException;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bukalapakdummy.R;

public class TransactionItem extends BaseAdapter {

	ArrayList<Transaction> transactions;
	Context context;
	LayoutInflater inflater;

	private static int ICON_OFF_ORDERED = R.drawable.payment_statuses_004;
	private static int ICON_OFF_PAID = R.drawable.payment_statuses_005;
	private static int ICON_OFF_SENT = R.drawable.payment_statuses_006;
	private static int ICON_OFF_ARRIVED = R.drawable.payment_statuses_008;

	private static int ICON_ON_ORDERED = R.drawable.payment_statuses_014;
	private static int ICON_ON_PAID = R.drawable.payment_statuses_015;
	private static int ICON_ON_SENT = R.drawable.payment_statuses_016;
	private static int ICON_ON_ARRIVED = R.drawable.payment_statuses_018;

	public TransactionItem(ArrayList<Transaction> t, Context c) {
		this.context = c;
		this.transactions = t;
		inflater = LayoutInflater.from(c);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.transactions.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return this.transactions.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if (arg1 == null) {
			arg1 = inflater.inflate(R.layout.transaction_item, null);
		}
		TextView tProduct = (TextView) arg1.findViewById(R.id.product_trans);
		TextView tID = (TextView) arg1.findViewById(R.id.trans_id);
		TextView tTime = (TextView) arg1.findViewById(R.id.trans_date);
		ImageView icon_Ordered = (ImageView) arg1
				.findViewById(R.id.trans_ordered);
		ImageView icon_Paid = (ImageView) arg1.findViewById(R.id.trans_paid);
		ImageView icon_Sent = (ImageView) arg1.findViewById(R.id.trans_sent);
		ImageView icon_Arrived = (ImageView) arg1
				.findViewById(R.id.trans_arrived);
		Button btnSend = (Button) arg1.findViewById(R.id.trans_btnsend);

		Transaction transaction = (Transaction) getItem(arg0);

		tProduct.setText(transaction.getProductName());

		try {
			tID.setText(transaction.getId());

			TransactionState status = transaction.getState();
			switch (status) {
			case DELIVERED:
				icon_Ordered.setImageResource(ICON_ON_ORDERED);
				icon_Paid.setImageResource(ICON_ON_PAID);
				icon_Sent.setImageResource(ICON_ON_SENT);
				icon_Arrived.setImageResource(ICON_OFF_ARRIVED);
				break;
			case PAID:
				icon_Ordered.setImageResource(ICON_ON_ORDERED);
				icon_Paid.setImageResource(ICON_ON_PAID);
				icon_Sent.setImageResource(ICON_OFF_SENT);
				icon_Arrived.setImageResource(ICON_OFF_ARRIVED);
				break;
			case RECEIVED:
				icon_Ordered.setImageResource(ICON_ON_ORDERED);
				icon_Paid.setImageResource(ICON_ON_PAID);
				icon_Sent.setImageResource(ICON_ON_SENT);
				icon_Arrived.setImageResource(ICON_ON_ARRIVED);
				break;
			case PENDING:
				icon_Ordered.setImageResource(ICON_ON_ORDERED);
				icon_Paid.setImageResource(ICON_OFF_PAID);
				icon_Sent.setImageResource(ICON_OFF_SENT);
				icon_Arrived.setImageResource(ICON_OFF_ARRIVED);
				break;
			case CANCELLED:
				break;
			case REMITTED:
				break;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<String> arr = transaction.getPossibleActions();
		for (String action : arr) {
			if (action.equals("deliver")) {
				btnSend.setText("Kirim Barang");
			}
		}

		return arg1;
	}

}
