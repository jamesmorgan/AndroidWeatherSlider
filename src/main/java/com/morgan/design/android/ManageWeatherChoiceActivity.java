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
import com.morgan.design.android.dao.WoeidChoiceDao;
import com.morgan.design.android.domain.orm.WoeidChoice;
import com.morgan.design.android.repository.DatabaseHelper;
import com.morgan.design.android.service.YahooWeatherLoaderService;
import com.morgan.design.android.util.DateUtils;
import com.morgan.design.android.util.GoogleAnalyticsService;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.PreferenceUtils;
import com.morgan.design.android.util.TimeUtils;
import com.weatherslider.morgan.design.R;

public class ManageWeatherChoiceActivity extends OrmLiteBaseListActivity<DatabaseHelper> implements SimpleGestureListener {

	// FIXME -> paid version
	// FIXME -> add provider
	// FIXME -> localisation
	// FIXME -> add flags for each country
	// FIXME -> add option for date format
	// FIXME -> debug mode on/off for deployment
	// FIXME -> Configure build process, updating versions/ debug enabled/ keys/ changelog etc
	// FIXME -> Change default preferences before go-live
	// FIXME -> Show location on a small map in overview mode?
	// FIXME -> Open location on map from notification click handler
	// FIXME -> cancel all active notifications
	// FIXME -> turn sun set and rise in time objects
	// FIXME -> Capture all shared preference values on application crash (ARCA)
	// FIXME -> Improve current notification layout
	// FIXME -> Allow users to narrow down location accuracy
	// FIXME -> new providers, Google / The Weather Channel - http://www.weather.com/services/xmloap.html
	// FIXME -> Consider Android Annotations (http://code.google.com/p/androidannotations/)
	// FIXME -> If GPS/network is disabled, prompt user to turn it on -> http://advback.com/android/checking-if-gps-is-enabled-android/
	// FIXME -> Potentially show accuracy and time when looking up GPS location
	// FIXME -> Option to disable collecting of logs via ACRA

	// FIXME -> Ensure notification tab is always updated
	// FIXME -> Fix loading
	// FIXME -> Ensure all necessary data is being stored/recorded correctly
	// FIXME -> DB Versioning
	// FIXME -> change tabs for view pager -> http://blog.stylingandroid.com/archives/537
	// FIXME -> Fix bug when parsing RFC822 dates from yahoo

	// FIXME -> Add ability to pay for application in-line

	// FIXME -> Pro-guard bug when running on real phone -> Update latest maven-android-plugin - 3.0.1

	// Friday
	// FIXME -> Always update notification when weather service lookup finished

	// Thursday
	// FIXME -> Get WOEID from current lat/long

	// FIXME -> Allow for roaming location based settings (paid version only) -
	// http://android-developers.blogspot.com/2011/06/deep-dive-into-location.html?m=1

	// FIXME -> Ensure all notifications always update correctly
	// FIXME -> Listen for 3rd party GPS position updated so can notifications if required.

	// FIXME -> Improve progress/interaction when looking up GPS location
	// FIXME -> Handle situations when no locations found
	// FIXME -> Investigate how to ensure service is always started/on
	// FIXME -> Add ability to launch service on phone start up

	// FIXME -> Allow re-loading of existing notifications
	// FIXME -> Allow removal of any notification via home screen
	// FIXME -> Show active state on home screen
	// FIXME -> Ensure correct service IDs matched against current weather

	// FIXME -> DONE - Record service and current notification in application
	// FIXME -> DONE - Update Overview screen to lookup weather
	// FIXME -> DONE - allow only maximum number of 3 active notifications at once
	// FIXME -> DONE - allow for multiple notifications at once (paid version only)
	// FIXME -> DONE - Check for battery level (15%), active networks (GPS and Mobile), location age
	// FIXME -> DONE - improve notification when no locations found
	// FIXME -> DONE - Created none-bindable intent based service
	// FIXME -> DONE - Add in and setup crash report handler (http://code.google.com/p/acra/), Customised with toast and logcat logs
	// FIXME -> DONE - Update latest ORMLite version - 4.31
	// FIXME -> DONE - Change log pop-up and menu option
	// FIXME -> DONE - Feedback log pop-up on new version
	// FIXME -> DONE - Setup merchant account on google
	// FIXME -> DONE - Added google analytics jar and initial tracking calls
	// FIXME -> DONE - download new icon sets, weather channel icons
	// FIXME -> DONE - add option for wind unit e.g. kmh/mph
	// FIXME -> DONE - add location information to overview screen
	// FIXME -> DONE - Improve notification layout
	// FIXME -> DONE - Condition code translation class
	// FIXME -> DONE - Convert all types correctly, dates, ints, floats, longs etc
	// FIXME -> DONE - add swipe gesture to overview tabs
	// FIXME -> DONE - custom tab view with xml skin
	// FIXME -> DONE - Add Link to more information on Overview screen
	// FIXME -> DONE - Add visibility/pressure/pressure rising to query and overview screen
	// FIXME -> DONE - Add option for C or F when retrieving temperature
	// FIXME -> DONE - improved error messages for no connection found for GPS and weather query
	// FIXME -> DONE - handle on click event notification event. opening overview, initial design
	// FIXME -> DONE - Create converter for decimal degrees to human readable wind direction
	// FIXME -> DONE - Add icons over view sun set and rise
	// FIXME -> DONE - Ensure Home screen is always updated on application focus/launch
	// FIXME -> DONE - Query Yahoo based on GPS location (paid version only)
	// FIXME -> DONE - GPS based location finding (paid version only)
	// FIXME -> DONE - on preferences change notification service click handler
	// FIXME -> DONE - on preferences change loader service polling options
	// FIXME -> DONE - On click notification user preference (paid version only)
	// FIXME -> DONE - start service on phone boot boot up (paid version only)
	// FIXME -> DONE - start last known service on open (paid version only)
	// FIXME -> DONE - swipe navigation path - (add to manual)
	// FIXME -> DONE - periodically query for weather
	// FIXME -> DONE - check phone has Internet before launching

