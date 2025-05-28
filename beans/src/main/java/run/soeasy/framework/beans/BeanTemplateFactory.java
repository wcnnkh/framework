package run.soeasy.framework.beans;

import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.transform.property.ObjectTemplateFactory;

@RequiredArgsConstructor
@Getter
public class BeanTemplateFactory implements ObjectTemplateFactory<BeanProperty> {
	private static final ConfigurableBeanInfoFactory BEAN_INFO_FACTORY = new ConfigurableBeanInfoFactory();
	static {
		BEAN_INFO_FACTORY.configure();
	}

	private final ConcurrentHashMap<Class<?>, BeanTemplate> templateMap = new ConcurrentHashMap<>();
	@NonNull
	private final BeanInfoFactory beanInfoFactory;

	public BeanTemplateFactory() {
		this(BEAN_INFO_FACTORY);
	}

	@Override
	public BeanTemplate getTemplate(Class<?> beanClass) {
		return templateMap.computeIfAbsent(beanClass, (key) -> new BeanTemplate(key, beanInfoFactory));
	}
}
