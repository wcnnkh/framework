package run.soeasy.framework.io;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.function.Pipeline;

@FunctionalInterface
public interface ReaderFactory<R extends Reader> {
	@NonNull
	Pipeline<R, IOException> getReaderPipeline();

	default Reader getReader() throws IOException {
		return new ReaderPipeline(getReaderPipeline());
	}

	default CharSequence toCharSequence() throws IOException {
		Reader reader = getReader();
		try {
			return IOUtils.toCharSequence(reader);
		} finally {
			reader.close();
		}
	}

	default Elements<String> readLines() {
		return Elements.of(() -> {
			try {
				Pipeline<R, IOException> channel = getReaderPipeline();
				return IOUtils.readLines(channel.get()).onClose(() -> {
					try {
						channel.close();
					} catch (IOException e) {
						// ignore
					}
				});
			} catch (IOException ignore) {
			}
			return Stream.empty();
		});
	}

	default <T extends Writer> long transferTo(@NonNull WriterFactory<? extends T> dest) throws IOException {
		Reader input = getReader();
		try {
			Writer out = dest.getWriter();
			try {
				return IOUtils.transferTo(input, out);
			} finally {
				out.close();
			}
		} finally {
			input.close();
		}
	}
}
