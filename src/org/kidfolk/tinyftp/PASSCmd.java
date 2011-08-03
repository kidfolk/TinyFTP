package org.kidfolk.tinyftp;

import android.util.Log;
/**
 * Authentication password.
 * @author kidfolk
 *
 */
public class PASSCmd extends FTPCmd {
	private static final String TAG = "PASSCmd";
	private String inputStr;
	
	

	public PASSCmd(Session session,String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}



	@Override
	public void run() {
		Log.v(TAG, "PASSCmd executing!");
		String password = FTPCmd.getParameter(inputStr);
		if(password.equals(GlobleConfig.password)){
			session.sendReplyString("230 Logged on\r\n");
			Log.v(TAG, "230 Logged on");
		}else{
			session.sendReplyString("530 Login or password incorrect!\r\n");
			Log.v(TAG, "530 Login or password incorrect!");
		}
		Log.v(TAG, "PASSCmd complete!");
	}
 
}
 
