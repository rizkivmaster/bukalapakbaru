package com.example.bukalapakdummy;


import java.util.ArrayList;

import model.business.DraftedLocalProduct;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.bukalapakdummy.R;

public class DraftUploadItem extends BaseAdapter{
	
	ArrayList<DraftedLocalProduct> drafts;
	Context context;
	LayoutInflater inflater;
	
	public DraftUploadItem(ArrayList<DraftedLocalProduct> t, Context c) {
		this.context = c;
		this.drafts = t;
		inflater = LayoutInflater.from(c);
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.drafts.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return this.drafts.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if (arg1 == null) {
			arg1 = inflater.inflate(R.layout.draft_item, null);
		}
		
		TextView titleDraft = (TextView) arg1.findViewById(R.id.draft_product_title);
		TextView dateDraft = (TextView) arg1.findViewById(R.id.draft_date);
		TextView timeDraft = (TextView) arg1.findViewById(R.id.draft_time);
		Button editDraft = (Button) arg1.findViewById(R.id.draft_edit);
		
		final DraftedLocalProduct draft = drafts.get(arg0);
		titleDraft.setText(draft.getName());
		timeDraft.setText(draft.getDateUpdated().toLocaleString());
		editDraft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(context,UploadLaterFragment.class);
				i.putExtra("path", draft.getFile().getAbsolutePath());
				context.startActivity(i);
			}
		});
		return arg1;
	}
}
