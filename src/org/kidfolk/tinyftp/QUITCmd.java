package org.kidfolk.tinyftp;

import android.util.Log;

public class QUITCmd extends FTPCmd {
	private static String TAG = "QUITCmd";
	private String inputStr;
	
	

	public QUITCmd(Session session,String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}



	@Override
	public void run() {
		Log.v(TAG, "QUITCmd operation!");
		session.sendReplyString("210 CWD successful\r\n");
		// TODO Auto-generated method stub
		
	}
 
}
 
