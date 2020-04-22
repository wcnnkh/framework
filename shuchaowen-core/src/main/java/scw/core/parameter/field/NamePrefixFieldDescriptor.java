package scw.core.parameter.field;

import scw.core.utils.StringUtils;

public class NamePrefixFieldDescriptor extends FieldDescriptorWrapper {
	private String namePrefix;

	public NamePrefixFieldDescriptor(FieldDescriptor fieldDescriptor, String namePrefix) {
		super(fieldDescriptor);
		this.namePrefix = namePrefix;
	}

	public String getNamePrefix() {
		return namePrefix;
	}

	public void setNamePrefix(String namePrefix) {
		this.namePrefix = namePrefix;
	}

	@Override
	public String getDisplayName() {
		if (StringUtils.isEmpty(namePrefix)) {
			return super.getDisplayName();
		}

		return namePrefix + super.getDisplayName();
	}
}
