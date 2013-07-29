package com.example.bukalapakdummy;


import java.util.ArrayList;

import model.business.IncomingMessage;
import model.business.OnlineMessage;
import model.business.SentMessage;

import org.json.JSONObject;

import  com.bukalapakdummy.*;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ConversationItem extends BaseAdapter{
	
	private ArrayList<OnlineMessage> conversations;
	public ArrayList<OnlineMessage> getConversations() {
		return conversations;
	}


	public void setConversations(ArrayList<OnlineMessage> conversations) {
		this.conversations = conversations;
	}

	Context context;
	LayoutInflater inflater;
	
	public ConversationItem( Context c) {
		this.context = c;
		this.conversations = new ArrayList<OnlineMessage>();
		inflater = LayoutInflater.from(c);
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.conversations.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return this.conversations.get(arg0);
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
			arg1 = inflater.inflate(R.layout.messaging_detail_item, null);
		}
		ImageView fotoConversation = (ImageView) arg1.findViewById(R.id.list_image);
		
		TextView fromConversation = (TextView) arg1.findViewById(R.id.msg_sender);
		TextView messageConversation = (TextView) arg1.findViewById(R.id.msg_content);
		TextView datetimeConversation = (TextView) arg1.findViewById(R.id.msg_date);
		Button deleteConversation = (Button) arg1.findViewById(R.id.message_delete);
		
		OnlineMessage conversation = conversations.get(arg0);
		
		if(conversation instanceof IncomingMessage)
		{
			fromConversation.setText(((IncomingMessage) conversation).getSenderName());
			
		}
		else if(conversation instanceof SentMessage)
		{
			fromConversation.setText("You");
		}
		
		messageConversation.setText(conversation.getBody());
		datetimeConversation.setText(conversation.getCreateTime().toLocaleString());
		
		
		
		
		
		return arg1;
	}

}
