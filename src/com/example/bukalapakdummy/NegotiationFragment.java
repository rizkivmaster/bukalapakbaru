package com.example.bukalapakdummy;

import java.util.ArrayList;
import java.util.HashMap;

import listener.APIListener;
import model.business.Credential;
import model.business.CredentialEditor;
import model.business.Negotiation;

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
import api.ListNegotiations;

import com.bukalapakdummy.R;

public class NegotiationFragment extends Fragment {
	ArrayList<JSONObject> res;
	private ArrayList<HashMap<String, String>> data;
	Context context;
	private LayoutInflater inflater;

	public NegotiationFragment() {
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		this.inflater = inflater;
		if (container == null) {
			return null;
		}
		View myFragmentView = inflater.inflate(R.layout.nego_list,
				container, false);
		final ListView gv = (ListView) myFragmentView.findViewById(R.id.nego_list);
		// gv.setBackgroundResource(android.R.color.black);
		Credential credential = CredentialEditor.loadCredential(context);
		final ListNegotiations task = new ListNegotiations(context, credential,10);
		
		task.setAPIListener(new APIListener<ArrayList<Negotiation>>() {
			ProgressDialog pd;
			@Override
			public void onSuccess(ArrayList<Negotiation> res) {
				pd.dismiss();
				NegosiasiAdapter adapter = new NegosiasiAdapter(res, context);
				gv.setAdapter(adapter);
				gv.invalidate();				
			}
			
			@Override
			public void onFailure(Exception e) {
				pd.dismiss();
				Toast.makeText(getActivity(), e.getMessage(),
						Toast.LENGTH_SHORT).show();
				
			}
			
			@Override
			public void onExecute() {
				pd = new ProgressDialog(context);
				pd.setTitle("Negosiasi");

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