package run.soeasy.framework.codec.io;

import java.nio.ByteBuffer;

import lombok.NonNull;
import run.soeasy.framework.io.buffer.BufferTransporter;
import run.soeasy.framework.io.buffer.SimpleByteBufferTransporter;

public class SimpleBinaryCoder<T extends BufferTransporter<? extends ByteBuffer>>
		extends SimpleByteBufferTransporter<T> implements BinaryCoder {

	public SimpleBinaryCoder(@NonNull T transporter) {
		super(transporter);
	}
}
