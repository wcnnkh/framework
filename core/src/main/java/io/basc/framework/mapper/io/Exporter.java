package io.basc.framework.mapper.io;

import java.io.Flushable;
import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;

public interface Exporter extends Flushable {
	default void doWrite(Object data) throws IOException {
		doWrite(data, TypeDescriptor.forObject(data));
	}

	void doWrite(Object data, TypeDescriptor typeDescriptor) throws IOException;

	@Override
	default void flush() throws IOException {
		// 默认不执行任何操作
	}
}
