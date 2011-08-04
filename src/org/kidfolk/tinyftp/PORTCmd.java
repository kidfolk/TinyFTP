package org.kidfolk.tinyftp;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.util.Log;

public class PORTCmd extends FTPCmd {
	private static final String TAG = "PORTCmd";
	private String inputStr;

	public PORTCmd(Session session, String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}

	@Override
	public void run() {
		Log.v(TAG, "PORT running");
		String errString = null;
		mainBlock: {
			String param = getParameter(inputStr);
			if (param.contains("|") && param.contains("::")) {
				errString = "550 No IPv6 support, reconfigure your client\r\n";
				break mainBlock;
			}
			String[] substrs = param.split(",");
			if (substrs.length != 6) {
				errString = "550 Malformed PORT argument\r\n";
				break mainBlock;
			}
			for (int i = 0; i < substrs.length; i++) {
				// Check that each IP/port octet is numeric and not too long
				if (!substrs[i].matches("[0-9]+") || substrs[i].length() > 3) {
					errString = "550 Invalid PORT argument: " + substrs[i]
							+ "\r\n";
					break mainBlock;
				}
			}
			byte[] ipBytes = new byte[4];
			for (int i = 0; i < 4; i++) {
				try {
					// We have to manually convert unsigned to signed
					// byte representation.
					int ipByteAsInt = Integer.parseInt(substrs[i]);
					if (ipByteAsInt >= 128) {
						ipByteAsInt -= 256;
					}
					ipBytes[i] = (byte) ipByteAsInt;
				} catch (Exception e) {
					errString = "550 Invalid PORT format: " + substrs[i]
							+ "\r\n";
					break mainBlock;
				}
			}
			InetAddress inetAddr;
			try {
				inetAddr = InetAddress.getByAddress(ipBytes);
			} catch (UnknownHostException e) {
				errString = "550 Unknown host\r\n";
				break mainBlock;
			}

			int port = Integer.parseInt(substrs[4]) * 256
					+ Integer.parseInt(substrs[5]);

			session.onPort(inetAddr, port);
		}
		if (errString == null) {
			session.sendReplyString("200 PORT OK\r\n");
			Log.v(TAG, "PORT completed");
		} else {
			Log.v(TAG, "PORT error: " + errString);
			session.sendReplyString(errString);
		}
	}
}
