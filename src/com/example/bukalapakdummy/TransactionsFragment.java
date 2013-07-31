package com.example.bukalapakdummy;

import java.util.ArrayList;

import listener.APIListener;
import model.business.Credential;
import model.business.CredentialEditor;
import model.business.Transaction;
import com.bukalapakdummy.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import api.ListTransaction;

public class TransactionsFragment extends Fragment {
	Context context;
	Credential credential;
	LayoutInflater inflater;
	TransactionItem adapter;
	public TransactionsFragment() {
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		credential = CredentialEditor.loadCredential(context);
		this.inflater = inflater;
		if (container == null) {
			return null;
		}
		View myFragmentView = inflater.inflate(R.layout.transaction_list,
				container, false);
		final ListView gv = (ListView) myFragmentView.findViewById(R.id.transaction_list);
		adapter= new TransactionItem(context);
		gv.setAdapter(adapter);
		final ListTransaction task = new ListTransaction(context, credential);
		task.setAPIListener(new APIListener<ArrayList<Transaction>>() {
			ProgressDialog pd = new ProgressDialog(context);
			@Override
			public void onSuccess(ArrayList<Transaction> res) {
				pd.dismiss();
				adapter.setTransactions(res);
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
				pd.setCancelable(false);
				pd.setIndeterminate(true);
				pd.show();
			}
		});
		task.execute();		
		return myFragmentView;
	}
}
