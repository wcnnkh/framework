package shuchaowen.core.http.client;

import shuchaowen.core.http.client.parameter.Parameter;

public interface Request{
	public void setRequestProperties(String name, String value);
	
	public String getRequestProperty(String key);
	
	public void setConnectionTimeout(int timeout);
	
	public void setReadTimeout(int timeout);
	
	public void addParam(Parameter parameter);
	
	public Response execute();
}
