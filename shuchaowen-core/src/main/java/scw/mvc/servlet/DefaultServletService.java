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
import scw.mvc.MVCUtils;
import scw.mvc.http.HttpChannel;
import scw.mvc.servlet.http.HttpServletChannelFactory;
import scw.mvc.support.ControllerService;

@Bean(proxy = false)
public class DefaultServletService implements ServletService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private final ControllerService controllerService;
	private final HttpServletChannelFactory httpServletChannelFactory;
	private final String charsetName;

	public DefaultServletService(BeanFactory beanFactory, PropertyFactory propertyFactory) throws Throwable {
		this.charsetName = MVCUtils.getCharsetName(propertyFactory);
		this.controllerService = new ControllerService(beanFactory, propertyFactory);
		this.httpServletChannelFactory = ServletUtils.getHttpServletChannelFactory(beanFactory, propertyFactory);
	}

	public final String getCharsetName() {
		return charsetName;
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
			HttpChannel httpChannel = httpServletChannelFactory.getHttpChannel((HttpServletRequest) req,
					(HttpServletResponse) resp);
			if (httpChannel == null) {
				return;
			}

			controllerService.service(httpChannel);
		}
	}
}
