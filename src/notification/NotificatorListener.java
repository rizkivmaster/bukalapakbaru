package notification;

import android.app.Notification;

public interface NotificatorListener {
	public abstract void onStart();
	public abstract void onSuccess(Notification result);
	public abstract void onFailure(Exception e);
}
