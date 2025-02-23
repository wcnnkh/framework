package io.basc.framework.net.server;

import java.io.Flushable;
import java.io.IOException;

import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.Response;

public interface ServerResponse extends Response, OutputMessage, Flushable {
	@FunctionalInterface
	public static interface ServerResponseWrapper<W extends ServerResponse>
			extends ServerResponse, ResponseWrapper<W>, OutputMessageWrapper<W> {
		@Override
		default boolean isCommitted() {
			return getSource().isCommitted();
		}

		@Override
		default void flush() throws IOException {
			getSource().flush();
		}
	}

	/**
	 * Returns a boolean indicating if the response has been committed.
	 * 
	 * @return
	 */
	boolean isCommitted();
}
