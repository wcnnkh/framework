package io.basc.framework.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.ServletResponse;

import io.basc.framework.net.server.ServerResponse;
import io.basc.framework.util.function.Wrapped;
import io.basc.framework.util.io.WriterSource;

public abstract class AbstractServletServerResponseWrapper<W extends ServletResponse> extends Wrapped<W>
		implements ServerResponse, WriterSource<PrintWriter> {
	private boolean bodyUse = false;

	public AbstractServletServerResponseWrapper(W source) {
		super(source);
	}

	@Override
	public void setContentLength(long contentLength) {
		source.setContentLengthLong(contentLength);
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		bodyUse = true;
		return source.getOutputStream();
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		bodyUse = true;
		return source.getWriter();
	}
	
	@Override
	public WriterSource<Writer> toWriterFactory() {
		return () -> getWriter();
	}

	@Override
	public void close() throws IOException {
		flush();
	}

	@Override
	public void flush() throws IOException {
		if (bodyUse) {
			source.flushBuffer();
		}
	}

	@Override
	public boolean isCommitted() {
		return source.isCommitted();
	}

}
