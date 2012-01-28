package com.morgan.design.android.adaptor;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.morgan.design.weatherslider.R;

/* Copyright 2008 Jeff Sharkey
 * 
 * From: http://www.jsharkey.org/blog/2008/08/18/separating-lists-with-headers-in-android-09/ */

public class SeparatedListAdapter extends BaseAdapter implements ObservableAdapter {

	public final Map<String, Adapter> sections = new LinkedHashMap<String, Adapter>();
	public final ArrayAdapter<String> headers;
	public final static int TYPE_SECTION_HEADER = 0;

	public SeparatedListAdapter(final Context context) {
		this.headers = new ArrayAdapter<String>(context, R.layout.list_header);
	}

	public SeparatedListAdapter(final Context context, final int layoutId) {
		super();
		this.headers = new ArrayAdapter<String>(context, layoutId);
	}

	public void addSection(final String section, final Adapter adapter) {
		this.headers.add(section);
		this.sections.put(section, adapter);

		// Register an observer so we can call notifyDataSetChanged() when our
		// children adapters are modified, otherwise no change will be visible.
		adapter.registerDataSetObserver(this.mDataSetObserver);
	}

	@Override
	public void removeObserver() {
		// Notify all our children that they should release their observers too.
		for (final Map.Entry<String, Adapter> it : this.sections.entrySet()) {
			if (it.getValue() instanceof ObservableAdapter) {
				final ObservableAdapter adapter = (ObservableAdapter) it.getValue();
				adapter.removeObserver();
			}
		}
	}

	public void clear() {
		this.headers.clear();
		this.sections.clear();
		notifyDataSetInvalidated();
	}

	@Override
	public Object getItem(int position) {
		for (final Object section : this.sections.keySet()) {
			final Adapter adapter = this.sections.get(section);
			final int size = adapter.getCount() + 1;

			// check if position inside this section
			if (position == 0)
				return section;
			if (position < size)
				return adapter.getItem(position - 1);

			// otherwise jump into next section
			position -= size;
		}
		return null;
	}

	@Override
	public int getCount() {
		// total together all sections, plus one for each section header
		int total = 0;
		for (final Adapter adapter : this.sections.values())
			total += adapter.getCount() + 1;
		return total;
	}

	@Override
	public int getViewTypeCount() {
		// assume that headers count as one, then total all sections
		int total = 1;
		for (final Adapter adapter : this.sections.values()) {
			total += adapter.getViewTypeCount();
		}
		return total;
	}

	@Override
	public int getItemViewType(int position) {
		int type = 1;
		for (final Object section : this.sections.keySet()) {
			final Adapter adapter = this.sections.get(section);
			final int size = adapter.getCount() + 1;

			// check if position inside this section
			if (position == 0)
				return TYPE_SECTION_HEADER;
			if (position < size)
				return type + adapter.getItemViewType(position - 1);

			// otherwise jump into next section
			position -= size;
			type += adapter.getViewTypeCount();
		}
		return -1;
	}

	public boolean areAllItemsSelectable() {
		return false;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(final int position) {
		return (getItemViewType(position) != TYPE_SECTION_HEADER);
	}

	@Override
	public boolean isEmpty() {
		return getCount() == 0;
	}

	@Override
	public View getView(int position, final View convertView, final ViewGroup parent) {
		int sectionnum = 0;
		for (final Object section : this.sections.keySet()) {
			final Adapter adapter = this.sections.get(section);
			final int size = adapter.getCount() + 1;

			// check if position inside this section
			if (position == 0)
				return this.headers.getView(sectionnum, convertView, parent);
			if (position < size)
				return adapter.getView(position - 1, convertView, parent);

			// otherwise jump into next section
			position -= size;
			sectionnum++;
		}
		return null;
	}

	@Override
	public long getItemId(final int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	private final DataSetObserver mDataSetObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			notifyDataSetChanged();
		}
	};

}
