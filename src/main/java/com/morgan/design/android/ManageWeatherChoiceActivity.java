package com.morgan.design.android;

import static com.morgan.design.android.util.ObjectUtils.isNotNull;
import static com.morgan.design.android.util.ObjectUtils.isNull;

import java.util.List;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.morgan.design.R;
import com.morgan.design.android.adaptor.CurrentChoiceAdaptor;
import com.morgan.design.android.dao.WoeidChoiceDao;
import com.morgan.design.android.domain.orm.WoeidChoice;
import com.morgan.design.android.repository.DatabaseHelper;
import com.morgan.design.android.service.YahooWeatherLoaderService;
import com.morgan.design.android.util.DateUtils;
import com.morgan.design.android.util.Logger;

public class ManageWeatherChoiceActivity extends OrmLiteBaseListActivity<DatabaseHelper> {

	// FIXME -> google analytics
	// FIXME -> add provider
	// FIXME -> paid version

	private static final String LOG_TAG = "ManageWeatherChoiceActivity";

	public static final String LATEST_WEATHER_QUERY_COMPLETE = "com.morgan.design.intent.COMPLETED_LATEST_WEATHER_LOAD";
	public static final int ENTER_LOCATION = 1;
	public static final int SELECT_LOCATION = 2;

	private WoeidChoiceDao woeidChoiceDao;

	private List<WoeidChoice> woeidChoices;

	private CurrentChoiceAdaptor adaptor;

	private NotificationManager notificationManager;

	private BroadcastReceiver broadcastReceiver;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_choice_layout);
		this.woeidChoiceDao = new WoeidChoiceDao(getHelper());

		this.woeidChoices = this.getWoeidChoiceDao().findAllWoeidChoices();
		if (this.woeidChoices.isEmpty()) {
			onAddNewLocation(null);
		}
		else {
			this.adaptor = new CurrentChoiceAdaptor(this, this.woeidChoices);
			setListAdapter(this.adaptor);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isNull(this.broadcastReceiver)) {
			this.broadcastReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(final Context context, final Intent intent) {
					reLoadWoeidChoices();
				}
			};
			registerReceiver(this.broadcastReceiver, new IntentFilter(LATEST_WEATHER_QUERY_COMPLETE));
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (isNotNull(this.broadcastReceiver)) {
			unregisterReceiver(this.broadcastReceiver);
			this.broadcastReceiver = null;
		}
	}

	public void onAddNewLocation(final View view) {
		final Intent intent = new Intent(this, EnterLocationActivity.class);
		startActivityForResult(intent, ENTER_LOCATION);
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case ENTER_LOCATION:
				if (resultCode == RESULT_OK) {
					Logger.d(LOG_TAG, "ENTER_LOCATION -> RESULT_OK");
				}
				else if (resultCode == RESULT_CANCELED) {
					Logger.d(LOG_TAG, "ENTER_LOCATION -> RESULT_CANCELED");
				}
				break;
			case SELECT_LOCATION:
				if (resultCode == RESULT_OK) {
					Logger.d(LOG_TAG, "SELECT_LOCATION -> RESULT_OK");
				}
				else if (resultCode == RESULT_CANCELED) {
					Logger.d(LOG_TAG, "SELECT_LOCATION -> RESULT_CANCELED");
				}
				break;
			default:
				break;
		}
	}

	private void reLoadWoeidChoices() {
		this.woeidChoices = this.getWoeidChoiceDao().findAllWoeidChoices();
		this.adaptor = new CurrentChoiceAdaptor(this, this.woeidChoices);
		setListAdapter(this.adaptor);
	}

	@Override
	protected void onListItemClick(final ListView l, final View v, final int position, final long id) {
		super.onListItemClick(l, v, position, id);
		final WoeidChoice woeidChoice = this.woeidChoices.get(position);

		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Manage Location");
		alertDialog.setCancelable(false);

		final String dialogText =
				" Location:\n" + woeidChoice.getCurrentLocationText() + "\nLast updated:\n"
					+ DateUtils.dateToSimpleDateFormat(woeidChoice.getLastUpdatedDateTime());
		alertDialog.setMessage(dialogText);

		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Load", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				loadWoeidLocation(woeidChoice);
			}
		});
		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Remove", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				getWoeidChoiceDao().delete(woeidChoice);
				attemptToKillNotifcation(woeidChoice);
				removeItemFromList(woeidChoice);
			}
		});
		alertDialog.show();
	}

	protected void loadWoeidLocation(final WoeidChoice woeidChoice) {
		final Bundle bundle = new Bundle();
		bundle.putSerializable(YahooWeatherLoaderService.CURRENT_WEATHER_WOEID, woeidChoice.getWoeid());

		final Intent intent = new Intent(this, YahooWeatherLoaderService.class);
		intent.putExtras(bundle);

		startService(intent);
	}

	protected void removeItemFromList(final WoeidChoice woeidChoice) {
		this.adaptor.remove(woeidChoice);
	}

	protected void attemptToKillNotifcation(final WoeidChoice woeidChoice) {
		if (isNull(this.notificationManager)) {
			this.notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		}
		this.notificationManager.cancel(woeidChoice.getLastknownNotifcationId());
	}

	protected WoeidChoiceDao getWoeidChoiceDao() {
		return this.woeidChoiceDao;
	}
}
