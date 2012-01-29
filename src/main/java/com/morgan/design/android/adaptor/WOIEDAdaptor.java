package com.morgan.design.android.adaptor;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.morgan.design.android.domain.WOEIDEntry;
import com.morgan.design.android.domain.types.Flags;
import com.morgan.design.weatherslider.R;

public class WOIEDAdaptor extends ArrayAdapter<WOEIDEntry> {

	protected final static String LOG_TAG = "WOIEDAdaptor";

	private final List<WOEIDEntry> woiedEntries;
	private final Activity context;

	public WOIEDAdaptor(final Activity context, final List<WOEIDEntry> woiedEntries) {
		super(context, R.layout.woied_rowlayout, woiedEntries);
		this.context = context;
		this.woiedEntries = woiedEntries;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		if (view == null) {
			final LayoutInflater vi = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.woied_rowlayout, null);
			holder = new ViewHolder();
			holder.name = (TextView) view.findViewById(R.id.name);
			holder.country = (TextView) view.findViewById(R.id.country);
			holder.secondary_text = (TextView) view.findViewById(R.id.secondary_text);
			view.setTag(holder);
		}
		else {
			holder = (ViewHolder) view.getTag();
		}

		final WOEIDEntry entry = this.woiedEntries.get(position);
		// Logger.d(this.LOG_TAG, entry.getName() + ", " + entry.getCountry());

		// Name and place type
		// Country
		// Admin OR Locality (secondary_text)
		if (entry != null) {

			final TextView name = (TextView) view.findViewById(R.id.name);
			name.setText(entry.getName());

			final TextView country = (TextView) view.findViewById(R.id.country);
			country.setText(entry.getCountry());

			if (null != entry.getCountryCode()) {
				Integer flagCode = Flags.getFlag(entry.getCountryCode());
				if (null != flagCode) {
					Drawable flag = this.context.getResources()
						.getDrawable(flagCode);
					country.setCompoundDrawablesWithIntrinsicBounds(flag, null, null, null);
				}
				else {
					country.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				}
			}
			else {
				country.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			}

			String secondary_text = null;
			if ("" != entry.getAdmin1()) {
				secondary_text = entry.getAdmin1();
			}
			else if ("" != entry.getAdmin2()) {
				secondary_text = entry.getAdmin2();
			}
			else if ("" != entry.getLocality1()) {
				secondary_text = entry.getLocality1();
			}
			else if ("" != entry.getLocality2()) {
				secondary_text = entry.getLocality2();
			}

			final TextView secondary = (TextView) view.findViewById(R.id.secondary_text);
			if (null != secondary_text) {
				secondary.setText(secondary_text);
			}
		}
		return view;
	}

	@Override
	public WOEIDEntry getItem(final int position) {
		return this.woiedEntries.get(position);
	}

	@Override
	public int getCount() {
		return this.woiedEntries.size();
	}

	class ViewHolder {
		TextView name;
		TextView secondary_text;
		TextView country;
	}
}
