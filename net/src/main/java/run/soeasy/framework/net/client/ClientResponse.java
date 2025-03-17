package run.soeasy.framework.net.client;

import run.soeasy.framework.net.InputMessage;
import run.soeasy.framework.net.Response;

public interface ClientResponse extends InputMessage, Response {
	public static interface ClientResponseWrapper<W extends ClientResponse>
			extends ClientResponse, InputMessageWrapper<W>, ResponseWrapper<W> {

	}
}
