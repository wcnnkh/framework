package scw.servlet.rpc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RPCServer {
	boolean isRPC(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

	void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Throwable;
}
