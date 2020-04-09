package scw.servlet.mvc;

import java.io.UnsupportedEncodingException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.beans.BeanFactory;
import scw.beans.annotation.Bean;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.MVCUtils;
import scw.mvc.http.HttpChannel;
import scw.mvc.service.ChannelService;
import scw.servlet.mvc.http.HttpServletChannelFactory;
import scw.util.value.property.PropertyFactory;

@Bean(proxy = false)
public class DefaultServletService implements ServletService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private final ChannelService channelService;
	private final HttpServletChannelFactory httpServletChannelFactory;
	private final String charsetName;

	public DefaultServletService(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		this(beanFactory.getInstance(HttpServletChannelFactory.class), MVCUtils
				.getCharsetName(propertyFactory), beanFactory
				.getInstance(ChannelService.class));
	}

	public DefaultServletService(
			HttpServletChannelFactory httpServletChannelFactory,
			String charsetName, ChannelService channelService) {
		this.httpServletChannelFactory = httpServletChannelFactory;
		this.charsetName = charsetName;
		this.channelService = channelService;
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

			channelService.service(httpChannel);
		}
	}
}
