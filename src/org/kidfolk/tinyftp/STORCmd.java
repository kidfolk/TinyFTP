package org.kidfolk.tinyftp;

import android.util.Log;

public class STORCmd extends AbstractSTORCmd {
	
	private static final String TAG = "STORCmd";
	private String inputStr;

	public STORCmd(Session session, String inputStr) {
		super(session, inputStr);
		this.inputStr = inputStr;
	}

	@Override
	public void run() {
		Log.v(TAG, "STORCmd executing!");
		doStorOrAppe(getParameter(inputStr), false);
	}

}
