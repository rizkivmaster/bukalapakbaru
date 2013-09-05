package bukalapak.view.draft;

import java.util.ArrayList;

import model.business.Credential;
import model.business.CredentialEditor;
import model.business.DraftedLocalProduct;
import tools.UploadProductEditor;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.bukalapakdummy.R;

public class DraftUploadFragment extends Fragment {
	Credential credential;
	Context context;
	ListView list;

	public DraftUploadFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		credential = CredentialEditor.loadCredential(context);
		LinearLayout gv = (LinearLayout) inflater.inflate(R.layout.draft_list,
				null);
		list = (ListView) gv.findViewById(R.id.draft_list);
		refresh();
		return gv;
	}

	public void refresh() {
		ArrayList<DraftedLocalProduct> d = UploadProductEditor
				.listDraftedProduct(credential);
		DraftUploadItem adapter = new DraftUploadItem(d, context,this);
		list.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

}
