package com.morgan.design.android.util;

public class YahooPlaceTypes {

	//@formatter:off
	//	<placeTypeName code="0">Undefined</placeTypeName>
	//    <placeTypeName code="7">Town</placeTypeName>
	//    <placeTypeName code="8">State</placeTypeName>
	//    <placeTypeName code="9">County</placeTypeName>
	//    <placeTypeName code="10">Local Administrative Area</placeTypeName>
	//    <placeTypeName code="11">Postal Code</placeTypeName>
	//    <placeTypeName code="12">Country</placeTypeName>
	//    <placeTypeName code="13">Island</placeTypeName>
	//    <placeTypeName code="14">Airport</placeTypeName>
	//    <placeTypeName code="15">Drainage</placeTypeName>
    //@formatter:on

	public static final String SAFE_LOCAITON = " and placeTypeName.code in (7,10,11,13,14,15) ";

}
