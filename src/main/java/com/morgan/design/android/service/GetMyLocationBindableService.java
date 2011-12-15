package com.morgan.design.android.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

/**
 * Based on :
 * 
 * <pre>
 * http://stackoverflow.com/questions/3145089/what-is-the-simplest-and-most-robust-way-to-get-the-users-current-location-in-a/3145655#3145655
 * </pre>
 * 
 * @author James Edward Morgan
 */
@Deprecated
public class GetMyLocationBindableService extends Service {

	Timer timer;
	LocationResult locationResult;
	boolean gps_enabled = false;
	boolean network_enabled = false;

	private LocationManager locationManager;

	public class LocalBinder extends Binder {
		public GetMyLocationBindableService getService() {
			return GetMyLocationBindableService.this;
		}
	}

	private final IBinder mBinder = new LocalBinder();

	@Override
	public IBinder onBind(final Intent intent) {
		return this.mBinder;
	}

	@Override
	public void onCreate() {
		this.locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		return START_STICKY;
	};

	public boolean getLocation(final Context context, final LocationResult result) {
		// I use LocationResult callback class to pass location value from MyLocation to user code.
		this.locationResult = result;

		// exceptions will be thrown if provider is not permitted.
		try {
			this.gps_enabled = this.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		}
		catch (final Exception ex) {}
		try {
			this.network_enabled = this.locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		}
		catch (final Exception ex) {}

		// don't start listeners if no provider is enabled
		if (!this.gps_enabled && !this.network_enabled) {
			return false;
		}

		if (this.gps_enabled) {
			this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this.locationListenerGps);
		}
		if (this.network_enabled) {
			this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this.locationListenerNetwork);
		}
		this.timer = new Timer();
		this.timer.schedule(new GetLastLocation(), 20000);
		return true;
	}

	public void cancelTimer() {
		if (null != this.timer) {
			this.timer.cancel();
		}
		if (null != this.locationManager) {
			if (null != this.locationListenerGps) {
				this.locationManager.removeUpdates(this.locationListenerGps);
			}
			if (null != this.locationListenerNetwork) {
				this.locationManager.removeUpdates(this.locationListenerNetwork);
			}
		}
	}

	LocationListener locationListenerGps = new LocationListener() {
		@Override
		public void onLocationChanged(final Location location) {
			GetMyLocationBindableService.this.timer.cancel();
			GetMyLocationBindableService.this.locationResult.onLocationChanged(location);
			GetMyLocationBindableService.this.locationManager.removeUpdates(this);
			GetMyLocationBindableService.this.locationManager.removeUpdates(GetMyLocationBindableService.this.locationListenerNetwork);
		}

		@Override
		public void onProviderDisabled(final String provider) {
		}

		@Override
		public void onProviderEnabled(final String provider) {
		}

		@Override
		public void onStatusChanged(final String provider, final int status, final Bundle extras) {
		}
	};

	LocationListener locationListenerNetwork = new LocationListener() {
		@Override
		public void onLocationChanged(final Location location) {
			GetMyLocationBindableService.this.timer.cancel();
			GetMyLocationBindableService.this.locationResult.onLocationChanged(location);
			GetMyLocationBindableService.this.locationManager.removeUpdates(this);
			GetMyLocationBindableService.this.locationManager.removeUpdates(GetMyLocationBindableService.this.locationListenerGps);
		}

		@Override
		public void onProviderDisabled(final String provider) {
		}

		@Override
		public void onProviderEnabled(final String provider) {
		}

		@Override
		public void onStatusChanged(final String provider, final int status, final Bundle extras) {
		}
	};

	class GetLastLocation extends TimerTask {
		@Override
		public void run() {
			GetMyLocationBindableService.this.locationManager.removeUpdates(GetMyLocationBindableService.this.locationListenerGps);
			GetMyLocationBindableService.this.locationManager.removeUpdates(GetMyLocationBindableService.this.locationListenerNetwork);

			Location net_loc = null;
			Location gps_loc = null;

			if (GetMyLocationBindableService.this.gps_enabled) {
				gps_loc = GetMyLocationBindableService.this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}
			if (GetMyLocationBindableService.this.network_enabled) {
				net_loc = GetMyLocationBindableService.this.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}

			// if there are both values use the latest one
			if (gps_loc != null && net_loc != null) {
				if (gps_loc.getTime() > net_loc.getTime()) {
					GetMyLocationBindableService.this.locationResult.onLocationChanged(gps_loc);
				}
				else {
					GetMyLocationBindableService.this.locationResult.onLocationChanged(net_loc);
				}
				return;
			}

			if (gps_loc != null) {
				GetMyLocationBindableService.this.locationResult.onLocationChanged(gps_loc);
				return;
			}
			if (net_loc != null) {
				GetMyLocationBindableService.this.locationResult.onLocationChanged(net_loc);
				return;
			}
			GetMyLocationBindableService.this.locationResult.onLocationNotFound();
		}
	}

	public static abstract class LocationResult {
		public abstract void onLocationChanged(Location location);

		public abstract void onLocationNotFound();
	}

}
