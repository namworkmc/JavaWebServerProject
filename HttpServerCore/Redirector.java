package HttpServerCore;

import java.io.OutputStream;
import java.net.Socket;

public class Redirector 
{
	/*
	 * Attributes
	 */
	// socket de chuyen huong page
	private Socket socket; 

	
	/*
	 * Constructor
	 */
	public Redirector(Socket socket) 
	{
		this.socket = socket;
	}
	
	/*
	 * Methods
	 */
	public void moveToPage(String location)
	{
		try
		{
			OutputStream socket_output_stream = socket.getOutputStream();
			socket_output_stream.write(("HTTP/1.1 301 Moved Permanently \r\n").getBytes());
			socket_output_stream.write(("Location: " + location + " \r\n").getBytes());
			socket_output_stream.write("\r\n".getBytes());
			socket_output_stream.flush();
			socket.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
}
