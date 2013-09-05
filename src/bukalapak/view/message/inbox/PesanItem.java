package bukalapak.view.message.inbox;

import java.util.ArrayList;
import java.util.HashMap;

import listener.APIListener;
import model.business.Credential;
import model.business.MessageLine;
import pagination.UpdateableAdapter;
import pagination.pageloader.InboxLoader;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;
import api.DeleteConversation;
import bukalapak.view.message.conversation.ConversationActivity;

import com.bukalapakdummy.R;

public class PesanItem extends BaseAdapter implements Filterable,
		UpdateableAdapter<MessageLine> {

	private ArrayList<MessageLine> messages;
	private ArrayList<MessageLine> showedData;
	private HashMap<String, Boolean> checked;
	private InboxLoader loader;
	private Credential credential;
	Context context;
	LayoutInflater inflater;

	public PesanItem(Context c, Credential cre) {
		this.context = c;
		credential = cre;
		messages = new ArrayList<MessageLine>();
		showedData = new ArrayList<MessageLine>();
		checked = new HashMap<String, Boolean>();
		inflater = LayoutInflater.from(c);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.showedData.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return this.showedData.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return this.showedData.size();
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		// TODO Auto-generated method stub
		final MessageViewHolder holder;
		// final MessageLine message = (MessageLine) getItem(arg0);
		if (v == null) {
			// arg1 = inflater.inflate(R.layout.messaging_inbox_item, null);
			LayoutInflater li = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.messaging_inbox_item, parent, false);
			holder = new MessageViewHolder();
			holder.fromPesan = (TextView) v.findViewById(R.id.message_username);
			holder.timePesan = (TextView) v.findViewById(R.id.message_time);
			holder.checkPesan = (CheckBox) v.findViewById(R.id.message_check);
			v.setTag(holder);
		} else {
			holder = (MessageViewHolder) v.getTag();
		}
		// TextView fromPesan = (TextView) arg1
		// .findViewById(R.id.message_username);
		// TextView timePesan = (TextView) arg1.findViewById(R.id.message_time);
		final MessageLine message = (MessageLine) getItem(position);
		if (message != null) {
			holder.fromPesan.setText(message.getPartnerName());
			holder.timePesan.setText(message.getUpdateTime().toLocaleString());
			if (checked.containsKey(message.getId())) {
				holder.checkPesan.setChecked(checked.get(message.getId()));
			} else {
				holder.checkPesan.setChecked(false);
			}
			holder.checkPesan.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					checked.put(message.getId(), holder.checkPesan.isChecked());
				}
			});
		}

		// CheckBox check = (CheckBox) arg1.findViewById(R.id.message_check);
		// if (checked.containsKey(message.getId())) {
		// check.setChecked(checked.get(message.getId()));
		// }

		// check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		//
		// @Override
		// public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		// checked.put(message.getId(), arg1);
		// }
		// });

		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(context, ConversationActivity.class);
				i.putExtra("inbox_id", message.getId());
				i.putExtra("partner_id", message.getPartnerID());
				i.putExtra("partner_name", message.getPartnerName());
				context.startActivity(i);
			}
		});
		return v;
	}

	private void processDeletion(final ArrayList<MessageLine> list,
			final int position) {
		DeleteConversation task = new DeleteConversation(context, credential,
				list.get(position).getId());
		APIListener<String> listener = new APIListener<String>() {
			ProgressDialog pd;

			@Override
			public void onExecute() {
				pd = new ProgressDialog(context);
				pd.setTitle("Hapus Percakapan");
				pd.setMessage("Sedang menghapus pesan " + (position + 1));
				pd.setCancelable(false);
				pd.setIndeterminate(true);
				pd.show();
			}

			@Override
			public void onSuccess(String res) {
				pd.dismiss();
				if (position + 1 < list.size()) {
					processDeletion(list, position + 1);
				} else {
					Toast.makeText(context, "Berhasil menghapus percakapan",
							Toast.LENGTH_SHORT).show();
					refreshElements();
				}
			}

			@Override
			public void onFailure(Exception e) {
				pd.dismiss();
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT)
						.show();
			}
		};

		task.setAPIListener(listener);
		task.execute();
	}

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		return new Filter() {

			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				showedData.clear();
				ArrayList<MessageLine> result = (ArrayList<MessageLine>) results.values;
				showedData.addAll(result);
				notifyDataSetChanged();
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();
				if (constraint == null || constraint.length() <= 0) {
					results.values = messages;
					results.count = messages.size();
				} else {

					ArrayList<MessageLine> result = new ArrayList<MessageLine>();
					for (int ii = 0; ii < messages.size(); ii++) {
						if (messages.get(ii).getPartnerName()

						.contains(constraint))
							result.add(messages.get(ii));

					}
					results.values = result;
					results.count = result.size();
				}
				return results;
			}
		};
	}

	@Override
	public void setElements(ArrayList<MessageLine> list) {
		messages.clear();
		messages.addAll(list);
		getFilter().filter("");
	}

	@Override
	public ArrayList<MessageLine> getElements() {
		return messages;
	}

	public void delete() {
		final ArrayList<MessageLine> list = new ArrayList<MessageLine>();
		for (MessageLine item : messages) {
			if (checked.containsKey(item.getId()) && checked.get(item.getId())) {
				list.add(item);
			}
		}
		if (list.size() > 0) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			dialog.setTitle("Hapus Percakapan");
			dialog.setMessage("Yakin hapus " + list.size() + " percakapan?");
			dialog.setPositiveButton("Ya",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							if (list.size() > 0)
								processDeletion(list, 0);
						}
					});
			dialog.setNegativeButton("Tidak", null);
			dialog.show();
		}
	}

	public InboxLoader getLoader() {
		return loader;
	}

	public void setLoader(InboxLoader loader) {
		this.loader = loader;
	}

	public void refreshElements() {
		getElements().clear();
		loader.loadMorePage();
	}

	static class MessageViewHolder {
		TextView fromPesan;
		TextView timePesan;
		CheckBox checkPesan;
	}
}
