package io.basc.framework.mvc;

import java.io.IOException;

@FunctionalInterface
public interface HttpChannelService {
	Object service(HttpChannel httpChannel) throws IOException;
}
