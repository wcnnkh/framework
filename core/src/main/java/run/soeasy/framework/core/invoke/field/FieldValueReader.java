package run.soeasy.framework.core.invoke.field;

import run.soeasy.framework.core.convert.value.SourceDescriptor;

public interface FieldValueReader extends SourceDescriptor {
	public static interface FieldValueReaderWrapper<W extends FieldValueReader>
			extends FieldValueReader, SourceDescriptorWrapper<W> {
		@Override
		default Object readFrom(Object target) {
			return getSource().readFrom(target);
		}
	}

	Object readFrom(Object target);
}
