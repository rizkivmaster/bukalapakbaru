package bukalapak.view;

import pagination.PageLoaderListener;
import pagination.Refreshable;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;
import bukalapak.view.lapak.available.LapakDijualFragment;
import bukalapak.view.upload.UploadFragment;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.bukalapakdummy.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * This activity is an example of a responsive Android UI. On phones, the
 * SlidingMenu will be enabled only in portrait mode. In landscape mode, it will
 * present itself as a dual pane layout. On tablets, it will will do the same
 * general thing. In portrait mode, it will enable the SlidingMenu, and in
 * landscape mode, it will be a dual pane layout.
 * 
 * @author jeremy
 * 
 */
public class MainWindowActivity extends SlidingFragmentActivity {
	private Fragment mContent;
	public Callback mCallback;
	private boolean isRefreshing;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String[] list_menu = getResources().getStringArray(R.array.slide_menu);
		setTitle(list_menu[1]);

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
		// setTitle(R.string.responsive_ui);
		// setTitle(int ResID);
		/*
		 * int pos = 0; if (getIntent().getExtras() != null) { pos =
		 * getIntent().getExtras().getInt("pos"); }
		 * Toast.makeText(MainWindowActivity.this, ""+pos,
		 * Toast.LENGTH_SHORT).show(); String[] birds =
		 * getResources().getStringArray(R.array.birds); TypedArray imgs =
		 * getResources().obtainTypedArray(R.array.birds_img); int resId =
		 * imgs.getResourceId(pos, -1);
		 * 
		 * setTitle(birds[pos]);
		 */

		setContentView(R.layout.responsive_content_frame);

		// check if the content frame contains the menu frame
		if (findViewById(R.id.menu_frame) == null) {
			setBehindContentView(R.layout.menu_frame);
			getSlidingMenu().setSlidingEnabled(true);
			getSlidingMenu()
					.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			// show home as up so we can toggle
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		} else {
			// add a dummy view
			View v = new View(this);
			setBehindContentView(v);
			getSlidingMenu().setSlidingEnabled(false);
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}

		// set the Above View Fragment
		if (savedInstanceState != null) {
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
			// setTitle("tidak null");
		}

		if (mContent == null) {
			mContent = new LapakDijualFragment();
			// setTitle("null");
		}
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent).commit();

		// set the Behind View Fragment
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new MenuFragment()).commit();

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindScrollScale(0.25f);
		sm.setFadeDegree(0.25f);

		// show the explanation dialog
		/*
		 * if (savedInstanceState == null) new AlertDialog.Builder(this)
		 * .setTitle(R.string.what_is_this)
		 * .setMessage(R.string.responsive_explanation) .show();
		 */
		setSlidingActionBarEnabled(false);
		// poster();
		// test();
	}

	/*
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { switch
	 * (item.getItemId()) { case android.R.id.home: toggle(); } return
	 * super.onOptionsItemSelected(item); }
	 */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
		case R.id.github:
			startActivity(new Intent(this, UploadFragment.class));
			return true;
		case R.id.refresh:
			item.setActionView(R.layout.action_bar_indeterminate_progress);
			refreshPage();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		menu.findItem(R.id.refresh).setVisible(mContent instanceof Refreshable);
		return true;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}

	public void switchContent(final Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
		Handler h = new Handler();
		h.postDelayed(new Runnable() {
			public void run() {
				getSlidingMenu().showContent();
			}
		}, 50);

		invalidateOptionsMenu();
	}

	/*
	 * 
	 * public void onBirdPressed(int pos) { Intent intent =
	 * BirdActivity.newInstance(this, pos); startActivity(intent);
	 * 
	 * }
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

//	private void test() {
//		// Start Service On Boot Start Up
//		Intent newintent = new Intent(getApplicationContext(),
//				NotificationService.class);
//		PendingIntent pendingIntent = PendingIntent.getService(
//				getApplicationContext(), 0, newintent, 0);
//		SharedPreferences preferences = getApplicationContext()
//				.getSharedPreferences(NAME_PREFERENCE, 0);
//		Editor editor = preferences.edit();
//
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTimeInMillis(System.currentTimeMillis());
//		calendar.add(Calendar.SECOND, 10);
//
//		// if (!preferences.contains(KEY_PREFERENCE)) {
//		editor.putInt(KEY_PREFERENCE, 50);
//		editor.commit();
//		// }
//
//		int seconds = (Integer) preferences.getInt(KEY_PREFERENCE, 0);
//		AlarmManager alarmManager = (AlarmManager) getApplicationContext()
//				.getSystemService(Context.ALARM_SERVICE);
//		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
//				calendar.getTimeInMillis(), seconds * 1000, pendingIntent);
//
//	}
//
//	private void poster() {
//		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//		Intent mainActivityIntent = new Intent(this, UploadFragment.class);
//		PendingIntent pIntent = PendingIntent.getActivity(this, 0,
//				mainActivityIntent, 0);
//
//		Notification notification = new NotificationCompat.Builder(this)
//				.setContentTitle("bothering you")
//				.setContentText("Just bothering you from example code")
//				.setSmallIcon(R.drawable.ic_launcher).setContentIntent(pIntent)
//				.build();
//		notification.flags |= Notification.FLAG_AUTO_CANCEL;
//		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
//		notification.defaults |= Notification.DEFAULT_SOUND;
//		notification.defaults |= Notification.DEFAULT_VIBRATE;
//		notification.defaults |= Notification.DEFAULT_LIGHTS;
//		nm.notify(0, notification);
//	}
	
	public void refreshPage()
	{
		if (mContent instanceof Refreshable) {
			PageLoaderListener refreshListener = new PageLoaderListener() {

				@Override
				public void onSuccess(boolean s) {
					isRefreshing = false;
					invalidateOptionsMenu();
				}

				@Override
				public void onStart() {
					isRefreshing = true;
					invalidateOptionsMenu();
				}

				@Override
				public void onFailure(Exception e) {
					isRefreshing = false;
					invalidateOptionsMenu();
					Toast.makeText(MainWindowActivity.this, e.getMessage(), Toast.LENGTH_SHORT)
							.show();
				}
			};
			((Refreshable) mContent).refresh(refreshListener);
		}
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if(isRefreshing)
		{
			menu.findItem(R.id.refresh).setActionView(R.layout.action_bar_indeterminate_progress);
		}
		return super.onPrepareOptionsMenu(menu);
	}
		
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		refreshPage();
	}
	
	


}
