package scw.net;

import java.net.URI;

import scw.net.message.Message;

public interface Request extends Message {
	URI getURI();
}