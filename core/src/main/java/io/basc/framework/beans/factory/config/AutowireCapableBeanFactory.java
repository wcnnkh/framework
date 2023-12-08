package io.basc.framework.beans.factory.config;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.execution.param.ParameterFactory;

/**
 * 自动注入
 * 
 * @author wcnnkh
 *
 */
public interface AutowireCapableBeanFactory extends BeanFactory, ParameterFactory {
}
