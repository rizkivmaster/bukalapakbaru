package notification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import listener.APIListener;
import model.business.Credential;
import model.business.CredentialEditor;
import model.session.OnlineEntity;
import model.session.Paginator;
import pagination.Updater;
import android.app.Notification;
import android.content.Context;
import android.os.Environment;

public class Notificator<T extends OnlineEntity> {
	private final static int RETRIEVE_SIZE = 10;
	private NotificatorListener listener;
	private Updater<T> updater;
	Credential credential;
	private Context context;
	private HashMap<String, T> dataset;
	private Paginator<T> paginator;

	public Notificator(Updater<T> u, Context c, Paginator<T> p,Credential cre) {
		updater = u;
		context = c;
		dataset = new HashMap<String, T>();
		paginator = p;
		credential = cre;
		loadLocalData();
	}

	protected void tellStart() {
		if (listener != null)
			listener.onStart();
	}

	protected void tellSuccess(Notification result) {
		if (listener != null)
			listener.onSuccess(result);
	}

	protected void tellFailure(Exception e) {
		if (listener != null)
			listener.onFailure(e);
	}

	public NotificatorListener getListener() {
		return listener;
	}

	public void setListener(NotificatorListener listener) {
		this.listener = listener;
	}

	public Updater<T> getUpdater() {
		return updater;
	}

	public void setUpdater(Updater<T> updater) {
		this.updater = updater;
	}

	private void saveLocalData() {
		FileOutputStream fout;
		try {
			String signature = this.getClass().getName();
			credential = CredentialEditor.loadCredential(context);
			File folder = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ "/bukalapak/"
					+ credential.getUserid()
					+ "/notifiables/");
			folder.mkdirs();
			File file = new File(folder, signature);
			if (file.exists())
				file.delete();
			file.createNewFile();
			fout = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(dataset);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadLocalData() {
		String signature = this.getClass().getName();
		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/bukalapak/"
				+ credential.getUserid()
				+ "/notifiables/" + signature);
		if (file.exists()) {
			try {
				FileInputStream fin = new FileInputStream(file);
				ObjectInputStream iis = new ObjectInputStream(fin);
				dataset.putAll((HashMap<String, T>) iis.readObject());
			} catch (Exception e) {
			}
		}
	}

	protected void update() {
		tellStart();
		ArrayList<T> newList = new ArrayList<T>();
		list(newList, 1);
	}

	private void checkUpdates(ArrayList<T> source) {
		for (T t : source) {
			if (!dataset.containsKey(t.getIDString())) {
				handleNewItem(t);
			} else {
				T item = dataset.get(t.getIDString());
				if (updater.isOutdated(t, item)) {
					handleUpdatedItem(t, item);
				}
			}
			dataset.put(t.getIDString(), t);
		}
		saveLocalData();
		tellSuccess(createNotification());
	}
	
	protected Notification createNotification()
	{
		return null;
	}
	

	protected void handleUpdatedItem(T newItem, T oldItem) {

	}

	protected void handleNewItem(T t) {

	}

	private void list(final ArrayList<T> result, final int index) {
		paginator.setPage(index, RETRIEVE_SIZE);
		APIListener<ArrayList<T>> apiListener = new APIListener<ArrayList<T>>() {

			@Override
			public void onExecute() {
			}

			@Override
			public void onSuccess(ArrayList<T> res) {
				result.addAll(res);
//				if (res.size() == RETRIEVE_SIZE) {
//					list(result, index + 1);
//				} else {
					checkUpdates(res);
//				}
			}

			@Override
			public void onFailure(Exception e) {
				tellFailure(e);
			}
		};
		paginator.setPaginatorListener(apiListener);
		paginator.executePaging();
	}

	public Paginator<T> getPaginator() {
		return paginator;
	}

	public void setPaginator(Paginator<T> paginator) {
		this.paginator = paginator;
	}

}
