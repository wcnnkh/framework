package io.basc.framework.net;

import java.io.IOException;
import java.net.URI;

import io.basc.framework.net.message.OutputMessage;

public interface ClientRequest extends OutputMessage {
	URI getURI();

	ClientResponse execute() throws IOException;
}