	private static final String LOG_TAG = "ManageWeatherChoiceActivity";

	private WoeidChoiceDao woeidChoiceDao;

	private List<WoeidChoice> woeidChoices;

	private CurrentChoiceAdaptor adaptor;

	private SimpleGestureFilter detector;
	private BroadcastReceiver weatherQueryCompleteBroadcastReceiver;

	private GoogleAnalyticsService googleAnalyticsService;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_choice_layout);
		this.woeidChoiceDao = new WoeidChoiceDao(getHelper());
		this.detector = new SimpleGestureFilter(this, this);
		this.detector.setEnabled(true);
		this.googleAnalyticsService = getToLevelApplication().getGoogleAnalyticsService();

		Changelog.show(this, false);
		reLoadWoeidChoices();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isNull(this.weatherQueryCompleteBroadcastReceiver)) {
			this.weatherQueryCompleteBroadcastReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(final Context context, final Intent intent) {
					final Bundle extras = intent.getExtras();
					if (isNotNull(extras)) {
						final boolean successful = extras.getBoolean(Constants.SUCCESSFUL);
						if (successful) {
							reLoadWoeidChoices();
						}
						else {
							displayFailedQuery();
						}
					}
				}
			};
			registerReceiver(this.weatherQueryCompleteBroadcastReceiver, new IntentFilter(Constants.LATEST_WEATHER_QUERY_COMPLETE));
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (isNotNull(this.weatherQueryCompleteBroadcastReceiver)) {
			unregisterReceiver(this.weatherQueryCompleteBroadcastReceiver);
			this.weatherQueryCompleteBroadcastReceiver = null;
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
		final WoeidChoice woeidChoice = this.woeidChoices.get(position);

		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Manage Location");
		final String dialogText =
				"Location:\n" + woeidChoice.getCurrentLocationText() + "\nLast updated:\n"
					+ DateUtils.dateToSimpleDateFormat(woeidChoice.getLastUpdatedDateTime());
		alertDialog.setMessage(dialogText);
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Load", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				onLoadWoeidLocation(woeidChoice);
			}
		});
		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Remove", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				onRemoveNotiifcaiton(woeidChoice);
			}
		});
		alertDialog.show();
	}

	// ///////////////////////////////////////
	// ///////////// Click Handlers //////////
	// ///////////////////////////////////////

	public void onAddNewLocation(final View view) {
		// final Intent intent = new Intent(this, EnterLocationActivity.class);
		final Intent intent = new Intent(this, EnterLocationActivity.class);
		startActivityForResult(intent, Constants.ENTER_LOCATION);
	}

	protected void onRemoveNotiifcaiton(final WoeidChoice woeidChoice) {
		this.woeidChoiceDao.delete(woeidChoice);
		attemptToKillNotifcation(woeidChoice);
		removeItemFromList(woeidChoice);
	}

	protected void onLoadWoeidLocation(final WoeidChoice woeidChoice) {
		final Bundle bundle = new Bundle();
		bundle.putSerializable(Constants.CURRENT_WEATHER_WOEID, woeidChoice.getWoeid());

		final Intent intent = new Intent(this, YahooWeatherLoaderService.class);
		intent.putExtras(bundle);

		startService(intent);
	}

	// //////////////////////////////////////////
	// ///////////// General / Utility //////////
	// //////////////////////////////////////////

	private void displayFailedQuery() {
		final String schedule = TimeUtils.convertMinutesHumanReadableTime(PreferenceUtils.getPollingSchedule(this));
		Toast.makeText(this, String.format("Unable to get weather details at present, will try again in %s", schedule), Toast.LENGTH_SHORT)
			.show();
	}

	private void reLoadWoeidChoices() {
		this.woeidChoices = this.woeidChoiceDao.findAllWoeidChoices();
		this.adaptor = new CurrentChoiceAdaptor(this, this.woeidChoices);
		setListAdapter(this.adaptor);
	}

	private void removeItemFromList(final WoeidChoice woeidChoice) {
		this.adaptor.remove(woeidChoice);
	}

	private void attemptToKillNotifcation(final WoeidChoice woeidChoice) {
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
