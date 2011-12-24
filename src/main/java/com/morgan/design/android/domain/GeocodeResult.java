package com.morgan.design.android.domain;

public class GeocodeResult implements Woeid {

	private boolean error;
	private String errorMessage;
	private int quality;
	private float latitude;
	private float londitude;
	private String woeid;

	public boolean isError() {
		return this.error;
	}

	public void setError(final boolean error) {
		this.error = error;
	}

	public final String getErrorMessage() {
		return this.errorMessage;
	}

	public final void setErrorMessage(final String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public final int getQuality() {
		return this.quality;
	}

	public final void setQuality(final int quality) {
		this.quality = quality;
	}

	public final float getLatitude() {
		return this.latitude;
	}

	public final void setLatitude(final float latitude) {
		this.latitude = latitude;
	}

	public final float getLonditude() {
		return this.londitude;
	}

	public final void setLonditude(final float londitude) {
		this.londitude = londitude;
	}

	@Override
	public final String getWoeid() {
		return this.woeid;
	}

	public final void setWoeid(final String woeid) {
		this.woeid = woeid;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("GeocodeResult [error=")
			.append(this.error)
			.append(", errorMessage=")
			.append(this.errorMessage)
			.append(", quality=")
			.append(this.quality)
			.append(", latitude=")
			.append(this.latitude)
			.append(", londitude=")
			.append(this.londitude)
			.append(", woeid=")
			.append(this.woeid)
			.append("]");
		return builder.toString();
	}

}
