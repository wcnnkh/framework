package io.basc.framework.orm.transfer;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import io.basc.framework.convert.TypeDescriptor;

/**
 * 导入
 * 
 * @author wcnnkh
 *
 */
public interface Importer {
	<T> Stream<T> read(File source, TypeDescriptor targetType) throws IOException;
}
