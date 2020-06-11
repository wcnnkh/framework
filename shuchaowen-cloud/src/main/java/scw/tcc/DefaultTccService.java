package scw.tcc;

import java.lang.reflect.Method;

import scw.aop.ProxyInvoker;
import scw.beans.BeanFactory;
import scw.complete.Complete;
import scw.complete.CompleteService;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.StringUtils;
import scw.tcc.annotation.Tcc;

@Configuration(order = Integer.MIN_VALUE, value = TccService.class)
public class DefaultTccService implements TccService {
	private BeanFactory beanFactory;
	private CompleteService completeService;

	public DefaultTccService(BeanFactory beanFactory, CompleteService completeService) {
		this.beanFactory = beanFactory;
		this.completeService = completeService;
	}

	private Stage createStage(TryInfo tryInfo, Tcc tcc, String stageName) {
		return new Stage(beanFactory,
				StringUtils.isEmpty(tcc.beanName()) ? tryInfo.getTargetClass().getName() : tcc.beanName(), tryInfo,
				stageName);
	}

	public Stage createConfirm(TryInfo tryInfo, Method tryMethod, Tcc tcc) {
		if (StringUtils.isEmpty(tcc.confirm())) {
			return null;
		}

		return createStage(tryInfo, tcc, tcc.confirm());
	}

	public Stage createConfirm(ProxyInvoker invoker, Object[] args, Object tryResult, Tcc tcc) {
		if (StringUtils.isEmpty(tcc.confirm())) {
			return null;
		}

		return createStage(new TryInfo(invoker, args, tryResult), tcc, tcc.confirm());
	}

	public Stage createCancel(ProxyInvoker invoker, Object[] args, Object tryResult, Tcc tcc) {
		if (StringUtils.isEmpty(tcc.cancel())) {
			return null;
		}

		return createStage(new TryInfo(invoker, args, tryResult), tcc, tcc.cancel());
	}

	public Stage createCancel(TryInfo tryInfo, Method tryMethod, Tcc tcc) {
		if (StringUtils.isEmpty(tcc.cancel())) {
			return null;
		}

		return createStage(tryInfo, tcc, tcc.cancel());
	}

	public Complete registerComplete(Stage stage) throws Exception {
		return completeService.register(stage);
	}
}
