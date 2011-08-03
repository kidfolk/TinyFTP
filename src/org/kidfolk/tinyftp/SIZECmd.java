package org.kidfolk.tinyftp;

import java.io.File;
import java.io.IOException;

import android.util.Log;

/**
 * Return the size of a file.
 * 
 * @author kidfolk
 * 
 */
public class SIZECmd extends FTPCmd {
	private static final String TAG = "SIZECmd";
	private String inputStr;

	public SIZECmd(Session session, String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}

	@Override
	public void run() {
		Log.v(TAG, "SIZE executing");

		String errString = null;
		String param = getParameter(inputStr);
		long size = 0;
		mainblock: {
			File currentDir = new File(GlobleConfig.rootdir);
			if(param.contains(File.separator)) {
				errString = "550 No directory traversal allowed in SIZE param\r\n";
				break mainblock;
			}
			File target = new File(currentDir, param);

			// We should have caught any invalid location access before now, but
			// here we check again, just to be explicitly sure.
			if(violatesChroot(target)) {
				errString = "550 SIZE target violates chroot\r\n";
				break mainblock;
			}
			if(!target.exists()) {
				errString = "550 Cannot get the SIZE of nonexistent object\r\n";
				try {
					Log.v(TAG,"Failed getting size of: " + target.getCanonicalPath());
				} catch (IOException e) {}
				break mainblock;
			}
			if(!target.isFile()) {
				errString = "550 Cannot get the size of a non-file\r\n";
				break mainblock;
			}
			size = target.length(); 
		}
		if (errString != null) {
			session.sendReplyString(errString);
		} else {
			session.sendReplyString("213 " + size + "\r\n");
		}
		Log.v(TAG, "SIZE complete: "+size);
	}

}
