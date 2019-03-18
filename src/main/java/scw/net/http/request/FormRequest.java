package scw.net.http.request;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import scw.common.LinkedMultiValueMap;
import scw.common.MultiValueMap;
import scw.net.http.enums.Method;

public class FormRequest extends HttpRequest {
	private MultiValueMap<String, String> parameterMap;
	private final String charsetName;

	public FormRequest(Method method, String charsetName) {
		super(method);
		this.charsetName = charsetName;
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

	@Override
	public void doOutput(OutputStream os) throws Throwable {
		if (parameterMap != null) {
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

			os.write(sb.toString().getBytes(charsetName));
		}
	}

	public String getCharsetName() {
		return charsetName;
	}
}
