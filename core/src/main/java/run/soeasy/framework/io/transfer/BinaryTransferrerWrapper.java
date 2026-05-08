package run.soeasy.framework.io.transfer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;

import lombok.NonNull;
import run.soeasy.framework.core.RandomUtils;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.io.BufferConsumer;
import run.soeasy.framework.io.BufferFeeder;
import run.soeasy.framework.io.ByteBufferInputStream;
import run.soeasy.framework.io.FileUtils;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.source.InputStreamFactory;
import run.soeasy.framework.io.source.OutputStreamFactory;
import run.soeasy.framework.io.source.Resource;

@FunctionalInterface
public interface BinaryTransferrerWrapper<W extends BinaryTransferrer> extends BinaryTransferrer,
		FromBinaryTransferrerWrapper<ByteBuffer, W>, ToBinaryTransferrerWrapper<ByteBuffer, W> {

	default int getRepetitions() {
		return 1;
	}

	@Override
	default <E extends Throwable> void toBinary(@NonNull ByteBuffer source,
			@NonNull BufferConsumer<? super byte[], ? extends E> target) throws E {
		int count = getRepetitions();
		// 只有一次传输，直接执行
		if (count == 1) {
			getSource().toBinary(source, target);
			return;
		}

		ByteBuffer buffer = ByteBuffer.allocate(IOUtils.DEFAULT_BYTE_BUFFER_SIZE);
		// 多次传输，使用临时文件
		File[] files = new File[2];
		try {
			// 创建两个临时文件
			files[0] = File.createTempFile(RandomUtils.uuid(), BinaryTransferrerWrapper.class.getSimpleName());
			files[1] = File.createTempFile(RandomUtils.uuid(), BinaryTransferrerWrapper.class.getSimpleName());
			// 初始传输：从输入流到第一个文件
			getSource().binaryTransfer(new ByteBufferInputStream(buffer), buffer, Resource.forFile(files[0]));
			count--;

			// 交替使用两个文件进行重复传输
			int currentFileIndex = 0;
			for (; count > 0; count--) {
				int nextFileIndex = (currentFileIndex + 1) % 2;
				getSource().binaryTransfer(Resource.forFile(files[currentFileIndex]), buffer,
						Resource.forFile(files[nextFileIndex]));
				currentFileIndex = nextFileIndex;
			}

			// 最终传输：从最后一个文件到目标
			getSource().binaryTransfer(Resource.forFile(files[currentFileIndex]), buffer, target);
		} catch (IOException e) {
			throw new IllegalStateException("Internal IO exception", e);
		} finally {
			// 确保删除所有临时文件
			for (File file : files) {
				if (file != null) {
					file.delete();
				}
			}
		}
	}

	@Override
	default <B extends ByteBuffer, E extends Throwable> void binaryTransfer(@NonNull BufferFeeder<? super B> source,
			@NonNull B buffer, @NonNull BufferConsumer<? super byte[], ? extends E> target) throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().binaryTransfer(source, buffer, target);
			return;
		}
		BinaryTransferrer.super.binaryTransfer(source, buffer, target);
	}

	@Override
	default <B extends ByteBuffer, E extends Throwable> void binaryTransfer(@NonNull BufferFeeder<? super B> source,
			@NonNull B buffer, @NonNull OutputStream target) throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().binaryTransfer(source, buffer, target);
			return;
		}
		BinaryTransferrer.super.binaryTransfer(source, buffer, target);
	}

	@Override
	default <B extends ByteBuffer, O extends OutputStream, E extends Throwable> void binaryTransfer(
			@NonNull BufferFeeder<? super B> source, @NonNull B buffer, @NonNull OutputStreamFactory<O> target)
			throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().binaryTransfer(source, buffer, target);
			return;
		}
		BinaryTransferrer.super.binaryTransfer(source, buffer, target);
	}

	@Override
	default <B extends ByteBuffer, E extends Throwable> void binaryTransfer(@NonNull BufferFeeder<? super B> source,
			@NonNull B buffer, @NonNull ThrowingConsumer<? super ByteBuffer, ? extends E> target)
			throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().binaryTransfer(source, buffer, target);
			return;
		}
		BinaryTransferrer.super.binaryTransfer(source, buffer, target);
	}

	@Override
	default <B extends ByteBuffer, E extends Throwable> void binaryTransfer(@NonNull BufferFeeder<? super B> source,
			@NonNull B buffer, @NonNull WritableByteChannel target) throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().binaryTransfer(source, buffer, target);
			return;
		}
		BinaryTransferrer.super.binaryTransfer(source, buffer, target);
	}

	@Override
	default <E extends Throwable> void binaryTransfer(@NonNull InputStream source, @NonNull ByteBuffer buffer,
			@NonNull BufferConsumer<? super byte[], ? extends E> target) throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().binaryTransfer(source, buffer, target);
			return;
		}
		BinaryTransferrer.super.binaryTransfer(source, buffer, target);
	}

	@Override
	default <E extends Throwable> void binaryTransfer(@NonNull InputStream source, @NonNull ByteBuffer buffer,
			@NonNull OutputStream target) throws IOException {
		if (getRepetitions() == 1) {
			binaryTransfer(source, buffer, target);
			return;
		}
		BinaryTransferrer.super.binaryTransfer(source, buffer, target);
	}

	@Override
	default <O extends OutputStream, E extends Throwable> void binaryTransfer(@NonNull InputStream source,
			@NonNull ByteBuffer buffer, @NonNull OutputStreamFactory<O> target) throws IOException {
		if (getRepetitions() == 1) {
			getSource().binaryTransfer(source, buffer, target);
			return;
		}
		BinaryTransferrer.super.binaryTransfer(source, buffer, target);
	}

	@Override
	default <E extends Throwable> void binaryTransfer(@NonNull InputStream source, @NonNull ByteBuffer buffer,
			@NonNull ThrowingConsumer<? super ByteBuffer, ? extends E> target) throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().binaryTransfer(source, buffer, target);
			return;
		}
		BinaryTransferrer.super.binaryTransfer(source, buffer, target);
	}

	@Override
	default <E extends Throwable> void binaryTransfer(@NonNull InputStream source, @NonNull ByteBuffer buffer,
			@NonNull WritableByteChannel target) throws IOException {
		if (getRepetitions() == 1) {
			getSource().binaryTransfer(source, buffer, target);
			return;
		}
		BinaryTransferrer.super.binaryTransfer(source, buffer, target);
	}

	@Override
	default <I extends InputStream, E extends Throwable> void binaryTransfer(@NonNull InputStreamFactory<I> source,
			@NonNull ByteBuffer buffer, @NonNull BufferConsumer<? super byte[], ? extends E> target)
			throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().binaryTransfer(source, buffer, target);
			return;
		}
		BinaryTransferrer.super.binaryTransfer(source, buffer, target);
	}

	@Override
	default <I extends InputStream, E extends Throwable> void binaryTransfer(@NonNull InputStreamFactory<I> source,
			@NonNull ByteBuffer buffer, @NonNull OutputStream target) throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().binaryTransfer(source, buffer, target);
			return;
		}
		BinaryTransferrer.super.binaryTransfer(source, buffer, target);
	}

	@Override
	default <I extends InputStream, O extends OutputStream, E extends Throwable> void binaryTransfer(
			@NonNull InputStreamFactory<I> source, @NonNull ByteBuffer buffer, @NonNull OutputStreamFactory<O> target)
			throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().binaryTransfer(source, buffer, target);
			return;
		}
		BinaryTransferrer.super.binaryTransfer(source, buffer, target);
	}

	@Override
	default <I extends InputStream, E extends Throwable> void binaryTransfer(@NonNull InputStreamFactory<I> source,
			@NonNull ByteBuffer buffer, @NonNull ThrowingConsumer<? super ByteBuffer, ? extends E> target)
			throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().binaryTransfer(source, buffer, target);
			return;
		}
		BinaryTransferrer.super.binaryTransfer(source, buffer, target);
	}

	@Override
	default <I extends InputStream, E extends Throwable> void binaryTransfer(@NonNull InputStreamFactory<I> source,
			@NonNull ByteBuffer buffer, @NonNull WritableByteChannel target) throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().binaryTransfer(source, buffer, target);
			return;
		}
		BinaryTransferrer.super.binaryTransfer(source, buffer, target);
	}

	@Override
	default <E extends Throwable> void binaryTransfer(@NonNull ReadableByteChannel source, @NonNull ByteBuffer buffer,
			@NonNull BufferConsumer<? super byte[], ? extends E> target) throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().binaryTransfer(source, buffer, target);
			return;
		}
		BinaryTransferrer.super.binaryTransfer(source, buffer, target);
	}

	@Override
	default <E extends Throwable> void binaryTransfer(@NonNull ReadableByteChannel source, @NonNull ByteBuffer buffer,
			@NonNull OutputStream target) throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().binaryTransfer(source, buffer, target);
			return;
		}
		BinaryTransferrer.super.binaryTransfer(source, buffer, target);
	}

	@Override
	default <O extends OutputStream, E extends Throwable> void binaryTransfer(@NonNull ReadableByteChannel source,
			@NonNull ByteBuffer buffer, @NonNull OutputStreamFactory<O> target) throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().binaryTransfer(source, buffer, target);
			return;
		}
		BinaryTransferrer.super.binaryTransfer(source, buffer, target);
	}

	@Override
	default <E extends Throwable> void binaryTransfer(@NonNull ReadableByteChannel source, @NonNull ByteBuffer buffer,
			@NonNull ThrowingConsumer<? super ByteBuffer, ? extends E> target) throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().binaryTransfer(source, buffer, target);
			return;
		}
		BinaryTransferrer.super.binaryTransfer(source, buffer, target);
	}

	@Override
	default <E extends Throwable> void binaryTransfer(@NonNull ReadableByteChannel source, @NonNull ByteBuffer buffer,
			@NonNull WritableByteChannel target) throws IOException, E {
		if (getRepetitions() == 1) {
			getSource().binaryTransfer(source, buffer, target);
			return;
		}
		BinaryTransferrer.super.binaryTransfer(source, buffer, target);
	}

	@Override
	default <B extends ByteBuffer> ByteBuffer fromBinary(@NonNull BufferFeeder<? super B> source, @NonNull B buffer)
			throws IOException {
		if (getRepetitions() == 1) {
			return getSource().fromBinary(source, buffer);
		}
		return BinaryTransferrer.super.fromBinary(source, buffer);
	}
}
