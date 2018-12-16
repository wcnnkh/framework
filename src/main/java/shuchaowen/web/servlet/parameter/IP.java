package shuchaowen.web.servlet.parameter;

import shuchaowen.web.servlet.RequestParameter;
import shuchaowen.web.servlet.Request;

public class IP extends RequestParameter{
	public IP(Request request) {
		super(request);
	}
	
	public String getIp() {
		return getRequest().getIP();
	}
}
