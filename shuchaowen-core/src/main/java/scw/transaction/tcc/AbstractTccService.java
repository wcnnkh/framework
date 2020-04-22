package scw.transaction.tcc;

import java.lang.reflect.Method;

import scw.beans.BeanFactory;
import scw.core.utils.StringUtils;
import scw.transaction.tcc.annotation.Tcc;

public abstract class AbstractTccService implements TccService {
	private BeanFactory beanFactory;

	public AbstractTccService(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
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
	
	public Stage createConfirm(Class<?> targetClass, Method tryMethod, Object tryResult, Object[] args, Tcc tcc) {
		if (StringUtils.isEmpty(tcc.confirm())) {
			return null;
		}

		return createStage(new TryInfo(targetClass, tryResult, tryMethod, args), tcc, tcc.confirm());
	}
	
	public Stage createCancel(Class<?> targetClass, Method tryMethod, Object tryResult, Object[] args, Tcc tcc) {
		if (StringUtils.isEmpty(tcc.confirm())) {
			return null;
		}

		return createStage(new TryInfo(targetClass, tryResult, tryMethod, args), tcc, tcc.confirm());
	}

	public Stage createCancel(TryInfo tryInfo, Method tryMethod, Tcc tcc) {
		if (StringUtils.isEmpty(tcc.cancel())) {
			return null;
		}

		return createStage(tryInfo, tcc, tcc.cancel());
	}
}
