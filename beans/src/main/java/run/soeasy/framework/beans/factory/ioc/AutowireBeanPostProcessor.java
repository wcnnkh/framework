package run.soeasy.framework.beans.factory.ioc;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.beans.BeansException;
import run.soeasy.framework.beans.factory.config.AutowireCapableBeanFactory;
import run.soeasy.framework.beans.factory.config.BeanPostProcessor;

@Getter
@Setter
@RequiredArgsConstructor
public class AutowireBeanPostProcessor implements BeanPostProcessor {
	@NonNull
	private final AutowireCapableBeanFactory autowireCapableBeanFactory;

	@Override
	public void postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if(bean == null) {
			return ;
		}
		
		
	}
}
