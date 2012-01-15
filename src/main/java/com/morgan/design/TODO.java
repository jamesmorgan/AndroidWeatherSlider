package com.morgan.design;

public class TODO {
	private TODO() {
	}

	//@formatter:off
	
	// ////////////////////////
	// Build / Configuration //
	// ////////////////////////
	
	// FIXME -> Debug mode on/off for deployment
	// FIXME -> Configure build process, updating versions/ debug enabled/ keys/ changelog etc
	// FIXME -> Consider signing apk with maven -> http://code.google.com/p/maven-android-plugin/wiki/SigningAPKWithMavenJarsigner
	// FIXME -> When generating sources, set debug to false.
	// FIXME -> Pro-guard bug when running on real phone -> Update latest maven-android-plugin - 3.0.1
	
	// ////////////////
	// Consideration //
	// ////////////////

	// FIXME -> Show location on a small map in overview mode?
	// FIXME -> Open location on map from notification click handler
	// FIXME -> Turn sun set and rise in time objects
	// FIXME -> Fix bug when parsing RFC822 dates from yahoo -> http://www.anyang-window.com.cn/sax-read-yahoo-service-weather-data/ - Date publish = new SimpleDateFormat ("EEE, dd MMM yyyy hh: mm az" Locale.US). Parse (s_date);
	// FIXME -> Allow for user to override default timeout, functionality already created, no preference
	// FIXME -> New providers, Google / The Weather Channel - http://www.weather.com/services/xmloap.html
	// FIXME -> Add temperature highs/lows for day on overview screen
	// FIXME -> DB Version's & Tests
	// FIXME -> Change to native XML parser -> http://android-developers.blogspot.com/2011/12/watch-out-for-xmlpullparsernexttext.html
	// FIXME -> Complete statuses, active, inactive, issues?
	// FIXME -> Add fine grained user control to location services -> http://android10.org/index.php/articleslocationmaps/226-android-location-providers-gps-network-passive
	// FIXME -> Improve location based service
	// FIXME -> Add option to stop lookup if battery % to low -> http://code.google.com/p/android-notifier/source/browse/AndroidNotifier/src/org/damazio/notifier/event/receivers/battery/BatteryEventReceiver.java
	// FIXME -> Monitor service to prevent services stopping
	// FIXME -> Lazy load service, are they needed -> is this possible?
	// FIXME -> Deal with service restarts, monitor services-to-service
	// FIXME -> Add option to extend location search, but may incur errors with weather lookup, must add in weather error checking e.g. http://weather.yahooapis.com/forecastrss?w=2347563&u=c
	// FIXME -> Set maximum number of allowed notifications in preference
	// FIXME -> Think about using a remote service and client for all operations -> http://saigeethamn.blogspot.com/2009/09/android-developer-tutorial-part-9.html
	// FIXME -> Add provider -> add switcher -> http://mvn.egoclean.com/
	// FIXME -> Add option for date format
	// FIXME -> Allow users to narrow down location accuracy
	// FIXME -> Consider alternative GeoPlannet features -> http://developer.yahoo.com/geo/geoplanet/
	// FIXME -> Android bug with int-array preferences -> http://code.google.com/p/android/issues/detail?id=2096
	// FIXME -> Possible calandar intergration for weather messages, warnings
	// FIXME -> Possible notification triggered on daily alarm with tip of the day about weather
	// FIXME -> How is the weather goign to affect me e.g. travel plans, car, plane, bus etc?
	// FIXME -> Look into hooking into alarm providers?
	// FIXME -> Try 'Add Location' at bottom of the screen
	// FIXME -> Always have GPS at the top, then last update time OR A_z
	// FIXME -> Potentially local time not last updated time in each notification, would require user option?
	// FIXME -> Changelog to techie? -> Possible change to have feedback more visible (menu) and place changelog in more hidden place
	
	// ///////////////
	// Nice To Have //
	// ///////////////
	
