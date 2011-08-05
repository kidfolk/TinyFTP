package org.kidfolk.tinyftp;

import java.io.File;

import android.util.Log;

public class RNTOCmd extends FTPCmd {
	private static final String TAG = "RNTOCmd";
	private String inputStr;

	public RNTOCmd(Session session, String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}

	@Override
	public void run() {
		String param = getParameter(inputStr);
		String errString = null;
		File toFile = null;
		Log.v(TAG, "RNTO executing\r\n");
		mainblock: {
			Log.v(TAG, "param: " + param);
			toFile = inputPathToChrootedFile(session.getCurrentDir(), param);
			Log.v(TAG, "RNTO parsed: " + toFile.getPath());
			if (violatesChroot(toFile)) {
				errString = "550 Invalid name or chroot violation\r\n";
				break mainblock;
			}
			File fromFile = session.getRenameFrom();
			if (fromFile == null) {
				errString = "550 Rename error, maybe RNFR not sent\r\n";
				break mainblock;
			}
			if (!fromFile.renameTo(toFile)) {
				errString = "550 Error during rename operation\r\n";
				break mainblock;
			}
		}
		if (errString != null) {
			session.sendReplyString(inputStr);
			Log.v(TAG, "RNFR failed: " + errString.trim());
		} else {
			session.sendReplyString("250 rename successful\r\n");
		}
		session.setRenameFrom(null);
		Log.v(TAG, "RNTO finished");

	}

}
