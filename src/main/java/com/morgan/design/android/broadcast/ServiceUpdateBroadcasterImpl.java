package com.morgan.design.android.broadcast;

import android.content.Context;
import android.content.Intent;

public class ServiceUpdateBroadcasterImpl implements IServiceUpdateBroadcaster {

	private final Context context;

	public ServiceUpdateBroadcasterImpl(final Context context) {
		this.context = context;
	}

	@Override
	public void loading(final CharSequence message) {
		this.context.sendBroadcast(new Intent(ServiceUpdateReceiver.ACTION).putExtra(ServiceUpdateReceiver.LOADING, message));
	}

	@Override
	public void onGoing(final CharSequence message) {
		this.context.sendBroadcast(new Intent(ServiceUpdateReceiver.ACTION).putExtra(ServiceUpdateReceiver.ONGOING, message));
	}

	@Override
	public void complete(final CharSequence message) {
		this.context.sendBroadcast(new Intent(ServiceUpdateReceiver.ACTION).putExtra(ServiceUpdateReceiver.COMPLETE, message));
	}
}
