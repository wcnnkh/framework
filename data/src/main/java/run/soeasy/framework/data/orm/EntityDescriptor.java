package run.soeasy.framework.data.orm;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.mapping.property.PropertyDescriptor;
import run.soeasy.framework.core.streaming.Streamable;

@Getter
@RequiredArgsConstructor
public class EntityDescriptor<T extends PropertyDescriptor> {
	@NonNull
	private final String name;
	@NonNull
	private final Streamable<String> alias;
	@NonNull
	private final TypeDescriptor typeDescriptor;
	@NonNull
	private final Streamable<ColumnDescriptor<T>> columns;
}
