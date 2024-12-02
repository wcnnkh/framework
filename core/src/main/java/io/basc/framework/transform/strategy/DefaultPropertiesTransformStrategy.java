package io.basc.framework.transform.strategy;

import java.util.List;
import java.util.stream.Collectors;

import io.basc.framework.core.convert.ConversionService;
import io.basc.framework.core.convert.IdentityConversionService;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.param.ConfigurableParameterDescriptorPredicate;
import io.basc.framework.transform.Properties;
import io.basc.framework.transform.Property;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Elements;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class DefaultPropertiesTransformStrategy extends ConfigurableParameterDescriptorPredicate
		implements PropertiesTransformStrategy {
	@NonNull
	private ConversionService conversionService = new IdentityConversionService();

	private Elements<String> getAliasNames(PropertiesTransformContext context, Property property) {
		if (context == null) {
			return Elements.singleton(property.getName()).concat(property.getAliasNames());
		}

		Elements<Property> fields = context.getElements().reverse().concat(Elements.singleton(property));
		// 组合出所有的名称
		List<Elements<String>> recursionNames = CollectionUtils.recursiveComposition(
				fields.map((e) -> Elements.singleton(e.getName()).concat(e.getAliasNames())).toList());
		return Elements.of(
				recursionNames.stream().map((e) -> e.collect(Collectors.joining("."))).collect(Collectors.toList()));
	}

	@Override
	public void doTransform(PropertiesTransformContext sourceContext, Properties sourceProperties,
			TypeDescriptor sourceTypeDescriptor, Property sourceProperty, PropertiesTransformContext targetContext,
			Properties targetProperties, TypeDescriptor targetTypeDescriptor) {
		if (!sourceProperty.isPresent()) {
			return;
		}

		if (!test(sourceTypeDescriptor, sourceProperty)) {
			// 不通过
			return;
		}

		Elements<String> names = getAliasNames(sourceContext, sourceProperty);
		Elements<Property> targetElements = names.map((name) -> targetProperties.getElements(name))
				.map((es) -> es.filter((property) -> !property.isReadOnly())).filter((e) -> !e.isEmpty()).first();
		// 如果使用名称匹配不到那么使用位置匹配
		if (targetElements.isEmpty()) {
			Property targetProperty = targetProperties.getElement(sourceProperty.getPositionIndex());
			if (targetProperty != null) {
				targetElements = Elements.singleton(targetProperty);
			}
		}

		targetElements.forEach((targetProperty) -> {
			if (conversionService.canConvert(sourceProperty.getTypeDescriptor(),
					targetProperty.getRequiredTypeDescriptor())) {
				Object value = conversionService.convert(sourceProperty.getValue(),
						sourceProperty.getRequiredTypeDescriptor(), targetProperty.getTypeDescriptor());
				targetProperty.setValue(value);
				return;
			}
		});
	}
}
