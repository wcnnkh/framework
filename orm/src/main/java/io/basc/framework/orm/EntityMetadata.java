package io.basc.framework.orm;

import io.basc.framework.util.Named;

public interface EntityMetadata extends Named {
	String getCharsetName();

	String getComment();
}
