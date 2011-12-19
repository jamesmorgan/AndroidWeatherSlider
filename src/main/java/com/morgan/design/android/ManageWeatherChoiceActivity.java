package com.morgan.design.android;

import static com.morgan.design.Constants.LAST_KNOWN_SERVICE_ID;
import static com.morgan.design.Constants.REMOVE_CURRENT_NOTIFCATION;
import static com.morgan.design.android.util.ObjectUtils.isNotNull;
import static com.morgan.design.android.util.ObjectUtils.isNull;

import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.morgan.design.Changelog;
import com.morgan.design.Constants;
import com.morgan.design.WeatherSliderApplication;
import com.morgan.design.android.SimpleGestureFilter.SimpleGestureListener;
import com.morgan.design.android.adaptor.CurrentChoiceAdaptor;
import com.morgan.design.android.dao.WeatherChoiceDao;
import com.morgan.design.android.domain.orm.WeatherChoice;
import com.morgan.design.android.repository.DatabaseHelper;
import com.morgan.design.android.service.YahooWeatherLoaderService;
import com.morgan.design.android.util.DateUtils;
import com.morgan.design.android.util.GoogleAnalyticsService;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.PreferenceUtils;
import com.morgan.design.android.util.TimeUtils;
import com.weatherslider.morgan.design.R;

public class ManageWeatherChoiceActivity extends OrmLiteBaseListActivity<DatabaseHelper> implements SimpleGestureListener {

	private static final String LOG_TAG = "ManageWeatherChoiceActivity";

	private WeatherChoiceDao woeidChoiceDao;

	private List<WeatherChoice> woeidChoices;

	private CurrentChoiceAdaptor adaptor;

	private SimpleGestureFilter detector;

	private BroadcastReceiver weatherQueryCompleteBroadcastReceiver;
	private BroadcastReceiver notificationRemovedBroadcastReciever;

	private GoogleAnalyticsService googleAnalyticsService;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_choice_layout);
		this.woeidChoiceDao = new WeatherChoiceDao(getHelper());
		this.detector = new SimpleGestureFilter(this, this);
		this.detector.setEnabled(true);
		this.googleAnalyticsService = getToLevelApplication().getGoogleAnalyticsService();

		Changelog.show(this, false);
		reLoadWeatherChoices();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isNull(this.weatherQueryCompleteBroadcastReceiver)) {
			this.weatherQueryCompleteBroadcastReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(final Context context, final Intent intent) {
					Logger.d(LOG_TAG, "Recieved : %s", intent.getAction());
					final Bundle extras = intent.getExtras();
					if (isNotNull(extras)) {
						final boolean successful = extras.getBoolean(Constants.SUCCESSFUL);
						if (successful) {
							reLoadWeatherChoices();
						}
						else {
							displayFailedQuery();
						}
					}
				}
			};
			registerReceiver(this.weatherQueryCompleteBroadcastReceiver, new IntentFilter(Constants.LATEST_WEATHER_QUERY_COMPLETE));
		}

		if (isNull(this.notificationRemovedBroadcastReciever)) {
			this.notificationRemovedBroadcastReciever = new BroadcastReceiver() {
				@Override
				public void onReceive(final Context context, final Intent intent) {
					Logger.d(LOG_TAG, "Recieved : %s", intent.getAction());
					reLoadWeatherChoices();
				}
			};
			registerReceiver(this.notificationRemovedBroadcastReciever, new IntentFilter(Constants.NOTIFICATION_REMOVED));
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (isNotNull(this.weatherQueryCompleteBroadcastReceiver)) {
			unregisterReceiver(this.weatherQueryCompleteBroadcastReceiver);
			this.weatherQueryCompleteBroadcastReceiver = null;
		}
		if (isNotNull(this.notificationRemovedBroadcastReciever)) {
			unregisterReceiver(this.notificationRemovedBroadcastReciever);
			this.notificationRemovedBroadcastReciever = null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home_menu, menu);
		return true;
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
			default:
				return super.onOptionsItemSelected(item);
		}
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
				}
				break;
			default:
				break;
		}
	}

	@Override
	protected void onListItemClick(final ListView l, final View v, final int position, final long id) {
		super.onListItemClick(l, v, position, id);
		final WeatherChoice woeidChoice = this.woeidChoices.get(position);

		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Manage Location");
		final String dialogText =
				"Location:\n" + woeidChoice.getCurrentLocationText() + "\nLast updated:\n"
					+ DateUtils.dateToSimpleDateFormat(woeidChoice.getLastUpdatedDateTime());
		alertDialog.setMessage(dialogText);

		if (woeidChoice.isActive()) {
			alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Remove", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int id) {
					onRemoveWeatherChoice(woeidChoice);
				}
			});
		}
		else {
			alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Load", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int id) {
					onLoadWeatherChoice(woeidChoice);
				}
			});
		}

		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Delete", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				onDeleteWeatherChoice(woeidChoice);
			}
		});

		alertDialog.show();

	}

	// ///////////////////////////////////////
	// ///////////// Click Handlers //////////
	// ///////////////////////////////////////

	public void onAddNewLocation(final View view) {
		final Intent intent = new Intent(this, EnterLocationActivity.class);
		startActivityForResult(intent, Constants.ENTER_LOCATION);
	}

	protected void onRemoveWeatherChoice(final WeatherChoice woeidChoice) {
		woeidChoice.setActive(false);
		this.woeidChoiceDao.update(woeidChoice);
		attemptToKillNotifcation(woeidChoice);
	}

	protected void onDeleteWeatherChoice(final WeatherChoice woeidChoice) {
		this.woeidChoiceDao.delete(woeidChoice);
		attemptToKillNotifcation(woeidChoice);
		removeItemFromList(woeidChoice);
	}

	protected void onLoadWeatherChoice(final WeatherChoice woeidChoice) {
		woeidChoice.setActive(true);
		this.woeidChoiceDao.update(woeidChoice);

		final Bundle bundle = new Bundle();
		bundle.putSerializable(Constants.CURRENT_WEATHER_WOEID, woeidChoice.getWoeid());
		startService(new Intent(this, YahooWeatherLoaderService.class).putExtras(bundle));
	}

	// //////////////////////////////////////////
	// ///////////// General / Utility //////////
	// //////////////////////////////////////////

	private void displayFailedQuery() {
		final String schedule = TimeUtils.convertMinutesHumanReadableTime(PreferenceUtils.getPollingSchedule(this));
		Toast.makeText(this, String.format("Unable to get weather details at present, will try again in %s", schedule), Toast.LENGTH_SHORT)
			.show();
	}

	private void reLoadWeatherChoices() {
		this.woeidChoices = this.woeidChoiceDao.findAllWoeidChoices();
		this.adaptor = new CurrentChoiceAdaptor(this, this.woeidChoices);
		setListAdapter(this.adaptor);
	}

	private void removeItemFromList(final WeatherChoice woeidChoice) {
		this.adaptor.remove(woeidChoice);
	}

	private void attemptToKillNotifcation(final WeatherChoice woeidChoice) {
		final Bundle bundle = new Bundle();
		bundle.putInt(LAST_KNOWN_SERVICE_ID, woeidChoice.getLastknownNotifcationId());
		sendBroadcast(new Intent(REMOVE_CURRENT_NOTIFCATION).putExtras(bundle));
	}

	protected WeatherSliderApplication getToLevelApplication() {
		return ((WeatherSliderApplication) getApplication());
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
