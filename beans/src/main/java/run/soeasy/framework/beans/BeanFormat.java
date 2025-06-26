package run.soeasy.framework.beans;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.codec.format.KeyValueFormat;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.transform.property.TypedProperties;
import run.soeasy.framework.core.transform.templates.Mapping;

@Getter
@Setter
public class BeanFormat extends KeyValueFormat {

	public BeanFormat(@NonNull CharSequence delimiter, @NonNull CharSequence connector,
			@NonNull Codec<String, String> keyCodec, @NonNull Codec<String, String> valueCodec) {
		super(delimiter, connector, keyCodec, valueCodec);
		getKeyValueMapper().getMappingProvider().register(Object.class, (bean, type) -> {
			TypedProperties typedProperties = BeanMapper.getInstane().getMapping(bean, type);
			Mapping<Object, TypedValueAccessor> mapping = () -> typedProperties.getElements()
					.map((e) -> KeyValue.of(e.getKey(), e.getValue()));
			return mapping;
		});
	}

	
}
