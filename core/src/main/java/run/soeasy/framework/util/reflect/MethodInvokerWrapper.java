package run.soeasy.framework.util.reflect;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class MethodInvokerWrapper implements MethodInvoker, Serializable {
	private static final long serialVersionUID = 1L;
	private Supplier<MethodInvoker> sourceSupplier;
	private MethodInvoker source;

	public MethodInvokerWrapper(Supplier<MethodInvoker> sourceSupplier) {
		this.sourceSupplier = sourceSupplier;
	}

	public MethodInvokerWrapper(MethodInvoker source) {
		this.source = source;
	}

	private final AtomicBoolean get = new AtomicBoolean();

	public MethodInvoker getSource() {
		if (source == null && sourceSupplier != null && !get.get()) {
			synchronized (this) {
				if (source == null) {
					if (get.compareAndSet(false, true)) {
						source = sourceSupplier.get();
					}
				}
			}
		}
		return source;
	}

	public Object invoke(Object... args) throws Throwable {
		return getSource().invoke(args);
	}

	public Object getInstance() {
		return getSource().getInstance();
	}

	@Override
	public String toString() {
		return getSource().toString();
	}

	@Override
	public boolean equals(Object obj) {
		return getSource().equals(obj);
	}

	@Override
	public int hashCode() {
		return getSource().hashCode();
	}

	public Method getMethod() {
		return getSource().getMethod();
	}

	@Override
	public Class<?> getSourceClass() {
		return getSource().getSourceClass();
	}
}
