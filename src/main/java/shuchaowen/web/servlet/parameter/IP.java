package shuchaowen.web.servlet.parameter;

import shuchaowen.web.servlet.RequestWrapper;
import shuchaowen.web.servlet.WebRequest;

public class IP extends RequestWrapper{
	public IP(WebRequest request) {
		super(request);
	}
	
	public String getIp() {
		return getRequest().getIP();
	}
}
