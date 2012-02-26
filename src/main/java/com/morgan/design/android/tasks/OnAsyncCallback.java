package com.morgan.design.android.tasks;

public interface OnAsyncCallback<T> {
	void onPreLookup();

	void onPostLookup(final T value);

	void onInitiateExecution();

}
