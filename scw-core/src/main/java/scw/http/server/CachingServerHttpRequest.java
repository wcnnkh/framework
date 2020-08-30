package scw.http.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import scw.http.HttpMethod;
import scw.io.IOUtils;
import scw.logger.SplitLineAppend;

/**
 * 缓存body
 * 
 * @author shuchaowen
 *
 */
public class CachingServerHttpRequest extends ServerHttpRequestWrapper {
	private byte[] body;

	public CachingServerHttpRequest(ServerHttpRequest targetRequest)
			throws IOException {
		super(targetRequest);
	}

	public byte[] getBytes() {
		return body;
	}

	@Override
	public BufferedReader getReader() throws IOException, OutOfMemoryError {
		if (getMethod() == HttpMethod.GET) {
			return null;
		}

		InputStreamReader isr = new InputStreamReader(getBody(),
				getCharacterEncoding());
		return new BufferedReader(isr);
	}

	@Override
	public InputStream getBody() throws IOException, OutOfMemoryError {
		if (getMethod() == HttpMethod.GET) {
			return null;
		}

		if (body == null) {
			body = IOUtils.toByteArray(super.getBody());
		}
		return new ByteArrayInputStream(body);
	}

	@Override
	public String toString() {
		if (getMethod() == HttpMethod.GET) {
			return super.toString();
		}
		
		String body = null;
		try {
			body = IOUtils.read(getReader(), -1);
		} catch (OutOfMemoryError e1) {
		} catch (IOException e1) {
		}

		if(body == null){
			return super.toString();
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(IOUtils.LINE_SEPARATOR);
		sb.append(new SplitLineAppend("request body[" + getRawContentType()	+ "] begin"));
		sb.append(IOUtils.LINE_SEPARATOR);
		sb.append(body);
		sb.append(IOUtils.LINE_SEPARATOR);
		sb.append(new SplitLineAppend("request body end"));
		sb.append(IOUtils.LINE_SEPARATOR);
		return sb.toString();
	}
}
