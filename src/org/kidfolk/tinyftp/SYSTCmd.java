package org.kidfolk.tinyftp;

import android.util.Log;
/**
 * Return system type.
 * @author kidfolk
 *
 */
public class SYSTCmd extends FTPCmd {
	private static final String TAG = "SYSTCmd";
	public static final String response = "215 UNIX Type: L8\r\n";
	private String inputStr;
	
	

	public SYSTCmd(Session session,String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}



	@Override
	public void run() {
		Log.v(TAG, "SYSTCmd executing!");
		session.sendReplyString(response);
		Log.v(TAG, response);
	}
 
}
 
