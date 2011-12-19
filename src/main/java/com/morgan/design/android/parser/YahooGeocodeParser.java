package com.morgan.design.android.parser;

import java.io.StringReader;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import com.morgan.design.android.domain.GeocodeResult;
import com.morgan.design.android.util.Logger;

public class YahooGeocodeParser implements Parser<GeocodeResult> {

	private static final String LOG_TAG = "YahooGeocodeParser";

	private static YahooGeocodeParser INSTANCE;

	private YahooGeocodeParser() {
		super();
	}

	public synchronized static YahooGeocodeParser getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new YahooGeocodeParser();
		}
		return INSTANCE;
	}

	@Override
	public GeocodeResult parse(final String result) {
		try {
			final GeocodeResult geocodeResult = new GeocodeResult();

			final SAXBuilder builder = new SAXBuilder();
			final Document document = builder.build(new InputSource(new StringReader(result)));

			final Iterator<?> content = document.getDescendants();
			while (content.hasNext()) {
				final Object elementObject = content.next();
				if (elementObject instanceof Element) {
					final Element element = (Element) elementObject;

					if (element.getName().equals("latitude")) {
						geocodeResult.setLatitude(element.getValue());
					}
					if (element.getName().equals("longitude")) {
						geocodeResult.setLonditude(element.getValue());
					}
					if (element.getName().equals("woeid")) {
						geocodeResult.setWoeid(element.getValue());
					}
					if (element.getName().equals("Error")) {
						geocodeResult.setError(parseBoolean(element.getValue()));
					}
					if (element.getName().equals("ErrorMessage")) {
						geocodeResult.setErrorMessage(element.getValue());
					}
					if (element.getName().equals("Quality")) {
						geocodeResult.setQuality(Integer.parseInt(element.getValue()));
					}
				}
			}
			return geocodeResult;
		}
		catch (final Exception e) {
			e.printStackTrace();
			Logger.e(LOG_TAG, e.getMessage());
		}
		return null;
	}

	private boolean parseBoolean(final String error) {
		return !"0".equalsIgnoreCase(error);
	}
}