package com.morgan.design.android.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.morgan.design.android.service.YahooImageLoaderUtils;

public class YahooWeatherInfo implements Serializable {

	private static final long serialVersionUID = 8831504790036616130L;

	private static final String YAHOO_WEATHER_FORECAST_LINK = "http://weather.yahoo.com/forecast/%s.html";
	// private static final String YAHOO_WEATHER_FORECAST_LINK = "http://weather.yahoo.com/forecast/UKXX0092_c";
	private String city;
	private String country;

	private String humidity;

	private String sunRise;
	private String sunSet;

	private String temperatureUnit;
	private String windSpeedUnit;

	private String windChill;
	private String windSpeed;

	private String webLink;
	private String link;

	private List<ForcastEntry> forcastEntries;
	private String region;
	private String windDirection;

	private String currentDate;
	private String currentTemp;
	private int currentCode;
	private String currentText;
	private String latitude;
	private String longitude;

	public final String getSunRise() {
		return this.sunRise;
	}

	public final void setSunRise(final String sunRise) {
		this.sunRise = sunRise;
	}

	public final String getSunSet() {
		return this.sunSet;
	}

	public final void setSunSet(final String sunSet) {
		this.sunSet = sunSet;
	}

	public final String getCity() {
		return this.city;
	}

	public final void setCity(final String city) {
		this.city = city;
	}

	public final String getCountry() {
		return this.country;
	}

	public final void setCountry(final String country) {
		this.country = country;
	}

	public final String getHumidity() {
		return this.humidity;
	}

	public final void setHumidity(final String humidity) {
		this.humidity = humidity;
	}

	public final String getTemperatureUnit() {
		return this.temperatureUnit;
	}

	public final void setTemperatureUnit(final String temperatureUnit) {
		this.temperatureUnit = temperatureUnit;
	}

	public final String getWindChill() {
		return this.windChill;
	}

	public final void setWindChill(final String windChill) {
		this.windChill = windChill;
	}

	public final String getWindSpeed() {
		return this.windSpeed;
	}

	public final void setWindSpeed(final String windSpeed) {
		this.windSpeed = windSpeed;
	}

	public final String getWindSpeedUnit() {
		return this.windSpeedUnit;
	}

	public final void setWindSpeedUnit(final String windSpeedUnit) {
		this.windSpeedUnit = windSpeedUnit;
	}

	public void setWebLink(final String webLink) {
		this.webLink = String.format(YAHOO_WEATHER_FORECAST_LINK, webLink);
	}

	public String getWebLink() {
		return this.webLink;
	}

	public String getLink() {
		return this.link;
	}

	public ForcastEntry addForcastEntry() {
		if (null == this.forcastEntries) {
			this.forcastEntries = new ArrayList<ForcastEntry>();
		}
		final ForcastEntry forcastEntry = new ForcastEntry();
		this.forcastEntries.add(forcastEntry);
		return forcastEntry;
	}

	public void setForcastEntries(final List<ForcastEntry> forcastEntries) {
		this.forcastEntries = forcastEntries;
	}

	public List<ForcastEntry> getForcastEntries() {
		return this.forcastEntries;
	}

	public void setLink(final String link) {
		this.link = link;
	}

	public void setRegion(final String region) {
		this.region = region;
	}

	public String getRegion() {
		return this.region;
	}

	public void setWindDirection(final String windDirection) {
		this.windDirection = windDirection;
	}

	public String getWindDirection() {
		return this.windDirection;
	}

	public void setCurrentText(final String currentText) {
		this.currentText = currentText;
	}

	public String getCurrentText() {
		return this.currentText;
	}

	public void setCurrentCode(final String currentCode) {
		this.currentCode = YahooImageLoaderUtils.getImageResourceFromCode(currentCode);
	}

	public int getCurrentCode() {
		return this.currentCode;
	}

	public void setCurrentTemp(final String currentTemp) {
		this.currentTemp = currentTemp;
	}

	public String getCurrentTemp() {
		return this.currentTemp;
	}

	public void setCurrentDate(final String currentDate) {
		this.currentDate = currentDate;
	}

	public String getCurrentDate() {
		return this.currentDate;
	}

	public void setLongitude(final String longitude) {
		this.longitude = longitude;
	}

	public String getLongitude() {
		return this.longitude;
	}

	public void setLatitude(final String latitude) {
		this.latitude = latitude;
	}

	public String getLatitude() {
		return this.latitude;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("YahooWeatherInfo [city=")
			.append(this.city)
			.append(", country=")
			.append(this.country)
			.append(", humidity=")
			.append(this.humidity)
			.append(", sunRise=")
			.append(this.sunRise)
			.append(", sunSet=")
			.append(this.sunSet)
			.append(", temperatureUnit=")
			.append(this.temperatureUnit)
			.append(", windSpeedUnit=")
			.append(this.windSpeedUnit)
			.append(", windChill=")
			.append(this.windChill)
			.append(", windSpeed=")
			.append(this.windSpeed)
			.append(", webLink=")
			.append(this.webLink)
			.append(", link=")
			.append(this.link)
			.append(", forcastEntries=")
			.append(this.forcastEntries)
			.append(", region=")
			.append(this.region)
			.append(", windDirection=")
			.append(this.windDirection)
			.append(", currentDate=")
			.append(this.currentDate)
			.append(", currentTemp=")
			.append(this.currentTemp)
			.append(", currentCode=")
			.append(this.currentCode)
			.append(", currentText=")
			.append(this.currentText)
			.append(", latitude=")
			.append(this.latitude)
			.append(", longitude=")
			.append(this.longitude)
			.append("]");
		return builder.toString();
	}
}
