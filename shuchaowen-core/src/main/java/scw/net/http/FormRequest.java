package scw.net.http;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.utils.CollectionUtils;
import scw.net.RequestException;
import scw.util.LinkedMultiValueMap;
import scw.util.MultiValueMap;

public class FormRequest extends HttpRequest {
	private MultiValueMap<String, String> parameterMap;
	private final String charsetName;

	public FormRequest(Method method, String url, String charsetName) {
		super(method, url);
		this.charsetName = charsetName;
	}

	@Override
	public String getRequestUrl() {
		if (getMethod() != Method.GET) {
			return super.getRequestUrl();
		}

		String url = super.getRequestUrl();
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

		parameterMap.add(key, HttpUtils.toJsonString(value));
	}

	public void addAll(Map<String, ?> map) {
		if (CollectionUtils.isEmpty(map)) {
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

			parameterMap.add(entry.getKey(), HttpUtils.toJsonString(v));
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
	protected void doOutput(URLConnection urlConnection, OutputStream os) throws Throwable {
		if (parameterMap != null) {
			os.write(getParameterString().getBytes(charsetName));
		}
	}

	public String getCharsetName() {
		return charsetName;
	}
}
