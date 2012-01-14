package com.morgan.design.android.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.morgan.design.Constants;
import com.morgan.design.android.domain.types.Temperature;
import com.morgan.design.android.domain.types.WindSpeed;
import com.morgan.design.android.parser.WeatherError;

public class YahooWeatherInfo implements Serializable {

	private static final long serialVersionUID = 8831504790036616130L;

	private String city;
	private String country;

	private int humidityPercentage;

	private String sunRise;
	private String sunSet;

	private WindSpeed windSpeedUnit;
	private String windChill;
	private String windSpeed;

	private String webLink;
	private String link;

	private List<ForcastEntry> forcastEntries;
	private String region;
	private String windDirection;

	private int currentTemp;
	private Temperature temperatureUnit;
	private String currentDate;
	private int currentCode;
	private String currentText;
	private String latitude;
	private String longitude;

	private String rising;

	private String pressureUnit;
	private float pressure;

	private int visiblityDistance;
	private String distanceUnit;

	private WeatherError weatherError;

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

	public int getHumidityPercentage() {
		return this.humidityPercentage;
	}

	public void setHumidityPercentage(final int humidityPercentage) {
		this.humidityPercentage = humidityPercentage;
	}

	public final Temperature getTemperatureUnit() {
		return this.temperatureUnit;
	}

	public final void setTemperatureUnit(final Temperature temperatureUnit) {
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

	public final WindSpeed getWindSpeedUnit() {
		return this.windSpeedUnit;
	}

	public final void setWindSpeedUnit(final WindSpeed windSpeedUnit) {
		this.windSpeedUnit = windSpeedUnit;
	}

	public void setWebLink(final String webLink) {
		this.webLink = String.format(Constants.YAHOO_WEATHER_FORECAST_LINK, webLink);
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

	public void setCurrentCode(final int currentCode) {
		this.currentCode = currentCode;
	}

	public int getCurrentCode() {
		return this.currentCode;
	}

	public void setCurrentTemp(final int currentTemp) {
		this.currentTemp = currentTemp;
	}

	public int getCurrentTemp() {
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

	public void setRising(final String rising) {
		this.rising = rising;
	}

	public String getRising() {
		return this.rising;
	}

	public void setPressure(final float pressure) {
		this.pressure = pressure;
	}

	public float getPressure() {
		return this.pressure;
	}

	public void setVisiblityDistance(final int visiblityDistance) {
		this.visiblityDistance = visiblityDistance;
	}

	public int getVisiblityDistance() {
		return this.visiblityDistance;
	}

	public String getPressureUnit() {
		return this.pressureUnit;
	}

	public void setPressureUnit(final String pressureUnit) {
		this.pressureUnit = pressureUnit;
	}

	public void setDistanceUnit(final String distanceUnit) {
		this.distanceUnit = distanceUnit;
	}

	public String getDistanceUnit() {
		return this.distanceUnit;
	}

	public WeatherError getWeatherError() {
		return this.weatherError;
	}

	public void setWeatherError(final WeatherError weatherError) {
		this.weatherError = weatherError;
	}

	public final boolean isError() {
		return null != this.weatherError;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("YahooWeatherInfo [city=")
			.append(this.city)
			.append(", country=")
			.append(this.country)
			.append(", humidityPercentage=")
			.append(this.humidityPercentage)
			.append(", sunRise=")
			.append(this.sunRise)
			.append(", sunSet=")
			.append(this.sunSet)
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
			.append(", currentTemp=")
			.append(this.currentTemp)
			.append(", temperatureUnit=")
			.append(this.temperatureUnit)
			.append(", currentDate=")
			.append(this.currentDate)
			.append(", currentCode=")
			.append(this.currentCode)
			.append(", currentText=")
			.append(this.currentText)
			.append(", latitude=")
			.append(this.latitude)
			.append(", longitude=")
			.append(this.longitude)
			.append(", rising=")
			.append(this.rising)
			.append(", pressureUnit=")
			.append(this.pressureUnit)
			.append(", pressure=")
			.append(this.pressure)
			.append(", visiblityDistance=")
			.append(this.visiblityDistance)
			.append(", distanceUnit=")
			.append(this.distanceUnit)
			.append(", weatherError=")
			.append(this.weatherError)
			.append("]");
		return builder.toString();
	}

}
