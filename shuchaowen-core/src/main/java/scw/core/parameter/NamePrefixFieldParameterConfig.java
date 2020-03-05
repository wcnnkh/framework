package scw.core.parameter;

import java.lang.annotation.Annotation;

import scw.core.utils.StringUtils;

public final class NamePrefixFieldParameterConfig extends FieldParameterConfig {
	private FieldParameterConfig fieldParameterConfig;
	private String name;

	public NamePrefixFieldParameterConfig(
			FieldParameterConfig fieldParameterConfig, String namePrefix) {
		super(fieldParameterConfig.getField());
		this.fieldParameterConfig = fieldParameterConfig;
		this.name = StringUtils.isEmpty(namePrefix) ? fieldParameterConfig
				.getName() : (namePrefix + fieldParameterConfig.getName());
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> type) {
		return fieldParameterConfig.getAnnotation(type);
	}

	@Override
	public String getName() {
		return name;
	}

	public String getBaseName() {
		return fieldParameterConfig.getName();
	}
}
