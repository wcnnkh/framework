package run.soeasy.framework.codec.format;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import lombok.NonNull;
import run.soeasy.framework.codec.io.StringCoder;
import run.soeasy.framework.io.buffer.StreamCharBufferTransporter;

public class UnicodeEncoder implements StringCoder, StreamCharBufferTransporter {
	public static final UnicodeEncoder DEFAULT = new UnicodeEncoder();

	@Override
	public void readInto(@NonNull Reader reader, @NonNull Writer writer) throws IOException {
		int code;
		while ((code = reader.read()) != 0) {
			if (code < 256) {
				writer.write(code);
			} else {
				writer.write("\\u" + Integer.toHexString(code & 0xffff));
			}
		}
	}
}
