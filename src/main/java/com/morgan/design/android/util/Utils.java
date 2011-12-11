package com.morgan.design.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.morgan.design.FeedbackFormActivity;
import com.weatherslider.morgan.design.R;

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

	public static void shortToast(final Activity activity, final CharSequence message) {
		Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
	}

}
