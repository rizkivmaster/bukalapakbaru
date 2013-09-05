package notification;

import model.business.Credential;
import model.business.Transaction;
import pagination.Updater;
import updater.TransactionUpdater;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import api.ListTransaction;
import bukalapak.view.MainWindowActivity;

import com.bukalapakdummy.R;

public class TransactionNotificator extends Notificator<Transaction> {
	int newitemCount;
	int updatedCount;
	Context context;

	public TransactionNotificator(Context c,
			Credential cre) {
		super(null, c, null, cre);
		context = c;
		TransactionUpdater updater = new TransactionUpdater();
		setUpdater(updater);
		ListTransaction paginator = new ListTransaction(c, cre);
		setPaginator(paginator);
	}

	@Override
	protected void update() {
		super.update();
		newitemCount = 0;
		updatedCount = 0;
	}

	@Override
	protected Notification createNotification() {
		Intent intent = new Intent(context,  MainWindowActivity.class);
	    PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

		Notification.Builder noti = new Notification.Builder(context);
        noti.setContentTitle("Transaction Notification");
        if(newitemCount>0&& updatedCount>0)
        {
        noti.setContentText("You got "+newitemCount+" new and "+updatedCount+" updated transactions" );
        }
        else if(newitemCount>0)
        {
        	noti.setContentText("You got "+newitemCount+" new transactions" );
        }
        else if(updatedCount>0)
        {
        	noti.setContentText("You got "+updatedCount+" updated transactions" );	
        }
        else
        {
        	return null;
        }
        return noti.setContentIntent(pIntent).getNotification();
	}

	@Override
	protected void handleNewItem(Transaction t) {
		super.handleNewItem(t);
		newitemCount++;
	}

	@Override
	protected void handleUpdatedItem(Transaction newItem, Transaction oldItem) {
		super.handleUpdatedItem(newItem, oldItem);
		updatedCount++;
	}

}
