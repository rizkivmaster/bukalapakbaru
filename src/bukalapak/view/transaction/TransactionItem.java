package bukalapak.view.transaction;

import java.util.ArrayList;
import java.util.List;

import model.business.Transaction;
import pagination.PageLoaderListener;
import pagination.Refreshable;
import pagination.UpdateableAdapter;
import android.content.Context;
import android.content.Intent;
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
import bukalapak.view.CurrencyFormatter;
import bukalapak.view.transaction.detail.KirimBarangActivity;
import bukalapak.view.transaction.detail.TransactionDetail;

import com.bukalapakdummy.R;

public class TransactionItem extends BaseAdapter implements
		UpdateableAdapter<Transaction>, Filterable {
	private ArrayList<Transaction> transactions, showedData;
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
		showedData = new ArrayList<Transaction>();
		inflater = LayoutInflater.from(c);
	}

	public ArrayList<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(ArrayList<Transaction> transactions) {
		this.transactions = transactions;
		this.showedData.addAll(transactions);
	}

	@Override
	public int getCount() {
		return this.showedData.size();
	}

	@Override
	public Object getItem(int arg0) {
		return this.showedData.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View arg1, ViewGroup arg2) {
		final Transaction t = showedData.get(position);
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
		// Button btnSend = (Button) arg1.findViewById(R.id.trans_btnsend);

		// btnSend.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View view) {
		//
		// Intent intent = new Intent(context, KirimBarangActivity.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// intent.putExtra("transaction_id", t.getId());
		// context.startActivity(intent);
		//
		// }
		// });

		tProduct.setText(t.getProductName());
		tID.setText(t.getTransactionID());
		String totalAmount = CurrencyFormatter.format(t.getTotalAmount());
		tAmount.setText("Rp " + totalAmount);
		arg1.setBackgroundResource(R.drawable.list_selector);
		switch (t.getState()) {
		case REFUNDED:
			icon_Pending.setImageResource(ICON_ON_REFUNDED);
			icon_Paid.setVisibility(View.INVISIBLE);
			icon_Delivered.setVisibility(View.INVISIBLE);
			icon_Received.setVisibility(View.INVISIBLE);
			icon_Remitted.setVisibility(View.INVISIBLE);
			// btnSend.setVisibility(View.INVISIBLE);
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
			arg1.setBackgroundColor(Color.GRAY);
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
			// btnSend.setVisibility(View.INVISIBLE);
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
			// btnSend.setVisibility(View.INVISIBLE);
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
			// btnSend.setVisibility(View.INVISIBLE);
			break;
		}

		arg1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, TransactionDetail.class);
				intent.putExtra("transaction_id", t.getId());
				context.startActivity(intent);
			}
		});
		return arg1;

	}

	@Override
	public void setElements(ArrayList<Transaction> list) {
		this.transactions.clear();
		this.transactions.addAll(list);
		getFilter().filter("");
	}

	@Override
	public ArrayList<Transaction> getElements() {
		return this.transactions;
	}

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		return new Filter() {

			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				showedData.clear();
				ArrayList<Transaction> result = (ArrayList<Transaction>) results.values;
				showedData.addAll(result);
				notifyDataSetChanged();
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();
				if (constraint == null || constraint.length() <= 0) {
					results.values = transactions;
					results.count = transactions.size();
				} else {

					ArrayList<Transaction> result = new ArrayList<Transaction>();
					for (int ii = 0; ii < transactions.size(); ii++) {
						if (transactions.get(ii).getProductName()
								.contains(constraint)
								|| transactions.get(ii).getBuyerName()
										.contains(constraint)) {
							result.add(transactions.get(ii));
						}
					}
					results.values = result;
					results.count = result.size();
				}
				return results;
			}
		};
	}
}
