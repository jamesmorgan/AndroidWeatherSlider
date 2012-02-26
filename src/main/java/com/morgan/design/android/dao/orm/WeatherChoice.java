package com.morgan.design.android.dao.orm;

import static com.morgan.design.android.util.ObjectUtils.stringHasValue;

import java.io.Serializable;
import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.morgan.design.android.domain.Woeid;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.domain.types.IconFactory;
import com.morgan.design.android.parser.WeatherError;

@DatabaseTable(tableName = "weather_choice")
public class WeatherChoice implements Serializable, Woeid {

	/** */
	private static final long serialVersionUID = 5180330886875011803L;

	public static final String WOEID_ID = "woeid";
	public static final String ACTIVE = "active";
	public static final String ROAMING = "roaming";
	public static final String VALID = "valid";
	public static final String WEATHER_LOCATION = "currentLocationText";

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(columnName = WOEID_ID)
	private String woeid;

	@DatabaseField(columnName = ACTIVE)
	private boolean active;

	@DatabaseField(columnName = ROAMING)
	private boolean roaming;

	@DatabaseField(columnName = VALID)
	private boolean valid;

	@DatabaseField
	private float latitude;

	@DatabaseField
	private float longitude;

	@DatabaseField(dataType = DataType.DATE)
	private Date createdDateTime;

	@DatabaseField(dataType = DataType.DATE)
	private Date lastUpdatedDateTime;

	@DatabaseField
	private String weatherUpdatedDateTimeString;

	@DatabaseField
	private int numberOfTimesUpdated = 0;

	@DatabaseField
	private int currentWeatherCode;

	@DatabaseField(columnName = WEATHER_LOCATION)
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

	@Override
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

	public String getWeatherUpdatedDateTimeString() {
		return this.weatherUpdatedDateTimeString;
	}

	public void setWeatherUpdatedDateTimeString(final String weatherUpdatedDateTimeString) {
		this.weatherUpdatedDateTimeString = weatherUpdatedDateTimeString;
	}

	public float getLatitude() {
		return this.latitude;
	}

	public void setLatitude(final float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return this.longitude;
	}

	public void setLongitude(final float longitude) {
		this.longitude = longitude;
	}

	public boolean isActive() {
		return this.active;
	}

	public final boolean isNotActive() {
		return !this.active;
	}

	public void setActive(final boolean active) {
		this.active = active;
	}

	public void setRoaming(final boolean roaming) {
		this.roaming = roaming;
	}

	public boolean isRoaming() {
		return this.roaming;
	}

	public boolean getValid() {
		return this.valid;
	}

	public void setValid(final boolean valid) {
		this.valid = valid;
	}

	public boolean isFirstAttempt() {
		return 0 == this.numberOfTimesUpdated;
	}

	public void failedQuery() {
		recordUdpate(true);
	}

	public void invalidQuery(final WeatherError weatherError) {
		recordUdpate(false);

		this.currentWeatherCode = IconFactory.NOTE_FOUND;
		this.currentWeatherText = weatherError.getReason();
		this.currentLocationText = weatherError.getDescription();
	}

	public void successfullyQuery(final YahooWeatherInfo currentWeather) {
		recordUdpate(true);

		this.currentWeatherText = currentWeather.getCurrentText();
		this.currentTemperature = currentWeather.getCurrentTemp();
		this.currentWeatherCode = currentWeather.getCurrentCode();
		this.weatherUpdatedDateTimeString = currentWeather.getCurrentDate();
		this.currentLocationText = getSafeLocation(currentWeather);
	}

	private void recordUdpate(final boolean valid) {
		this.lastUpdatedDateTime = new Date();
		this.numberOfTimesUpdated++;
		this.valid = valid;
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
			.append(", createdDateTime=")
			.append(this.createdDateTime)
			.append(", lastUpdatedDateTime=")
			.append(this.lastUpdatedDateTime)
			.append(", lastSuccessfulUpdateDateTime=")
			.append(this.weatherUpdatedDateTimeString)
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
