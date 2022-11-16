package io.basc.framework.orm.transfer;

import java.io.File;
import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.Cursor;

/**
 * 导入
 * 
 * @author wcnnkh
 *
 */
public interface Importer {
	<T> Cursor<T> read(File source, TypeDescriptor targetType) throws IOException;

	default <T> Cursor<T> read(File source, Class<? extends T> targetType) throws IOException {
		return read(source, TypeDescriptor.valueOf(targetType));
	}
}
