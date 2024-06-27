package io.basc.framework.net.client;

import java.io.IOException;

import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.Request;

public interface ClientRequest extends OutputMessage, Request {
	ClientResponse execute() throws IOException;
}
