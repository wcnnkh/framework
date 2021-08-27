package io.basc.framework.mvc.security;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.annotation.AnnotationUtils;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mvc.HttpChannel;
import io.basc.framework.mvc.action.Action;
import io.basc.framework.mvc.action.ActionInterceptor;
import io.basc.framework.mvc.action.ActionInterceptorAccept;
import io.basc.framework.mvc.action.ActionInterceptorChain;
import io.basc.framework.mvc.action.ActionParameters;
import io.basc.framework.mvc.annotation.IPSecurity;
import io.basc.framework.security.ip.IPValidationFailedException;
import io.basc.framework.security.ip.IPVerification;

@Provider(order = Ordered.HIGHEST_PRECEDENCE)
public final class IPSecurityActionInterceptor implements ActionInterceptor, ActionInterceptorAccept {
	private static Logger logger = LoggerFactory.getLogger(IPSecurityActionInterceptor.class);
	private BeanFactory beanFactory;

	public IPSecurityActionInterceptor(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public boolean isAccept(HttpChannel httpChannel, Action action, ActionParameters parameters) {
		return getIPSecurity(action) != null;
	}

	private IPSecurity getIPSecurity(Action action) {
		return AnnotationUtils.getAnnotation(IPSecurity.class, action.getDeclaringClass(),
				action);
	}

	public Object intercept(HttpChannel httpChannel, Action action, ActionParameters parameters,
			ActionInterceptorChain filterChain) throws Throwable {
		IPSecurity ipSecurity = getIPSecurity(action);
		if (ipSecurity != null) {
			boolean b = verificationIP(httpChannel.getRequest().getIp(), ipSecurity);
			if (!b) {
				throw new IPValidationFailedException("ip验证失败");
			}
		}
		return filterChain.intercept(httpChannel, action, parameters);
	}

	private boolean verificationIP(String ip, IPSecurity ipSecurity) {
		if (!beanFactory.isInstance(ipSecurity.value())) {
			logger.warn("无法初始化:{}", ipSecurity.value());
			return false;
		}

		IPVerification ipVerification = beanFactory.getInstance(ipSecurity.value());
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
