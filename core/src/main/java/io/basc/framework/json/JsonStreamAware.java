package io.basc.framework.json;

import java.io.IOException;
import java.io.Writer;

/**
 * Beans that support customized output of JSON text to a writer shall implement
 * this interface.
 */
public interface JsonStreamAware {
	void writeJSONString(Writer out) throws IOException;
}
