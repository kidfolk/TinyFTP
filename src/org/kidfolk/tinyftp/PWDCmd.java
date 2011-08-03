package org.kidfolk.tinyftp;

import java.io.IOException;


import android.util.Log;

/**
 * Print working directory. Returns the current directory of the host.
 * 
 * @author kidfolk
 * 
 */
public class PWDCmd extends FTPCmd {
	private static String TAG = "PWDCmd";
	private String inputStr;

	public PWDCmd(Session session, String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}

	@Override
	public void run() {
		Log.v(TAG, "PWDCmd executing!");
		try {
			String currentDir = session.getCurrentDir().getCanonicalPath();
			// 获得当前目录名，不包括父目录
			currentDir = currentDir.substring(GlobleConfig.rootdir.length());
			// The root directory requires special handling to restore its
			// leading slash
			if (currentDir.length() == 0) {
				currentDir = "/";
			}
			session.sendReplyString("257 \"" + currentDir + "\"\r\n");
		} catch (IOException e) {
			// This shouldn't happen unless our input validation has failed
			Log.v(TAG, "PWD canonicalize");
			session.closeCmdSocket(); // should cause thread termination
		}
		Log.v(TAG, "PWD complete");
	}

}
