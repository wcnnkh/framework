package scw.servlet.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.common.exception.NestedRuntimeException;
import scw.servlet.Request;
import scw.servlet.ServletApplication;

public final class AsyncRequestService implements Runnable {
	private Request request;
	private ServletApplication servletApplication;

	public AsyncRequestService(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			ServletApplication servletApplication) {
		try {
			this.request = servletApplication.formatRequest(httpServletRequest,
					httpServletResponse);
		} catch (IOException e) {
			throw new NestedRuntimeException(e);
		}
		this.servletApplication = servletApplication;
	}

	public void run() {
		try {
			servletApplication.service(request);
		} catch (Throwable e) {
			e.printStackTrace();
			servletApplication.sendError(request, request.getResponse(),
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
