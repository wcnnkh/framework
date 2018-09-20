package shuchaowen.core.http.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;

import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.http.client.decoder.Decoder;
import shuchaowen.core.http.client.decoder.StringDecoder;
import shuchaowen.core.http.client.parameter.Parameter;

public class HttpRequest implements Request{
	private HttpURLConnection httpURLConnection;
	
	public HttpRequest(String httpUrl){
		URL url = null;
		try {
			url = new URL(httpUrl);
			httpURLConnection = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			throw new ShuChaoWenRuntimeException(e);
		}
		initDefaultConfig();
	}
	
	public HttpRequest(String httpUrl, Proxy proxy){
		URL url = null;
		try {
			url = new URL(httpUrl);
			httpURLConnection = (HttpURLConnection) url.openConnection(proxy);
		} catch (IOException e) {
			throw new ShuChaoWenRuntimeException(e);
		}
		
		initDefaultConfig();
	}
	
	public HttpRequest(HttpURLConnection httpURLConnection){
		this.httpURLConnection = httpURLConnection;
		initDefaultConfig();
	}
	
	public void setRequestMethod(String method){
		try {
			httpURLConnection.setRequestMethod(method);
		} catch (ProtocolException e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}
	
	private void initDefaultConfig(){
		this.httpURLConnection.setDoInput(true);
		setConnectionTimeout(5000);
		setReadTimeout(5000);
	}
	
	public void setContentType(String contentType) {
		setRequestProperties("Content-Type", contentType);
	}

	public void setRequestProperties(String name, String value) {
		httpURLConnection.setRequestProperty(name, value);
	}

	public void setConnectionTimeout(int timeout) {
		httpURLConnection.setConnectTimeout(timeout);
	}

	public void setReadTimeout(int timeout) {
		httpURLConnection.setReadTimeout(timeout);
	}
	
	public Response execute() {
		if(httpURLConnection.getDoOutput()){
			try {
				httpURLConnection.getOutputStream().flush();
				httpURLConnection.getOutputStream().close();
			} catch (IOException e) {
				
			}
		}
		return new HttpResponse(httpURLConnection);
	}


	public void addParam(Parameter parameter) {
		httpURLConnection.setDoOutput(true);
		try {
			parameter.wrapper(httpURLConnection.getOutputStream());
		} catch (IOException e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}
	
	public <T> T executeAndClose(Decoder<T> decoder){
		Response response = null;
		try {
			response = execute();
			return response.decode(decoder);
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
		}finally {
			if(response != null){
				response.disconnect();
			}
		}
	}
	
	@Override
	public String toString() {
		return getBody("UTF-8");
	}
	
	public String getBody() {
		return getBody("UTF-8");
	}
	
	public String getBody(String charsetName) {
		return executeAndClose(new StringDecoder(charsetName));
	}

	public String getRequestProperty(String key) {
		return httpURLConnection.getRequestProperty(key);
	}
}
