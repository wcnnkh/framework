package scw.mvc.security;

import scw.beans.BeanFactory;
import scw.context.annotation.Provider;
import scw.core.Ordered;
import scw.core.annotation.AnnotationUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.HttpChannel;
import scw.mvc.action.Action;
import scw.mvc.action.ActionInterceptor;
import scw.mvc.action.ActionInterceptorAccept;
import scw.mvc.action.ActionInterceptorChain;
import scw.mvc.action.ActionParameters;
import scw.mvc.annotation.IPSecurity;
import scw.security.ip.IPValidationFailedException;
import scw.security.ip.IPVerification;

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
		return AnnotationUtils.getAnnotation(IPSecurity.class, action.getSourceClass(),
				action.getAnnotatedElement());
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
