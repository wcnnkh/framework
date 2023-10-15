package io.basc.framework.web.servlet.convert;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.web.ServerRequest;
import io.basc.framework.web.ServerResponse;

public class ServletConverters extends ConfigurableServices<ServletConverter> implements ServletConverter {

	public ServletConverters() {
		super(ServletConverter.class);
	}

	@Override
	public boolean canConvert(ServletRequest servletRequest) {
		return getServices().anyMatch((e) -> e.canConvert(servletRequest));
	}

	@Override
	public ServerRequest convert(ServletRequest servletRequest) {
		for (ServletConverter converter : getServices()) {
			if (converter.canConvert(servletRequest)) {
				return converter.convert(servletRequest);
			}
		}
		return unsupported(servletRequest);
	}

	protected ServerRequest unsupported(ServletRequest servletRequest) {
		throw new UnsupportedException(servletRequest.toString());
	}

	@Override
	public boolean canConvert(ServletResponse servletResponse) {
		return getServices().anyMatch((e) -> e.canConvert(servletResponse));
	}

	@Override
	public ServerResponse convert(ServletResponse servletResponse) {
		for (ServletConverter converter : getServices()) {
			if (converter.canConvert(servletResponse)) {
				return converter.convert(servletResponse);
			}
		}
		return unsupported(servletResponse);
	}

	protected ServerResponse unsupported(ServletResponse servletResponse) {
		throw new UnsupportedException(servletResponse.toString());
	}
}
