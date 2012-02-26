package com.morgan.design.android.broadcast;

import static com.morgan.design.Constants.CANCEL_ALL_WEATHER_NOTIFICATIONS;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class CancelAllLookupsReciever {

	private final OnCancelAll onCancelAll;
	private final Context context;

	private final BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {
			CancelAllLookupsReciever.this.onCancelAll.onCancelAll();
		}
	};

	public CancelAllLookupsReciever(final Context context, final OnCancelAll onCancelAll) {
		this.context = context;
		this.onCancelAll = onCancelAll;
		this.context.registerReceiver(this.receiver, new IntentFilter(CANCEL_ALL_WEATHER_NOTIFICATIONS));
	}

	public void unregister() {
		if (null != this.receiver) {
			this.context.unregisterReceiver(this.receiver);
		}
	}

	public interface OnCancelAll {
		void onCancelAll();
	}
}
