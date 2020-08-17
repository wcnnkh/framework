package scw.core.parameter;

public class RenameParameterDescriptor extends ParameterDescriptorWrapper {
	private final String name;

	public RenameParameterDescriptor(ParameterDescriptor parameterDescriptor,
			String name) {
		super(parameterDescriptor);
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