	// FIXME -> Potentially show accuracy and time when looking up GPS location
	// FIXME -> Option to disable collecting of logs via ACRA
	// FIXME -> Check try/catch blocks report to ACRA if required
	// FIXME -> Ability to sort front screen by various means, e.ge. active, roaming at top, place name, date time etc, Always have GPS at the top, then last update time OR A_z?
	// FIXME -> Overlay weather on google maps -> http://www.androidhive.info/2012/01/android-working-with-google-maps/
	// FIXME -> Proper error handling when no weather details found, parse response.
	// FIXME -> Create Broadcast Service helper e.g. BroadcastRegister.register(this); @BroadcastReciever on file will deal with unbind/bind
	// FIXME -> Consider Android Annotations (http://code.google.com/p/androidannotations/)
	// FIXME -> Progress bar with service broadcast register updates, show failures, loading, success etc
	// FIXME -> Weather animations?
	// FIXME -> Add in 3-5 day summary of weather
	// FIXME -> Create weather tips section, e.g. wear a coat it raining, it may take you longer to get to work, etc. -> opt-in & opt-out
	// FIXME -> Add ability to pay for application in-line
	// FIXME -> The home screen and notification order should always match the same ordering
	// FIXME -> Doc bar at the bottom of home screen, replace menu with refresh symbol and plus button for new location, could display service register updates
	// FIXME -> Give user option to create shortcuts to either open application, or open overview for one of there locations
	
	// ///////////////////
	// Next Version ?.? //
	// ///////////////////
	
	// FIXME -> Complete localisation
	// FIXME -> Add flags for each country
	// FIXME -> Change tabs for view pager 
	// -> http://www.zylinc.com/blog-reader/items/viewpager-page-indicator.html 
	// -> http://blog.stylingandroid.com/archives/537
	// FIXME -> Add 3rd party library's to credits -> ORMLite, ACRA, Yahoo Weather API
	// FIXME -> Present user with error screen when application starts if background data transmission is disabled
	// FIXME -> Hook into search button to enter a location, search-able activity
	// FIXME -> Listen for 3rd party GPS position updated so can update notifications if required.
	// FIXME -> Add ability to launch service from wake up i.e. unlock -> manifest changed - implementation missing
	// FIXME -> Improve current notification layout
	// FIXME -> Capture all shared preference values on application crash (ARCA)
	// FIXME -> Long click open management pop-up menu & short click open overview mode
	// FIXME -> Auto presets option, e.g. GPS, London, Toyko, New York, GPS?
	// FIXME -> Add Google Guava to project, plus possible refactoring, removal of Apache Commons
	// FIXME -> Clean up LaunchControllerReceiver, remove if required
	// FIXME -> Clean up front screen, possible move add location button to bottom, along with refresh/canel all icon
	
	// //////////////////////
	// Release Version 1.0 //
	// //////////////////////

	// FIXME -> Add Application on Android Market
	// FIXME -> Style up Market page once deployed
	
	// ///////////////////////////////////
	// User Feedback Version 1.0-BETA-2 //
	// ///////////////////////////////////
	
	// FIXME -> On first successful weather loaded, present user with rate me link to market.
	// FIXME -> Bug connectivity change broadcast not handled
	
	// FIXME -> DONE - (15/01/2012) - Create Application Icon
	// FIXME -> DONE - (15/01/2012) - Clean up home screen
	// FIXME -> DONE - (15/01/2012) - Confirm Google Analytics integration complete, make sure present on all areas possible, about, feedback form, all menu items
	// FIXME -> DONE - (15/01/2012) - Add menu to overview screen
	// FIXME -> DONE - (15/01/2012) - Re-ordered menu on home screen
	// FIXME -> DONE - (15/01/2012) - On application load/start, if no weathers set, show enter location screen first
	// FIXME -> DONE - (15/01/2012) - Change home screen ordering, roaming at top, errors at bottom 
	// FIXME -> DONE - (15/01/2012) - Put all strings in strings.xml for localisation, fixed typos

