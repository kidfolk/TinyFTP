package org.kidfolk.tinyftp;

import android.util.Log;

public class FEATCmd extends FTPCmd {
	private static final String TAG = "FEATCmd";
	private String inputStr;

	public FEATCmd(Session session, String inputStr) {
		super(session);
		this.inputStr = FEATCmd.class.toString();
	}

	@Override
	public void run() {
		Log.v(TAG, "FEATCmd executing!");
		session.sendReplyString("211-Features supported\r\n");
		session.sendReplyString(" UTF8\r\n");
		session.sendReplyString("211 End\r\n");
		Log.v(TAG, "Gave FEAT response");
	}

}
