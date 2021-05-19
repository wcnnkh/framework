package scw.util;

@FunctionalInterface
public interface Creator<T> {
	/**
	 * 创建一个对象
	 * 
	 * @return
	 */
	T create();

	default Supplier<T> toSupplier() {
		return new Supplier<T>() {

			@Override
			public T get() {
				return Creator.this.create();
			}
		};
	}
}