	// FIXME -> DONE - (14/01/2012) - Fix bug when no weather found for successful location woeid lookup. -> http://weather.yahooapis.com/forecastrss?w=2175446&u=c
	// FIXME -> DONE - (14/01/2012) - Re styled Service update area
	// FIXME -> DONE - (14/01/2012) - BUG - Service update text lost when more than X numbers of weathers, needs to be sticky
	// FIXME -> DONE - (14/01/2012) - BUG - Swipe left from "Today" overview screen goes to enter screen, should return to Overview
	// FIXME -> DONE - (14/01/2012) - Update change log, mentioning 'swipe gestures throughout'
	// FIXME -> DONE - (14/01/2012) - Rename loaded notifications button labels -> "Refresh, Disable, Delete"
	// FIXME -> DONE - (14/01/2012) - Update ORMLite 4.33
	// FIXME -> DONE - (14/01/2012) - Allow move to SD feature, upgrade to android 2.2, level 8
	// FIXME -> DONE - (14/01/2012) - Reduce size of install, remove unused/unneeded libraries from pom
	
	// Cosmetics:
	// FIXME -> DONE - (14/01/2012) - Grey out text when weather is disabled (keep red light)
	// FIXME -> DONE - (14/01/2012) - Make overview update time title bold
	// FIXME -> DONE - (14/01/2012) - Re-style clickable links on overview screen
	// FIXME -> DONE - (11/01/2012) - Clear up enter screen, reduce clutter at the top, move e.g. to below each one, add title?
	// FIXME -> DONE - (11/01/2012) - Remove info from overview titles
	// FIXME -> DONE - (11/01/2012) - Add location name to overview screen, replace current text e.g. Manchester, UK
	
	// /////////////////////////////////
	// User Feedback Version 1.0-BETA //
	// /////////////////////////////////
	/**
	 * --------------------------
	 * Captured - (10/01/2012) --
	 * --------------------------
	 *
	 * (+) Changelog to techie? -> Possible change to have feedback more visible (menu) and place changelog in more hidden place
	 * (C) Try 'Add Location' at bottom of the screen
	 * (/) On application load/start, if no weathers set, show enter location screen first
	 * (C) Always have GPS at the top, then last update time OR A_z 
	 * (NTH) The home screen and notification order should always match the same ordering
	 * (x) Red/warning if server weather
	 * (+) Long click open management pop-up menu & short click open overview mode
	 * (C) Potentially local time not last updated time in each notification, would require user option?
	 * (/) Add location name to overview screen, replace current text e.g. Manchester, UK
	 * (/) Buttons not links on overview screen, potential bug with clickable links, needs fixing.
	 * (/) Standard menu on overview screen
	 * (C) Doc bar at the bottom of home screen, replace menu with refresh symbol and plus button for new location, could display service register updates
	 * (+) Auto presets option, e.g. GPS, London, Tokoyo, New York, GPS?
	 * (/) Menu Icons
	 *
	 * --------------------------
	 * Captured - (09/01/2012) --
	 * --------------------------
	 *
	 * (/) Create Icon for application -> http://android-ui-utils.googlecode.com/hg/asset-studio/dist/icons-launcher.html
	 * (/) Rename loaded notifications button labels -> "Refresh, Disable, Delete"
	 * (/) Grey out text when weather is disabled (keep red light)
	 * (/) Clear up enter screen, reduce clutter at the top, move e.g. to below each one, add title?
	 * (/) Make overview update time title bold
	 * (/) Remove info from overview titles
	 **/
	
	// //////////////////////////////////////////
	// Release Version 1.0-BETA - (08/01/2012) //
	// //////////////////////////////////////////
	
	// FIXME -> DONE - (08/01/2012) - Clean up preferences, check defaults before go-live
	// FIXME -> DONE - (08/01/2012) - Ensure all necessary data is being stored correctly, missing created datetime for roaming notifications
	// FIXME -> DONE - (08/01/2012) - Set main menu view to default sort weathers by active, alphabetical
	// FIXME -> DONE - (08/01/2012) - Added "About" section, Me, Twitter, Morgan-Design, Market Link
	// FIXME -> DONE - (08/01/2012) - Ensure on reload -> cancel -> reload, all ongoing commands are cancelled
	// FIXME -> DONE - (08/01/2012) - Prevent no Location details shown when roaming service fails to find location on first attempt.
	
