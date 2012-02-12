package com.morgan.design.android.parser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import com.morgan.design.android.domain.WOEIDEntry;
import com.morgan.design.android.util.ACRAErrorLogger;
import com.morgan.design.android.util.ACRAErrorLogger.Type;
import com.morgan.design.android.util.Logger;

public class WOIEDParser implements Parser<List<WOEIDEntry>> {

	private static final String TAG = "WOIEDParser";

	private static final String WOEID = "woeid";
	private static final String PARMA_PLACE_TYPE_NAME = "placeTypeName";
	private static final String NAME = "name";

	private static final String COUNTRY = "country";
	private static final String ADMIN1 = "admin1";
	private static final String ADMIN2 = "admin2";
	private static final String ADMIN3 = "admin3";

	private static final String LOCATILITY_1 = "locality1";
	private static final String LOCATILITY_2 = "locality2";

	private static WOIEDParser INSTANCE;

	private WOIEDParser() {
		super();
	}

	public synchronized static WOIEDParser getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new WOIEDParser();
		}
		return INSTANCE;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<WOEIDEntry> parse(final String results) {
		if (results == null) {
			Logger.e(TAG, "Invalid results");
			return null;
		}
		try {

			final SAXBuilder builder = new SAXBuilder();
			final Document document = builder.build(new InputSource(new StringReader(results)));

			final List<Element> root = document.getRootElement().getChild("results").getContent();

			final List<WOEIDEntry> woiedEntries = new ArrayList<WOEIDEntry>();

			for (final Element result : root) {

				final List<Element> children = result.getChildren();

				final WOEIDEntry entry = new WOEIDEntry();
				woiedEntries.add(entry);

				for (final Element element : children) {

					if (element.getName().equals(WOEID)) {
						entry.setWoeid(element.getValue());
					}
					if (element.getName().equals(PARMA_PLACE_TYPE_NAME)) {
						entry.setPlaceTypeName(element.getValue());
					}

					if (element.getName().equals(NAME)) {
						entry.setName(element.getValue());
					}
					if (element.getName().equals(COUNTRY)) {
						entry.setCountryCode(element.getAttributeValue("code"));
						entry.setCountry(element.getValue());
					}

					extractAdminData(entry, element);
					extractLocalityData(entry, element);
				}
			}

			return woiedEntries;
		}
		catch (final Exception e) {
			ACRAErrorLogger.recordUnknownIssue(Type.WOEID_PARSER, results);
			ACRAErrorLogger.logSlientExcpetion(e);
			Logger.w(TAG, e.getMessage());
		}
		return null;
	}

	private void extractLocalityData(final WOEIDEntry entry, final Element element) {
		if (element.getName().equals(LOCATILITY_1)) {
			entry.setLocality1(element.getValue());
		}
		if (element.getName().equals(LOCATILITY_2)) {
			entry.setLocality2(element.getValue());
		}
	}

	private void extractAdminData(final WOEIDEntry entry, final Element element) {
		if (element.getName().equals(ADMIN1)) {
			entry.setAdmin1(element.getValue());
		}
		if (element.getName().equals(ADMIN2)) {
			entry.setAdmin2(element.getValue());
		}
		if (element.getName().equals(ADMIN3)) {
			entry.setAdmin3(element.getValue());
		}
	}
}
