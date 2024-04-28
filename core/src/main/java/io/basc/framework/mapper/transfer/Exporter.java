package io.basc.framework.mapper.transfer;

import java.io.Flushable;
import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;

public interface Exporter extends Flushable {
	void doWrite(Object data, TypeDescriptor typeDescriptor) throws IOException;

	@Override
	default void flush() throws IOException {
		// 默认不执行任何操作
	}
}
