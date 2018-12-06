package shuchaowen.web.servlet.parameter;

import shuchaowen.web.servlet.RequestWrapper;
import shuchaowen.web.servlet.Request;

public class IP extends RequestWrapper{
	public IP(Request request) {
		super(request);
	}
	
	public String getIp() {
		return getRequest().getIP();
	}
}
