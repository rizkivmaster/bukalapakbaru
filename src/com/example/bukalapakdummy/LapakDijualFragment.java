package com.example.bukalapakdummy;

import java.util.ArrayList;

import listener.APIListener;
import model.business.AvailableProduct;
import model.business.Credential;
import model.business.CredentialEditor;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.bukalapakdummy.*;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import api.ListLapakDijual;

public class LapakDijualFragment extends Fragment {
	Context context;
	Credential credential;
	LayoutInflater inflater;
	View mainView;
	ViewGroup container;
	LapakAvailableItemAdapter adapter;
	ActionMode m;

	public LapakDijualFragment() {
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
		credential = CredentialEditor.loadCredential(context);
		adapter = new LapakAvailableItemAdapter(context, this);
		listview.setAdapter(adapter);
		adapter.refreshView();
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
			((ResponsiveUIActivity) getActivity()).getSupportMenuInflater()
					.inflate(R.menu.sellingtoggle, menu);
			m = mode;
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
			case R.id.sold:
				adapter.soldProduct();
				break;
			}
			return false;
		}

	};

	public void onDestroyView() {
		super.onDestroyView();
		if(m!=null) m.finish();
	};
	
	public Callback getCallback()
	{
		return mCallback;
	}

}
