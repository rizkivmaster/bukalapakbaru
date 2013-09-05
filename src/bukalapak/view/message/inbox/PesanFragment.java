package bukalapak.view.message.inbox;

import java.util.ArrayList;

import model.business.Credential;
import model.business.CredentialEditor;

import org.json.JSONObject;

import pagination.PageLoaderListener;
import pagination.Refreshable;
import pagination.pageloader.InboxLoader;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bukalapakdummy.R;

public class PesanFragment extends Fragment implements Refreshable {
	ArrayList<JSONObject> res;
	Context context;
	InboxLoader loader;
	PesanItem adapter;

	public PesanFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		if (container == null) {
			return null;
		}
		View myFragmentView = inflater.inflate(R.layout.messaging_inbox,
				container, false);
		final ListView gv = (ListView) myFragmentView.findViewById(R.id.list);
		final EditText searchBox = (EditText) myFragmentView
				.findViewById(R.id.searchBox);
		Button deleteBtn = (Button) myFragmentView.findViewById(R.id.message_delete);

		// gv.setBackgroundResource(android.R.color.black);
		Credential credential = CredentialEditor.loadCredential(context);
		adapter = new PesanItem(context,credential);
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
		deleteBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				adapter.delete();
			}
		});
		loader = new InboxLoader(adapter, context, credential);
		adapter.setLoader(loader);
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
					pd.setTitle("Pesan");
					pd.setMessage("Harap menunggu");
					pd.setCancelable(false);
					pd.setIndeterminate(true);
					pd.show();
				}

				@Override
				public void onFailure(Exception e) {
					Toast.makeText(getActivity(), e.getMessage(),
							Toast.LENGTH_SHORT).show();
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