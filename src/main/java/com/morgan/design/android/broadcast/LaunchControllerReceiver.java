package com.morgan.design.android.broadcast;

import static com.morgan.design.Constants.ANDROID_CONNECTIVITY_CHANGE_BROADCAST;
import static com.morgan.design.Constants.RELOAD_WEATHER_BROADCAST;
import static com.morgan.design.android.util.ObjectUtils.isNotNull;
import static com.morgan.design.android.util.ObjectUtils.isNull;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.morgan.design.android.util.Logger;

public class LaunchControllerReceiver extends BroadcastReceiver {

	private static final String LOG_TAG = "LaunchController";

	@Override
	public void onReceive(final Context context, final Intent intent) {
		Logger.d(LOG_TAG, "LaunchControllerReceiver recieved action intent [%s]", getAction(intent));

		if (isNotNull(intent)) {

			if (isPhoneStartUpBroadcast(intent)) {
				launchWeatherServiceIfStartOnBootEnabled(context, intent);
			}
			else if (isUserPresentBroadcast(intent)) {
				// FIXME -> add ability to launch service from wake up i.e. unlock
			}
			else if (isPhoneConnectivityChangedBroadcast(intent)) {
				lauchConnectivityChangeWeatherServiceRequest(context, intent);
			}
			else if (isReloadWeatherBroadcast(intent)) {
				launchReloadWeatherService(context, intent);
			}

		}
	}

	private void launchWeatherServiceIfStartOnBootEnabled(final Context context, final Intent intent) {
		Logger.d(LOG_TAG, "Recieved phone boot broadcast, initiating weather service load");

		// final Intent service = new Intent(context, YahooWeatherLoaderService.class);
		// service.putExtra(PHONE_BOOT, true);
		// context.startService(service);
	}

	private void lauchConnectivityChangeWeatherServiceRequest(final Context context, final Intent intent) {
		Logger.d(LOG_TAG, "Recieved connectivity changed broadcast, initiating weather service refresh");

		// final Intent service = new Intent(context, YahooWeatherLoaderService.class);
		// service.putExtra(CONNECTIVITY_CHANGED, true);
		// context.startService(service);
	}

	private void launchReloadWeatherService(final Context context, final Intent intent) {
		Logger.d(LOG_TAG, "Recieved reload weather service broadcast, initiating weather service load");

		// final Intent service = new Intent(context, YahooWeatherLoaderService.class);
		// service.putExtra(FROM_LAUNCHER, true);
		// context.startService(service);
	}

	public static boolean isPhoneStartUpBroadcast(final Intent intent) {
		if (isNull(intent)) {
			return false;
		}
		return Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction());
	}

	public static boolean isPhoneConnectivityChangedBroadcast(final Intent intent) {
		if (isNull(intent)) {
			return false;
		}
		return intent.getAction().equals(ANDROID_CONNECTIVITY_CHANGE_BROADCAST);
	}

	public static boolean isReloadWeatherBroadcast(final Intent intent) {
		if (isNull(intent)) {
			return false;
		}
		return intent.getAction().equals(RELOAD_WEATHER_BROADCAST);
	}

	private boolean isUserPresentBroadcast(final Intent intent) {
		if (isNull(intent)) {
			return false;
		}
		return Intent.ACTION_USER_PRESENT.equals(intent.getAction());
	}

	private String getAction(final Intent intent) {
		if (isNull(intent)) {
			return "N/A";
		}
		return intent.getAction();
	}
}
