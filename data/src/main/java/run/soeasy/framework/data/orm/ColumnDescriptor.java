package run.soeasy.framework.data.orm;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.mapping.property.PropertyDescriptor;
import run.soeasy.framework.core.streaming.Streamable;

@RequiredArgsConstructor
@Getter
public class ColumnDescriptor<T extends PropertyDescriptor> {
	@NonNull
	private final T property;
	@NonNull
	private final String name;
	
	private final boolean primaryKey;
	
	@NonNull
	private final Streamable<String> alias;
}
