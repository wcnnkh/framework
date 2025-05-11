package run.soeasy.framework.core.invoke.field;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.invoke.field.FieldDescriptor.FieldDescriptorWrapper;
import run.soeasy.framework.core.transform.indexed.IndexedAccessor;

@RequiredArgsConstructor
@Getter
@Setter
public class FieldAccessor<W extends FieldDescriptor> implements IndexedAccessor, FieldDescriptorWrapper<W> {
	@NonNull
	private final W source;
	private Object target;

	@Override
	public Object get() throws ConversionException {
		return source.readFrom(target);
	}

	@Override
	public void set(Object value) {
		source.writeTo(value, target);
	}

	@Override
	public IndexedAccessor reindex(Object index) {
		return IndexedAccessor.super.reindex(index);
	}
}
