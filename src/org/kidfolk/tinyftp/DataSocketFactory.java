package org.kidfolk.tinyftp;

import java.net.InetAddress;
import java.net.Socket;
/**
 * 打开，关闭数据Socket
 * @author kidfolk
 *
 */
public abstract class DataSocketFactory {
 
	public abstract boolean onPort(InetAddress dest, int port);
	/**
	 * server listen a free port
	 * @return
	 */
	public abstract int onPasv();
	public abstract Socket onTransfer();
	public abstract InetAddress getPasvIP();
	public abstract void reportTraffic(long numBytes);
}
 
