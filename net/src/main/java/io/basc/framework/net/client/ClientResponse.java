package io.basc.framework.net.client;

import io.basc.framework.net.InputMessage;
import io.basc.framework.net.Response;

public interface ClientResponse extends InputMessage, Response {
	public static interface ClientResponseWrapper<W extends ClientResponse>
			extends ClientResponse, InputMessageWrapper<W>, ResponseWrapper<W> {

	}
}
