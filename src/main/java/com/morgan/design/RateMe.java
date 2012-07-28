package com.morgan.design;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.morgan.design.android.util.PreferenceUtils;
import com.morgan.design.weatherslider.R;

public class RateMe {

	public static void showOnFirstSuccess(final Context context, final boolean override) {
		try {
			if (override) {
				createAlertPopup(context);
			}
			else {
				showOnFirstSuccess(context);
			}
		}
		catch (final Throwable e) {
			Toast.makeText(context, "Unable to show market place", Toast.LENGTH_SHORT)
				.show();
		}
	}

	public static void showOnFirstSuccess(final Context context) {
		try {
			// If been successful and not shown already
			if (PreferenceUtils.hasSuccessfulWeatherLookup(context) && !PreferenceUtils.shownRateMePopup(context)) {

				createAlertPopup(context);

				// Set shown rate me pop-up as presented to user
				PreferenceUtils.setShownMeRateMePopup(context);
			}
		}
		catch (final Throwable e) {
			Toast.makeText(context, "Unable to show market place", Toast.LENGTH_SHORT)
				.show();
		}
	}

	private static void createAlertPopup(final Context context) {
		new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_info)
			.setTitle(R.string.rate_me_title)
			.setPositiveButton(R.string.no_thanks, null)
			.setNegativeButton(R.string.okay, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int which) {
					context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.MARKET_LINK_URL)));
				}
			})
			.show();
	}

	public static void setSuccessIfRequired(final Context context) {
		if (!PreferenceUtils.hasSuccessfulWeatherLookup(context)) {
			PreferenceUtils.setHasHadFirstSuccessfulLookup(context, true);
		}
	}

}
