package org.kidfolk.tinyftp;

import android.util.Log;
/**
 * Authentication username.
 * @author kidfolk
 *
 */
public class USERCmd extends FTPCmd {
	private static final String TAG = "USERCmd";
	private String inputStr;
	
	

	public USERCmd(Session session,String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}



	@Override
	public void run() {
		Log.v(TAG, "USERCmd executing!");
		String username = FTPCmd.getParameter(inputStr);
		session.sendReplyString("331 Password required for "+username+"\r\n");
		Log.v(TAG, "331 Password required for "+username);
	}
 
}
 
