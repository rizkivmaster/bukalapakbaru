package com.example.bukalapakdummy;

import java.util.ArrayList;

import model.business.CredentialEditor;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bukalapakdummy.R;
import com.example.bukalapakdummy.section.EntryAdapter;
import com.example.bukalapakdummy.section.EntryItem;
import com.example.bukalapakdummy.section.Item;
import com.example.bukalapakdummy.section.SectionItem;

public class MenuFragment extends ListFragment {
	ArrayList<Item> items = new ArrayList<Item>();
	Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		return inflater.inflate(R.layout.list, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		// String[] list_menu =
		// getResources().getStringArray(R.array.slide_menu);
		/*
		 * ArrayAdapter<String> colorAdapter = new
		 * ArrayAdapter<String>(getActivity(),
		 * android.R.layout.simple_list_item_1, android.R.id.text1, list_menu);
		 */

		items.add(new SectionItem("Produk"));
		items.add(new EntryItem("Draft"));
		items.add(new EntryItem("Lapak Dijual"));
		items.add(new EntryItem("Lapak Tidak Dijual"));
		items.add(new SectionItem(""));
		items.add(new EntryItem("Pesan"));
		items.add(new SectionItem("Payment"));
		items.add(new EntryItem("Transaksi"));
		items.add(new EntryItem("Negosiasi"));
		items.add(new SectionItem(""));
		items.add(new EntryItem("Profil"));
		items.add(new SectionItem(""));
		items.add(new EntryItem("Terms and Condition"));
		items.add(new SectionItem(""));
		items.add(new EntryItem("Logout"));

		EntryAdapter colorAdapter = new EntryAdapter(getActivity(), items);
		setListAdapter(colorAdapter);

	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		// Fragment newContent = new BirdGridFragment(position);

		// nyoba fragment baru
		// String[] list_menu =
		// getResources().getStringArray(R.array.slide_menu);

		Fragment newContent = null;
		if (!items.get(position).isSection()) {
			EntryItem item = (EntryItem) items.get(position);
			/*
			 * if ((item.title).equals("Upload")) { newContent = new
			 * UploadFragment(); } else
			 */if ((item.title).equals("Lapak Dijual")) {
				newContent = new LapakDijualFragment();
			} else if ((item.title).equals("Draft")) {
				newContent = new DraftUploadFragment();
			} else if ((item.title).equals("Lapak Tidak Dijual")) {
				newContent = new LapakTidakDijualFragment();
			} else if ((item.title).equals("Transaksi")) {
				newContent = new TransactionsFragment();
			} else if ((item.title).equals("Pesan")) {
				newContent = new PesanFragment();
			} else if ((item.title).equals("Negosiasi")) {
				newContent = new NegotiationFragment();
			} else if ((item.title).equals("Profil")) {
				newContent = new ProfilFragment();
			} else if ((item.title).equals("Logout")) {
				CredentialEditor.removeCredential(context);
				getActivity().startActivity(
						new Intent(getActivity(), LoginActivity.class));
				getActivity().finish();
			}
		}

		if (newContent != null)
			switchFragment(newContent, position);

	}

	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment, int pos) {
		EntryItem item = (EntryItem) items.get(pos);

		if (getActivity() == null)

			return;

		if (getActivity() instanceof ResponsiveUIActivity) {
			ResponsiveUIActivity ra = (ResponsiveUIActivity) getActivity();
			// String[] list_menu =
			// getResources().getStringArray(R.array.slide_menu);
			ra.setTitle(item.title);
			ra.switchContent(fragment);
		}
	}

}
