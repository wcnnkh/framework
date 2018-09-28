package shuchaowen.core.application;

import java.lang.reflect.Modifier;
import java.util.Collection;

import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.BeanUtils;
import shuchaowen.core.beans.SingletonFactory;
import shuchaowen.core.db.DB;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.util.ClassUtils;

public class CommonApplication implements Application {
	private SingletonFactory singletonFactory;
	private String packageName;

	public CommonApplication() {
		this("");
	}

	public CommonApplication(String packageName) {
		this.packageName = packageName;
		this.singletonFactory = new SingletonFactory(packageName);
	}

	public Collection<Class<?>> getClasses() {
		return ClassUtils.getClasses(packageName);
	}

	public BeanFactory getBeanFactory() {
		return singletonFactory;
	}

	protected void initDB(Collection<Class<?>> classList) {
		for (Class<?> clz : classList) {
			Deprecated deprecated = clz.getAnnotation(Deprecated.class);
			if (deprecated != null) {
				continue;
			}

			if (Modifier.isAbstract(clz.getModifiers()) || Modifier.isInterface(clz.getModifiers())) {
				continue;
			}

			if (!Modifier.isPublic(clz.getModifiers())) {
				continue;
			}

			if (!DB.class.isAssignableFrom(clz)) {
				continue;
			}

			getBeanFactory().get(clz);
		}
	}

	public void init() {
		Collection<Class<?>> classList = getClasses();
		try {
			BeanUtils.initAutowriteStatic(getBeanFactory(), classList);
			initDB(classList);
			BeanUtils.invokerInitStaticMethod(classList);
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}

	public void destroy() {
		try {
			BeanUtils.destroyStaticMethod(getClasses());
		} catch (Exception e) {
			e.printStackTrace();
		}
		singletonFactory.destroy();
	}
}
