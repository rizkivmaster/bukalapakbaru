package bukalapak.view;

import android.content.Context;
import android.widget.Toast;
import exception.SystemBusyException;

public class GlobarErrorHandling {
	public static void handle(Context context, Exception e) {
		if (e instanceof SystemBusyException) {
			showToast(context, "Sistem sedang sibuk. Coba sekali lagi");
		} else {
			showToast(context, "Coba sekali lagi");
		}
	}

	private static void showToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
}
