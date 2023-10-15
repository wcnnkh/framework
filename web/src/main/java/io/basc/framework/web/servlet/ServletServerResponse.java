package io.basc.framework.web.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletResponse;

import io.basc.framework.util.Wrapper;
import io.basc.framework.web.ServerResponse;

public abstract class ServletServerResponse<W extends ServletResponse> extends Wrapper<W> implements ServerResponse {
	private boolean bodyUse = false;

	public ServletServerResponse(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public void setContentLength(long contentLength) {
		wrappedTarget.setContentLengthLong(contentLength);
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		bodyUse = true;
		return wrappedTarget.getOutputStream();
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		bodyUse = true;
		return wrappedTarget.getWriter();
	}

	@Override
	public void close() throws IOException {
		flush();
	}

	@Override
	public void flush() throws IOException {
		if (bodyUse) {
			wrappedTarget.flushBuffer();
		}
	}

	@Override
	public boolean isCommitted() {
		return wrappedTarget.isCommitted();
	}

}
