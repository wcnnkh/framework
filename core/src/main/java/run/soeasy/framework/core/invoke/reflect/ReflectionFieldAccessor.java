package run.soeasy.framework.core.invoke.reflect;

import java.lang.reflect.Field;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.transform.indexed.IndexedAccessor;

@Getter
@Setter
public class ReflectionFieldAccessor extends ReflectionField implements IndexedAccessor {
	private static final long serialVersionUID = 1L;
	private Object target;

	public ReflectionFieldAccessor(@NonNull Field member) {
		super(member);
	}

	@Override
	public Object get() throws ConversionException {
		return readFrom(target);
	}

	@Override
	public void set(Object value) {
		writeTo(value, target);
	}

	@Override
	public Object getIndex() {
		return getName();
	}

}
