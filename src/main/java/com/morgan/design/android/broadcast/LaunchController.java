package com.morgan.design.android.broadcast;

import static com.morgan.design.android.util.ObjectUtils.isNotNull;
import static com.morgan.design.android.util.ObjectUtils.isNull;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.morgan.design.Constants;
import com.morgan.design.android.service.YahooWeatherLoaderService;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.PreferenceUtils;

public class LaunchController extends BroadcastReceiver {

	public static final String RELOAD_WEATHER_BROADCAST = "com.morgan.design.android.broadcast.RELOAD_WEATHER_BROADCAST";
	public static final String ANDROID_CONNECTIVITY_CHANGE_BROADCAST = "android.net.conn.CONNECTIVITY_CHANGE";

	public static final String FROM_BOOT = "FROM_BOOT";
	public static final String FROM_LAUNCHER = "FROM_LAUNCHER";
	public static final String CONNECTIVITY_CHANGED = "CONNECTIVITY_CHANGED";

	@Override
	public void onReceive(final Context context, final Intent intent) {
		Logger.d("LaunchController", "LaunchControllerReceiver recieved action intent [%s]", getAction(intent));

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
		if (PreferenceUtils.shouldStartOnBoot(context)) {
			Logger.d("LaunchController", "Recieved phone boot broadcast, initiating weather service load");
			final Intent service = new Intent(context, YahooWeatherLoaderService.class);
			service.putExtra(FROM_BOOT, true);
			context.startService(service);
		}
	}

	private void lauchConnectivityChangeWeatherServiceRequest(final Context context, final Intent intent) {
		Logger.d("LaunchController", "Recieved connectivity changed broadcast, initiating weather service refresh");

		final Intent service = new Intent(context, YahooWeatherLoaderService.class);
		if (intent.hasExtra(Constants.CURRENT_WEATHER_WOEID)) {
			service.putExtra(Constants.CURRENT_WEATHER_WOEID, intent.getStringExtra(Constants.CURRENT_WEATHER_WOEID));
		}

		service.putExtra(CONNECTIVITY_CHANGED, true);
		context.startService(service);
	}

	private void launchReloadWeatherService(final Context context, final Intent intent) {
		Logger.d("LaunchController", "Recieved reload weather service broadcast, initiating weather service load");

		final Intent service = new Intent(context, YahooWeatherLoaderService.class);
		if (intent.hasExtra(Constants.CURRENT_WEATHER_WOEID)) {
			service.putExtra(Constants.CURRENT_WEATHER_WOEID, intent.getStringExtra(Constants.CURRENT_WEATHER_WOEID));
		}

		service.putExtra(FROM_LAUNCHER, true);
		context.startService(service);
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
