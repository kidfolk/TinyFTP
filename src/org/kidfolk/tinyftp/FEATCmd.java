package org.kidfolk.tinyftp;

public class FEATCmd extends FTPCmd {
	private static String TAG = "FEATCmd";
	private String inputStr;

	public FEATCmd(Session session,String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}

	@Override
	public void run() {
		session.sendReplyString("211-Features supported\r\n");
		session.sendReplyString(" UTF8\r\n"); 
		session.sendReplyString("211 End\r\n");

	}

}
