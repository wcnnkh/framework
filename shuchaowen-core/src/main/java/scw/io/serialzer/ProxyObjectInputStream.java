package scw.io.serialzer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

import scw.aop.ProxyUtils;
import scw.core.utils.ClassUtils;

public class ProxyObjectInputStream extends ObjectInputStream {
	private ClassLoader classLoader;

	public ProxyObjectInputStream(InputStream in) throws IOException {
		super(in);
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
		if (readBoolean()) {// 代理类
			String className = (String) readObject();
			Class<?> clazz = ClassUtils.forName(className, getClassLoader());
			int interfaceSize = readInt();
			Class<?>[] interfaces = new Class<?>[interfaceSize];
			for (int i = 0; i < interfaceSize; i++) {
				String interfaceClassName = (String) readObject();
				interfaces[i] = ClassUtils.forName(interfaceClassName, getClassLoader());
			}
			Class<?> proxyClass = ProxyUtils.getProxyFactory().getProxyClass(clazz, interfaces);
			return ObjectStreamClass.lookup(proxyClass);
		} else {
			return super.readClassDescriptor();
		}
	}
}
