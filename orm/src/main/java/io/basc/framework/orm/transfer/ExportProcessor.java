package io.basc.framework.orm.transfer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;

import io.basc.framework.http.HttpOutputMessage;
import io.basc.framework.http.HttpUtils;
import io.basc.framework.io.FileUtils;
import io.basc.framework.lang.Constants;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.StringUtils;

@FunctionalInterface
public interface ExportProcessor<S> {
	void process(Iterator<? extends S> source, File target) throws IOException;

	default void process(Collection<? extends S> source, File target) throws IOException {
		if (CollectionUtils.isEmpty(source)) {
			return;
		}

		process(source.iterator(), target);
	}

	default void process(Iterator<? extends S> source, HttpOutputMessage target, String fileName) throws IOException {
		process(source, target, fileName, null);
	}

	default void process(Collection<? extends S> source, HttpOutputMessage target, String fileName) throws IOException {
		process(source, target, fileName, null);
	}

	default void process(Collection<? extends S> source, HttpOutputMessage target, String fileName,
			@Nullable Charset charset) throws IOException {
		if (CollectionUtils.isEmpty(source)) {
			return;
		}

		process(source.iterator(), target, fileName, charset);
	}

	default void process(Iterator<? extends S> source, HttpOutputMessage target, String fileName,
			@Nullable Charset charset) throws IOException {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(target != null, "target");
		Assert.requiredArgument(StringUtils.hasText(fileName), "fileName");
		HttpUtils.writeFileMessageHeaders(target, fileName, charset);
		Charset charsetToUse = charset;
		if (charsetToUse == null) {
			charsetToUse = target.getCharset();
		}

		if (charsetToUse == null) {
			charsetToUse = Constants.UTF_8;
		}

		HttpUtils.writeFileMessageHeaders(target, fileName, charsetToUse);
		File tempFile = File.createTempFile("export", fileName);
		try {
			process(source, tempFile);
			FileUtils.copyFile(tempFile, target.getOutputStream());
		} finally {
			tempFile.delete();
		}
	}
}
