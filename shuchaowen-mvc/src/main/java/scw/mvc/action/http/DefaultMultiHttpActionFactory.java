package scw.mvc.action.http;

import java.lang.reflect.Method;
import java.util.Collection;

import scw.beans.BeanFactory;
import scw.core.annotation.AnnotationFactory;
import scw.core.annotation.SimpleAnnotationFactory;
import scw.core.instance.InstanceFactory;
import scw.mvc.annotation.Controller;
import scw.security.authority.SimpleAuthorityManager;
import scw.security.authority.http.HttpAuthority;
import scw.security.authority.http.HttpAuthorityManager;
import scw.security.authority.http.SimpleHttpAuthorityManager;
import scw.util.value.property.PropertyFactory;

public final class DefaultMultiHttpActionFactory extends MultiHttpActionFactory {
	private SimpleAuthorityManager<HttpAuthority> httpAuthorityManager;

	public DefaultMultiHttpActionFactory(InstanceFactory instanceFactory) {
		if (instanceFactory.isInstance(HttpAuthorityManager.class)
				&& instanceFactory.isSingleton(HttpAuthorityManager.class)) {
			HttpAuthorityManager httpAuthorityManager = instanceFactory
					.getInstance(HttpAuthorityManager.class);
			if (httpAuthorityManager instanceof SimpleHttpAuthorityManager) {
				this.httpAuthorityManager = (SimpleHttpAuthorityManager) httpAuthorityManager;
			}
		}
	}

	public DefaultMultiHttpActionFactory(
			SimpleAuthorityManager<HttpAuthority> httpAuthorityManager) {
		this.httpAuthorityManager = httpAuthorityManager;
	}

	public void init(BeanFactory beanFactory, PropertyFactory propertyFactory,
			Collection<Class<?>> classes) {
		for (Class<?> clz : classes) {
			Controller clzController = clz.getAnnotation(Controller.class);
			if (clzController == null) {
				continue;
			}

			AnnotationFactory clazzAnnotationFactory = new SimpleAnnotationFactory(
					clz);
			for (Method method : clz.getDeclaredMethods()) {
				Controller methodController = method
						.getAnnotation(Controller.class);
				if (methodController == null) {
					continue;
				}

				HttpAction httpAction = new AnnotationHttpAction(beanFactory,
						propertyFactory, clz, method, clazzAnnotationFactory);
				if (httpAuthorityManager != null
						&& httpAction.getAuthority() != null) {
					httpAuthorityManager
							.addAuthroity(httpAction.getAuthority());
				}

				scanning(httpAction);
			}
		}
	}

}
