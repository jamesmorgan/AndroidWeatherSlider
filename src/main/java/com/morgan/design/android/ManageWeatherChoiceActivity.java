package com.morgan.design.android;

import static com.morgan.design.Constants.CANCEL_ALL_WEATHER_NOTIFICATIONS;
import static com.morgan.design.Constants.DELETE_CURRENT_NOTIFCATION;
import static com.morgan.design.Constants.FROM_INACTIVE_LOCATION;
import static com.morgan.design.Constants.NOTIFICATION_ID;
import static com.morgan.design.Constants.OPEN_WEATHER_OVERVIEW;
import static com.morgan.design.Constants.REMOVE_CURRENT_NOTIFCATION;
import static com.morgan.design.Constants.WEATHER_ID;
import static com.morgan.design.android.util.ObjectUtils.isNotNull;
import static com.morgan.design.android.util.ObjectUtils.isNull;

import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.morgan.design.Changelog;
import com.morgan.design.Constants;
import com.morgan.design.RateMe;
import com.morgan.design.WeatherSliderApplication;
import com.morgan.design.android.SimpleGestureFilter.SimpleGestureListener;
import com.morgan.design.android.adaptor.CurrentChoiceAdaptor;
import com.morgan.design.android.analytics.GoogleAnalyticsService;
import com.morgan.design.android.broadcast.IServiceUpdateBroadcaster;
import com.morgan.design.android.broadcast.ServiceUpdateBroadcasterImpl;
import com.morgan.design.android.broadcast.ServiceUpdateReceiver;
import com.morgan.design.android.dao.NotificationDao;
import com.morgan.design.android.dao.WeatherChoiceDao;
import com.morgan.design.android.dao.orm.WeatherChoice;
import com.morgan.design.android.dao.orm.WeatherNotification;
import com.morgan.design.android.domain.types.IconFactory;
import com.morgan.design.android.repository.DatabaseHelper;
import com.morgan.design.android.service.RoamingLookupService;
import com.morgan.design.android.service.StaticLookupService;
import com.morgan.design.android.util.DateUtils;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.PreferenceUtils;
import com.morgan.design.android.util.Utils;
import com.morgan.design.weatherslider.R;

