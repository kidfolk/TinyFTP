/*
Copyright 2009 David Revell

This file is part of SwiFTP.

SwiFTP is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SwiFTP is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.kidfolk.tinyftp;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

abstract public class Util {
	private static final String TAG = "Util";
	
	public static byte byteOfInt(int value, int which) {
		int shift = which * 8;
		return (byte)(value >> shift); 
	}
	
	public static String ipToString(int addr, String sep) {
		//myLog.l(Log.DEBUG, "IP as int: " + addr);
		if(addr > 0) {
			StringBuffer buf = new StringBuffer();
			buf.
			append(byteOfInt(addr, 0)).append(sep).
			append(byteOfInt(addr, 1)).append(sep).
			append(byteOfInt(addr, 2)).append(sep).
			append(byteOfInt(addr, 3));
			Log.v(TAG, "ipToString returning: " + buf.toString());
			return buf.toString();
		} else {
			return null;
		}	
	}
	
	public static InetAddress intToInet(int value) {
		byte[] bytes = new byte[4];
		for(int i = 0; i<4; i++) {
			bytes[i] = byteOfInt(value, i);
		}
		try {
			return InetAddress.getByAddress(bytes);
		} catch (UnknownHostException e) {
			// This only happens if the byte array has a bad length
			return null;
		}
	}
	
	public static String ipToString(int addr) {
		if(addr == 0) {
			// This can only occur due to an error, we shouldn't blindly
			// convert 0 to string.
			Log.v(TAG, "ipToString won't convert value 0");
			return null;
		}
		return ipToString(addr, ".");
	}
	

}
