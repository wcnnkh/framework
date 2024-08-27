package io.basc.framework.web.mvc;

import io.basc.framework.net.InputMessage;
import io.basc.framework.util.Elements;

public interface WebMessageConverter {
	Elements<Object> read(InputMessage message);
}
