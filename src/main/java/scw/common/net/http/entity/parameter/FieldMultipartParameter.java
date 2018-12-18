package scw.common.net.http.entity.parameter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class FieldMultipartParameter extends AbstractMultipartParameter{
	private final String key;
	private final String value;

	public FieldMultipartParameter(Charset charset, String boundary, String key, String value) {
		super(charset, boundary);
		this.key = key;
		this.value = value;
	}

	public void write(OutputStream out) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(BOUNDARY_TAG).append(getBoundary()).append(BR);
		sb.append("Content-Disposition: form-data;");
		sb.append("name=\"").append(key).append("\";");
		sb.append(BR);
		sb.append(BR);
		sb.append(value);
		out.write(sb.toString().getBytes(getCharset()));
		out.write(BR.getBytes(getCharset()));
	}
}
