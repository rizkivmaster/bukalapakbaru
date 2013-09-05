package bukalapak.view.transaction;

import model.business.Credential;
import model.business.CredentialEditor;
import pagination.PageLoaderListener;
import pagination.Refreshable;
import pagination.pageloader.LapakDijualLoader;
import pagination.pageloader.TransactionLoader;

import bukalapak.view.MainWindowActivity;

import com.bukalapakdummy.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class TransactionsFragment extends Fragment implements Refreshable {
	Context context;
	Credential credential;
	TransactionItem adapter;
	TransactionLoader loader;

	public TransactionsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		// proto
		context = getActivity();
		credential = CredentialEditor.loadCredential(context);
		// get view
		// prepare view
		View myFragmentView = inflater.inflate(R.layout.transaction_list,
				container, false);
		final ListView gv = (ListView) myFragmentView
				.findViewById(R.id.transaction_list);
		EditText searchBox = (EditText) myFragmentView
				.findViewById(R.id.editText1);

		final Button loadmore = new Button(context);
		loadmore.setText("Lihat Selebihnya");

		View view = inflater.inflate(R.layout.no_items, null);
		final TextView noItemsView = (TextView) view.findViewById(R.id.no_item);

		gv.addFooterView(loadmore);
		loadmore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loader.loadMorePage();
				adapter.notifyDataSetChanged();
			}
		});

		final ProgressBar progressLoadMore = new ProgressBar(context);

		// data registration
		adapter = new TransactionItem(context);
		loader = new TransactionLoader(adapter, context, credential);
		loader.initializePage();
		PageLoaderListener loadMorelistener = new PageLoaderListener() {
			@Override
			public void onSuccess(boolean isAtEnd) {
				progressLoadMore.setVisibility(ProgressBar.GONE);
				gv.removeFooterView(progressLoadMore);
				if (adapter.isEmpty()) {
					gv.addFooterView(noItemsView);
				} else {
					if (!isAtEnd)
						gv.addFooterView(loadmore);
				}
			}

			@Override
			public void onFailure(Exception e) {
				progressLoadMore.setVisibility(ProgressBar.GONE);
				gv.removeFooterView(progressLoadMore);
				gv.addFooterView(loadmore);
			}

			@Override
			public void onStart() {
				gv.removeFooterView(loadmore);
				gv.addFooterView(progressLoadMore);
				progressLoadMore.setVisibility(ProgressBar.VISIBLE);
			}
		};
		loader.setLoadMoreListener(loadMorelistener);
		loader.initializePage();
		searchBox.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				adapter.getFilter().filter(arg0);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
		gv.setAdapter(adapter);
		if (adapter.getElements().isEmpty()) {
			loader.loadMorePage();
		}
		return myFragmentView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		loader.closePage();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		((MainWindowActivity) getActivity()).refreshPage();
	}

	@Override
	public void refresh(PageLoaderListener listener) {
		loader.setRefreshListener(listener);
		loader.refreshPage();
	}
}
