package io.basc.framework.net.client.convert;

import io.basc.framework.net.convert.MessageReader;
import io.basc.framework.net.convert.RequestWriter;

public interface ClientMessageConverter extends MessageReader, RequestWriter {
}