package org.kidfolk.tinyftp;

import android.util.Log;

public class QUITCmd extends FTPCmd {
	private static final String TAG = "QUITCmd";
	private String inputStr;
	
	

	public QUITCmd(Session session,String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}



	@Override
	public void run() {
		Log.v(TAG, "QUITting");
		session.sendReplyString("221 Goodbye\r\n");
		session.closeCmdSocket();
	}
 
}
 
