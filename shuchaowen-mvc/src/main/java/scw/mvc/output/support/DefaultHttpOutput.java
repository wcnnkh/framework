package scw.mvc.output.support;

import scw.core.utils.ClassUtils;
import scw.mvc.http.HttpChannel;
import scw.mvc.output.HttpOutput;

public class DefaultHttpOutput extends HttpOutput<Object> {

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
