package org.kidfolk.tinyftp;
/**
 * ftp命令类，封装ftp命令的name和所代表的类
 * @author kidfolk
 *
 */
public class CmdMap {
	
	protected Class<? extends FTPCmd> cmdClass;
	private String name;
	
	
	public CmdMap(Class<? extends FTPCmd> cmdClass, String name) {
		super();
		this.cmdClass = cmdClass;
		this.name = name;
	}

	public Class<? extends FTPCmd> getCmdClass() {
		return cmdClass;
	}
	
	public String getName() {
		return name;
	}
	
	

}
