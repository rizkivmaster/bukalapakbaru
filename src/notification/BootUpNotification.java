package notification;

import java.util.Calendar;
import java.util.Map;

import notification.NotificationService;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class BootUpNotification extends BroadcastReceiver {
	private final static String NAME_PREFERENCE = "notification_time";
	private final static String KEY_PREFERENCE = "seconds";

	@Override
	public void onReceive(Context context, Intent intent) {
		// Start Service On Boot Start Up
		Intent newintent = new Intent(context, NotificationService.class);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0,
				newintent, 0);
		SharedPreferences preferences = context.getSharedPreferences(
				NAME_PREFERENCE, 0);
		Editor editor = preferences.edit();
		
		Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 10);
        
        

		if (!preferences.contains(KEY_PREFERENCE)) {
			editor.putInt(KEY_PREFERENCE, 10);
			editor.commit();
		}
		
		int seconds = (Integer) preferences.getInt(KEY_PREFERENCE,0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), seconds*1000, pendingIntent);

	}
}
