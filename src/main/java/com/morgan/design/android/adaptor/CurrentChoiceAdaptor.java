package com.morgan.design.android.adaptor;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.morgan.design.android.domain.orm.WeatherChoice;
import com.morgan.design.android.domain.types.IconFactory;
import com.morgan.design.android.util.DateUtils;
import com.weatherslider.morgan.design.R;

public class CurrentChoiceAdaptor extends ArrayAdapter<WeatherChoice> {

	protected final static String LOG_TAG = "CurrentChoiceAdaptor";

	private final List<WeatherChoice> woeidChoices;
	private final Activity context;

	public CurrentChoiceAdaptor(final Activity context, final List<WeatherChoice> woeidChoices) {
		super(context, R.layout.woeid_choice_adaptor_layout, woeidChoices);
		this.context = context;
		this.woeidChoices = woeidChoices;
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
			holder.weather_text = (TextView) view.findViewById(R.id.weather_text);
			holder.last_time_updated = (TextView) view.findViewById(R.id.last_time_updated);
			holder.location = (TextView) view.findViewById(R.id.location);
			holder.status_icon = (ImageView) view.findViewById(R.id.status_icon);
			view.setTag(holder);
		}
		else {
			holder = (ViewHolder) view.getTag();
		}

		final WeatherChoice woeidChoice = this.woeidChoices.get(position);

		if (woeidChoice != null) {

			final TextView weatherText = (TextView) view.findViewById(R.id.weather_text);
			weatherText.setText(woeidChoice.getCurrentWeatherText());

			final TextView lastTimeUpdated = (TextView) view.findViewById(R.id.last_time_updated);
			lastTimeUpdated.setText(DateUtils.dateToSimpleDateFormat(woeidChoice.getLastUpdatedDateTime()));

			final TextView location = (TextView) view.findViewById(R.id.location);
			location.setText(woeidChoice.getCurrentLocationText());

			final ImageView image = (ImageView) view.findViewById(R.id.image);
			image.setImageResource(IconFactory.getImageResourceFromCode(woeidChoice.getCurrentWeatherCode()));

			final ImageView status_icon = (ImageView) view.findViewById(R.id.status_icon);
			status_icon.setImageResource(IconFactory.getStatus(woeidChoice));

		}
		return view;
	}

	@Override
	public WeatherChoice getItem(final int position) {
		return this.woeidChoices.get(position);
	}

	@Override
	public int getCount() {
		return this.woeidChoices.size();
	}

	class ViewHolder {
		ImageView image;
		ImageView status_icon;
		TextView location;
		TextView weather_text;
		TextView last_time_updated;
	}
}
