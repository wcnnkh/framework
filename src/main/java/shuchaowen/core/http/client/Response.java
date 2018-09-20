package shuchaowen.core.http.client;

import java.io.InputStream;
import java.net.URLConnection;

import shuchaowen.core.http.client.decoder.Decoder;


public interface Response{
	StatusLine getStatusLine();
	
	<T> T decode(Decoder<T> decoder);
	
	InputStream getInputStream();
	
	URLConnection getURLConnection();
	
	void disconnect();
}
