package com.morgan.design.android.adaptor;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.morgan.design.R;
import com.morgan.design.android.domain.WOEIDEntry;

public class WOIEDAdaptor extends ArrayAdapter<WOEIDEntry> {
	private final String LOG_TAG = "WOIEDAdaptor";

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
			holder.admin = (TextView) view.findViewById(R.id.admin);
			view.setTag(holder);
		}
		else {
			holder = (ViewHolder) view.getTag();
		}

		final WOEIDEntry entry = this.woiedEntries.get(position);
		// Logger.d(this.LOG_TAG, entry.getName() + ", " + entry.getCountry());

		// FIXME -> add country flags

		if (entry != null) {
			final TextView name = (TextView) view.findViewById(R.id.name);
			name.setText(entry.getName());

			final TextView country = (TextView) view.findViewById(R.id.country);
			country.setText(entry.getCountry());

			final TextView admin = (TextView) view.findViewById(R.id.admin);
			if ("" != entry.getAdmin1()) {
				admin.setText(entry.getAdmin1());
			}
			else if ("" != entry.getAdmin2()) {
				admin.setText(entry.getAdmin2());
			}
			else {
				admin.setText("");
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
		TextView admin;
		TextView country;
	}
}
