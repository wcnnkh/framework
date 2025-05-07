package run.soeasy.framework.core.convert.property;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import run.soeasy.framework.core.convert.SimpleAccessibleDescriptor;
import run.soeasy.framework.core.convert.TypeDescriptor;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SimplePropertyDescriptor extends SimpleAccessibleDescriptor implements PropertyDescriptor {
	private static final long serialVersionUID = 1L;
	private String name;

	public SimplePropertyDescriptor(@NonNull TypeDescriptor typeDescriptor) {
		super(typeDescriptor);
	}

}
