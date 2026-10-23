package run.soeasy.framework.codec.io;

import java.nio.CharBuffer;

import lombok.NonNull;
import run.soeasy.framework.io.buffer.BufferTransporter;

public class SimpleStringEncoder<T extends BufferTransporter<? extends CharBuffer>> extends SimpleStringCoder<T>
		implements StringEncoder {

	public SimpleStringEncoder(@NonNull T transporter) {
		super(transporter);
	}
}
