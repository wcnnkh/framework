package io.basc.framework.web;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.PrintWriter;

import io.basc.framework.net.message.OutputMessage;

public interface ServerResponse extends OutputMessage, Closeable, Flushable {
	PrintWriter getWriter() throws IOException;

	boolean isCommitted();
}
