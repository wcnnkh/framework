package scw.mvc.action.filter.support;

import scw.beans.BeanFactory;
import scw.beans.annotation.Configuration;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.Channel;
import scw.mvc.MVCUtils;
import scw.mvc.action.Action;
import scw.mvc.action.filter.Filter;
import scw.mvc.action.filter.FilterChain;
import scw.mvc.annotation.IPSecurity;
import scw.security.ip.IPValidationFailedException;
import scw.security.ip.IPVerification;

@Configuration(order=Integer.MAX_VALUE)
public final class IPSecurityFilter implements Filter{
	private static Logger logger = LoggerFactory.getLogger(IPSecurityFilter.class);
	private BeanFactory beanFactory;
	
	public IPSecurityFilter(BeanFactory beanFactory){
		this.beanFactory = beanFactory;
	}

	public Object doFilter(Channel channel, Action action, FilterChain chain)
			throws Throwable {
		IPSecurity ipSecurity = action.getAnnotation(IPSecurity.class);
		if (ipSecurity != null) {
			boolean b = verificationIP(MVCUtils.getIP(channel), ipSecurity);
			if (!b) {
				throw new IPValidationFailedException("ip验证失败");
			}
		}
		
		return chain.doFilter(channel, action);
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