public class ManageWeatherChoiceActivity extends OrmLiteBaseListActivity<DatabaseHelper> implements
		SimpleGestureListener {

	private static final String LOG_TAG = "ManageWeatherChoiceActivity";

	private WeatherChoiceDao weatherDao;

	private List<WeatherChoice> weatherChoices;

	private CurrentChoiceAdaptor adaptor;

	private SimpleGestureFilter detector;

	private BroadcastReceiver updateWeatherListBroadcastReceiver;

	private GoogleAnalyticsService googleAnalyticsService;

	protected ServiceUpdateReceiver serviceUpdateRegister;
	private IServiceUpdateBroadcaster serviceUpdate;

	private NotificationDao notificationDao;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_choice_layout);

		this.weatherDao = new WeatherChoiceDao(getHelper());
		this.notificationDao = new NotificationDao(getHelper());
		this.detector = new SimpleGestureFilter(this, this);
		this.detector.setEnabled(true);
		this.googleAnalyticsService = WeatherSliderApplication.locate(this).getGoogleAnalyticsService();
		this.serviceUpdate = new ServiceUpdateBroadcasterImpl(this);
		this.serviceUpdateRegister = new ServiceUpdateReceiver(this);

		reLoadWeatherChoices();

		if (PreferenceUtils.shouldStartOnBoot(this)) {
			for (final WeatherChoice choice : this.weatherChoices) {
				if (choice.isActive()) {
					onLoadWeatherChoice(choice);
				}
			}
		}

		if (null != this.weatherChoices && !this.weatherChoices.isEmpty()) {
			this.serviceUpdate.onGoing(getString(R.string.service_update_loading_existing_notifications));
			RateMe.showOnFirstSuccess(this);
		}

		// Launch enter screen if no active/existing locations found
		if (null == this.weatherChoices || this.weatherChoices.isEmpty()) {
			onAddNewLocation(null);
		}
		else {
			Changelog.show(this, false);
		}

		getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
				return handleOnItemLongClick(pos);
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isNull(this.updateWeatherListBroadcastReceiver)) {
			this.updateWeatherListBroadcastReceiver = new BroadcastReceiver() {

				@Override
				public void onReceive(final Context context, final Intent intent) {
					Logger.d(LOG_TAG, "Recieved : %s", intent.getAction());
					reLoadWeatherChoices();
				}
			};
			registerReceiver(this.updateWeatherListBroadcastReceiver, new IntentFilter(Constants.UPDATE_WEATHER_LIST));
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (isNotNull(this.updateWeatherListBroadcastReceiver)) {
			unregisterReceiver(this.updateWeatherListBroadcastReceiver);
			this.updateWeatherListBroadcastReceiver = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (isNotNull(this.serviceUpdateRegister)) {
			this.serviceUpdateRegister.unregisterReceiver();
		}
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.home_menu_changelog:
				this.googleAnalyticsService.trackPageView(this, GoogleAnalyticsService.OPEN_CHANGE_LOG);
				Changelog.show(this, true);
				return true;
			case R.id.home_menu_settings:
				this.googleAnalyticsService.trackPageView(this, GoogleAnalyticsService.OPEN_PREFERENCES);
				PreferenceUtils.openUserPreferenecesActivity(this);
				return true;
			case R.id.home_menu_feedback:
				this.googleAnalyticsService.trackPageView(this, GoogleAnalyticsService.OPEN_FEEDBACK);
				Utils.openFeedback(this);
				return true;
			case R.id.home_menu_about:
				this.googleAnalyticsService.trackPageView(this, GoogleAnalyticsService.OPEN_ABOUT);
				Utils.openAbout(this);
				return true;
			case R.id.home_menu_cancel_all:
				this.googleAnalyticsService.trackPageView(this, GoogleAnalyticsService.CANCEL_ALL);
				sendBroadcast(new Intent(CANCEL_ALL_WEATHER_NOTIFICATIONS));
				return true;
			case R.id.home_menu_create_shortcut:
				this.googleAnalyticsService.trackPageView(this, GoogleAnalyticsService.CREATE_HOME_SHORTCUT);
				createShortcut();
				return true;
			case R.id.home_menu_reload_all:
				this.googleAnalyticsService.trackPageView(this, GoogleAnalyticsService.RELOAD_ALL_ACTIVE);
				if (null != this.weatherChoices) {
					for (final WeatherChoice choice : this.weatherChoices) {
						if (choice.isActive()) {
							onLoadWeatherChoice(choice);
						}
					}
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void createShortcut() {
		Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		shortcutIntent.setClassName(this, this.getClass().getName());

		Intent intent = new Intent();
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "WeatherSlider");
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(getResources(), R.drawable.launch_icon), 72, 72, true));
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

		sendBroadcast(intent);
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		menu.clear();
		final MenuInflater inflater = getMenuInflater();
		if (null != this.weatherChoices) {
			for (final WeatherChoice choice : this.weatherChoices) {
				if (choice.isActive()) {
					inflater.inflate(R.menu.home_menu_with_notifcations, menu);
					return true;
				}
			}
		}
		inflater.inflate(R.menu.home_menu, menu);
		return true;
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		this.googleAnalyticsService.trackPageView(this, GoogleAnalyticsService.ADD_NEW_LOCATION);
		switch (requestCode) {
			case Constants.ENTER_LOCATION:
				if (resultCode == RESULT_OK) {
					Logger.d(LOG_TAG, "ENTER_LOCATION -> RESULT_OK");
				}
				break;
			case Constants.SELECT_LOCATION:
				if (resultCode == RESULT_OK) {
					Logger.d(LOG_TAG, "SELECT_LOCATION -> RESULT_OK");
				}
				break;
			case Constants.UPDATED_PREFERENCES:
				if (resultCode == RESULT_OK) {
					sendBroadcast(new Intent(Constants.PREFERENCES_UPDATED));
					this.serviceUpdate.onGoing(getString(R.string.service_update_preferences_changed_updating));
				}
				break;
			default:
				break;
		}
	}

	protected boolean handleOnItemLongClick(int pos) {
		final WeatherChoice weatherChoice = this.weatherChoices.get(pos);

		if (isNotNull(weatherChoice)) {

			final WeatherNotification notification = this.notificationDao.findNotificationForWeatherId(weatherChoice
					.getId());

			if (isNotNull(notification) && weatherChoice.isActive()) {

				final AlertDialog alertDialog = createCurrentAlertDialog(weatherChoice,
						R.string.alert_title_open_overview);
				alertDialog.setCancelable(true);
				alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.alert_open),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(final DialogInterface dialog, final int id) {
								final Intent overviewIntent = new Intent(OPEN_WEATHER_OVERVIEW);
								overviewIntent.putExtra(NOTIFICATION_ID, notification.getServiceId());
								overviewIntent.setClass(getApplicationContext(), WeatherOverviewActivity.class);
								startActivity(overviewIntent);
							}
						});
				alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.alert_cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(final DialogInterface dialog, final int id) {
								alertDialog.cancel();
							}
						});
				alertDialog.show();
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onListItemClick(final ListView l, final View v, final int position, final long id) {
		super.onListItemClick(l, v, position, id);
		final WeatherChoice weatherChoice = this.weatherChoices.get(position);

		throw new UnknownError("Some Exception");

		// final AlertDialog alertDialog =
		// createCurrentAlertDialog(weatherChoice,
		// R.string.alert_title_manage_location);
		// alertDialog.setCancelable(true);
		//
		// if (weatherChoice.isActive()) {
		// // //////////////////////////////
		// // Refresh | Disabled | Delete //
		// // //////////////////////////////
		// alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,
		// getString(R.string.alert_refresh),
		// new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(final DialogInterface dialog, final int id) {
		// onLoadWeatherChoice(weatherChoice);
		// }
		// });
		// alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,
		// getString(R.string.alert_disable),
		// new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(final DialogInterface dialog, final int id) {
		// attemptToKillNotifcation(weatherChoice);
		// }
		// });
		// }
		// else {
		// // //////////////////
		// // Enable | Delete //
		// // //////////////////
		// alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,
		// getString(R.string.alert_enable),
		// new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(final DialogInterface dialog, final int id) {
		// onLoadWeatherChoice(weatherChoice);
		// }
		// });
		// }
		//
		// alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,
		// getString(R.string.alert_delete),
		// new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(final DialogInterface dialog, final int id) {
		// attemptToDeleteNotifcation(weatherChoice);
		// }
		// });
		//
		// alertDialog.show();
	}

	// ///////////////////////////////////////
	// ///////////// Click Handlers //////////
	// ///////////////////////////////////////

	public void onAddNewLocation(final View view) {
		final Intent intent = new Intent(this, EnterLocationActivity.class);
		startActivityForResult(intent, Constants.ENTER_LOCATION);
	}

	protected void onLoadWeatherChoice(final WeatherChoice woeidChoice) {
		final Bundle bundle = new Bundle();
		bundle.putSerializable(WEATHER_ID, woeidChoice.getId());

		final Intent service = woeidChoice.isRoaming() ? new Intent(this, RoamingLookupService.class) : new Intent(
				this, StaticLookupService.class);

		service.putExtra(FROM_INACTIVE_LOCATION, true);
		service.putExtras(bundle);
		startService(service);
	}

	// //////////////////////////////////////////
	// ///////////// General / Utility //////////
	// //////////////////////////////////////////

	private void reLoadWeatherChoices() {
		this.weatherChoices = this.weatherDao.findAllWeathers();
		this.adaptor = new CurrentChoiceAdaptor(this, this.weatherChoices);
		setListAdapter(this.adaptor);
	}

	private void attemptToKillNotifcation(final WeatherChoice woeidChoice) {
		final Bundle bundle = new Bundle();
		bundle.putInt(WEATHER_ID, woeidChoice.getId());
		sendBroadcast(new Intent(REMOVE_CURRENT_NOTIFCATION).putExtras(bundle));
	}

	private void attemptToDeleteNotifcation(final WeatherChoice woeidChoice) {
		final Bundle bundle = new Bundle();
		bundle.putInt(WEATHER_ID, woeidChoice.getId());
		sendBroadcast(new Intent(DELETE_CURRENT_NOTIFCATION).putExtras(bundle));
		this.adaptor.remove(woeidChoice);
	}

	private AlertDialog createCurrentAlertDialog(final WeatherChoice weatherChoice, int titleResource) {
		final String dialogText = weatherChoice.getCurrentLocationText() + "\n"
				+ DateUtils.dateToSimpleDateFormat(weatherChoice.getLastUpdatedDateTime());
		return new AlertDialog.Builder(this)
				.setIcon(IconFactory.getImageResourceFromCode(weatherChoice.getCurrentWeatherCode()))
				.setTitle(getString(titleResource)).setMessage(dialogText).create();
	}

	// /////////////////////////////////////////////
	// ////////// Swipe Gestures ///////////////////
	// /////////////////////////////////////////////

	@Override
	public void onSwipe(final int direction) {
		switch (direction) {
			case SimpleGestureFilter.SWIPE_LEFT:
				onAddNewLocation(null);
				break;
		}
	}

	@Override
	public boolean dispatchTouchEvent(final MotionEvent me) {
		this.detector.onTouchEvent(me);
		return super.dispatchTouchEvent(me);
	}

	@Override
	public void onDoubleTap() {
		// Do nothing at present
	}
}
