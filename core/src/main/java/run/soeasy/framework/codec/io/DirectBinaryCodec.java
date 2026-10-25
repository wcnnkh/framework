package run.soeasy.framework.codec.io;

import java.io.IOException;
import java.nio.ByteBuffer;

import lombok.NonNull;
import run.soeasy.framework.codec.DirectCodec;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.buffer.BufferReader;
import run.soeasy.framework.io.buffer.BufferWriter;

public interface DirectBinaryCodec extends BinaryCodec, DirectCodec<byte[], byte[]> {

	@Override
	default BinaryDecoder decoder() {
		return new BinaryDecoder() {

			@Override
			public <R extends Throwable, W extends Throwable> void transfer(
					@NonNull BufferReader<? super ByteBuffer, ? extends R> reader,
					@NonNull BufferWriter<? super ByteBuffer, ? extends W> writer) throws IOException, R, W {
				byte[] source = IOUtils.binary().toByteArray(reader);
				byte[] target = DirectBinaryCodec.this.decode(source);
				writer.write(ByteBuffer.wrap(target));
			}
		};
	}

	@Override
	default BinaryEncoder encoder() {
		return new BinaryEncoder() {

			@Override
			public <R extends Throwable, W extends Throwable> void transfer(
					@NonNull BufferReader<? super ByteBuffer, ? extends R> reader,
					@NonNull BufferWriter<? super ByteBuffer, ? extends W> writer) throws IOException, R, W {
				byte[] source = IOUtils.binary().toByteArray(reader);
				byte[] target = DirectBinaryCodec.this.encode(source);
				writer.write(ByteBuffer.wrap(target));
			}
		};
	}

	@Override
	byte[] encode(byte[] source);

	@Override
	byte[] decode(byte[] target);
}
