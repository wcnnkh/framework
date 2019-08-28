package scw.beans.auto;

import java.util.Arrays;
import java.util.Collection;

import scw.beans.BeanFactory;

public final class AutoBeanUtils {
	private static final AutoBeanService DEFAULT_AUTO_BEAN_SERVICE = new DefaultAutoBeanService();

	private AutoBeanUtils() {
	};

	public static Collection<AutoBeanService> getAutoBeanServices(
			BeanFactory beanFactory, Collection<String> serviceNames) {
		return Arrays.asList(DEFAULT_AUTO_BEAN_SERVICE);
	}
}
