package scw.net.client;

import java.io.IOException;

import scw.net.Request;
import scw.net.message.OutputMessage;

public interface ClientRequest extends OutputMessage, Request {
	ClientResponse execute() throws IOException;
}
