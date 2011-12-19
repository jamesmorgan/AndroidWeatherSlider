package com.morgan.design.android.service;

public interface ServiceUpdate {

	void loading(CharSequence message);

	void onGoing(CharSequence message);

	void complete(CharSequence message);
}
