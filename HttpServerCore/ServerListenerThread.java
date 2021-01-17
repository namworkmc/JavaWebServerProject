package HttpServerCore;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import HttpServer.HttpServer;

public class ServerListenerThread extends Thread 
{
	private final static Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);
	
	//// Attributes ////
	private int port;
	private String web_root;
	private ServerSocket listener;

	
	/**
	 * Da luong dung de nghe request tu port
	 * 
	 * @param port so port (0 - 65535)
	 * @param web_root ten mien cua web
	 */
	public ServerListenerThread(int port, String web_root) throws IOException {

		this.port = port;
		this.web_root = web_root;
		this.listener = new ServerSocket(this.port, 1, InetAddress.getByName(this.web_root));
	}

	
	/**
	 * Ham run de chay da luong
	 */
	@Override
	public void run() 
	{
		try
		{
			while (listener.isBound() && !listener.isClosed())
			{
				//// Server ////
				HttpServerConnection worker_thread = new HttpServerConnection(listener);
				worker_thread.run();
				
				
				//// Client ////
				HttpClientConnection client_thread = new HttpClientConnection(listener);
				client_thread.run();
			}
		}
		finally
		{
			try
			{ //// close listener socket ////
				listener.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Reconnect
	 * @throws IOException
	 */
	private void reconnect() throws IOException
	{
		LOGGER.info("Reconnect");
		listener.close();
		listener = new ServerSocket(port, 1, InetAddress.getByName(web_root));
	}
}
