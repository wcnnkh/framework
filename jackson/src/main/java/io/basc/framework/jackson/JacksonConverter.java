package io.basc.framework.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.basc.framework.beans.factory.spi.SPI;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.json.JsonConverter;
import io.basc.framework.json.JsonException;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class JacksonConverter implements JsonConverter {
	@NonNull
	private final ObjectMapper mapper;

	public JacksonConverter() {
		this(SPI.getServices(ObjectMapper.class).findFirst().orElseGet(() -> {
			ObjectMapper mapper = new ObjectMapper();
			// 对于空的对象转json的时候不抛出错误
			mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
			// 允许属性名称没有引号
			mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
			// 允许单引号
			mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
			// 设置输入时忽略在json字符串中存在但在java对象实际没有的属性
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			// 设置输出时包含属性的风格
			mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			return mapper;
		}));
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) throws JsonException {
		JavaType javaType = mapper.constructType(targetType.getResolvableType().getType());
		return mapper.convertValue(source, javaType);
	}
}
