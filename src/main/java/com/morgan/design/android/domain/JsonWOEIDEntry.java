package com.morgan.design.android.domain;

public class JsonWOEIDEntry implements Woeid {

	// http://weather.yahooapis.com/forecastjson?w=2295424

	//@formatter:off
	/**
	 * // {"query":{ "count":9,"
	 * 		created":"2012-02-04T23:38:45Z","lang":"en-US","
	 * 		results":{"
	 * 	place":[{
	 * 		"lang":" en-US",
 * 			"uri":"http://where.yahooapis.com/v1/place/28218",
 * 			"woeid":"28218","
	 * 			placeTypeName":{"
	 * 					code":"7",
	 *  				"content":"Town"
	 *  		},
	 *  	"name":"Manchester",
	 *  		"country":{
	 *  			"code":"GB",
	 *  			"type":"Country",
	 *  			"content":"UnitedKingdom
	 * "			},
	 * 		"admin1":{
	 * 				"code":"GB-ENG",
	 * 				"type":"Country",
	 * 				"content":"England"},"
	 * 			admin2":{
	 * 				"code":"GB-GTM",
	 * 				"type":"County",
	 * 				"content":"Greater Manchester"
	 * 			},"
	 * 			admin3":null,
	 * 		"locality1":{
	 * 			"type":"Town",
	 * 			"content":"Manchester
	 * 		"},
	 * 		"locality2:null,
	 * 		"postal":null,
	 * 		"centroid":{
	 * 			"latitude":"53.479599",
	 * 			"longitude":"-2.248810
	 * 		"},
	 * 		"boundingBox":{"southWest":{"latitude":"53.347031","longitude
	 * ":"-2.522890
	 * "},"northEast":{"latitude":"53.587540","longitude":"-2.023210"}},"
	 * areaRank
	 * ":"6","popRank":"13"},{"lang":"en-US","uri":"http://where.yahooapis
	 * .com/v1
	 * /place/2444674","woeid":"2444674","placeTypeName":{"code":"7","content
	 * ":"Town
	 * "},"name":"Manchester","country":{"code":"US","type":"Country","content
	 * ":"United States"},"admin1":{"code":"US-NH","type":"State","content":"New
	 * Hampshire
	 * "},"admin2":null,"admin3":null,"locality1":{"type":"Town","content
	 * ":"Manchester
	 * "},"locality2":null,"postal":null,"centroid":{"latitude":"42.991169
	 * ","longitude
	 * ":"-71.463089"},"boundingBox":{"southWest":{"latitude":"42.889961
	 * ","longitude
	 * ":"-71.522163"},"northEast":{"latitude":"43.053131","longitude
	 * ":"-71.373810
	 * "}},"areaRank":"4","popRank":"10"},{"lang":"en-US","uri":"http
	 * ://where.yahooapis
	 * .com/v1/place/2444676","woeid":"2444676","placeTypeName":{"
	 * code":"7","content
	 * ":"Town"},"name":"Manchester","country":{"code":"US","type
	 * ":"Country","content":"United
	 * States"},"admin1":{"code":"US-CT","type":"State
	 * ","content":"Connecticut"},"
	 * admin2":{"code":"","type":"County","content":"
	 * Hartford"},"admin3":null,"locality1
	 * ":{"type":"Town","content":"Manchester"},"
	 * locality2":null,"postal":null,"centroid
	 * ":{"latitude":"41.775360","longitude
	 * ":"-72.522667"},"boundingBox":{"southWest
	 * ":{"latitude":"41.733780","longitude
	 * ":"-72.583679"},"northEast":{"latitude
	 * ":"41.820351","longitude":"-72.463631
	 * "}},"areaRank":"3","popRank":"9"},{"lang
	 * ":"en-US","uri":"http://where.yahooapis
	 * .com/v1/place/2444659","woeid":"2444659
	 * ","placeTypeName":{"code":"7","content
	 * ":"Town"},"name":"Manchester","country
	 * ":{"code":"US","type":"Country","content":"United
	 * States"},"admin1":{"code
	 * ":"US-OK","type":"State","content":"Oklahoma"},"admin2
	 * ":{"code":"","type":"
	 * County","content":"Grant"},"admin3":null,"locality1":{"
	 * type":"Town","content
	 * ":"Manchester"},"locality2":null,"postal":{"type":"Zip
	 * Code","content":"73758
	 * "},"centroid":{"latitude":"36.993950","longitude":"-
	 * 98.036636"},"boundingBox
	 * ":{"southWest":{"latitude":"36.984859","longitude":"
	 * -98.048027"},"northEast
	 * ":{"latitude":"37.003040","longitude":"-98.025269"}},"
	 * areaRank":"2","popRank
	 * ":"1"},{"lang":"en-US","uri":"http://where.yahooapis
	 * .com/v1/place/2444673","
	 * woeid":"2444673","placeTypeName":{"code":"7","content
	 * ":"Town"},"name":"Manchester
	 * ","country":{"code":"US","type":"Country","content":"United
	 * States"},"admin1
	 * ":{"code":"US-MI","type":"State","content":"Michigan"},"admin2
	 * ":{"code":"","
	 * type":"County","content":"Washtenaw"},"admin3":null,"locality1
	 * ":{"type":"Town
	 * ","content":"Manchester"},"locality2":null,"postal":{"type":"Zip
	 * Code","content
	 * ":"48158"},"centroid":{"latitude":"42.145851","longitude":"-
	 * 84.068817"},"boundingBox
	 * ":{"southWest":{"latitude":"42.137100","longitude":"
	 * -84.072319"},"northEast
	 * ":{"latitude":"42.161800","longitude":"-84.016670"}},"
	 * areaRank":"2","popRank
	 * ":"1"},{"lang":"en-US","uri":"http://where.yahooapis
	 * .com/v1/place/2444651","
	 * woeid":"2444651","placeTypeName":{"code":"7","content
	 * ":"Town"},"name":"Manchester
	 * ","country":{"code":"US","type":"Country","content":"United
	 * States"},"admin1
	 * ":{"code":"US-MA","type":"State","content":"Massachusetts"},"
	 * admin2":{"code
	 * ":"","type":"County","content":"Essex"},"admin3":null,"locality1
	 * ":{"type":"
	 * Town","content":"Manchester"},"locality2":null,"postal":{"type":"Zip
	 * Code","
	 * content":"01944"},"centroid":{"latitude":"42.577621","longitude":"-
	 * 70.769470
	 * "},"boundingBox":{"southWest":{"latitude":"42.566189","longitude":"
	 * -70.784538
	 * "},"northEast":{"latitude":"42.584370","longitude":"-70.759842"}},"
	 * areaRank
	 * ":"2","popRank":"1"},{"lang":"en-US","uri":"http://where.yahooapis
	 * .com/v1/
	 * place/2444653","woeid":"2444653","placeTypeName":{"code":"7","content
	 * ":"Town
	 * "},"name":"Manchester","country":{"code":"US","type":"Country","content
	 * ":"United
	 * States"},"admin1":{"code":"US-MO","type":"State","content":"Missouri
	 * "},"admin2":{"code":"","type":"County","content":"St.
	 * Louis"},"admin3":null,"
	 * locality1":{"type":"Town","content":"Manchester"},"
	 * locality2":null,"postal":{"type":"Zip
	 * Code","content":"63011"},"centroid":{"
	 * latitude":"38.592861","longitude":"-
	 * 90.510681"},"boundingBox":{"southWest":{"
	 * latitude":"38.582100","longitude":"
	 * -90.529541"},"northEast":{"latitude":"38.604481
	 * ","longitude":"-90.496979"}},"
	 * areaRank":"2","popRank":"8"},{"lang":"en-US","
	 * uri":"http://where.yahooapis
	 * .com/v1/place/2444667","woeid":"2444667","placeTypeName
	 * ":{"code":"7","content
	 * ":"Town"},"name":"Manchester","country":{"code":"US","
	 * type":"Country","content":"United
	 * States"},"admin1":{"code":"US-TN","type":"
	 * State","content":"Tennessee"},"admin2
	 * ":{"code":"","type":"County","content
	 * ":"Coffee"},"admin3":null,"locality1":{"
	 * type":"Town","content":"Manchester
	 * "},"locality2":null,"postal":null,"centroid
	 * ":{"latitude":"35.476238","longitude
	 * ":"-86.081017"},"boundingBox":{"southWest
	 * ":{"latitude":"35.418701","longitude
	 * ":"-86.110909"},"northEast":{"latitude
	 * ":"35.515541","longitude":"-86.040062
	 * "}},"areaRank":"3","popRank":"8"},{"lang
	 * ":"en-US","uri":"http://where.yahooapis
	 * .com/v1/place/2444672","woeid":"2444672
	 * ","placeTypeName":{"code":"7","content
	 * ":"Town"},"name":"Manchester","country
	 * ":{"code":"US","type":"Country","content":"United
	 * States"},"admin1":{"code
	 * ":"US-VT","type":"State","content":"Vermont"},"admin2
	 * ":{"code":"","type":"
	 * County","content":"Bennington"},"admin3":null,"locality1
	 * ":{"type":"Town","
	 * content":"Manchester"},"locality2":null,"postal":{"type":"Zip
	 * Code","content
	 * ":"05254"},"centroid":{"latitude":"43.162991","longitude":"-
	 * 73.072083"},"boundingBox
	 * ":{"southWest":{"latitude":"43.140499","longitude":"
	 * -73.092010"},"northEast
	 * ":{"latitude":"43.181702","longitude":"-73.051529"}},"
	 * areaRank":"2","popRank":"8"}]}}}
	 */
	private String woeid;

	private String placeTypeName;
	private String name;

	private String country;
	private String countryCode;
	private String admin1;
	private String admin2;
	private String admin3;

	private String locality1;
	private String locality2;

	@Override
	public String getWoeid() {
		return this.woeid;
	}
}
