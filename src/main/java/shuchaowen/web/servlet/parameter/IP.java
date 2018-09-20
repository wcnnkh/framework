package shuchaowen.web.servlet.parameter;

import shuchaowen.web.servlet.WebParameter;
import shuchaowen.web.servlet.WebRequest;

public class IP extends WebParameter{
	public IP(WebRequest request) {
		super(request);
	}
	
	public String getIp() {
		return getRequest().getIP();
	}
}
