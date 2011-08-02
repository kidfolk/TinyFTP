package org.kidfolk.tinyftp;

import android.util.Log;
/**
 * Enter extended passive mode.
 * like PASV
 * @author kidfolk
 *
 */
public class EPSVCmd extends FTPCmd {

	private static String TAG = "EPSVCmd";
	private String inputStr;
	
	public EPSVCmd(Session session,String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}

	@Override
	public void run() {
		int port = session.onEPSV();
		StringBuilder sb;
		if(port!=0){
			//listen success!
			sb = new StringBuilder("229 Entering Extended Passive Mode(|||");
			sb.append(port);
			sb.append("|)");
		}else{
			sb = new StringBuilder("500 server listen failure!");
		}
		session.sendReplyString(sb.toString());
		Log.v(TAG, sb.toString());
	}
 
}
 
