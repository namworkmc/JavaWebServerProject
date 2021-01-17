package HttpServerCore;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpServerHandler
{
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerHandler.class);
	
	
	//// Attributes ////
	private ServerSocket listener;
	private Socket socket;
	
	
	//// Constructors ////
	/**
	 * Class xu ly cac dich vu nhu chuyen den trang neu khong tim thay account 404 Not Found
	 * chuyen den trang info neu tim thay account
	 * ...
	 * 
	 *  @param ServerSocket listener
	 */
	public HttpServerHandler(ServerSocket listener) 
	{
		this.listener = listener;
		
		try
		{
			socket = listener.accept();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public HttpServerHandler(Socket socket)
	{
		this.socket = socket;
	}
	
	public HttpServerHandler(ServerSocket listener, Socket socket)
	{
		this.listener = listener;
		this.socket = socket;
	}
	
	/**
	 * Ham sendResponse gui response ve cho web thuc hien ket noi server
	 * 
	 * 
	 * @param status cac lenh cua HTTP Protocol, vi du "200 OK", "404 Not Found", ...
	 * @param content_type kieu du lieu
	 * @param content noi dung du lieu
	 */
	public void sendResponse(String status, String content_type, byte[] content) throws IOException
	{
		LOGGER.info("Response");
		
		
		OutputStream socketOutputStream = socket.getOutputStream();
		socketOutputStream.write(("HTTP/1.1 \r\n" + status).getBytes());
		socketOutputStream.write(("ContentType: " + content_type + "\r\n").getBytes());
		socketOutputStream.write("\r\n".getBytes());
		socketOutputStream.write(content);
        socketOutputStream.write("\r\n\r\n".getBytes());
        socketOutputStream.flush();
        socket.close();
	}
	
	/**
	 * Kiem tra account co phai admin hay khong
	 * 
	 * 
	 * @param username "admin"
	 * @param password "admin"
	 */
	public void checkingAccount(String request)
	{
		try
		{
			if (request.contains("Username=admin&Password=admin"))
			{
				LOGGER.info("Welcome Admin");
				LOGGER.info("Login Successful");
				
				
				LOGGER.info("Redirect. Location: http:/" + listener.getInetAddress()+ ":" + listener.getLocalPort() + "/info.html");
				Redirector redirector = new Redirector(socket);
				//redirector.moveToPage("http://127.0.0.1:9999/info.html"); // local host
				redirector.moveToPage("http:/" + listener.getInetAddress()+ ":" + listener.getLocalPort() + "/info.html");
				reconnect();
				
				
				//Path path = Paths.get("./src/html/infoLocalHost.html"); // local host
				Path path = Paths.get("./src/html/info.html");
				String content_type = Files.probeContentType(path);
				sendResponse("200 OK", content_type, Files.readAllBytes(path));
			}
			else
			{
				LOGGER.info("Unknow Account");
				LOGGER.info("Not Found");
				
				
				LOGGER.info("Redirect. Location: http:/" + listener.getInetAddress()+ ":" + listener.getLocalPort() + "/404.html");
				Redirector redirector = new Redirector(socket);
				//redirector.moveToPage("http://127.0.0.1:9999/404.html"); // local host
				redirector.moveToPage("http:/" + listener.getInetAddress()+ ":" + listener.getLocalPort() + "/404.html");
				reconnect();
				
				
				Path path = Paths.get("./src/html/404.html");
				String content_type = Files.probeContentType(path);
				sendResponse("200 OK", content_type, Files.readAllBytes(path));
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally 
		{
			try 
			{
				if (!socket.isClosed())
				{
					socket.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Send download file files.html cho Client
	 */
	void sendDownload()
	{
		LOGGER.info("Redirect. Location: http:/" + listener.getInetAddress()+ ":" + listener.getLocalPort() + "/files.html");
		Redirector redirector = new Redirector(socket);
		//redirector.moveToPage("http://127.0.0.1:9999/files.html"); // local host
		redirector.moveToPage("http:/" + listener.getInetAddress()+ ":" + listener.getLocalPort() + "/files.html");
		
		
		try 
		{
			reconnect();
			
			Path path = Paths.get("./src/html/files.html");
			String content_type = Files.probeContentType(path);
			sendResponse("200 OK", content_type, Files.readAllBytes(path));
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try 
			{
				if (!socket.isClosed()) 
				{
					socket.close();
				}
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Ham reconnect thuc hien ket noi lai den web neu redirect
	 * 
	 * 
	 * @throws IOException loi IO thong thuong
	 */
	private void reconnect() throws IOException
	{
		if (!socket.isClosed())
		{
			socket.close();
		}
		socket = listener.accept();
	}
}
