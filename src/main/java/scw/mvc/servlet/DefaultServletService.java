package scw.mvc.servlet;

import java.io.UnsupportedEncodingException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.beans.BeanFactory;
import scw.beans.annotation.Bean;
import scw.core.PropertyFactory;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.ControllerService;
import scw.mvc.DefaultControllerService;
import scw.mvc.MVCUtils;
import scw.mvc.http.HttpChannel;
import scw.mvc.servlet.http.HttpServletChannelFactory;

@Bean(proxy = false)
public class DefaultServletService implements ServletService {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	private ControllerService controllerService;
	private final HttpServletChannelFactory httpServletChannelFactory;
	private final String charsetName;

	public DefaultServletService(BeanFactory beanFactory, PropertyFactory propertyFactory) throws Throwable {
		this.charsetName = MVCUtils.getCharsetName(propertyFactory);
		this.controllerService = new DefaultControllerService(beanFactory, propertyFactory);
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

		controllerService.service(httpChannel);
	}

	public void service(ServletRequest req, ServletResponse resp) {
		if (charsetName != null) {
			try {
				req.setCharacterEncoding(charsetName);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			resp.setCharacterEncoding(charsetName);
		}

		if (req instanceof HttpServletRequest && resp instanceof HttpServletResponse) {
			service((HttpServletRequest) req, (HttpServletResponse) resp);
		}
	}
}
