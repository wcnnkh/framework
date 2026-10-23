package run.soeasy.framework.codec.io;

import java.nio.CharBuffer;

import lombok.NonNull;
import run.soeasy.framework.io.buffer.BufferTransporter;

public class SimpleStringDecoder<T extends BufferTransporter<? extends CharBuffer>> extends SimpleStringCoder<T> implements StringDecoder {

	public SimpleStringDecoder(@NonNull T transporter) {
		super(transporter);
	}
}
