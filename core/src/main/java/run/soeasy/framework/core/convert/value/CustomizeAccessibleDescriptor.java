package run.soeasy.framework.core.convert.value;

import java.io.Serializable;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;

@Data
public class CustomizeAccessibleDescriptor implements AccessibleDescriptor, Serializable {
	private static final long serialVersionUID = 1L;
	@NonNull
	private TypeDescriptor returnTypeDescriptor;
	@NonNull
	private TypeDescriptor requiredTypeDescriptor;
	private boolean requried = false;
	private boolean readable = true;
	private boolean writeable = true;

	public CustomizeAccessibleDescriptor(@NonNull TypeDescriptor typeDescriptor) {
		this.requiredTypeDescriptor = typeDescriptor;
		this.returnTypeDescriptor = typeDescriptor;
	}
}