package scw.mvc.support;

import scw.core.utils.ClassUtils;
import scw.mvc.Channel;
import scw.mvc.Text;
import scw.mvc.View;
import scw.result.ResultFactory;

/**
 * 兼容ResultFactory
 * 
 * @author shuchaowen
 *
 */
public final class ResultResponseBodyService implements ResponseBodyService {
	private ResultFactory resultFactory;

	public ResultResponseBodyService(ResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	public Object responseBody(Channel channel, Object value) throws Throwable {
		if (value != null) {
			if (value instanceof View || value instanceof Text || value instanceof String
					|| ClassUtils.isPrimitiveOrWrapper(value.getClass())) {
				return value;
			}
		}

		return value == null ? resultFactory.success() : resultFactory.success(value);
	}

}
