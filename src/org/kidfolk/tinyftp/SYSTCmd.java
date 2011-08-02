package org.kidfolk.tinyftp;

import android.util.Log;
/**
 * Return system type.
 * @author kidfolk
 *
 */
public class SYSTCmd extends FTPCmd {
	private static String TAG = "SYSTCmd";
	private String inputStr;
	
	

	public SYSTCmd(Session session,String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}



	@Override
	public void run() {
		Log.v(TAG, "215 UNIX Type: L8\r\n");
		session.sendReplyString("215 UNIX Type: L8\r\n");
		
	}
 
}
 
