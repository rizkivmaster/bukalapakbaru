package bukalapak.view.negotiation;

import java.util.ArrayList;
import java.util.HashMap;

import listener.APIListener;
import model.business.Credential;
import model.business.CredentialEditor;
import model.business.Negotiation;

import org.json.JSONObject;

import pagination.PageLoaderListener;
import pagination.Refreshable;
import pagination.pageloader.NegotiationLoader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import api.ListNegotiations;

import com.bukalapakdummy.R;

public class NegotiationFragment extends Fragment implements Refreshable {
	ArrayList<JSONObject> res;
	// private ArrayList<HashMap<String, String>> data;
	Context context;
	NegotiationLoader loader;

	// private LayoutInflater inflater;

	public NegotiationFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		if (container == null) {
			return null;
		}
		View myFragmentView = inflater.inflate(R.layout.nego_list, container,
				false);
		final ListView gv = (ListView) myFragmentView
				.findViewById(R.id.nego_list);
		final EditText searchBox = (EditText) myFragmentView
				.findViewById(R.id.searchBox);
		// gv.setBackgroundResource(android.R.color.black);
		Credential credential = CredentialEditor.loadCredential(context);
		final NegosiasiAdapter adapter = new NegosiasiAdapter(context);
		gv.setAdapter(adapter);
		searchBox.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				adapter.getFilter().filter(s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		loader = new NegotiationLoader(adapter, context, credential);
		loader.initializePage();
		if (adapter.getElements().isEmpty()) {
			PageLoaderListener listener = new PageLoaderListener() {
				ProgressDialog pd;

				@Override
				public void onSuccess(boolean s) {
					pd.dismiss();
				}

				@Override
				public void onStart() {
					pd = new ProgressDialog(context);
					pd.setTitle("Negosiasi");
					pd.setMessage("Harap menunggu");
					pd.setCancelable(false);
					pd.setIndeterminate(true);
					pd.show();
				}

				@Override
				public void onFailure(Exception e) {
					pd.dismiss();
				}
			};
			loader.setLoadMoreListener(listener);
			loader.loadMorePage();
		}
		return myFragmentView;
	}

	@Override
	public void refresh(PageLoaderListener listener) {
		loader.setRefreshListener(listener);
		loader.refreshPage();
	}

}