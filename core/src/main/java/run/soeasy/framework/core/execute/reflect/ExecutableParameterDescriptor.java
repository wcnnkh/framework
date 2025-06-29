package run.soeasy.framework.core.execute.reflect;

import java.lang.reflect.Parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.property.PropertyDescriptor;

@Data
@EqualsAndHashCode(of = "parameter")
@ToString(of = "parameter")
public class ExecutableParameterDescriptor implements PropertyDescriptor {
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
	public String getName() {
		return parameter.getName();
	}

	@Override
	public final TypeDescriptor getReturnTypeDescriptor() {
		return getTypeDescriptor();
	}

	@Override
	public final TypeDescriptor getRequiredTypeDescriptor() {
		return getTypeDescriptor();
	}

}
