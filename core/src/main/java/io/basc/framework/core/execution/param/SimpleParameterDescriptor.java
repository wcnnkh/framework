package io.basc.framework.core.execution.param;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.ParameterDescriptor;
import io.basc.framework.util.Item;
import io.basc.framework.util.SimpleItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class SimpleParameterDescriptor extends SimpleItem implements ParameterDescriptor {
	public static final TypeDescriptor DEFAULT_TYPE_DESCRIPTOR = TypeDescriptor.valueOf(Object.class);
	private TypeDescriptor typeDescriptor;

	public SimpleParameterDescriptor(Item item) {
		super(item);
	}

	public SimpleParameterDescriptor(ParameterDescriptor parameterDescriptor) {
		this((Item) parameterDescriptor);
		this.typeDescriptor = parameterDescriptor.getTypeDescriptor();
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return typeDescriptor == null ? DEFAULT_TYPE_DESCRIPTOR : typeDescriptor;
	}
}
