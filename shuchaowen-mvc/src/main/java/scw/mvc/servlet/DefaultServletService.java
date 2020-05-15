package scw.mvc.servlet;

import java.io.UnsupportedEncodingException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.beans.BeanFactory;
import scw.beans.annotation.Bean;
import scw.core.instance.annotation.Configuration;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.HttpChannelService;
import scw.mvc.servlet.http.HttpServletChannelFactory;
import scw.net.http.server.mvc.HttpChannel;
import scw.net.http.server.mvc.MVCUtils;
import scw.value.property.PropertyFactory;

@Configuration(order=Integer.MIN_VALUE)
@Bean(proxy = false)
public class DefaultServletService implements ServletService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private final HttpChannelService httpChannelService;
	private final HttpServletChannelFactory httpServletChannelFactory;
	private final String charsetName;

	public DefaultServletService(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		this(beanFactory.getInstance(HttpServletChannelFactory.class), MVCUtils
				.getCharsetName(propertyFactory), beanFactory
				.getInstance(HttpChannelService.class));
	}

	public DefaultServletService(
			HttpServletChannelFactory httpServletChannelFactory,
			String charsetName, HttpChannelService httpChannelService) {
		this.httpServletChannelFactory = httpServletChannelFactory;
		this.charsetName = charsetName;
		this.httpChannelService = httpChannelService;
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

		if (req instanceof HttpServletRequest
				&& resp instanceof HttpServletResponse) {
			HttpChannel httpChannel = httpServletChannelFactory.getHttpChannel(
					(HttpServletRequest) req, (HttpServletResponse) resp);
			if (httpChannel == null) {
				return;
			}

			httpChannelService.service(httpChannel);
		}
	}
}
