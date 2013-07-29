package com.example.bukalapakdummy;


import java.util.ArrayList;
import java.util.Date;

import model.business.MessageLine;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.bukalapakdummy.R;

public class PesanItem extends BaseAdapter{
	
	ArrayList<MessageLine> messages;
	Context context;
	LayoutInflater inflater;
	
	public PesanItem(ArrayList<MessageLine> t, Context c) {
		this.context = c;
		this.messages = t;
		inflater = LayoutInflater.from(c);
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.messages.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return this.messages.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		final MessageLine message = messages.get(arg0);
		if (arg1 == null) {
			arg1 = inflater.inflate(R.layout.messaging_inbox_item, null);
		}
		TextView fromPesan = (TextView) arg1.findViewById(R.id.message_username);
		TextView timePesan = (TextView) arg1.findViewById(R.id.message_time);
		
		fromPesan.setText(message.getPartnerName());
		timePesan.setText(message.getUpdateTime().toLocaleString());
		
		arg1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(context,ConversationActivity.class);
				i.putExtra("inbox_id", message.getId());
				i.putExtra("partner_id", message.getPartnerID());
				i.putExtra("partner_name", message.getPartnerName());
				context.startActivity(i);
			}
		});
		return arg1;
	}
}
