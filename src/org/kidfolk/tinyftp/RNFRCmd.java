package org.kidfolk.tinyftp;

import java.io.File;

import android.util.Log;
/**
 * Rename from.
 * @author kidfolk
 *
 */
public class RNFRCmd extends FTPCmd {
	private static final String TAG = "RNFRCmd";
	private String inputStr;

	
	public RNFRCmd(Session session,String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}
	@Override
	public void run() {
		String param = getParameter(inputStr);
		String errString = null;
		File file = null;
		mainblock: {
			file = inputPathToChrootedFile(session.getCurrentDir(), param);
			if(violatesChroot(file)) {
				errString = "550 Invalid name or chroot violation\r\n";
				break mainblock;
			}
			if(!file.exists()) {
				errString = "450 Cannot rename nonexistent file\r\n";
			}
		}
		if(errString != null) {
			session.sendReplyString(errString);
			Log.v(TAG, "RNFR failed: " + errString.trim());
			session.setRenameFrom(null);
		} else {
			session.sendReplyString("350 Filename noted, now send RNTO\r\n");
			session.setRenameFrom(file);
		}
	}

}
