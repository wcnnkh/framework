package run.soeasy.framework.net.client;

import java.io.IOException;

import run.soeasy.framework.net.OutputMessage;
import run.soeasy.framework.net.Request;

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
