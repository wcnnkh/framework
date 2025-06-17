package run.soeasy.framework.core.convert.value;

import lombok.Getter;
import lombok.Setter;
import run.soeasy.framework.core.convert.Converter;

@Getter
@Setter
public class CustomizeTypedValueAccessor extends CustomizeTypedDataAccessor<Object> implements TypedValueAccessor {
	private Converter converter = Converter.assignable();
}
