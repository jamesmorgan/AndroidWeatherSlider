package com.morgan.design.android.domain;

import java.io.Serializable;

public class WOEIDEntry implements Serializable, Woeid {

	private static final long serialVersionUID = 6471458930942344285L;

	private String woeid;

	private String placeTypeName;
	private String name;

	private String country;
	private String countryCode;
	private String admin1;
	private String admin2;
	private String admin3;

	private String locality1;
	private String locality2;

	@Override
	public final String getWoeid() {
		return this.woeid;
	}

	public final void setWoeid(final String woeid) {
		this.woeid = woeid;
	}

	public final String getPlaceTypeName() {
		return this.placeTypeName;
	}

	public final void setPlaceTypeName(final String placeTypeName) {
		this.placeTypeName = placeTypeName;
	}

	public final String getName() {
		return this.name;
	}

	public final void setName(final String name) {
		this.name = name;
	}

	public final String getCountry() {
		return this.country;
	}

	public final void setCountry(final String country) {
		this.country = country;
	}

	public String getCountryCode() {
		return this.countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	public final String getAdmin1() {
		return this.admin1;
	}

	public final void setAdmin1(final String admin1) {
		this.admin1 = admin1;
	}

	public final String getAdmin2() {
		return this.admin2;
	}

	public final void setAdmin2(final String admin2) {
		this.admin2 = admin2;
	}

	public final String getLocality1() {
		return this.locality1;
	}

	public final void setLocality1(final String locality1) {
		this.locality1 = locality1;
	}

	public void setAdmin3(final String admin3) {
		this.admin3 = admin3;
	}

	public String getAdmin3() {
		return this.admin3;
	}

	public void setLocality2(final String locality2) {
		this.locality2 = locality2;
	}

	public String getLocality2() {
		return this.locality2;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("WOEIDEntry [woeid=")
			.append(this.woeid)
			.append(", placeTypeName=")
			.append(this.placeTypeName)
			.append(", name=")
			.append(this.name)
			.append(", country=")
			.append(this.country)
			.append(", countryCode=")
			.append(this.countryCode)
			.append(", admin1=")
			.append(this.admin1)
			.append(", admin2=")
			.append(this.admin2)
			.append(", admin3=")
			.append(this.admin3)
			.append(", locality1=")
			.append(this.locality1)
			.append(", locality2=")
			.append(this.locality2)
			.append("]");
		return builder.toString();
	}

}
