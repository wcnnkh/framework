package scw.net;

import java.io.Closeable;

import scw.net.message.InputMessage;

public interface ClientResponse extends InputMessage, Closeable {
}
