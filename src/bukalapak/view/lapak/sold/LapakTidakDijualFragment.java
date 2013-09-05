package bukalapak.view.lapak.sold;

import pagination.PageLoaderListener;
import pagination.Refreshable;
import pagination.pageloader.LapakDijualLoader;
import pagination.pageloader.LapakTidakDijualLoader;
import model.business.Credential;
import model.business.CredentialEditor;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import bukalapak.view.MainWindowActivity;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.bukalapakdummy.R;

public class LapakTidakDijualFragment extends Fragment implements Refreshable {
	Context context;
	Credential credential;
	LayoutInflater inflater;
	View mainView;
	ViewGroup container;
	LapakSoldItemAdapter adapter;
	ActionMode m;
	private LapakTidakDijualLoader loader;

	public LapakTidakDijualFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		this.inflater = inflater;
		if (container == null) {
			return null;
		}

		this.container = container;
		View myFragmentView = inflater.inflate(R.layout.view_product_list_grid,
				container, false);
		final GridView listview = (GridView) myFragmentView
				.findViewById(R.id.lapak_grid);
		EditText searchBox = (EditText) myFragmentView
				.findViewById(R.id.searchBox);
		View view = inflater.inflate(R.layout.no_items, null);
		final TextView noItemsView = (TextView) view.findViewById(R.id.no_item);

		credential = CredentialEditor.loadCredential(context);
		adapter = new LapakSoldItemAdapter(context, this);
		listview.setAdapter(adapter);
		loader = new LapakTidakDijualLoader(adapter, context, credential);
		loader.initializePage();
		if (adapter.getElements().isEmpty()) {
			PageLoaderListener listener = new PageLoaderListener() {
				ProgressDialog pd;

				@Override
				public void onSuccess(boolean isAtEnd) {
					pd.dismiss();
					if (adapter.isEmpty()) {
						listview.setEmptyView(noItemsView);
					}
				}

				@Override
				public void onStart() {
					pd = new ProgressDialog(context);
					pd.setTitle("Lapak Dijual");
					pd.setMessage("Harap menunggu...");
					pd.setCancelable(false);
					pd.setIndeterminate(true);
					pd.show();
				}

				@Override
				public void onFailure(Exception e) {
					pd.dismiss();
					Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT)
							.show();
				}
			};
			loader.setLoadMoreListener(listener);
			loader.loadMorePage();
		}
		
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
		return myFragmentView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		adapter.dismissCheckbox();
	}

	Callback mCallback = new Callback() {

		/**
		 * Invoked whenever the action mode is shown. This is invoked
		 * immediately after onCreateActionMode
		 */
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		/** Called when user exits action mode */
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			adapter.dismissCheckbox();
		}

		/**
		 * This is called when the action mode is created. This is called by
		 * startActionMode()
		 */
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mode.setTitle("Edit");
			((MainWindowActivity) getActivity()).getSupportMenuInflater()
					.inflate(R.menu.soldtoggle, menu);
			return true;
		}

		/**
		 * This is called when an item in the context menu is selected
		 */
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.hapus:
				adapter.deleteProduct();
				break;
			case R.id.relist:
				adapter.relistProduct();
				break;
			}
			mode.finish();
			return false;
		}

	};

	public void onDestroyView() {
		super.onDestroyView();
	};

	public Callback getCallback() {
		return mCallback;
	}

	@Override
	public void refresh(PageLoaderListener listener) {
		loader.setRefreshListener(listener);
		loader.refreshPage();
	}

}
