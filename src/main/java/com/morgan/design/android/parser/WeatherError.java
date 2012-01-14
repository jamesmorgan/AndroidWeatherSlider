package com.morgan.design.android.parser;

public class WeatherError {

	private String description;
	private String reason;

	public WeatherError(final String description, final String reason) {
		this.description = description;
		this.reason = reason;
	}

	public final String getDescription() {
		return this.description;
	}

	public final void setDescription(final String description) {
		this.description = description;
	}

	public final String getReason() {
		return this.reason;
	}

	public final void setReason(final String reason) {
		this.reason = reason;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("WeatherError [description=").append(this.description).append(", reason=").append(this.reason).append("]");
		return builder.toString();
	}

}
