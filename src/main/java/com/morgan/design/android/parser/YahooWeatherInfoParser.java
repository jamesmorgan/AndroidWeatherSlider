package com.morgan.design.android.parser;

import static com.morgan.design.android.util.ObjectUtils.isNotBlank;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import android.util.Log;

import com.morgan.design.android.domain.ForcastEntry;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.domain.types.DayOfWeek;
import com.morgan.design.android.domain.types.IconFactory;
import com.morgan.design.android.domain.types.Temperature;
import com.morgan.design.android.domain.types.WindSpeed;
import com.morgan.design.android.util.DateUtils;

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
	private static final String DISTANCE_UNIT = "distance";

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

	// yweather:forecast The weather forecast for a specific day. The item element contains multiple forecast elements for today and
	// tomorrow. Attributes:
	//
	// day: day of the week to which this forecast applies. Possible values are Mon Tue Wed Thu Fri Sat Sun (string)
	// date: the date to which this forecast applies. The date is in "dd Mmm yyyy" format, for example "30 Nov 2005" (string)
	// low: the forecasted low temperature for this day, in the units specified by the yweather:units element (integer)
	// high: the forecasted high temperature for this day, in the units specified by the yweather:units element (integer)
	// text: a textual description of conditions, for example, "Partly Cloudy" (string)
	// code: the condition code for this forecast. You could use this code to choose a text description or image for the forecast. The
	// possible values for this element are described in Condition Codes (integer)

	private void extractForcastData(final YahooWeatherInfo weatherBean, final Element element) {
		if (element.getName().equals(FORECAST)) {
			final ForcastEntry forcast = weatherBean.addForcastEntry();
			forcast.setCode(valueOrZero(element.getAttributeValue(CODE)));
			forcast.setText(element.getAttributeValue(TEXT));
			forcast.setHigh(valueOrZero(element.getAttributeValue(HIGH)));
			forcast.setLow(valueOrZero(element.getAttributeValue(LOW)));
			forcast.setDate(DateUtils.fromSimpleDate(element.getAttributeValue(DATE)));
			forcast.setDayOfWeek(DayOfWeek.fromYahoo(element.getAttributeValue(DAY)));
		}
	}

	// yweather:condition The current weather conditions. Attributes:
	//
	// text: a textual description of conditions, for example, "Partly Cloudy" (string)
	// code: the condition code for this forecast. You could use this code to choose a text description or image for the forecast. The
	// possible values for this element are described in Condition Codes (integer)
	// temp: the current temperature, in the units specified by the yweather:units element (integer)
	// date: the current date and time for which this forecast applies. The date is in RFC822 Section 5 format, for example
	// "Wed, 30 Nov 2005 1:56 pm PST" (string)

	private void extractCurrentConditionData(final YahooWeatherInfo info, final Element element) {
		if (element.getName().equals(CONDITION)) {
			info.setCurrentText(element.getAttributeValue(CURRENT_TEXT));
			info.setCurrentCode(valueOrDefault(element.getAttributeValue(CURRENT_CODE), IconFactory.NA));
			info.setCurrentTemp(valueOrZero(element.getAttributeValue(CURRENT_TEMP)));
			info.setCurrentDate(element.getAttributeValue(CURRENT_DATE));
		}
	}

	// Forecast information about current astronomical conditions. Attributes:
	//
	// sunrise: today's sunrise time. The time is a string in a local time format of "h:mm am/pm", for example "7:02 am" (string)
	// sunset: today's sunset time. The time is a string in a local time format of "h:mm am/pm", for example "4:51 pm" (string)

	private void extractAstronomyData(final YahooWeatherInfo info, final Element element) {
		if (element.getName().equals(ASTRONOMY)) {
			info.setSunRise(element.getAttributeValue(SUNRISE));
			info.setSunSet(element.getAttributeValue(SUNSET));
		}
	}

	// Forecast information about current atmospheric pressure, humidity, and visibility. Attributes:
	//
	// humidity: humidity, in percent (integer)
	// visibility, in the units specified by the distance attribute of the yweather:units element (mi or km). Note that the visibility is
	// specified as the actual value * 100. For example, a visibility of 16.5 miles will be specified as 1650. A visibility of 14 kilometers
	// will appear as 1400. (integer)
	// pressure: barometric pressure, in the units specified by the pressure attribute of the yweather:units element (in or mb). (float).
	// rising: state of the barometric pressure: steady (0), rising (1), or falling (2). (integer: 0, 1, 2)

	private void extractAtmosphereData(final YahooWeatherInfo info, final Element element) {
		if (element.getName().equals(ATMOSPHERE)) {
			info.setHumidityPercentage(valueOrZero(element.getAttributeValue(HUMIDITY)));
			info.setVisiblityDistance(valueOrZero(element.getAttributeValue(VISIBILITY)));
			info.setRising(element.getAttributeValue(RISING));
			info.setPressure(valueOrFloat(element.getAttributeValue(PRESSURE)));
		}
	}

	// Forecast information about wind. Attributes:
	//
	// chill: wind chill in degrees (integer)
	// direction: wind direction, in degrees (integer)
	// speed: wind speed, in the units specified in the speed attribute of the yweather:units element (mph or kph). (integer)

	private void extractWindData(final YahooWeatherInfo info, final Element element) {
		if (element.getName().equals(WIND)) {
			info.setWindChill(element.getAttributeValue(CHILL));
			info.setWindDirection(element.getAttributeValue(DIRECTION));
			info.setWindSpeed(element.getAttributeValue(SPEED));
		}
	}

	// Units for various aspects of the forecast. Attributes:
	//
	// temperature: degree units, f for Fahrenheit or c for Celsius (character)
	// distance: units for distance, mi for miles or km for kilometers (string)
	// pressure: units of barometric pressure, in for pounds per square inch or mb for millibars (string)
	// speed: units of speed, mph for miles per hour or kph for kilometers per hour (string)

	private void extractUnitData(final YahooWeatherInfo info, final Element element) {
		if (element.getName().equals(UNIT)) {
			info.setTemperatureUnit(Temperature.to(element.getAttributeValue(TEMPERATURE)));
			info.setWindSpeedUnit(WindSpeed.fromYahoo(element.getAttributeValue(SPEED_UNIT)));
			info.setPressureUnit(element.getAttributeValue(PRESSURE_UNIT));
			info.setDistanceUnit(element.getAttributeValue(DISTANCE_UNIT));
		}
	}

	// The location of this forecast. Attributes:
	//
	// city: city name (string)
	// region: state, territory, or region, if given (string)
	// country: two-character country code. (string)

	private void extractLocationData(final YahooWeatherInfo info, final Element element) {
		if (element.getName().equals(LOCATION)) {
			info.setCity(element.getAttributeValue(CITY));
			info.setCountry(element.getAttributeValue(COUNTRY));
			info.setRegion(element.getAttributeValue(REGION));
		}
	}

	private float valueOrFloat(final String attributeValue) {
		try {
			return isNotBlank(attributeValue)
					? Float.parseFloat(attributeValue)
					: 0f;
		}
		catch (final Exception e) {
			return 0f;
		}
	}

	private int valueOrDefault(final String attributeValue, final int defaultVal) {
		try {
			return isNotBlank(attributeValue)
					? Integer.parseInt(attributeValue)
					: defaultVal;
		}
		catch (final Exception e) {
			return defaultVal;
		}
	}

	private int valueOrZero(final String attributeValue) {
		try {
			return isNotBlank(attributeValue)
					? Integer.parseInt(attributeValue)
					: 0;
		}
		catch (final Exception e) {
			return 0;
		}
	}
}
