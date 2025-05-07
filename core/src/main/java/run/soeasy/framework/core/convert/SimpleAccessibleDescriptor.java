package run.soeasy.framework.core.convert;

import java.io.Serializable;

import lombok.Data;
import lombok.NonNull;

@Data
public class SimpleAccessibleDescriptor implements AccessibleDescriptor, Serializable {
	private static final long serialVersionUID = 1L;
	@NonNull
	private TypeDescriptor returnTypeDescriptor;
	@NonNull
	private TypeDescriptor requiredTypeDescriptor;
	private boolean requried = false;
	private boolean readable = true;
	private boolean writeable = true;

	public SimpleAccessibleDescriptor(@NonNull TypeDescriptor typeDescriptor) {
		this.requiredTypeDescriptor = typeDescriptor;
		this.returnTypeDescriptor = typeDescriptor;
	}
}