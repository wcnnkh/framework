package scw.mvc.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.beans.BeanFactory;
import scw.beans.annotation.Bean;
import scw.core.PropertyFactory;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.Filter;
import scw.mvc.MVCUtils;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.filter.NotFoundFilter;
import scw.mvc.servlet.http.HttpServletChannelFactory;

@Bean(proxy = false)
public class DefaultServletService implements ServletService {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	private final HttpServletChannelFactory httpServletChannelFactory;
	private final String charsetName;
	private final Collection<Filter> filters;
	private final int warnExecuteTime;

	public DefaultServletService(BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Throwable {
		this.charsetName = MVCUtils.getCharsetName(propertyFactory);
		this.warnExecuteTime = MVCUtils.getWarnExecuteTime(propertyFactory);
		this.filters = ServletUtils.getFilters(beanFactory, propertyFactory);
		this.filters.add(new NotFoundFilter());
		this.httpServletChannelFactory = ServletUtils.getHttpServletChannelFactory(beanFactory, propertyFactory);
	}

	public final String getCharsetName() {
		return charsetName;
	}

	protected void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		HttpChannel httpChannel = httpServletChannelFactory.getHttpChannel(httpServletRequest, httpServletResponse);
		if (httpChannel == null) {
			return;
		}

		try {
			MVCUtils.service(filters, httpChannel, warnExecuteTime);
		} catch (Throwable e) {
			error(httpServletRequest, httpServletResponse, e);
		}
	}

	protected void error(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Throwable e) {
		logger.error(e, "执行servletPath={},method={}异常", httpServletRequest.getServletPath(),
				httpServletRequest.getMethod());
		if (!httpServletResponse.isCommitted()) {
			try {
				httpServletResponse.sendError(500, "system error");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	public void service(ServletRequest req, ServletResponse resp) {
		if (getCharsetName() != null) {
			try {
				req.setCharacterEncoding(getCharsetName());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			resp.setCharacterEncoding(getCharsetName());
		}

		if (req instanceof HttpServletRequest && resp instanceof HttpServletResponse) {
			service((HttpServletRequest) req, (HttpServletResponse) resp);
		}
	}
}
