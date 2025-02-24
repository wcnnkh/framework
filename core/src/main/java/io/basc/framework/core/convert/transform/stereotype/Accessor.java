package io.basc.framework.core.convert.transform.stereotype;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.Source;
import io.basc.framework.util.Assert;
import lombok.NonNull;

public interface Accessor extends Source, AccessDescriptor {
	@FunctionalInterface
	public static interface AccessWrapper<W extends Accessor>
			extends Accessor, SourceWrapper<W>, AccessDescriptorWrapper<W> {

		@Override
		default boolean isReadable() {
			return getSource().isReadable();
		}

		@Override
		default boolean isWriteable() {
			return getSource().isWriteable();
		}

		@Override
		default void set(Object source) throws UnsupportedOperationException {
			getSource().set(source);
		}
	}
	
	public static class StandardAccess<W extends AccessDescriptor> extends StandardSource<W>
			implements Accessor, AccessDescriptorWrapper<W> {
		private static final long serialVersionUID = 1L;

		public StandardAccess(@NonNull W source) {
			super(source);
		}

		@Override
		public void set(Object value) throws UnsupportedOperationException {
			Assert.isTrue(isRequired() && value == null, "Required parameters cannot be empty");
			setValue(value);
		}
	}

	/**
	 * 通过value来创建一个Access
	 * 
	 * @param value
	 * @return
	 */
	public static Accessor create(Source value) {
		return new SourceAccess<>(value);
	}

	/**
	 * 通过value返回一个Access，如果本身是一个Access那么返回自身
	 * 
	 * @param value
	 * @return
	 */
	public static Accessor of(Source value) {
		if (value instanceof Accessor) {
			return (Accessor) value;
		}
		return create(value);
	}

	public static class SourceAccess<W extends Source> implements Accessor, SourceWrapper<W> {
		private final W source;
		private volatile StandardSource<W> holder;

		public SourceAccess(@NonNull W source) {
			this.source = source;
		}

		@Override
		public W getSource() {
			return source;
		}

		public StandardSource<W> getHolder() {
			if (holder == null) {
				synchronized (this) {
					if (holder == null) {
						holder = new StandardSource<>(source);
						holder.setValue(source.get());
					}
				}
			}
			return holder;
		}

		@Override
		public Object get() throws ConversionException {
			return getHolder().get();
		}

		@Override
		public void set(Object value) throws UnsupportedOperationException {
			this.getHolder().setValue(value);
		}
	}

	/**
	 * 是否可读
	 * 
	 * @return
	 */
	default boolean isReadable() {
		return true;
	}

	/**
	 * 是否可写
	 * 
	 * @return
	 */
	default boolean isWriteable() {
		return true;
	}

	/**
	 * 设置
	 * 
	 * @param value
	 * @throws UnsupportedOperationException 只读属性,不能操作
	 */
	void set(Object value) throws UnsupportedOperationException;
}
