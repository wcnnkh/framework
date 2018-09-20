package shuchaowen.core.http.client;

import java.net.URLConnection;

import shuchaowen.core.util.StringUtils;

@Deprecated
public class HttpClient {
	public Response execute(Request request){
		Response response = request.execute();
		URLConnection urlConnection = response.getURLConnection();
		String cookie = urlConnection.getHeaderField("cookie");
		if(!StringUtils.isNull(cookie)){
		}
		
		String setCookie = urlConnection.getHeaderField("Set-Cookie");
		if(!StringUtils.isNull(setCookie)){
		}
		return response;
	}
}
