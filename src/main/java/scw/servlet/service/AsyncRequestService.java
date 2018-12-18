package scw.servlet.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.common.exception.ShuChaoWenRuntimeException;
import scw.servlet.HttpServerApplication;
import scw.servlet.Request;

public final class AsyncRequestService implements Runnable {
	private Request request;
	private HttpServerApplication httpServerApplication;

	public AsyncRequestService(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			HttpServerApplication httpServerApplication) {
		try {
			this.request = httpServerApplication.formatRequest(httpServletRequest,
					httpServletResponse);
		} catch (IOException e) {
			throw new ShuChaoWenRuntimeException(e);
		}
		this.httpServerApplication = httpServerApplication;
	}

	public void run() {
		try {
			httpServerApplication.service(request);
		} catch (Throwable e) {
			e.printStackTrace();
			httpServerApplication.sendError(request, request.getResponse(),
					500, "system error");
		} finally {
			if (request.isAsyncStarted()) {
				request.getAsyncContext().complete();
			}
		}
	}

	public Request getRequest() {
		return request;
	}
}
