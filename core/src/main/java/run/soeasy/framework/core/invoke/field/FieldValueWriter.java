package run.soeasy.framework.core.invoke.field;

import run.soeasy.framework.core.convert.value.TargetDescriptor;

public interface FieldValueWriter extends TargetDescriptor {
	public static interface FieldValueWriterWrapper<W extends FieldValueWriter>
			extends FieldValueWriter, TargetDescriptorWrapper<W> {
		@Override
		default void writeTo(Object value, Object target) {
			getSource().writeTo(value, target);
		}
	}

	void writeTo(Object value, Object target);
}
