package com.morgan.design.android.parser;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import android.util.Log;

import com.morgan.design.android.domain.ForcastEntry;
import com.morgan.design.android.domain.YahooWeatherInfo;

public class YahooWeatherInfoParser implements Parser<YahooWeatherInfo> {

	private static final String TAG = "YahooWeatherInfoParser";

	// yweather:location
	private static final String LOCATION = "location";
	private static final String CITY = "city";
	private static final String COUNTRY = "country";
	private static final String REGION = "region";

	// yweather:units
	private static final String UNIT = "units";
	private static final String TEMPERATURE = "temperature";
	private static final String SPEED_UNIT = "speed";
	private static final String PRESSURE_UNIT = "pressure";

	// yweather:wind
	private static final String WIND = "wind";
	private static final String CHILL = "chill";
	private static final String SPEED = "speed";
	private static final String DIRECTION = "direction";

	// yweather:atmosphere
	private static final String ATMOSPHERE = "atmosphere";
	private static final String HUMIDITY = "humidity";
	private static final String VISIBILITY = "visibility";
	private static final String PRESSURE = "pressure";
	private static final String RISING = "rising";

	// yweather:astronomy
	private static final String ASTRONOMY = "astronomy";
	private static final String SUNSET = "sunset";
	private static final String SUNRISE = "sunrise";

	// yweather:condition
	private static final String CONDITION = "condition";
	private static final String CURRENT_TEXT = "text";
	private static final String CURRENT_CODE = "code";
	private static final String CURRENT_TEMP = "temp";
	private static final String CURRENT_DATE = "date";

	// yweather:forecast
	private static final String FORECAST = "forecast";
	private static final String CODE = "code";
	private static final String TEXT = "text";
	private static final String HIGH = "high";
	private static final String LOW = "low";
	private static final String DATE = "date";
	private static final String DAY = "day";

	// Other
	private static final String LINK = "link";
	private static final String LONG = "long";
	private static final String LAT = "lat";

	private static YahooWeatherInfoParser INSTANCE;

	private YahooWeatherInfoParser() {
		super();
	}

	public synchronized static YahooWeatherInfoParser getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new YahooWeatherInfoParser();
		}
		return INSTANCE;
	}

	@Override
	public YahooWeatherInfo parse(final String result) {
		try {
			final YahooWeatherInfo weatherBean = new YahooWeatherInfo();

			final SAXBuilder builder = new SAXBuilder();
			final Document document = builder.build(new ByteArrayInputStream(result.getBytes()));

			final List<?> channelContentElements = document.getRootElement().getChild("channel").getContent();

			for (final Object outerElement : channelContentElements) {

				if (outerElement instanceof Element) {
					final Element element = (Element) outerElement;

					extractLocationData(weatherBean, element);
					extractUnitData(weatherBean, element);
					extractWindData(weatherBean, element);
					extractAtmosphereData(weatherBean, element);
					extractAstronomyData(weatherBean, element);
				}
			}

			final List<?> itemContentElements = document.getRootElement().getChild("channel").getChild("item").getContent();

			for (final Object itemElement : itemContentElements) {

				if (itemElement instanceof Element) {
					final Element element = (Element) itemElement;

					extractCurrentConditionData(weatherBean, element);
					extractForcastData(weatherBean, element);

					if (element.getName().equals(LINK)) {
						weatherBean.setLink(element.getValue());
					}
					if (element.getName().equals(LAT)) {
						weatherBean.setLatitude(element.getValue());
					}
					if (element.getName().equals(LONG)) {
						weatherBean.setLongitude(element.getValue());
					}
				}
			}
			return weatherBean;
		}
		catch (final Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return null;
	}

	private void extractForcastData(final YahooWeatherInfo weatherBean, final Element element) {
		if (element.getName().equals(FORECAST)) {
			final ForcastEntry forcast = weatherBean.addForcastEntry();
			forcast.setCode(element.getAttributeValue(CODE));
			forcast.setText(element.getAttributeValue(TEXT));
			forcast.setHigh(element.getAttributeValue(HIGH));
			forcast.setLow(element.getAttributeValue(LOW));
			forcast.setDate(element.getAttributeValue(DATE));
			forcast.setDay(element.getAttributeValue(DAY));
		}
	}

	private void extractCurrentConditionData(final YahooWeatherInfo info, final Element element) {
		if (element.getName().equals(CONDITION)) {
			info.setCurrentText(element.getAttributeValue(CURRENT_TEXT));
			info.setCurrentCode(element.getAttributeValue(CURRENT_CODE));
			info.setCurrentTemp(element.getAttributeValue(CURRENT_TEMP));
			info.setCurrentDate(element.getAttributeValue(CURRENT_DATE));
		}
	}

	private void extractAstronomyData(final YahooWeatherInfo info, final Element element) {
		if (element.getName().equals(ASTRONOMY)) {
			info.setSunRise(element.getAttributeValue(SUNRISE));
			info.setSunSet(element.getAttributeValue(SUNSET));
		}
	}

	private void extractAtmosphereData(final YahooWeatherInfo info, final Element element) {
		if (element.getName().equals(ATMOSPHERE)) {
			info.setHumidity(element.getAttributeValue(HUMIDITY));
			info.setVisiblity(element.getAttributeValue(VISIBILITY));
			info.setRising(element.getAttributeValue(RISING));
			info.setPressure(element.getAttributeValue(PRESSURE));
		}
	}

	private void extractWindData(final YahooWeatherInfo info, final Element element) {
		if (element.getName().equals(WIND)) {
			info.setWindChill(element.getAttributeValue(CHILL));
			info.setWindDirection(element.getAttributeValue(DIRECTION));
			info.setWindSpeed(element.getAttributeValue(SPEED));
		}
	}

	private void extractUnitData(final YahooWeatherInfo info, final Element element) {
		if (element.getName().equals(UNIT)) {
			info.setTemperatureUnit(element.getAttributeValue(TEMPERATURE));
			info.setWindSpeedUnit(element.getAttributeValue(SPEED_UNIT));
			info.setPressureUnit(element.getAttributeValue(PRESSURE_UNIT));
		}
	}

	private void extractLocationData(final YahooWeatherInfo info, final Element element) {
		if (element.getName().equals(LOCATION)) {
			info.setCity(element.getAttributeValue(CITY));
			info.setCountry(element.getAttributeValue(COUNTRY));
			info.setRegion(element.getAttributeValue(REGION));
		}
	}
}
