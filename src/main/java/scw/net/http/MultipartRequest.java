package scw.net.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import scw.common.exception.NotSupportException;
import scw.net.http.entity.parameter.AbstractMultipartParameter;
import scw.net.http.enums.Method;

public class MultipartRequest extends HttpRequest {
	public static final String DEFAULT_BOUNDARY = "----WebKitFormBoundaryKSD2ndz6G9RPNjx0";

	private final String charsetName;
	private final String boundary;
	private List<AbstractMultipartParameter> parameters;

	public MultipartRequest(Method method, String url, String charsetName, String boundary) {
		super(method, url);
		if (method == Method.GET) {
			throw new NotSupportException("发送文件不支持" + method + "请求");
		}

		this.charsetName = charsetName;
		this.boundary = boundary;
	}

	public String getCharsetName() {
		return charsetName;
	}

	@Override
	public void request(URLConnection urlConnection) throws Throwable {
		HttpURLConnection http = (HttpURLConnection) urlConnection;

		if (http.getRequestProperty("Charset") == null) {
			http.setRequestProperty("Charset", charsetName);
		}

		if (http.getRequestProperty("Content-Type") == null) {
			http.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
		}

		super.request(urlConnection);
	}

	@Override
	public void doOutput(OutputStream os) throws Throwable {
		if (parameters != null) {
			Iterator<AbstractMultipartParameter> iterator = parameters.iterator();
			while (iterator.hasNext()) {
				iterator.next().write(os);
			}
			end(os);
		}
	}

	private void end(OutputStream out) throws IOException {
		StringBuilder end = new StringBuilder();
		end.append(AbstractMultipartParameter.BR);
		end.append(AbstractMultipartParameter.BOUNDARY_TAG);
		end.append(boundary);
		end.append(AbstractMultipartParameter.BOUNDARY_TAG);
		end.append(AbstractMultipartParameter.BR);
		out.write(end.toString().getBytes(charsetName));
	}

	public void addParameter(AbstractMultipartParameter parameter) {
		if (parameters == null) {
			parameters = new LinkedList<AbstractMultipartParameter>();
		}

		parameters.add(parameter);
	}
}
