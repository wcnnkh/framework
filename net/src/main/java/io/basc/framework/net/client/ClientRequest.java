package io.basc.framework.net.client;

import java.io.IOException;

import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.Request;

public interface ClientRequest extends OutputMessage, Request {
	public static interface ClientRequestWrapper<W extends ClientRequest>
			extends ClientRequest, OutputMessageWrapper<W>, RequestWrapper<W> {
		@Override
		default ClientResponse execute() throws IOException {
			return getSource().execute();
		}
	}

	ClientResponse execute() throws IOException;
}
