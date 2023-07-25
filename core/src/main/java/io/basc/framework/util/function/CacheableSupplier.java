package io.basc.framework.util.function;

import java.util.function.Supplier;

import io.basc.framework.util.Assert;
import io.basc.framework.util.ObjectUtils;

public class CacheableSupplier<T> extends CacheableSource<T, RuntimeException> implements Supplier<T> {
	public CacheableSupplier(T source) {
		super(source);
	}

	public CacheableSupplier(Supplier<? extends T> supplier, Object lock) {
		super(new InternalSource<>(supplier), lock);
	}

	private static class InternalSource<V> implements Source<V, RuntimeException> {
		private final Supplier<? extends V> supplier;

		public InternalSource(Supplier<? extends V> supplier) {
			Assert.requiredArgument(supplier != null, "supplier");
			this.supplier = supplier;
		}

		@Override
		public V get() {
			return supplier.get();
		}

		@Override
		public String toString() {
			return supplier.toString();
		}

		@Override
		public int hashCode() {
			return supplier.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}

			if (obj instanceof InternalSource) {
				return ObjectUtils.equals(supplier, ((InternalSource<?>) obj).supplier);
			}
			return false;
		}
	}
}
