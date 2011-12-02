package com.morgan.design.android.broadcast;

import static com.morgan.design.android.util.ObjectUtils.isNotNull;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.morgan.design.android.service.YahooWeatherLoaderService;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.PreferenceUtils;

public class MyStartupIntentReceiver extends BroadcastReceiver {

	public static final String FROM_BOOT = "FROM_BOOT";

	@Override
	public void onReceive(final Context context, final Intent intent) {
		Logger.d("MyStartupIntentReceiver", "MyStartupIntentReceiver recieved [%s]", intent.getAction());

		if (PreferenceUtils.shouldStartOnBoot(context)) {
			final Intent service = new Intent(context, YahooWeatherLoaderService.class);

			if (isNotNull(intent)) {
				final Bundle extras = intent.getExtras();
				if (isNotNull(extras)) {
					final String woeidId = intent.getStringExtra(YahooWeatherLoaderService.CURRENT_WEATHER_WOEID);
					Logger.d("LaunchReceiver", "LaunchReceiver recieved intent containing CURRENT_WEATHER_WOEID [%s]", woeidId);
					service.putExtra(YahooWeatherLoaderService.CURRENT_WEATHER_WOEID, woeidId);
				}
			}

			service.putExtra(FROM_BOOT, true);
			context.startService(service);
		}

	}

}
