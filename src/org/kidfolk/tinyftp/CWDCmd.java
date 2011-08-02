package org.kidfolk.tinyftp;

import android.util.Log;

public class CWDCmd extends FTPCmd {
	private static String TAG = "CWDCmd";
	
	private String inputStr;
	
	

	public CWDCmd(Session session,String inpoutStr) {
		super(session);
		this.inputStr = inpoutStr;
	}



	@Override
	public void run() {
		Log.v(TAG, "CWDCmd operation!");
		session.sendReplyString("210 CWD successful\r\n");
		// TODO Auto-generated method stub
		
	}
 
}
 
