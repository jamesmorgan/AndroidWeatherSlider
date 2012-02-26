package com.morgan.design.android.domain;

import java.io.Serializable;
import java.util.Date;

import com.morgan.design.android.domain.types.DayOfWeek;

public class ForcastEntry implements Serializable {

	private static final long serialVersionUID = -5786425131508243418L;

	private int code;
	private String text;
	private int high;
	private int low;
	private Date date;
	private String day;

	private DayOfWeek dayOfWeek;

	public final int getCode() {
		return this.code;
	}

	public final void setCode(final int code) {
		this.code = code;
	}

	public final String getText() {
		return this.text;
	}

	public final void setText(final String text) {
		this.text = text;
	}

	public final int getHigh() {
		return this.high;
	}

	public final void setHigh(final int high) {
		this.high = high;
	}

	public final int getLow() {
		return this.low;
	}

	public final void setLow(final int low) {
		this.low = low;
	}

	public final Date getDate() {
		return this.date;
	}

	public final void setDate(final Date date) {
		this.date = date;
	}

	public void setDayOfWeek(final DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public DayOfWeek getDayOfWeek() {
		return this.dayOfWeek;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ForcastEntry [code=")
			.append(this.code)
			.append(", text=")
			.append(this.text)
			.append(", high=")
			.append(this.high)
			.append(", low=")
			.append(this.low)
			.append(", date=")
			.append(this.date)
			.append(", day=")
			.append(this.day)
			.append(", dayOfWeek=")
			.append(this.dayOfWeek)
			.append("]");
		return builder.toString();
	}

}
