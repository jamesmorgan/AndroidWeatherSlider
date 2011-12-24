package com.morgan.design.android.service;

public interface ServiceUpdateBroadcaster {

	void loading(CharSequence message);

	void onGoing(CharSequence message);

	void complete(CharSequence message);
}
