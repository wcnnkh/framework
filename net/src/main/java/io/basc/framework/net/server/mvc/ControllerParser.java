package io.basc.framework.net.server.mvc;

import io.basc.framework.execution.Function;

public interface ControllerParser {
	Controller parse(Function function);
}
