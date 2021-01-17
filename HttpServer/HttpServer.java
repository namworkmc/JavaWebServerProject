package HttpServer;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import HttpServerCore.ServerListenerThread;

public class HttpServer 
{
	private final static Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);
	
	
	private final static int port = 9999;
	private final static String localhost = "localhost";
	private final static String web_root = "192.168.48.2";
	
    public static void main(String[] args)
    {
        
    	LOGGER.info("Server starting...");
    	LOGGER.info("Using port: " + port);
    	LOGGER.info("Using web root: " + web_root);
    	
    	ServerListenerThread server = null;
    	
		try 
		{
			server = new ServerListenerThread(port, web_root);
			server.start();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
    }
}