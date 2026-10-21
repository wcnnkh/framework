package run.soeasy.framework.codec.io;

import java.nio.CharBuffer;

import lombok.NonNull;
import run.soeasy.framework.io.buffer.BufferTransporter;
import run.soeasy.framework.io.buffer.SimpleCharBufferTransporter;

public class SimpleStringCoder<T extends BufferTransporter<? extends CharBuffer>> extends SimpleCharBufferTransporter<T>
		implements StringCoder {

	public SimpleStringCoder(@NonNull T transporter) {
		super(transporter);
	}
}
