package io.basc.framework.net;

import java.io.Closeable;

import io.basc.framework.net.message.InputMessage;

public interface ClientResponse extends InputMessage, Closeable {
}
