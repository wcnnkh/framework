package scw.mvc.security;

import scw.beans.BeanFactory;
import scw.core.instance.annotation.Configuration;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.net.http.server.mvc.HttpChannel;
import scw.net.http.server.mvc.action.Action;
import scw.net.http.server.mvc.action.ActionFilter;
import scw.net.http.server.mvc.action.ActionService;
import scw.net.http.server.mvc.annotation.IPSecurity;
import scw.security.ip.IPValidationFailedException;
import scw.security.ip.IPVerification;

@Configuration(order=Integer.MAX_VALUE)
public final class IPSecurityActionFilter implements ActionFilter{
	private static Logger logger = LoggerFactory.getLogger(IPSecurityActionFilter.class);
	private BeanFactory beanFactory;
	
	public IPSecurityActionFilter(BeanFactory beanFactory){
		this.beanFactory = beanFactory;
	}

	public Object doFilter(HttpChannel httpChannel, Action action, ActionService service)
			throws Throwable {
		IPSecurity ipSecurity = action.getAnnotatedElement().getAnnotation(IPSecurity.class);
		if (ipSecurity != null) {
			boolean b = verificationIP(httpChannel.getRequest().getIp(), ipSecurity);
			if (!b) {
				throw new IPValidationFailedException("ip验证失败");
			}
		}
		
		return service.doAction(httpChannel, action);
	}

	private boolean verificationIP(String ip, IPSecurity ipSecurity) {
		if (!beanFactory.isInstance(ipSecurity.value())) {
			logger.warn("无法初始化:{}", ipSecurity.value());
			return false;
		}

		IPVerification ipVerification = beanFactory.getInstance(ipSecurity
				.value());
		boolean b = ipVerification.verification(ip);
		if (b) {
			if (logger.isDebugEnabled()) {
				logger.debug("verification ip [{}] success", ip);
			}
		} else {
			logger.warn("verification ip [{}] fail", ip);
		}
		return b;
	}
}
