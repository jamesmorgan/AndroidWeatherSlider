package com.morgan.design.android.adaptor;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.morgan.design.weatherslider.R;
import com.morgan.design.android.dao.orm.WeatherChoice;
import com.morgan.design.android.factory.IconFactory;
import com.morgan.design.android.util.DateUtils;

public class CurrentChoiceAdaptor extends ArrayAdapter<WeatherChoice> {

	protected final static String LOG_TAG = "CurrentChoiceAdaptor";

	private final List<WeatherChoice> weatherChoices;
	private final Activity context;

	public CurrentChoiceAdaptor(final Activity context, final List<WeatherChoice> weatherChoices) {
		super(context, R.layout.woeid_choice_adaptor_layout, weatherChoices);
		this.context = context;
		this.weatherChoices = weatherChoices;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;

		if (view == null) {
			final LayoutInflater vi = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.woeid_choice_adaptor_layout, null);
			holder = new ViewHolder();
			holder.image = (ImageView) view.findViewById(R.id.image);
			holder.layout = (LinearLayout) view.findViewById(R.id.layout);
			holder.weather_text = (TextView) view.findViewById(R.id.weather_text);
			holder.last_time_updated = (TextView) view.findViewById(R.id.last_time_updated);
			holder.location = (TextView) view.findViewById(R.id.location);
			view.setTag(holder);
		}
		else {
			holder = (ViewHolder) view.getTag();
		}

		final WeatherChoice weatherChoice = this.weatherChoices.get(position);

		if (weatherChoice != null) {
			final Resources resources = getContext().getResources();

			final TextView weatherText = (TextView) view.findViewById(R.id.weather_text);
			weatherText.setText(weatherChoice.getCurrentWeatherText());

			final TextView location = (TextView) view.findViewById(R.id.location);
			location.setText(weatherChoice.getCurrentLocationText());
			location.setCompoundDrawablesWithIntrinsicBounds(null, null, resources.getDrawable(IconFactory.getStatus(weatherChoice)), null);

			final TextView lastTimeUpdated = (TextView) view.findViewById(R.id.last_time_updated);
			lastTimeUpdated.setText(DateUtils.dateToSimpleDateFormat(weatherChoice.getLastUpdatedDateTime()));
			lastTimeUpdated.setCompoundDrawablesWithIntrinsicBounds(null, null, weatherChoice.isRoaming()
					? resources.getDrawable(R.drawable.roaming)
					: null, null);

			final ImageView image = (ImageView) view.findViewById(R.id.image);
			image.setImageResource(IconFactory.getImageResourceFromCode(weatherChoice.getCurrentWeatherCode()));

			final LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout);
			if (weatherChoice.isNotActive()) {
				final AlphaAnimation alpha = new AlphaAnimation(0.5F, 0.5F);
				alpha.setDuration(0); // Make animation instant
				alpha.setFillAfter(true); // Tell it to persist after the animation ends

				layout.startAnimation(alpha);
			}
			else {
				layout.clearAnimation();
			}

		}
		return view;
	}

	@Override
	public WeatherChoice getItem(final int position) {
		return this.weatherChoices.get(position);
	}

	@Override
	public int getCount() {
		return this.weatherChoices.size();
	}

	class ViewHolder {
		LinearLayout layout;
		ImageView image;
		TextView location;
		TextView weather_text;
		TextView last_time_updated;
	}
}
