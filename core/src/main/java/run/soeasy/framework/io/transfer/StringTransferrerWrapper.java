package run.soeasy.framework.io.transfer;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.Arrays;

import lombok.NonNull;
import run.soeasy.framework.core.RandomUtils;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.io.BufferConsumer;
import run.soeasy.framework.io.BufferFeeder;
import run.soeasy.framework.io.FileUtils;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.source.ReaderFactory;
import run.soeasy.framework.io.source.Resource;
import run.soeasy.framework.io.source.WriterFactory;

@FunctionalInterface
public interface StringTransferrerWrapper<W extends StringTransferrer> extends StringTransferrer,
		FromStringTransferrerWrapper<CharBuffer, W>, ToStringTransferrerWrapper<CharBuffer, W> {
	default int getRepetitions() {
		return 1;
	}

	@Override
	default <E extends Throwable> void toString(CharBuffer source, BufferConsumer<? super char[], ? extends E> target)
			throws E {
		int count = getRepetitions();
		// 只有一次传输，直接执行
		if (count == 1) {
			getSource().toString(source, target);
			return;
		}

		CharBuffer buffer = CharBuffer.allocate(IOUtils.DEFAULT_CHAR_BUFFER_SIZE);
		// 多次传输，使用临时文件
		File[] files = new File[2];
		try {
			// 创建两个临时文件
			files[0] = File.createTempFile(RandomUtils.uuid(), BinaryTransferrerWrapper.class.getSimpleName());
			files[1] = File.createTempFile(RandomUtils.uuid(), BinaryTransferrerWrapper.class.getSimpleName());
			// 初始传输：从输入流到第一个文件
			getSource().stringTransfer(buffer::read, buffer, Resource.forFile(files[0]));
			count--;

			// 交替使用两个文件进行重复传输
			int currentFileIndex = 0;
			for (; count > 0; count--) {
				int nextFileIndex = (currentFileIndex + 1) % 2;
				getSource().stringTransfer(Resource.forFile(files[currentFileIndex]), buffer,
						Resource.forFile(files[nextFileIndex]));
				currentFileIndex = nextFileIndex;
			}

			// 最终传输：从最后一个文件到目标
			getSource().stringTransfer(Resource.forFile(files[currentFileIndex]), buffer, target);
		} catch (IOException e) {
			throw new IllegalStateException("Internal IO exception", e);
		} finally {
			// 确保删除所有临时文件
			FileUtils.deleteQuietly(Arrays.asList(files));
		}
	}

	@Override
	default <B extends CharBuffer> CharBuffer fromString(@NonNull BufferFeeder<? super B> source, @NonNull B buffer)
			throws IOException {
		if (getRepetitions() == 1) {
			return getSource().fromString(source, buffer);
		}
		return StringTransferrer.super.fromString(source, buffer);
	}

	@Override
	default <B extends CharBuffer, E extends Throwable> void stringTransfer(@NonNull BufferFeeder<? super B> source,
			@NonNull B buffer, @NonNull Appendable target) throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().stringTransfer(source, buffer, target);
			return;
		}
		StringTransferrer.super.stringTransfer(source, buffer, target);
	}

	@Override
	default <B extends CharBuffer, E extends Throwable> void stringTransfer(@NonNull BufferFeeder<? super B> source,
			@NonNull B buffer, @NonNull BufferConsumer<? super char[], ? extends E> target) throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().stringTransfer(source, buffer, target);
			return;
		}
		StringTransferrer.super.stringTransfer(source, buffer, target);
	}

	@Override
	default <B extends CharBuffer, E extends Throwable> void stringTransfer(@NonNull BufferFeeder<? super B> source,
			@NonNull B buffer, @NonNull ThrowingConsumer<? super CharBuffer, ? extends E> target)
			throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().stringTransfer(source, buffer, target);
			return;
		}
		StringTransferrer.super.stringTransfer(source, buffer, target);
	}

	@Override
	default <B extends CharBuffer, T extends Writer, E extends Throwable> void stringTransfer(
			@NonNull BufferFeeder<? super B> source, @NonNull B buffer, @NonNull WriterFactory<T> target)
			throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().stringTransfer(source, buffer, target);
			return;
		}
		StringTransferrer.super.stringTransfer(source, buffer, target);
	}

	@Override
	default <E extends Throwable> void stringTransfer(@NonNull Readable source, @NonNull CharBuffer buffer,
			@NonNull Appendable target) throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().stringTransfer(source, buffer, target);
			return;
		}
		StringTransferrer.super.stringTransfer(source, buffer, target);
	}

	@Override
	default <E extends Throwable> void stringTransfer(@NonNull Readable source, @NonNull CharBuffer buffer,
			@NonNull BufferConsumer<? super char[], ? extends E> target) throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().stringTransfer(source, buffer, target);
			return;
		}
		StringTransferrer.super.stringTransfer(source, buffer, target);
	}

	@Override
	default <E extends Throwable> void stringTransfer(@NonNull Readable source, @NonNull CharBuffer buffer,
			@NonNull ThrowingConsumer<? super CharBuffer, ? extends E> target) throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().stringTransfer(source, buffer, target);
			return;
		}
		StringTransferrer.super.stringTransfer(source, buffer, target);
	}

	@Override
	default <T extends Writer, E extends Throwable> void stringTransfer(@NonNull Readable source,
			@NonNull CharBuffer buffer, @NonNull WriterFactory<T> target) throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().stringTransfer(source, buffer, target);
			return;
		}
		StringTransferrer.super.stringTransfer(source, buffer, target);
	}

	@Override
	default <R extends Reader, E extends Throwable> void stringTransfer(@NonNull ReaderFactory<R> source,
			@NonNull CharBuffer buffer, @NonNull Appendable target) throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().stringTransfer(source, buffer, target);
			return;
		}
		StringTransferrer.super.stringTransfer(source, buffer, target);
	}

	@Override
	default <R extends Reader, E extends Throwable> void stringTransfer(@NonNull ReaderFactory<R> source,
			@NonNull CharBuffer buffer, @NonNull BufferConsumer<? super char[], ? extends E> target)
			throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().stringTransfer(source, buffer, target);
			return;
		}
		StringTransferrer.super.stringTransfer(source, buffer, target);
	}

	@Override
	default <R extends Reader, E extends Throwable> void stringTransfer(@NonNull ReaderFactory<R> source,
			@NonNull CharBuffer buffer, @NonNull ThrowingConsumer<? super CharBuffer, ? extends E> target)
			throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().stringTransfer(source, buffer, target);
			return;
		}
		StringTransferrer.super.stringTransfer(source, buffer, target);
	}

	@Override
	default <R extends Reader, T extends Writer, E extends Throwable> void stringTransfer(
			@NonNull ReaderFactory<R> source, @NonNull CharBuffer buffer, @NonNull WriterFactory<T> target)
			throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().stringTransfer(source, buffer, target);
			return;
		}
		StringTransferrer.super.stringTransfer(source, buffer, target);
	}
}
