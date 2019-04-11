package scw.net.http;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.common.LinkedMultiValueMap;
import scw.common.MultiValueMap;
import scw.net.RequestException;
import scw.net.http.enums.Method;

public class FormRequest extends HttpRequest {
	private MultiValueMap<String, String> parameterMap;
	private final String charsetName;

	public FormRequest(Method method, String url, String charsetName) {
		super(method, url);
		this.charsetName = charsetName;
	}

	@Override
	public String getRequestAddress() {
		if (method != Method.GET) {
			return super.getRequestAddress();
		}

		String url = super.getRequestAddress();
		if (url == null) {
			return url;
		}

		try {
			if (url.lastIndexOf("?") == -1) {
				return url + "?" + getParameterString();
			} else {
				return url + "&" + getParameterString();
			}
		} catch (UnsupportedEncodingException e) {
			throw new RequestException(url, e);
		}
	}

	public void addParameter(String key, Object value) {
		if (value == null) {
			return;
		}

		if (parameterMap == null) {
			parameterMap = new LinkedMultiValueMap<String, String>();
		}

		parameterMap.add(key, value.toString());
	}

	public void addAll(Map<String, ?> map) {
		if (map == null) {
			return;
		}

		if (parameterMap == null) {
			parameterMap = new LinkedMultiValueMap<String, String>();
		}

		for (Entry<String, ?> entry : map.entrySet()) {
			Object v = entry.getValue();
			if (v == null) {
				continue;
			}

			parameterMap.add(entry.getKey(), v.toString());
		}
	}

	private String getParameterString() throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder(512);
		boolean append = false;
		Iterator<Entry<String, List<String>>> iterator = parameterMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, List<String>> entry = iterator.next();

			String key = entry.getKey();
			if (key == null || key.length() == 0) {
				continue;
			}

			List<String> values = entry.getValue();
			if (values == null || values.size() == 0) {
				continue;
			}

			key = URLEncoder.encode(key, charsetName);
			Iterator<String> valueIterator = values.iterator();
			while (valueIterator.hasNext()) {
				String v = valueIterator.next();
				if (append) {
					sb.append("&");
				}

				sb.append(key);
				sb.append("=");
				sb.append(URLEncoder.encode(v, charsetName));
				append = true;
			}
		}
		return sb.toString();
	}

	@Override
	public void doOutput(OutputStream os) throws Throwable {
		if (parameterMap != null) {
			os.write(getParameterString().getBytes(charsetName));
		}
	}

	public String getCharsetName() {
		return charsetName;
	}
}
