package run.soeasy.framework.mybatis.pagehelper;

import org.apache.ibatis.session.Configuration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.github.pagehelper.PageInterceptor;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * MyBatis 集成 PageHelper
 */
@Slf4j
@Getter
@Setter
public class PageInterceptorRegisterPostProcessor implements BeanPostProcessor, BeanFactoryAware {
	private BeanFactory beanFactory;

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof Configuration) {
			Configuration mybatisConfiguration = (Configuration) bean;
			for (PageInterceptor pageInterceptor : beanFactory.getBeanProvider(PageInterceptor.class)) {
				addInterceptor(mybatisConfiguration, pageInterceptor);
			}
		}
		return bean;
	}

	private void addInterceptor(Configuration configuration, PageInterceptor pageInterceptor) {
		if (configuration.getInterceptors().stream().anyMatch(interceptor -> interceptor instanceof PageInterceptor)) {
			return;
		}

		configuration.addInterceptor(pageInterceptor);
		log.info("{} 已成功添加到 Configuration 拦截器链", pageInterceptor.getClass());
	}
}