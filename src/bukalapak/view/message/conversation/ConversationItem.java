package bukalapak.view.message.conversation;

import java.util.ArrayList;

import listener.APIListener;
import model.business.Credential;
import model.business.CredentialEditor;
import model.business.IncomingMessage;
import model.business.OnlineMessage;
import model.business.SentMessage;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import api.DeleteMessage;

import com.bukalapakdummy.R;

import dialog.ProgressDialogManager;

public class ConversationItem extends BaseAdapter {

	private ArrayList<OnlineMessage> conversations;
	private Activity fragment;

	public ArrayList<OnlineMessage> getConversations() {
		return conversations;
	}

	public void setConversations(ArrayList<OnlineMessage> conversations) {
		this.conversations = conversations;
	}

	Context context;
	LayoutInflater inflater;

	public ConversationItem(Context c, Activity f) {
		fragment = f;
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

		final OnlineMessage conversation = conversations.get(arg0);
		if (arg1 == null) {
			arg1 = inflater.inflate(R.layout.messaging_detail_item, null);
		}
		ImageView fotoConversation = (ImageView) arg1
				.findViewById(R.id.list_image);

		TextView fromConversation = (TextView) arg1
				.findViewById(R.id.msg_sender);
		TextView messageConversation = (TextView) arg1
				.findViewById(R.id.msg_content);
		TextView datetimeConversation = (TextView) arg1
				.findViewById(R.id.msg_date);
		Button deleteConversation = (Button) arg1
				.findViewById(R.id.message_delete);

		deleteConversation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(context);
				dialog.setTitle("Hapus pesan");
				dialog.setMessage("Yakin untuk menghapus pesan ini?");
				dialog.setPositiveButton("Ya",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								Credential credential = CredentialEditor
										.loadCredential(context);
								final DeleteMessage task = new DeleteMessage(context,
										credential, conversation.getId());
								APIListener<String> listener = new APIListener<String>() {
									ProgressDialogManager manager = new ProgressDialogManager(
											"Hapus Pesan", context);

									@Override
									public void onSuccess(String res) {
										manager.successProgress("Pesan berhasil dihapus");
										((ConversationActivity) fragment)
												.refresh();
									}

									@Override
									public void onFailure(Exception e) {
										manager.failedProgress(e);
									}

									@Override
									public void onExecute() {
										manager.startProgress(task);
									}
								};
								task.setAPIListener(listener);
								task.execute();
							}
						});
				dialog.setNegativeButton("Tidak", null);
				dialog.show();

			}
		});

		if (conversation instanceof IncomingMessage) {
			fromConversation.setText(((IncomingMessage) conversation)
					.getSenderName());

		} else if (conversation instanceof SentMessage) {
			fromConversation.setText("You");
		}

		messageConversation.setText(Html.fromHtml(conversation.getBody()));
		datetimeConversation.setText(conversation.getCreateTime()
				.toLocaleString());
		return arg1;
	}
}
