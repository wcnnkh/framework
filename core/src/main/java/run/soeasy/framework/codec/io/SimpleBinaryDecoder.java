package run.soeasy.framework.codec.io;

import java.nio.ByteBuffer;

import lombok.NonNull;
import run.soeasy.framework.io.buffer.BufferTransporter;

public class SimpleBinaryDecoder<T extends BufferTransporter<? extends ByteBuffer>> extends SimpleBinaryCoder<T>
		implements BinaryDecoder {

	public SimpleBinaryDecoder(@NonNull T transporter) {
		super(transporter);
	}
}
