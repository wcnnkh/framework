package scw.mvc.wrapper;

import scw.beans.annotation.Bean;
import scw.core.utils.ClassUtils;
import scw.mvc.Channel;
import scw.mvc.View;
import scw.mvc.http.Text;
import scw.result.ResultFactory;

/**
 * 兼容ResultFactory
 * 
 * @author shuchaowen
 *
 */
@Bean(proxy = false)
public class CompatibleResponseWrapperService implements ResponseWrapperService {
	private ResultFactory resultFactory;

	public CompatibleResponseWrapperService(ResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	public Object wrapper(Channel channel, Object value) throws Throwable {
		if (value != null) {
			if (value instanceof View || value instanceof Text || value instanceof String
					|| ClassUtils.isPrimitiveOrWrapper(value.getClass())) {
				return value;
			}
		}

		return value == null ? resultFactory.success() : resultFactory.success(value);
	}

}
