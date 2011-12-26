package com.morgan.design.android.broadcast;

public interface IServiceUpdateBroadcaster {

	void loading(CharSequence message);

	void onGoing(CharSequence message);

	void complete(CharSequence message);
}
