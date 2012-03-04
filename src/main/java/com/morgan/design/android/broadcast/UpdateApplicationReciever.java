package com.morgan.design.android.broadcast;

import static com.morgan.design.Constants.APPLICATION_UPDATE_AVAILABLE;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;

import com.morgan.design.android.analytics.GoogleAnalyticsService;
import com.morgan.design.weatherslider.R;

public class UpdateApplicationReciever {

	private final Context context;
	private final GoogleAnalyticsService googleAnalyticsService;

	private boolean alertDisplayed = false;
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			if (!alertDisplayed) {
				alertDisplayed = true;
				new AlertDialog.Builder(context).setIcon(R.drawable.launch_icon).setTitle(R.string.update_available).setMessage(R.string.update_checker_alert)
						.setPositiveButton(R.string.alert_yes, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int whichButton) {
								alertDisplayed = false;
								Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pname:com.morgan.design.weatherslider"));
								context.startActivity(intent);
								googleAnalyticsService.trackClickEvent(context, GoogleAnalyticsService.APP_UPDATE_FOUND_CLICKED);
							}
						}).setNegativeButton(R.string.alert_no, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int whichButton) {
								googleAnalyticsService.trackClickEvent(context, GoogleAnalyticsService.APP_UPDATE_FOUND_CANCELLED);
								alertDisplayed = false;
								dialog.cancel();
							}
						}).show();

			}
		}
	};

	public UpdateApplicationReciever(final Context context, GoogleAnalyticsService googleAnalyticsService) {
		this.context = context;
		this.googleAnalyticsService = googleAnalyticsService;
		this.context.registerReceiver(this.receiver, new IntentFilter(APPLICATION_UPDATE_AVAILABLE));
	}

	public boolean alertDisplayed() {
		return this.alertDisplayed;
	}

	public void unregister() {
		this.alertDisplayed = false;
		if (null != this.receiver) {
			this.context.unregisterReceiver(this.receiver);
		}
	}
}
