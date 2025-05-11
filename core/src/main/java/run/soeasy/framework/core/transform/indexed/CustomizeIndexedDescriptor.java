package run.soeasy.framework.core.transform.indexed;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import run.soeasy.framework.core.convert.CustomizeAccessibleDescriptor;
import run.soeasy.framework.core.convert.TypeDescriptor;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CustomizeIndexedDescriptor extends CustomizeAccessibleDescriptor implements IndexedDescriptor {
	private static final long serialVersionUID = 1L;
	private Object index;

	public CustomizeIndexedDescriptor(@NonNull TypeDescriptor typeDescriptor) {
		super(typeDescriptor);
	}

}