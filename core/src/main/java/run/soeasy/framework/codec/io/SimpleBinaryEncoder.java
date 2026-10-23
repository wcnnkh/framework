package run.soeasy.framework.codec.io;

import java.nio.ByteBuffer;

import lombok.NonNull;
import run.soeasy.framework.io.buffer.BufferTransporter;

public class SimpleBinaryEncoder<T extends BufferTransporter<? extends ByteBuffer>> extends SimpleBinaryCoder<T>
		implements BinaryEncoder {

	public SimpleBinaryEncoder(@NonNull T transporter) {
		super(transporter);
	}
}
