package run.soeasy.framework.io.source;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;

public class CharArraySource extends CharArrayWriter implements ReaderSource<Reader>, WriterSource<Writer>{
	
	public CharArraySource() {
		super();
	}
	
	public CharArraySource(int initialSize) {
		super(initialSize);
	}
	
	@Override
	public Writer getWriter() throws IOException {
		return this;
	}

	@Override
	public Reader getReader() throws IOException {
		return new CharArrayReader(this.buf, 0, this.count);
	}
	
	@Override
	public CharBuffer readToCharBuffer() throws IOException {
		return CharBuffer.wrap(this.buf, 0, this.count).asReadOnlyBuffer();
	}
}
