package org.kidfolk.tinyftp;

import java.io.File;


import android.util.Log;
/**
 * Delete file.
 * @author kidfolk
 *
 */
public class DELECmd extends FTPCmd {
	private static final String TAG = "DELECmd";
	private String inputStr;

	public DELECmd(Session session,String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}

	@Override
	public void run() {
		Log.v(TAG, "DELECmd executing!");
		String param = getParameter(inputStr);
		File storeFile = inputPathToChrootedFile(session.getCurrentDir(), param);
		String errString = null;
		if(violatesChroot(storeFile)) {
			errString = "550 Invalid name or chroot violation\r\n";
		} else if(storeFile.isDirectory()) {
			errString = "550 Can't DELE a directory\r\n";
		} else if(!storeFile.delete()) {
			errString = "450 Error deleting file\r\n";
		}
		
		if(errString != null) {
			session.sendReplyString(errString);
			Log.v(TAG, "DELE failed: " + errString.trim());
		} else {
			session.sendReplyString("250 File successfully deleted\r\n");
			//Delete file
			storeFile.delete();
		}
		Log.v(TAG, "DELE finished");
	}

}
