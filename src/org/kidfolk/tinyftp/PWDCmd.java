package org.kidfolk.tinyftp;

import android.util.Log;
/**
 * Print working directory. Returns the current directory of the host.
 * @author kidfolk
 *
 */
public class PWDCmd extends FTPCmd {
	private static String TAG = "PWDCmd";
	private String inputStr;
	
	

	public PWDCmd(Session session,String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}



	@Override
	public void run() {
		Log.v(TAG, "257 "+GlobleConfig.rootdir+" is current directory.");
		session.sendReplyString("257 "+GlobleConfig.rootdir+" is current directory.\r\n");
	}
 
}
 
