package org.kidfolk.tinyftp;

public abstract class AbstractLISTCmd extends FTPCmd{
	private static final String TAG = "AbstractLISTCmd";
	private String inputStr;

	public AbstractLISTCmd(Session session,String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}

}
