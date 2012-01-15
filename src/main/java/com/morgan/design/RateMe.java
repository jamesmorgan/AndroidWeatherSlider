package com.morgan.design;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.morgan.design.android.util.PreferenceUtils;
import com.weatherslider.morgan.design.R;

public class RateMe {

	public static void showOnFirstSuccess(final Context context) {

		if (PreferenceUtils.hasSuccessfulWeatherLookup(context)) {
			try {

				new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_info)
					.setTitle(R.string.rate_me_title)
					.setPositiveButton(R.string.no_thanks, null)
					.setNegativeButton(R.string.okay, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface dialog, final int which) {
							context.startActivity(new Intent(Constants.ANDROID_MARKET));
						}
					})
					.show();
			}
			catch (final Exception e) {
				Toast.makeText(context, "Unable to show market place", Toast.LENGTH_SHORT);
			}

		}
	}

	public static void setSuccessIfRequired(final Context context) {
		if (!PreferenceUtils.hasSuccessfulWeatherLookup(context)) {
			PreferenceUtils.setHasHadFirstSuccessfulLookup(context, true);
		}
	}

}
