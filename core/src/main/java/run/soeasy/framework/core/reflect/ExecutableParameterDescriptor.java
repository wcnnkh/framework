package run.soeasy.framework.core.reflect;

import java.lang.reflect.Parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.indexed.IndexedDescriptor;

@Data
@EqualsAndHashCode(of = "parameter")
@ToString(of = "parameter")
public class ExecutableParameterDescriptor implements IndexedDescriptor {
	@NonNull
	private final Parameter parameter;
	private volatile TypeDescriptor typeDescriptor;

	public TypeDescriptor getTypeDescriptor() {
		if (typeDescriptor == null) {
			synchronized (this) {
				if (typeDescriptor == null) {
					typeDescriptor = TypeDescriptor.forParameter(parameter);
				}
			}
		}
		return typeDescriptor;
	}

	@Override
	public Object getIndex() {
		return parameter.getName();
	}

	@Override
	public final TypeDescriptor getReturnTypeDescriptor() {
		return getReturnTypeDescriptor();
	}

	@Override
	public final TypeDescriptor getRequiredTypeDescriptor() {
		return getRequiredTypeDescriptor();
	}

}
