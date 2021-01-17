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

public class HttpClientConnection extends Thread 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientConnection.class);
	
	
	private ServerSocket listener;
	private Socket socket;
	
	
	/**
	 * Constructor da luong client
	 * 
	 * 
	 * @param client_socket
	 */
	public HttpClientConnection(ServerSocket listener)
	{
		this.listener = listener;
	}
	
	@Override
	public void run()
	{
		try
		{
			connect();
			/*
			 *  Mo luong vao cho client 
			 */
			InputStream input_stream = socket.getInputStream();
			
			
			/*
			 * Lay du lieu luong vao
			 */
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input_stream));
			char[] char_buffer = new char[1000000];
			int _byte = bufferedReader.read(char_buffer);
			
			if (_byte >= 0)
			{
				String request = new String(char_buffer, 0, _byte);
				if (!request.contains("POST")) // Khong phai phuong thuc POST
				{
					return;
				}
				else
				{
					LOGGER.info("Request");
					System.out.println(request + "\n");
					LOGGER.info("Response");
					
					if (request.contains("submit=Download"))
					{
						HttpServerHandler handler = new HttpServerHandler(listener, socket);
						handler.sendDownload();
					}
					else
					{
						HttpServerHandler handler = new HttpServerHandler(listener, socket);
						handler.checkingAccount(request);
					}
				}
			}
		}
		catch (IOException e)
		{
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
	 * Thuc hien ket noi
	 * 
	 * Kiem tra ket noi: neu thoi gian accept nho hon 1s tuc la socket ket noi chua dung
	 * 
	 * 
	 * @throws IOException 
	 */
	private void connect() throws IOException
	{
		LOGGER.info("Waiting for client");
		
		long start = System.currentTimeMillis();
		socket = listener.accept();
		long end = System.currentTimeMillis();
		
		if (!(end - start >= 1000))
		{
			socket.close();
			socket = listener.accept();
		}
		LOGGER.info("Connection accepted: " + socket.getInetAddress());
	}
}
