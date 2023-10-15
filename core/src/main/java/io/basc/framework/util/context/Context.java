package io.basc.framework.util.context;

import java.io.Closeable;

import io.basc.framework.util.ParentDiscover;

public interface Context extends ParentDiscover<Context>, Closeable {
	boolean isNew();

	@Override
	void close();

}
