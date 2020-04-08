package scw.net.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractTextInputMessage extends AbstractInputMessage{
	
	public InputStream getBody() throws IOException {
		return new ByteArrayInputStream(getTextContent().getBytes(getCharsetName()));
	}
	
	protected abstract String getTextContent();
	
	@Override
	public String toString() {
		return getTextContent();
	}
}
