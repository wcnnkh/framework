package scw.ibatis.beans;

import org.apache.ibatis.session.SqlSessionFactory;

import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeanlifeCycleEvent;
import scw.beans.BeanlifeCycleEvent.Step;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.context.annotation.Provider;
import scw.event.EventListener;

@Provider
public class IbatisBeanFactoryPostProcessor implements BeanFactoryPostProcessor, EventListener<BeanlifeCycleEvent> {

	@Override
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		if (!beanFactory.containsDefinition(SqlSessionFactory.class.getName())) {
			beanFactory.registerDefinition(new SqlSessionFactoryBeanDefinition(beanFactory));
		}
		beanFactory.getLifecycleDispatcher().registerListener(this);
	}

	@Override
	public void onEvent(BeanlifeCycleEvent event) {
		if (event.getSource() != null && event.getStep() == Step.AFTER_DEPENDENCE) {
			Object source = event.getSource();
			if (source instanceof SqlSessionFactory) {
				IbatisBeanUtils.configuration(((SqlSessionFactory) source).getConfiguration(), event.getBeanFactory());
			}
		}
	}
}
