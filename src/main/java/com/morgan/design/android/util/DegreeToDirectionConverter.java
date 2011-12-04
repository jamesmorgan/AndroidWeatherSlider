package com.morgan.design.android.util;

public class DegreeToDirectionConverter {

	//@formatter:off
	//	N 		North 			0°
	//	NNE 	North-northeast 22.5°
	//	NE 		NorthEast 		45°
	//	ENE 	East-northeast 	67.5°
	//	E 		East 			90°
	//	ESE 	East-southeast 	112.5°
	//	SE 		SouthEast 		135°
	//	SSE 	South-southeast 157.5°
	//	S 		South 			180°
	//	SSW 	South-southwest 202.5°
	//	SW 		Southwest 		225°
	//	WSW 	West-southwest 	247.5°
	//	W 		West 			270°
	//	WNW 	West-northwest 	292.5°
	//	NW 		Northwest 		315°
	//	NNW 	North-northwest 337.5°
	//@formatter:on

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
