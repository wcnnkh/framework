package scw.servlet.rpc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.beans.BeanFactory;
import scw.beans.rpc.http.DefaultService;
import scw.beans.rpc.http.Service;
import scw.net.http.enums.Method;

/**
 * 默认的rpc服务
 * 
 * @author shuchaowen
 *
 */
public final class DefaultRPCServer implements RPCServer {
	private final String path;
	private final boolean enable;
	private final Service service;

	public DefaultRPCServer(BeanFactory beanFactory, String path, String sign, boolean enable, String charsetName) {
		this.path = path;
		this.enable = enable;
		this.service = new DefaultService(beanFactory, sign, charsetName);
	}

	public boolean isRPC(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		return enable && Method.POST.name().equals(httpServletRequest.getMethod())
				&& httpServletRequest.getServletPath().equals(path);
	}

	public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws Throwable {
		service.service(httpServletRequest.getInputStream(), httpServletResponse.getOutputStream());
	}
}