	// FIXME -> DONE - (07/01/2012) - Open location on map from overview screen
	// FIXME -> DONE - (07/01/2012) - Add roaming symbol/icon to roaming notifications.
	// FIXME -> DONE - (07/01/2012) - Moved active icon on home screen
	// FIXME -> DONE - (07/01/2012) - Updated Change log to indicate initial release, confirm still works via menu and start up
	// FIXME -> DONE - (07/01/2012) - Improved cancel all notifications so force deletes all.
	// FIXME -> DONE - (07/01/2012) - Don't use notification services
	// FIXME -> DONE - (07/01/2012) - BUG - Not allowed to add multiple notifications bug
	// FIXME -> DONE - (07/01/2012) - BUG - When changing GPS location notifications are not updated correctly
	// FIXME -> DONE - (07/01/2012) - Bug relates to locations which have a WOEID but yahoo returns no weather, reduced scope of Geocode and GPS lookup YahooPlaceTypes
	
	// FIXME -> DONE - (02/01/2012) - Improve cancel all, verbose cancellation if three types
	// FIXME -> DONE - (02/01/2012) - Add ability to force update all active notifications from home menu
	// FIXME -> DONE - (02/01/2012) - Add ability to force update individual locations from context menu
	// FIXME -> DONE - (02/01/2012) - Removal of old notification services 
	// FIXME -> DONE - (02/01/2012) - Set correct flags on overview screen as to not keep old data
	
	// FIXME -> DONE - (28/12/2011) - Trailing single notification service, deprecated old ones
	
	// FIXME -> DONE - (27/12/2011) - Handle situations when no locations found
	// FIXME -> DONE - (27/12/2011) - Added interface of Woeid when creating weather requests
	
	// FIXME -> DONE - (26/12/2011) - Fixed NPE when unable to find Geocode location 
	// FIXME -> DONE - (26/12/2011) - Set read time out on rest template, move all rest calls to RestTemplateFactory.class
	// FIXME -> DONE - (26/12/2011) - Alarm control via application start, ensuring services both register for alarm updates
	// FIXME -> DONE - (26/12/2011) - Added service update to alarm service so GUI updates with info about on going refresh
	// FIXME -> DONE - (26/12/2011) - Change yahoo date to string as no computation needed, update overview and notification
	// FIXME -> DONE - (26/12/2011) - Fix overview screens in-line with latest changes, no long passes object to activity
	// FIXME -> DONE - (26/12/2011) - Add phone boot alarm service & option - http://it-ride.blogspot.com/2010/10/android-implementing-notification.html

	// FIXME -> DONE - (25/12/2011) - Add alarm service
	// FIXME -> DONE - (25/12/2011) - Allow for roaming location based weather (paid version only) - http://android-developers.blogspot.com/2011/06/deep-dive-into-location.html
	// FIXME -> DONE - (25/12/2011) - When reloading weather ensure still uses same service not creating a new one
	// FIXME -> DONE - (25/12/2011) - Add preference for updating on connectivity change (StaticLookupService)
	// FIXME -> DONE - (25/12/2011) - Ensure max 3 notifications, only one roaming based weather lookup, 2-3 optional other locations
	// FIXME -> DONE - (25/12/2011) - Added protection if no weather icon code found after query
	// FIXME -> DONE - (25/12/2011) - Allow re-loading of existing notifications on start up

	// FIXME -> DONE - (24/12/2011) - Disable ACRA if running emulator

	// FIXME -> DONE - (23/12/2011) - On preferences updated reload all notifications
	// FIXME -> DONE - (23/12/2011) - Ability to Cancel all active notifications from home menu

	// FIXME -> DONE - (22/12/2011) - Added Gmaven plugin in-order synchronise android and maven version numbers 
	// -> http://code.google.com/p/maven-android-plugin/wiki/SynchronisingVersionWithGmaven
	// FIXME -> DONE - (22/12/2011) - ZipAlign apg as part of maven build 
	// -> http://code.google.com/p/maven-android-plugin/wiki/ZipalignAPKBuiltByMAven
	// -> http://www.simpligility.com/2010/06/maven-android-plugin-with-zipalign-and-improved-verification/

