package org.kidfolk.tinyftp;

import java.net.InetAddress;

import android.util.Log;

/**
 * Enter passive mode.
 * 
 * @author kidfolk
 * 
 */
public class PASVCmd extends FTPCmd {
	private static final String TAG = "PASVCmd";
	private String inputStr;

	public PASVCmd(Session session, String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}

	@Override
	public void run() {
		Log.v(TAG, "PASVCmd executing!");
		String cantOpen = "502 Couldn't open a port\r\n";
		int port = session.onPASV();
		if (port != 0) {
			InetAddress inetAddress = session.getDataSocketPasvIP();
			StringBuilder response = new StringBuilder(
					"227 Entering Passive Mode (");
			// Output our IP address in the format xxx,xxx,xxx,xxx
			response.append(inetAddress.getHostAddress().replace('.', ','));
			response.append(",");

			// Output our port in the format p1,p2 where port=p1*256+p2
			response.append(port / 256);
			response.append(",");
			response.append(port % 256);
			response.append(").\r\n");
			session.sendReplyString(response.toString());
//			session.sendReplyString("datasocketport:"+port);
			Log.v(TAG, "server port:"+port);
			Log.v(TAG, "PASVCmd complete!sent: "+response.toString());
		}else{
			session.sendReplyString(cantOpen);
		}
		
	}

}
