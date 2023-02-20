package io.basc.framework.orm.transfer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;

import io.basc.framework.http.HttpOutputMessage;
import io.basc.framework.http.HttpUtils;
import io.basc.framework.io.FileUtils;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;

@FunctionalInterface
public interface ExportProcessor<S> {
	void process(Iterator<? extends S> source, File target) throws IOException;

	default void process(Iterable<? extends S> source, File target) throws IOException {
		if (source == null) {
			return;
		}

		process(source.iterator(), target);
	}

	default void process(Iterator<? extends S> source, HttpOutputMessage target, String fileName) throws IOException {
		process(source, target, fileName, null);
	}

	default void process(Iterable<? extends S> source, HttpOutputMessage target, String fileName) throws IOException {
		process(source, target, fileName, null);
	}

	default void process(Iterable<? extends S> source, HttpOutputMessage target, String fileName,
			@Nullable Charset charset) throws IOException {
		if (source == null) {
			return;
		}

		process(source.iterator(), target, fileName, charset);
	}

	default void process(Iterator<? extends S> source, HttpOutputMessage target, String fileName,
			@Nullable Charset charset) throws IOException {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(target != null, "target");
		Assert.requiredArgument(StringUtils.hasText(fileName), "fileName");
		File tempFile = File.createTempFile("export", fileName);
		try {
			process(source, tempFile);
			HttpUtils.writeFileMessageHeaders(target, fileName, charset);
			FileUtils.copyFile(tempFile, target.getOutputStream());
		} finally {
			tempFile.delete();
		}
	}
}
