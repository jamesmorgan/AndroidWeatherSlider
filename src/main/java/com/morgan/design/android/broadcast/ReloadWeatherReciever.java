package com.morgan.design.android.broadcast;

import static com.morgan.design.Constants.RELOAD_WEATHER_BROADCAST;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class ReloadWeatherReciever {

	private final OnReloadWeather onReloadWeather;
	private final Context context;

	private final BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {
			ReloadWeatherReciever.this.onReloadWeather.onReload();
		}
	};

	public ReloadWeatherReciever(final Context context, final OnReloadWeather onReloadWeather) {
		this.context = context;
		this.onReloadWeather = onReloadWeather;
		this.context.registerReceiver(this.receiver, new IntentFilter(RELOAD_WEATHER_BROADCAST));
	}

	public void unregister() {
		if (null != this.receiver) {
			this.context.unregisterReceiver(this.receiver);
		}
	}

	public interface OnReloadWeather {
		void onReload();
	}
}
