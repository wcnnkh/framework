package io.basc.framework.orm.transfer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Iterator;

import io.basc.framework.http.HttpOutputMessage;
import io.basc.framework.http.HttpUtils;
import io.basc.framework.io.FileSystemResource;
import io.basc.framework.io.FileUtils;
import io.basc.framework.io.WritableResource;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;

public interface ExportProcessor<S> {
	default void write(Iterator<? extends S> source, WritableResource target) throws IOException {
		OutputStream outputStream = target.getOutputStream();
		try {
			write(source, outputStream);
		} finally {
			outputStream.close();
		}
	}

	default void write(Iterator<? extends S> source, File target) throws IOException {
		OutputStream outputStream = new FileOutputStream(target);
		try {
			write(source, outputStream);
		} finally {
			outputStream.close();
		}
	}

	void write(Iterator<? extends S> source, Writer target) throws IOException;

	void write(Iterator<? extends S> source, OutputStream target) throws IOException;

	default void write(Iterator<? extends S> source, HttpOutputMessage target, String fileName) throws IOException {
		write(source, target, fileName, null);
	}

	default void write(Iterator<? extends S> source, HttpOutputMessage target, String fileName,
			@Nullable Charset charset) throws IOException {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(target != null, "target");
		Assert.requiredArgument(StringUtils.hasText(fileName), "fileName");
		File tempFile = File.createTempFile("export", fileName);
		try {
			write(source, new FileSystemResource(tempFile));
			HttpUtils.writeFileMessageHeaders(target, fileName, charset);
			FileUtils.copyFile(tempFile, target.getOutputStream());
		} finally {
			tempFile.delete();
		}
	}
}
