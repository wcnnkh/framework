package scw.mvc.output.support;

import scw.core.utils.ClassUtils;
import scw.mvc.http.HttpChannel;
import scw.mvc.output.HttpOutput;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;

public class DefaultHttpOutput extends HttpOutput<Object> {

	@Override
	protected MimeType getContentType(HttpChannel channel, Object body) {
		return MimeTypeUtils.TEXT_HTML;
	}

	@Override
	protected boolean canWriteInternal(HttpChannel httpChannel, Object body) {
		return true;
	}

	@Override
	protected void writeBody(HttpChannel channel, Object body)
			throws Throwable {
		String content;
		if ((body instanceof String)
				|| (ClassUtils.isPrimitiveOrWrapper(body.getClass()))) {
			content = body.toString();
		} else {
			content = getJsonSupport().toJSONString(body);
		}
		channel.getResponse().getWriter().write(content);
		if (channel.isLogEnabled()) {
			channel.log(content);
		}
	}
}
