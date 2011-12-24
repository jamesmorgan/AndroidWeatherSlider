package com.morgan.design.android.service;

import android.content.Context;
import android.content.Intent;

public class ServiceUpdateBroadcasterImpl implements ServiceUpdateBroadcaster {

	private final Context context;

	public ServiceUpdateBroadcasterImpl(final Context context) {
		this.context = context;
	}

	@Override
	public void loading(final CharSequence message) {
		this.context.sendBroadcast(new Intent(ServiceUpdateRegister.ACTION).putExtra(ServiceUpdateRegister.LOADING, message));
	}

	@Override
	public void onGoing(final CharSequence message) {
		this.context.sendBroadcast(new Intent(ServiceUpdateRegister.ACTION).putExtra(ServiceUpdateRegister.ONGOING, message));
	}

	@Override
	public void complete(final CharSequence message) {
		this.context.sendBroadcast(new Intent(ServiceUpdateRegister.ACTION).putExtra(ServiceUpdateRegister.COMPLETE, message));
	}
}
