package io.basc.framework.net.server;

import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.Response;

public interface ServerResponse extends Response, OutputMessage {
	/**
	 * Returns a boolean indicating if the response has been committed.
	 * 
	 * @return
	 */
	boolean isCommitted();
}
