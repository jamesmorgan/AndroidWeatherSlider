package com.morgan.design.android.domain.orm;

import static com.morgan.design.android.util.ObjectUtils.stringHasValue;
import static com.morgan.design.android.util.ObjectUtils.valueOrDefault;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.morgan.design.android.domain.YahooWeatherInfo;

@DatabaseTable(tableName = "weather_choice")
public class WeatherChoice implements Serializable {

	/** */
	private static final long serialVersionUID = 5180330886875011803L;

	public static final String WOEID_ID = "woeid";
	public static final String ACTIVE = "active";

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(columnName = WOEID_ID)
	private String woeid;

	@DatabaseField(columnName = ACTIVE)
	private boolean active;

	@DatabaseField
	private BigDecimal latitude;

	@DatabaseField
	private BigDecimal longitude;

	@DatabaseField
	private int lastknownNotifcationId;

	@DatabaseField(dataType = DataType.DATE)
	private Date createdDateTime;

	@DatabaseField(dataType = DataType.DATE)
	private Date lastUpdatedDateTime;

	@DatabaseField(dataType = DataType.DATE)
	private Date lastSuccessfulUpdateDateTime;

	@DatabaseField
	private int numberOfTimesUpdated = 0;

	@DatabaseField
	private int currentWeatherCode;

	@DatabaseField
	private String currentLocationText;

	@DatabaseField
	private String currentWeatherText;

	private int currentTemperature;

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

	public int getCurrentTemperature() {
		return this.currentTemperature;
	}

	public void setCurrentTemperature(final int currentTemperature) {
		this.currentTemperature = currentTemperature;
	}

	public String getCurrentWeatherText() {
		return this.currentWeatherText;
	}

	public void setCurrentWeatherText(final String currentWeatherText) {
		this.currentWeatherText = currentWeatherText;
	}

	public boolean isFirstAttempt() {
		return null == this.createdDateTime;
	}

	public void setLastSuccessfulUpdateDateTime(final Date lastSuccessfulUpdateDateTime) {
		this.lastSuccessfulUpdateDateTime = lastSuccessfulUpdateDateTime;
	}

	public Date getLastSuccessfulUpdateDateTime() {
		return this.lastSuccessfulUpdateDateTime;
	}

	public BigDecimal getLatitude() {
		return this.latitude;
	}

	public void setLatitude(final BigDecimal latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getLongitude() {
		return this.longitude;
	}

	public void setLongitude(final BigDecimal longitude) {
		this.longitude = longitude;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(final boolean active) {
		this.active = active;
	}

	public void failedQuery() {
		recordUdpate();
	}

	public void successfullyQuery(final YahooWeatherInfo currentWeather) {
		recordUdpate();

		this.currentWeatherText = currentWeather.getCurrentText();
		this.currentTemperature = currentWeather.getCurrentTemp();
		this.currentWeatherCode = currentWeather.getCurrentCode();
		this.lastSuccessfulUpdateDateTime = currentWeather.getCurrentDate();
		this.currentLocationText = getSafeLocation(currentWeather);
		this.longitude = valueOrDefault(currentWeather.getLongitude(), new BigDecimal("0"));
		this.latitude = valueOrDefault(currentWeather.getLatitude(), new BigDecimal("0"));
	}

	private void recordUdpate() {
		this.lastUpdatedDateTime = new Date();
		this.numberOfTimesUpdated++;
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

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("WeatherChoice [id=")
			.append(this.id)
			.append(", woeid=")
			.append(this.woeid)
			.append(", active=")
			.append(this.active)
			.append(", latitude=")
			.append(this.latitude)
			.append(", longitude=")
			.append(this.longitude)
			.append(", lastknownNotifcationId=")
			.append(this.lastknownNotifcationId)
			.append(", createdDateTime=")
			.append(this.createdDateTime)
			.append(", lastUpdatedDateTime=")
			.append(this.lastUpdatedDateTime)
			.append(", lastSuccessfulUpdateDateTime=")
			.append(this.lastSuccessfulUpdateDateTime)
			.append(", numberOfTimesUpdated=")
			.append(this.numberOfTimesUpdated)
			.append(", currentWeatherCode=")
			.append(this.currentWeatherCode)
			.append(", currentLocationText=")
			.append(this.currentLocationText)
			.append(", currentWeatherText=")
			.append(this.currentWeatherText)
			.append(", currentTemperature=")
			.append(this.currentTemperature)
			.append(", getClass()=")
			.append(this.getClass())
			.append(", hashCode()=")
			.append(this.hashCode())
			.append(", toString()=")
			.append(super.toString())
			.append("]");
		return builder.toString();
	}

}
