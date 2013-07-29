package com.example.bukalapakdummy;

import java.util.ArrayList;
import java.util.HashMap;

import listener.APIListener;
import model.business.Credential;
import model.business.CredentialEditor;
import model.business.MessageLine;

import org.json.JSONObject;

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
import android.widget.Toast;
import api.GetInbox;

import com.bukalapakdummy.R;

public class PesanFragment extends Fragment {
	ArrayList<JSONObject> res;
	Context context;
	private LayoutInflater inflater;

	public PesanFragment() {
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		this.inflater = inflater;
		if (container == null) {
			return null;
		}
		View myFragmentView = inflater.inflate(R.layout.messaging_inbox,
				container, false);
		final ListView gv = (ListView) myFragmentView.findViewById(R.id.list);
		// gv.setBackgroundResource(android.R.color.black);
		Credential credential = CredentialEditor.loadCredential(context);
		final GetInbox task = new GetInbox(context, credential);
		task.setAPIListener(new APIListener<ArrayList<MessageLine>>() {
			ProgressDialog pd;

			@Override
			public void onSuccess(ArrayList<MessageLine> res) {
				pd.dismiss();
				PesanItem adapter = new PesanItem(res, context);
				gv.setAdapter(adapter);
				gv.invalidate();
			}

			@Override
			public void onFailure(Exception e) {
				pd.dismiss();
				Toast.makeText(getActivity(), "",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onExecute() {
				pd = new ProgressDialog(context);
				pd.setTitle("Lapak");

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
		return myFragmentView;
	}
}