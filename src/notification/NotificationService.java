package notification;

import java.util.ArrayList;

import model.business.Credential;
import model.business.CredentialEditor;
import model.session.OnlineEntity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class NotificationService extends Service {
	Context context;
	Credential credential;
	private ArrayList<Notificator> notificators;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		context = getApplicationContext();
		credential = CredentialEditor.loadCredential(context);
		notificators = new ArrayList<Notificator>();
		notificators.add(new TransactionNotificator(context, credential));
		process(0);
		return super.onStartCommand(intent, flags, startId);
	}

	private void process(final int position) {
		NotificatorListener listener = new NotificatorListener() {

			@Override
			public void onSuccess(Notification result) {
				if (result != null) {
					NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
					notificationManager.notify(0, result);
				}
//				if (position + 1 < notificators.size()) {
//					process(position + 1);
//				}
			}

			@Override
			public void onStart() {
				
			}

			@Override
			public void onFailure(Exception e) {

			}
		};

		Notificator notificator = notificators.get(position);
		notificator.setListener(listener);
		notificator.update();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}