package org.kidfolk.tinyftp;

import android.util.Log;

public abstract class FTPCmd implements Runnable {
	private static final String TAG = "FTPCmd";

	@Override
	public abstract void run();
	
	public static void dispatchCmd(Session session,String cmd){
		String[] strings = cmd.split(" ");
		String unrecognizedCmdMsg = "502 Command not recognized\r\n";
		if(null==strings){
			//nothing get from client
			String errString = "502 Command parse error\r\n";
			Log.v(TAG, errString);
			session.writeString(errString);
			return;
		}
		if(strings.length<1){
			//cmd is ""
			Log.v(TAG, unrecognizedCmdMsg);
			session.writeString(unrecognizedCmdMsg);
			return;
		}
		
		String cmdAction = strings[0];
		FTPCmd ftpCmdInstance;
		//get the normal case
		cmdAction = cmdAction.trim();
		cmdAction = cmdAction.toUpperCase();
	}

}