	// FIXME -> DONE - (19/12/2011) - Remove as much as possible Android Lint warnings/errors
	// FIXME -> DONE - (19/12/2011) - When adding more than 3 weather notifications, don't set active flag to true
	// FIXME -> DONE - (19/12/2011) - Get WOEID from current lat/long
	// FIXME -> DONE - (19/12/2011) - Fix GPS bug when finding location
	// FIXME -> DONE - (19/12/2011) - Add constant feedback on home screen of ongoing lookups/queries e.g. loading
	// FIXME -> DONE - (19/12/2011) - Add ability to launch service on phone start up, check existing functionality
	// FIXME -> DONE - (19/12/2011) - Allow removal not just deletion of any notification via home screen
	// FIXME -> DONE - (19/12/2011) - Broadcast active notifications changed in order to reload choices on home screen
	// FIXME -> DONE - (19/12/2011) - Add fields to DB, Active, Latitude, Longitude -> rename to WeatherChoice.java
	// FIXME -> DONE - (19/12/2011) - Show active state on home screen

	// FIXME -> DONE - (18/12/2011) - Improve progress/interaction when looking up GPS location
	// FIXME -> DONE - (18/12/2011) - If GPS && network are disabled, prompt user to turn it on - http://advback.com/android/checking-if-gps-is-enabled-android/

	// ////////////////////////////////////
	// Previously Completed - 18/12/2011 //
	// ////////////////////////////////////

	// FIXME -> DONE - Record service and current notification in application
	// FIXME -> DONE - Update Overview screen to lookup weather
	// FIXME -> DONE - allow only maximum number of 3 active notifications at once
	// FIXME -> DONE - allow for multiple notifications at once (paid version only)
	// FIXME -> DONE - Check for battery level (15%), active networks (GPS and Mobile), location age
	// FIXME -> DONE - improve notification when no locations found
	// FIXME -> DONE - Created none-bindable intent based service
	// FIXME -> DONE - Add in and setup crash report handler (http://code.google.com/p/acra/), Customised with toast and logcat logs
	// FIXME -> DONE - Update latest ORMLite version - 4.31
	// FIXME -> DONE - Change log pop-up and menu option
	// FIXME -> DONE - Feedback log pop-up on new version
	// FIXME -> DONE - Setup merchant account on google
	// FIXME -> DONE - Added google analytics jar and initial tracking calls
	// FIXME -> DONE - download new icon sets, weather channel icons
	// FIXME -> DONE - add option for wind unit e.g. kmh/mph
	// FIXME -> DONE - add location information to overview screen
	// FIXME -> DONE - Improve notification layout
	// FIXME -> DONE - Condition code translation class
	// FIXME -> DONE - Convert all types correctly, dates, ints, floats, longs etc
	// FIXME -> DONE - add swipe gesture to overview tabs
	// FIXME -> DONE - custom tab view with xml skin
	// FIXME -> DONE - Add Link to more information on Overview screen
	// FIXME -> DONE - Add visibility/pressure/pressure rising to query and overview screen
	// FIXME -> DONE - Add option for C or F when retrieving temperature
	// FIXME -> DONE - improved error messages for no connection found for GPS and weather query
	// FIXME -> DONE - handle on click event notification event. opening overview, initial design
	// FIXME -> DONE - Create converter for decimal degrees to human readable wind direction
	// FIXME -> DONE - Add icons over view sun set and rise
	// FIXME -> DONE - Ensure Home screen is always updated on application focus/launch
	// FIXME -> DONE - Query Yahoo based on GPS location (paid version only)
	// FIXME -> DONE - GPS based location finding (paid version only)
	// FIXME -> DONE - on preferences change notification service click handler
	// FIXME -> DONE - on preferences change loader service polling options
	// FIXME -> DONE - On click notification user preference (paid version only)
	// FIXME -> DONE - start service on phone boot boot up (paid version only)
	// FIXME -> DONE - start last known service on open (paid version only)
	// FIXME -> DONE - swipe navigation path - (add to manual)
	// FIXME -> DONE - periodically query for weather
	// FIXME -> DONE - check phone has Internet before launching

	//@formatter:on

}
