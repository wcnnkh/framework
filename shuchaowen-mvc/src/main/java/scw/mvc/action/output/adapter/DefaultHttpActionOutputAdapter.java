package scw.mvc.action.output.adapter;

import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.json.JSONSupport;
import scw.mvc.MVCUtils;
import scw.mvc.View;
import scw.mvc.action.Action;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.net.Text;

public class DefaultHttpActionOutputAdapter extends HttpActionOutputAdapter {
	private JSONSupport jsonSupport;
	private String jsonp;

	public DefaultHttpActionOutputAdapter(JSONSupport jsonSupport, String jsonp) {
		this.jsonSupport = jsonSupport;
		this.jsonp = jsonp;
	}

	public void output(HttpChannel channel, Action action, Object write)
			throws Throwable {
		if (write == null) {
			return;
		}

		if (write instanceof View) {
			((View) write).render(channel);
			return;
		}

		String callbackTag = null;
		if (scw.net.http.Method.GET == channel.getRequest().getMethod()) {
			if (!StringUtils.isEmpty(jsonp)) {
				callbackTag = channel.getString(jsonp);
				if (StringUtils.isEmpty(callbackTag)) {
					callbackTag = null;
				}
			}
		}

		HttpResponse httpResponse = channel.getResponse();
		if (write instanceof String) {
			String redirect = parseRedirect(channel.getRequest(),
					(String) write, true);
			if (redirect != null) {
				httpResponse.sendRedirect(redirect);
				return;
			}
		}

		if (callbackTag != null) {
			httpResponse.setMimeType(MimeTypeUtils.TEXT_JAVASCRIPT);
			httpResponse.getWriter().write(callbackTag);
			httpResponse.getWriter().write(MVCUtils.JSONP_RESP_PREFIX);
		}

		String content;
		if (write instanceof Text) {
			content = ((Text) write).getTextContent();
			if (callbackTag == null) {
				MimeType mimeType = ((Text) write).getMimeType();
				if (mimeType != null) {
					httpResponse.setMimeType(mimeType);
				}
			}
		} else if ((write instanceof String)
				|| (ClassUtils.isPrimitiveOrWrapper(write.getClass()))) {
			content = write.toString();
		} else {
			content = jsonSupport.toJSONString(write);
		}

		if (callbackTag == null) {
			if (StringUtils.isEmpty(httpResponse.getContentType())) {
				httpResponse.setMimeType(MimeTypeUtils.TEXT_HTML);
			}
		}

		httpResponse.getWriter().write(content);

		if (callbackTag != null) {
			httpResponse.getWriter().write(MVCUtils.JSONP_RESP_SUFFIX);
		}

		if (channel.isLogEnabled()) {
			channel.log(content);
		}
	}

	@Override
	protected boolean isAdapter(HttpChannel channel, Object obj) {
		return true;
	}

	public String parseRedirect(HttpRequest httpRequest, String text,
			boolean ignoreCase) {
		if (text == null) {
			return null;
		}

		if (StringUtils.startsWith(text, MVCUtils.REDIRECT_PREFIX, ignoreCase)) {
			String url = text.substring(MVCUtils.REDIRECT_PREFIX.length());
			if (StringUtils.isEmpty(url) || url.equals("/")) {
				return httpRequest.getContextPath();
			} else if (url.startsWith("/")) {
				return httpRequest.getContextPath() + url;
			} else {
				return url;
			}
		}
		return null;
	}
}
