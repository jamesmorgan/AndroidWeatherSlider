package com.morgan.design.android.domain.orm;

import static com.morgan.design.android.util.ObjectUtils.stringHasValue;

import java.io.Serializable;
import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.morgan.design.android.domain.YahooWeatherInfo;

@DatabaseTable(tableName = "weather_selection")
public class WoeidChoice implements Serializable {

	/** */
	private static final long serialVersionUID = 5180330886875011803L;

	public static final String WOEID_ID = "woeid";

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(columnName = WOEID_ID)
	private String woeid;

	@DatabaseField
	private int lastknownNotifcationId;

	@DatabaseField(dataType = DataType.DATE)
	private Date createdDateTime = new Date();

	@DatabaseField(dataType = DataType.DATE)
	private Date lastUpdatedDateTime;

	@DatabaseField
	private int numberOfTimesUpdated = 0;

	@DatabaseField
	private int currentWeatherCode;

	@DatabaseField
	private String currentLocationText;

	@DatabaseField
	private String currentWeatherText;

	private String currentTemp;

	public final int getId() {
		return this.id;
	}

	public final void setId(final int id) {
		this.id = id;
	}

	public final String getWoeid() {
		return this.woeid;
	}

	public final void setWoeid(final String woeid) {
		this.woeid = woeid;
	}

	public final Date getCreatedDateTime() {
		return this.createdDateTime;
	}

	public final void setCreatedDateTime(final Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public final Date getLastUpdatedDateTime() {
		return this.lastUpdatedDateTime;
	}

	public final void setLastUpdatedDateTime(final Date lastUpdatedDateTime) {
		this.lastUpdatedDateTime = lastUpdatedDateTime;
	}

	public final int getNumberOfTimesUpdated() {
		return this.numberOfTimesUpdated;
	}

	public final void setNumberOfTimesUpdated(final int numberOfTimesUpdated) {
		this.numberOfTimesUpdated = numberOfTimesUpdated;
	}

	public String getCurrentLocationText() {
		return this.currentLocationText;
	}

	public void setCurrentLocationText(final String currentLocationText) {
		this.currentLocationText = currentLocationText;
	}

	public int getCurrentWeatherCode() {
		return this.currentWeatherCode;
	}

	public void setCurrentWeatherCode(final int currentWeatherCode) {
		this.currentWeatherCode = currentWeatherCode;
	}

	public int getLastknownNotifcationId() {
		return this.lastknownNotifcationId;
	}

	public void setLastknownNotifcationId(final int lastknownNotifcationId) {
		this.lastknownNotifcationId = lastknownNotifcationId;
	}

	public String getCurrentTemp() {
		return this.currentTemp;
	}

	public void setCurrentTemp(final String currentTemp) {
		this.currentTemp = currentTemp;
	}

	public String getCurrentWeatherText() {
		return this.currentWeatherText;
	}

	public void setCurrentWeatherText(final String currentWeatherText) {
		this.currentWeatherText = currentWeatherText;
	}

	public void failedQuery() {
		this.lastUpdatedDateTime = new Date();
		this.numberOfTimesUpdated++;
	}

	public void successfullyQuery(final YahooWeatherInfo currentWeather) {
		this.lastUpdatedDateTime = new Date();
		this.numberOfTimesUpdated++;

		this.currentWeatherText = currentWeather.getCurrentText();
		this.currentTemp = currentWeather.getCurrentTemp();
		this.currentWeatherCode = currentWeather.getCurrentCode();
		this.currentLocationText = getSafeLocation(currentWeather);
	}

	private String getSafeLocation(final YahooWeatherInfo currentWeather) {
		final StringBuilder sb = new StringBuilder();

		if (stringHasValue(currentWeather.getCity())) {
			sb.append(currentWeather.getCity());
		}

		if (stringHasValue(currentWeather.getRegion())) {
			if (0 != sb.length()) {
				sb.append(", ");
			}
			sb.append(currentWeather.getRegion());
		}

		if (stringHasValue(currentWeather.getCountry())) {
			if (0 != sb.length()) {
				sb.append(", ");
			}
			sb.append(currentWeather.getCountry());
		}
		return sb.toString();
	}

	public boolean isFirstAttempt() {
		return null == this.createdDateTime;
	}

}
