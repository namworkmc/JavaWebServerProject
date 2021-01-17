package HttpServerCore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServerConnection extends Thread
{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerConnection.class);

	
	//// Attributes ////
	private ServerSocket listener;
	private Socket socket;
	
	
	//// Constructor ////
	/**
	 * Constructor da luong ket noi server
	 * 
	 * @param listener tao socket
	 */
	public HttpServerConnection(ServerSocket listener) 
	{
		this.listener = listener;
	}
	

	/**
	 * Chay chuong trinh
	 */
	@Override
	public void run()
	{
		try
		{
			connect();
			
			
			LOGGER.info("Connection accepted: " + socket.getInetAddress());
			
			
			//// Doc request ////
			StringBuilder request_builder = readRequest();
			LOGGER.info("Request");
			System.out.println(request_builder.toString());
			
			
			//// Parse ////
			var requestParse = HttpRequestParser.parse(request_builder);
			
			
			if (requestParse.get("path").equals("/index.html")) 
			{
				//// Dung URL thuc hien ket noi ////
				LOGGER.info("Path: " + requestParse.get("path"));
				
				
				//// Doc file ////
				//Path path = Paths.get("./src/html/indexLocalHost.html"); // local host
				Path path = Paths.get("./src/html/index.html");
				String content_type = Files.probeContentType(path);
				HttpServerHandler responser = new HttpServerHandler(socket);
				responser.sendResponse("200 OK", content_type, Files.readAllBytes(path));
			}
			else 
			{
				//// Sai URL redirect thuc hien lai ket noi ////
				LOGGER.info("Path: " + requestParse.get("path"));
				LOGGER.info("Redirect. Location: http:/" + listener.getInetAddress()+ ":" + listener.getLocalPort() + "/index.html");
				
				
				Redirector redirector = new Redirector(socket);
				//redirector.moveToPage("http://127.0.0.1:9999/index.html"); // local host
				redirector.moveToPage("http:/" + listener.getInetAddress()+ ":" + listener.getLocalPort() +"/index.html");
				reconnect();
				
				
				//Path path = Paths.get("./src/html/indexLocalHost.html"); // local host
				Path path = Paths.get("./src/html/index.html");
				String content_type = Files.probeContentType(path);
				HttpServerHandler responser = new HttpServerHandler(socket);
				responser.sendResponse("200 OK", content_type, Files.readAllBytes(path));
			}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally 
		{
			LOGGER.info("Connection Processing Finished");
			
			try 
			{ //// close socket ////
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
	
	
	//// Methods ////
	/**
	 * readRequest doc request tu web de phan tich method
	 * 
	 * 
	 * @return StringBuilder
	 * 
	 * @throws IOException
	 */
	private StringBuilder readRequest() throws IOException
	{
		//// Mo luong vao
		InputStream inputStream = socket.getInputStream();
		
		//// Doc request tu web ////
		String request_lines; // buffer luu tung dong request
		StringBuilder request_builder = new StringBuilder(); // stream luu request lines
		BufferedReader buffer_reader = new BufferedReader(new InputStreamReader(inputStream)); // buffer doc request dau vao
		while (!(request_lines = buffer_reader.readLine()).isEmpty()) 
		{
			request_builder.append(request_lines + "\r\n");
		}
		
		return request_builder;
	}
	
	private void connect() throws IOException
	{
		LOGGER.info("Waiting for client");
		socket = listener.accept();
	}
	
	/**
	 * Ham reconnect thuc ket 
	 * noi lai den web neu redirect
	 * 
	 * Dong ServerSocket mo lai den localhost
	 * Dong socket mo lai
	 * 
	 * 
	 * @throws IOException 
	 */
	private void reconnect() throws IOException
	{
		socket.close();
		socket = listener.accept();
	}
}