package org.kidfolk.tinyftp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public class LISTCmd extends AbstractLISTCmd {
	// The approximate number of milliseconds in 6 months
	public final static long MS_IN_SIX_MONTHS = 6 * 30 * 24 * 60 * 60 * 1000;
	private static String TAG = "LISTCmd";
	private String inputStr;

	public LISTCmd(Session session, String inputStr) {
		super(session, inputStr);
		this.inputStr = inputStr;
	}

	@Override
	public void run() {
		Log.v(TAG, "LISTCmd executing!");
		String errString = null;
		mainblock: {
			String param = getParameter(inputStr);
			Log.v(TAG,"LIST parameter: " + param);
			while (param.startsWith("-")) {
				// Skip all dashed -args, if present
				Log.v(TAG,"LIST is skipping dashed arg " + param);
				param = getParameter(param);
			}
			File fileToList = null;
			if (param.equals("")) {
				fileToList = session.getCurrentDir();
			} else {
				if (param.contains("*")) {
					errString = "550 LIST does not support wildcards\r\n";
					break mainblock;
				}
				session.addSubDirToCurrentDir(param);
				fileToList = session.getCurrentDir();
				if (violatesChroot(fileToList)) {
					errString = "450 Listing target violates chroot\r\n";
					break mainblock;
				}
			}
			String listing;
			if (fileToList.isDirectory()) {
				StringBuilder response = new StringBuilder();
				errString = listDir(response, fileToList);
				if (errString != null) {
					break mainblock;
				}
				listing = response.toString();
			} else {
				listing = makeLISTString(fileToList);
				if (listing == null) {
					errString = "450 Couldn't list that file\r\n";
					break mainblock;
				}
			}
			errString = sendLISTString(listing);
			if (errString != null) {
				break mainblock;
			}
		}

		if (errString != null) {
			session.sendReplyString(errString);
			Log.v(TAG, "LIST failed with: " + errString);
		} else {
			Log.v(TAG, "LIST completed OK");
		}
	}

	@Override
	public String makeLISTString(File file) {
		Log.v(TAG, "makeLISTString start!");
		long start = System.currentTimeMillis();
		StringBuilder response = new StringBuilder();

		if (!file.exists()) {
			Log.v(TAG, "makeLsString had nonexistent file");
			return null;
		}

		// See Daniel Bernstein's explanation of /bin/ls format at:
		// http://cr.yp.to/ftp/list/binls.html
		// This stuff is almost entirely based on his recommendations.

		String lastNamePart = file.getName();
		// Many clients can't handle files containing these symbols
		if (lastNamePart.contains("*") || lastNamePart.contains("/")) {
			Log.v(TAG, "Filename omitted due to disallowed character");
			return null;
		} else {
			// The following line generates many calls in large directories
			// staticLog.l(Log.DEBUG, "Filename: " + lastNamePart);
		}

		if (file.isDirectory()) {
			response.append("drwxr-xr-x 1 owner group");
		} else {
			// todo: think about special files, symlinks, devices
			response.append("-rw-r--r-- 1 owner group");
		}

		// The next field is a 13-byte right-justified space-padded file size
		long fileSize = file.length();
		String sizeString = new Long(fileSize).toString();
		int padSpaces = 13 - sizeString.length();
		while (padSpaces-- > 0) {
			response.append(' ');
		}
		response.append(sizeString);

		// The format of the timestamp varies depending on whether the mtime
		// is 6 months old
		long mTime = file.lastModified();
		SimpleDateFormat format;
		// Temporarily commented out.. trying to fix Win7 display bug
		if (System.currentTimeMillis() - mTime > MS_IN_SIX_MONTHS) {
			// The mtime is less than 6 months ago
			format = new SimpleDateFormat(" MMM dd HH:mm ", Locale.US);
		} else {
			// The mtime is more than 6 months ago
			format = new SimpleDateFormat(" MMM dd  yyyy ", Locale.US);
		}
		response.append(format.format(new Date(file.lastModified())));
		response.append(lastNamePart);
		response.append("\r\n");
		long end = System.currentTimeMillis();
		Log.v(TAG, "makeLISTString end!");
		Log.v(TAG, "total millis:"+(end-start));
		return response.toString();
	}

}
