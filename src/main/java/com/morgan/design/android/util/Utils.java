package com.morgan.design.android.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.provider.Settings;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.morgan.design.AboutActivity;
import com.morgan.design.FeedbackFormActivity;
import com.morgan.design.android.domain.types.Abrev;
import com.weatherslider.morgan.design.R;

/**
 * @author James Edward Morgan
 */
public class Utils {

	private static final String LOG_TAG = "Utils";

	public static View dialogWebView(final Context context, final String fileName) {
		final View view = View.inflate(context, R.layout.dialog_webview, null);
		final WebView web = (WebView) view.findViewById(R.id.wv_dialog);
		web.loadUrl("file:///android_asset/" + fileName);
		return view;
	}

	public static void openFeedback(final Activity activity) {
		try {
			activity.startActivity(new Intent(activity, FeedbackFormActivity.class));
		}
		catch (final Exception e) {
			shortToast(activity, "Unable to open feedback");
			Logger.e(LOG_TAG, "Unable to open feedback: ", e);
		}
	}

	public static void openAbout(final Activity activity) {
		try {
			activity.startActivity(new Intent(activity, AboutActivity.class));
		}
		catch (final Exception e) {
			shortToast(activity, "Unable to about activity");
			Logger.e(LOG_TAG, "Unable to about activity: ", e);
		}
	}

	public static void openGoogleMaps(final Activity activity, final String latitude, final String longitude) {
		try {
			final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("geo:%s,%s?z=15", latitude, longitude)));
			activity.startActivity(intent);
		}
		catch (final Exception e) {
			shortToast(activity, "Unable to open google maps");
			Logger.e(LOG_TAG, "Unable to open google maps: ", e);
		}
	}

	public static void openUrl(final Activity activity, final String url) {
		try {
			activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
		}
		catch (final Exception e) {
			Utils.shortToast(activity, "Unable to open URL");
			Logger.e(LOG_TAG, "Unable to open URL: [%s], excpetion= [%s]", url, e.getMessage());
		}
	}

	public static void shortToast(final Activity activity, final CharSequence message) {
		Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * @return double the percentage of battery remaining
	 */
	public static double getBatteryLevel(final Context context) {
		final Intent batteryIntent =
				context.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		final int rawlevel = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		final double scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		double level = -1;
		if (rawlevel >= 0 && scale > 0) {
			level = (rawlevel * 100) / scale;
		}
		return level;
	}

	/**
	 * @return true if an active network connection if found, either connected or connecting
	 */
	public static boolean isConnectedOrConnecting(final Context context) {
		final NetworkInfo activeNetwork =
				((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
	}

	/**
	 * @return true if the GPS setting is enabled for the phone
	 */
	public static boolean isGpsEnabled(final ContentResolver contentResolver) {
		final String gpsProviders = Settings.Secure.getString(contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		return (null != gpsProviders && !"".equals(gpsProviders));
	}

	public static final String abrev(final Abrev abrev) {
		return null != abrev
				? abrev.abrev()
				: "";
	}

}
