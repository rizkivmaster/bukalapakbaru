package com.example.bukalapakdummy;

import java.util.ArrayList;

import listener.APIListener;
import model.business.Credential;
import model.business.CredentialEditor;
import model.business.Transaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import api.ListTransaction;

import com.bukalapakdummy.R;

public class TransaksiFragment extends Fragment {

	Context context;
	Credential credential;

	public TransaksiFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		final View gv = inflater.inflate(R.layout.transaction_list, null);
		// gv.setBackgroundResource(android.R.color.black);

		context = getActivity();
		credential = CredentialEditor.loadCredential(context);
		final ListTransaction task = new ListTransaction(context, credential);
		task.setAPIListener(new APIListener<ArrayList<Transaction>>() {
			ProgressDialog pd;

			@Override
			public void onSuccess(ArrayList<Transaction> res) {
				pd.dismiss();
				ListView list = (ListView) gv
						.findViewById(R.id.transaction_list);
				TransactionItem adapter = new TransactionItem(res, context);
				list.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onFailure(Exception e) {
				pd.dismiss();

			}

			@Override
			public void onExecute() {
				pd = new ProgressDialog(context);
				pd.setTitle("Transaksi");

				pd.setMessage("Tunggu sebentar, sedang mengambil...");

				pd.setCancelable(true);
				pd.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						task.cancel();
					}
				});

				pd.setIndeterminate(false);
				pd.show();
			}
		});
		task.execute();
		return gv;
	}
}
