package shuchaowen.connection.http.write;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.connection.Write;
import shuchaowen.connection.http.HttpUtils;

public class FormData implements Write {
	private final String charsetName;
	private StringBuilder sb;

	public FormData(String charsetName) {
		this.charsetName = charsetName;
	}

	public FormData addEncodeParameter(String key, Object value) throws UnsupportedEncodingException {
		if (value == null) {
			return this;
		}

		return addParameter(key, HttpUtils.encode(value, charsetName));
	}

	public FormData addParameter(String key, String value) {
		if (value == null) {
			return this;
		}

		if (sb == null) {
			sb = new StringBuilder();
		} else {
			sb.append("&");
		}

		sb.append(key);
		sb.append("=");
		sb.append(value);
		return this;
	}

	public void write(OutputStream outputStream) throws IOException {
		if (sb != null) {
			outputStream.write(sb.toString().getBytes(charsetName));
		}
	}

	public static FormData wrapper(Map<String, ?> parameterMap, String charsetName)
			throws UnsupportedEncodingException {
		if (parameterMap == null || parameterMap.isEmpty()) {
			return null;
		}

		FormData formData = new FormData(charsetName);
		for (Entry<String, ?> entry : parameterMap.entrySet()) {
			formData.addEncodeParameter(entry.getKey(), entry.getValue());
		}
		return formData;
	}
}
