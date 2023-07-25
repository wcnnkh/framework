package io.basc.framework.orm.transfer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.stream.Stream;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.io.FileUtils;
import io.basc.framework.io.IOUtils;
import io.basc.framework.io.Resource;
import io.basc.framework.lang.NestedRuntimeException;
import io.basc.framework.util.Assert;
import io.basc.framework.util.element.Elements;

/**
 * 导入
 * 
 * @author wcnnkh
 *
 */
public interface Importer {
	default <T> Elements<T> read(Resource source, TypeDescriptor targetType) {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(targetType != null, "targetType");
		return Elements.of(() -> {
			InputStream inputStream;
			try {
				inputStream = source.getInputStream();
			} catch (IOException e) {
				throw new NestedRuntimeException(source.getDescription(), e);
			}

			try {
				return read(inputStream, targetType);
			} catch (IOException e) {
				IOUtils.closeQuietly(inputStream);
				throw new NestedRuntimeException(source.getDescription(), e);
			}
		});
	}

	default <T> Elements<T> read(File source, TypeDescriptor targetType) throws IOException {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(targetType != null, "targetType");
		return Elements.of(() -> {
			InputStream inputStream;
			try {
				inputStream = FileUtils.openInputStream(source);
			} catch (IOException e) {
				throw new NestedRuntimeException(source.getName(), e);
			}

			try {
				return read(inputStream, targetType);
			} catch (IOException e) {
				IOUtils.closeQuietly(inputStream);
				throw new NestedRuntimeException(source.getName(), e);
			}
		});
	}

	<T> Stream<T> read(Reader source, TypeDescriptor targetType) throws IOException;

	<T> Stream<T> read(InputStream source, TypeDescriptor targetType) throws IOException;

	default <T> Elements<T> read(Resource source, Class<? extends T> targetType) {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(targetType != null, "targetType");
		return read(source, TypeDescriptor.valueOf(targetType));
	}

	default <T> Elements<T> read(File source, Class<? extends T> targetType) throws IOException {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(targetType != null, "targetType");
		return read(source, TypeDescriptor.valueOf(targetType));
	}

	default <T> Stream<T> read(Reader source, Class<? extends T> targetType) throws IOException {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(targetType != null, "targetType");
		return read(source, TypeDescriptor.valueOf(targetType));
	}

	default <T> Stream<T> read(InputStream source, Class<? extends T> targetType) throws IOException {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(targetType != null, "targetType");
		return read(source, TypeDescriptor.valueOf(targetType));
	}
}
