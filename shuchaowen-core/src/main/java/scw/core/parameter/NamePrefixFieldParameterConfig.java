package scw.core.parameter;

import java.lang.reflect.AnnotatedElement;

import scw.core.utils.StringUtils;

public final class NamePrefixFieldParameterConfig extends FieldParameterDescriptor {
	private FieldParameterDescriptor fieldParameterConfig;
	private String name;

	public NamePrefixFieldParameterConfig(
			FieldParameterDescriptor fieldParameterConfig, String namePrefix) {
		super(fieldParameterConfig.getField());
		this.fieldParameterConfig = fieldParameterConfig;
		this.name = StringUtils.isEmpty(namePrefix) ? fieldParameterConfig
				.getName() : (namePrefix + fieldParameterConfig.getName());
	}
	
	@Override
	public AnnotatedElement getAnnotatedElement() {
		return fieldParameterConfig.getAnnotatedElement();
	}

	@Override
	public String getName() {
		return name;
	}

	public String getBaseName() {
		return fieldParameterConfig.getName();
	}
}
