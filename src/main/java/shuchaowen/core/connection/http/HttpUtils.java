package shuchaowen.core.connection.http;

import java.io.IOException;
import java.net.MalformedURLException;

import shuchaowen.core.connection.reader.StringReader;

public final class HttpUtils {
	private HttpUtils(){};
	
	public static String doGet(String url){
		return doGet(url);
	}
	
	public static String doGet(String url, String charsetName){
		HttpGET request = null;
		try {
			request = new HttpGET(url);
			return request.reader(new StringReader(charsetName));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(request != null){
				request.disconnect();
			}
		}
		return null;
	}
}
