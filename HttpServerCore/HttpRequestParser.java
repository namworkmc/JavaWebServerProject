package HttpServerCore;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class HttpRequestParser
{
	static HashMap<String, String> parse(StringBuilder request_builder)
	{
		//// parse thong tin cua request tu luong vao ////
		String request = request_builder.toString();
        String[] requests_lines = request.split("\r\n");
        String[] request_line = requests_lines[0].split(" ");
        String method = request_line[0];
        String path = request_line[1];
        String version = request_line[2];
        String host = requests_lines[1].split(" ")[1];
        
        
        //// Map luu thong tin cua request ////
        HashMap<String, String> request_info = new HashMap<String, String>();
        request_info.put("method", method);
        request_info.put("path", path);
        request_info.put("version", version);
        request_info.put("host", host);
        
        return request_info;
	}
	
	static int parseContentLength(InputStream input_stream) throws IOException
	{
		/*
		 * Luu do dai thong tin gui ve
		 */
		int _byte = input_stream.read(); // bo qua ":"
		_byte = input_stream.read(); // bo qua " "
		
		
		StringBuilder content_length_buffer = new StringBuilder();
		_byte = input_stream.read();
		content_length_buffer.append((char) _byte);
		_byte = input_stream.read();
		content_length_buffer.append((char) _byte);
		
		return Integer.parseInt(content_length_buffer.toString());
	}
	
	static HashMap<String, String> parseDataPOSTMethod(InputStream input_stream, long content_length) throws IOException
	{
		HashMap<String, String> account_info = new HashMap<String, String>();
		
		
		/*
		 * User
		 */
		int _byte;
		StringBuilder user_builder = new StringBuilder();
		while ((_byte = input_stream.read()) != (int) '&')
		{
			user_builder.append((char) _byte);
		}
		
		String user = user_builder.toString();
		account_info.put("Username", user);
		content_length = content_length - user.length() - 1; // update content-length
		
		
		/*
		 * Pass
		 */
		StringBuilder pass_builder = new StringBuilder();
		for (int i = 0; i < content_length; i++) 
		{
			_byte = input_stream.read();
			pass_builder.append((char) _byte);
		}
		String[] getPass = pass_builder.toString().split("=");
		String pass = getPass[1];
		account_info.put("Password", pass);
		
		
		return account_info;
	}
}
