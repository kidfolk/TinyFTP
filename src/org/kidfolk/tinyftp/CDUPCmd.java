package org.kidfolk.tinyftp;

import java.io.File;
import java.io.IOException;

import android.util.Log;

public class CDUPCmd extends FTPCmd {
	private static final String TAG = "CDUPCmd";
	private String inputStr;

	public CDUPCmd(Session session, String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}

	@Override
	public void run() {
		Log.v(TAG, "CDUP executing");
		File newDir;
		String errString = null;
		mainBlock: {
			File workingDir = session.getCurrentDir();
			newDir = workingDir.getParentFile();
			if (newDir == null) {
				errString = "550 Current dir cannot find parent\r\n";
				break mainBlock;
			}
			// Ensure the new path does not violate the chroot restriction
			if (violatesChroot(newDir)) {
				errString = "550 Invalid name or chroot violation\r\n";
				break mainBlock;
			}

			try {
				newDir = newDir.getCanonicalFile();
				if (!newDir.isDirectory()) {
					errString = "550 Can't CWD to invalid directory\r\n";
					break mainBlock;
				} else if (newDir.canRead()) {
					session.setCurrentDir(newDir);
				} else {
					errString = "550 That path is inaccessible\r\n";
					break mainBlock;
				}
			} catch (IOException e) {
				errString = "550 Invalid path\r\n";
				break mainBlock;
			}
		}
		if (errString != null) {
			session.sendReplyString(errString);
			Log.v(TAG, "CDUP error: " + errString);
		} else {
			session.sendReplyString("200 CDUP successful\r\n");
			Log.v(TAG, "CDUP success");
		}

	}

}
