package com.morgan.design.android.domain;

import java.io.Serializable;

public class ForcastEntry implements Serializable {

	private static final long serialVersionUID = -5786425131508243418L;

	private String code;
	private String text;
	private String high;
	private String low;
	private String date;
	private String day;

	public final String getCode() {
		return this.code;
	}

	public final void setCode(final String code) {
		this.code = code;
	}

	public final String getText() {
		return this.text;
	}

	public final void setText(final String text) {
		this.text = text;
	}

	public final String getHigh() {
		return this.high;
	}

	public final void setHigh(final String high) {
		this.high = high;
	}

	public final String getLow() {
		return this.low;
	}

	public final void setLow(final String low) {
		this.low = low;
	}

	public final String getDate() {
		return this.date;
	}

	public final void setDate(final String date) {
		this.date = date;
	}

	public final String getDay() {
		return this.day;
	}

	public final void setDay(final String day) {
		this.day = day;
	}

	@Override
	public String toString() {
		return "ForcastEntry [code=" + this.code + ", text=" + this.text + ", high=" + this.high + ", low=" + this.low + ", date="
			+ this.date + ", day=" + this.day + "]";
	}

}
