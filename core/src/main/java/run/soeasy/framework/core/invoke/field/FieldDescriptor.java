package run.soeasy.framework.core.invoke.field;

import run.soeasy.framework.core.transform.indexed.IndexedDescriptor;

public interface FieldDescriptor extends IndexedDescriptor, FieldValueReader, FieldValueWriter {
	public static interface FieldDescriptorWrapper<W extends FieldDescriptor> extends FieldDescriptor,
			IndexedDescriptorWrapper<W>, FieldValueReaderWrapper<W>, FieldValueWriterWrapper<W> {
	}
}
