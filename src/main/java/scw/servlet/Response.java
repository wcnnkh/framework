package scw.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.alibaba.fastjson.JSONObject;

import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.core.net.http.ContentType;
import scw.core.utils.ClassUtils;

public class Response extends HttpServletResponseWrapper {
	private static Logger logger = LoggerFactory.getLogger(Response.class);

	private static final String JSONP_CALLBACK = "callback";
	private static final String JSONP_RESP_PREFIX = "(";
	private static final String JSONP_RESP_SUFFIX = ");";
	private Request request;

	public Response(Request request, HttpServletResponse httpServletResponse) {
		super(httpServletResponse);
		this.request = request;
	}

	public Request getRequest() {
		return request;
	}

	protected String toJsonString(Object data) {
		return JSONObject.toJSONString(data);
	}

	public void write(Object obj) throws IOException {
		if (obj != null) {
			if (obj instanceof View) {
				((View) obj).render(request, this);
			} else {
				String content;
				if ((obj instanceof String) || (ClassUtils.isPrimitiveOrWrapper(obj.getClass()))) {
					content = obj.toString();
				} else {
					content = toJsonString(obj);
				}

				String callback = null;
				try {
					callback = request.getParameter(String.class, JSONP_CALLBACK);
				} catch (Throwable e) {
					e.printStackTrace();
				}

				if (callback != null && callback.length() != 0) {
					setContentType(ContentType.TEXT_JAVASCRIPT);
					StringBuilder sb = new StringBuilder();
					sb.append(callback);
					sb.append(JSONP_RESP_PREFIX);
					sb.append(content);
					sb.append(JSONP_RESP_SUFFIX);
					content = sb.toString();
				} else {
					if (getContentType() == null) {
						setContentType(ContentType.TEXT_HTML);
					}
				}

				if (request.isDebug()) {
					logger.debug(content);
				}
				getWriter().write(content);
			}
		}
	}
}
