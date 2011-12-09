package com.morgan.design.android.domain.types;

public class Wind {
	//@formatter:off
		static final String[] CONVERTER_HUMAN = new String[] { 
			"North", 
			"North-northeast", 
			"NorthEast", 
			"East-northeast", 
			"East", 
			"East-southeast",
			"SouthEast",
			"South-southeast",
			"South",
			"South-southwest",
			"Southwest",
			"West-southwest",
			"West",
			"West-northwest",
			"Northwest",
			"North-northwest"
		};
		static final String[] CONVERTER_ABREVIATION = new String[] { 
			"N", 
			"NNE", 
			"NE", 
			"ENE", 
			"E", 
			"ESE",
			"SE",
			"SSE",
			"S",
			"SSW",
			"SW",
			"WSW",
			"W",
			"WNW",
			"NW",
			"NNW"
		};
		//@formatter:on

	public static String fromDegreeToHumanDirection(final String degree) {
		final double pDegree = Double.parseDouble(degree);
		return CONVERTER_HUMAN[(int) Math.floor((pDegree % 360) / 22.5)];
	}

	public static String fromDegreeToAbbreviation(final String degree) {
		final double pDegree = Double.parseDouble(degree);
		return CONVERTER_ABREVIATION[(int) Math.floor((pDegree % 360) / 22.5)];
	}
}
