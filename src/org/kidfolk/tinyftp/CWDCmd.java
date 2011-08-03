package org.kidfolk.tinyftp;

import java.io.File;
import java.io.IOException;

import android.util.Log;

public class CWDCmd extends FTPCmd {
	private static final String TAG = "CWDCmd";

	private String inputStr;

	public CWDCmd(Session session, String inpoutStr) {
		super(session);
		this.inputStr = inpoutStr;
	}

	@Override
	public void run() {
		Log.v(TAG, "CWD executing");
		String param = getParameter(inputStr);
		File newDir;
		String errString = null;
		mainblock: {
			newDir = inputPathToChrootedFile(session.getCurrentDir(), param);

			// Ensure the new path does not violate the chroot restriction
			if (violatesChroot(newDir)) {
				errString = "550 Invalid name or chroot violation\r\n";
				session.sendReplyString(errString);
				Log.v(TAG, errString);
				break mainblock;
			}

			try {
				newDir = newDir.getCanonicalFile();
				if (!newDir.isDirectory()) {
					session.sendReplyString("550 Can't CWD to invalid directory\r\n");
				} else if (newDir.canRead()) {
					session.currentDir = newDir;
					session.sendReplyString("250 CWD successful\r\n");
				} else {
					session.sendReplyString("550 That path is inaccessible\r\n");
				}
			} catch (IOException e) {
				session.sendReplyString("550 Invalid path\r\n");
				break mainblock;
			}
		}
		Log.v(TAG, "CWD complete");

	}

}
