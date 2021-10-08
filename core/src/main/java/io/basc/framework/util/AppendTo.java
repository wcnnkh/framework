package io.basc.framework.util;

import java.io.IOException;

@FunctionalInterface
public interface AppendTo {
	void appendTo(Appendable appendable) throws IOException;
}
