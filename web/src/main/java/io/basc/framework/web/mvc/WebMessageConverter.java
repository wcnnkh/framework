package io.basc.framework.web.mvc;

import io.basc.framework.net.InputMessage;
import io.basc.framework.util.collection.Elements;

public interface WebMessageConverter {
	Elements<Object> read(InputMessage message);
}
