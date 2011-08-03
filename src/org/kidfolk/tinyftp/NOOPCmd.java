package org.kidfolk.tinyftp;

import android.util.Log;

public class NOOPCmd extends FTPCmd {
	private static final String TAG = "NOOPCmd";
	private String inputStr;

	public NOOPCmd(Session session,String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}

	@Override
	public void run() {
		Log.v(TAG, "NOOPCmd executing!");
		session.sendReplyString("200 NOOP OK\r\n");
	}

}
