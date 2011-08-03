package org.kidfolk.tinyftp;

import android.util.Log;

public class STORCmd extends FTPCmd {
	private static final String TAG = "STORCmd";
	private String inputStr;

	public STORCmd(Session session,String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}

	@Override
	public void run() {
		Log.v(TAG, "STORCmd executind!");
		//TODO
	}

}
