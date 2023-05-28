package io.basc.framework.beans;

import io.basc.framework.beans.config.BeanDefinition;
import io.basc.framework.execution.Executor;
import io.basc.framework.util.Elements;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BeanSource {
	private final String name;
	private final BeanDefinition beanDefinition;
	private final Executor executor;
	private final Elements<? extends Object> args;
	private final Object bean;
}
