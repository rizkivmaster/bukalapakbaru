package bukalapak.view.message.conversation;

import java.util.ArrayList;

import listener.APIListener;
import model.business.Credential;
import model.business.CredentialEditor;
import model.business.DraftedMessage;
import model.business.MessageCategory;
import model.business.OnlineMessage;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import api.CreateMessage;
import api.ListConversation;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.bukalapakdummy.R;

import dialog.ProgressDialogManager;

public class ConversationActivity extends SherlockActivity {

	Context context;
	Credential credential;
	TextView detailName;
	EditText composeMessage;
	Button sendBtn;
	String inboxID;
	String partnerID;
	String partnerName;
	ConversationItem adapter;
	ListView list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle("Conversation");
		setContentView(R.layout.messaging_detail);
		context = this;
		credential = CredentialEditor.loadCredential(context);
		
		list = (ListView) findViewById(R.id.list);
		adapter = new ConversationItem(context,this);
		list.setAdapter(adapter);
		
		composeMessage = (EditText) findViewById(R.id.compose_msg);
		detailName = (TextView) findViewById(R.id.detail_message);
		sendBtn = (Button) findViewById(R.id.btn_send);
		sendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (composeMessage.getText().toString().length() > 0) {
					String message = composeMessage.getText().toString();
					DraftedMessage d = new DraftedMessage();
					d.setCategory(MessageCategory.NORMAL_MESSAGE);
					d.setReceiverID(partnerID);
					d.setBodyBB(message);
					final CreateMessage task = new CreateMessage(context, credential, d);
					task.setAPIListener(new APIListener<String>() {
						ProgressDialogManager manager = new ProgressDialogManager("Kirim Pesan",context);
						@Override
						public void onSuccess(String res) {
							manager.successProgress("Pesan berhasil dikirim");
							refresh();
						}
						
						@Override
						public void onFailure(Exception e) {
							manager.failedProgress(e);
						}
						
						@Override
						public void onExecute() {
							manager.startProgress(task);
						}
					});
					task.execute();
				} else {
					Toast.makeText(context, "Teks tidak boleh kosong", Toast.LENGTH_SHORT).show();
				}
			}
		});
		inboxID = getIntent().getStringExtra("inbox_id");
		partnerID = getIntent().getStringExtra("partner_id");
		partnerName = getIntent().getStringExtra("partner_name");
		detailName.setText("Message: "+partnerName);
		refresh();
	}
	
	public void refresh()
	{
		final ListConversation task = new ListConversation(context, credential,
				inboxID);
		task.setAPIListener(new APIListener<ArrayList<OnlineMessage>>() {
			ProgressDialog pd;
			ProgressDialogManager manager = new ProgressDialogManager("Ambil Pesan", context);
			@Override
			public void onSuccess(ArrayList<OnlineMessage> res) {
				manager.successProgress("");
				adapter.setConversations(res);
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onFailure(Exception e) {
				manager.failedProgress(e);
			}

			@Override
			public void onExecute() {
				manager.startProgress(task);
			}
		});
		task.execute();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

}
