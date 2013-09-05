package dialog;

import restful.RESTTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.View;
import android.widget.Toast;

public class ProgressDialogManager {
	String title;
	ProgressDialog pd;
	Context context;

	public ProgressDialogManager(String t, Context c) {
		title = t;
		context = c;
	}

	public void startProgress(final RESTTask task) {
		pd = new ProgressDialog(context);
		pd.setTitle(title);
		pd.setMessage("Harap Menunggu");
		pd.setIndeterminate(true);
		pd.setCancelable(task != null);
		if (task != null) {
			pd.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					pd.dismiss();
					if (task != null) {
						task.cancel();
						Toast.makeText(context, "Proses dibatalkan",
								Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
		pd.show();
	}

	public void failedProgress(Exception e) {
		pd.dismiss();
		if (e != null)
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
	}

	public void successProgress(String successMessage) {
		pd.dismiss();
		if (successMessage != null && successMessage.length() > 0)
			Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show();
	}

}
