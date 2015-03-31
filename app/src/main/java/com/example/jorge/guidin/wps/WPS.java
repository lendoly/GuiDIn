package com.example.jorge.guidin.wps;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.SystemClock;



public class WPS {

	private WifiManager manager;
	
	public WPS(Context context) {
		manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	}
	
	public List<ScanResult> scanAndShow() throws WPSException{

		if(manager.isWifiEnabled()){
			manager.startScan();
			SystemClock.sleep(500);
			return manager.getScanResults();
		}else{
			throw new WPSException("El dispositivo WIFI est� desactivado.");
		}
	}	
	
}
