package scw.servlet.mvc.http;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import scw.core.Assert;
import scw.core.utils.CollectionUtils;
import scw.lang.Nullable;
import scw.net.http.HttpHeaders;

public class HttpServletResponseHeaders extends HttpHeaders {
	private static final long serialVersionUID = 1L;
	private HttpServletResponse httpServletResponse;

	public HttpServletResponseHeaders(HttpServletResponse httpServletResponse) {
		this.httpServletResponse = httpServletResponse;
	}

	@Override
	public boolean containsKey(Object key) {
		return (super.containsKey(key) || (get(key) != null));
	}

	@Override
	@Nullable
	public String getFirst(String headerName) {
		String value = httpServletResponse.getHeader(headerName);
		if (value != null) {
			return value;
		} else {
			return super.getFirst(headerName);
		}
	}

	@Override
	public List<String> get(Object key) {
		Assert.isInstanceOf(String.class, key,
				"Key must be a String-based header name");
		Collection<String> values1 = httpServletResponse
				.getHeaders((String) key);
		if (isReadyOnly()) {
			return new ArrayList<String>(values1);
		}
		boolean isEmpty1 = CollectionUtils.isEmpty(values1);

		List<String> values2 = super.get(key);
		boolean isEmpty2 = CollectionUtils.isEmpty(values2);

		if (isEmpty1 && isEmpty2) {
			return null;
		}

		List<String> values = new ArrayList<String>();
		if (!isEmpty1) {
			values.addAll(values1);
		}
		if (!isEmpty2) {
			values.addAll(values2);
		}
		return values;
	}

	public void write() {
		if(isReadyOnly()){
			return ;
		}
		
		for (Entry<String, List<String>> entry : entrySet()) {
			for (String value : entry.getValue()) {
				this.httpServletResponse.addHeader(entry.getKey(), value);
			}
		}

		// HttpServletResponse exposes some headers as properties: we should
		// include those if not already present
		if (this.httpServletResponse.getContentType() == null
				&& getContentType() != null) {
			this.httpServletResponse
					.setContentType(getContentType().toString());
		}
		if (this.httpServletResponse.getCharacterEncoding() == null
				&& getContentType() != null
				&& getContentType().getCharsetName() != null) {
			this.httpServletResponse.setCharacterEncoding(getContentType()
					.getCharsetName());
		}
		readyOnly();
	}
}
