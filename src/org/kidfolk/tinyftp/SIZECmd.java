package org.kidfolk.tinyftp;

import android.util.Log;

public class SIZECmd extends FTPCmd {
	private static String TAG = "SIZECmd";
	private String inputStr;
	
	

	public SIZECmd(Session session,String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}



	@Override
	public void run() {
		Log.v(TAG, "SIZECmd operation!");
		session.sendReplyString("210 CWD successful\r\n");
		// TODO Auto-generated method stub
		
	}
 
}
 
