package org.kidfolk.tinyftp;

import android.util.Log;
/**
 * Sets the transfer mode
 * @author kidfolk
 *
 */
public class TYPECmd extends FTPCmd {
	private static String TAG = "TYPECmd";
	private String inputStr;
	
	

	public TYPECmd(Session session,String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}



	@Override
	public void run() {
		String output;
		String type = getParameter(inputStr);
		if(type.equals("I") || type.equals("L 8")) {
			output = "200 Binary type set\r\n";
			session.setBinaryMode(true);
		} else if (type.equals("A") || type.equals("A N")) {
			output = "200 ASCII type set\r\n";
			session.setBinaryMode(false);
		} else {
			output = "503 Malformed TYPE command\r\n";
		}
		session.sendReplyString(output);
	}
 
}
 
