package io.basc.framework.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.basc.framework.core.annotation.Order;
import io.basc.framework.env.Sys;
import io.basc.framework.json.AbstractJsonSupport;
import io.basc.framework.json.JsonException;
import io.basc.framework.json.JsonSupport;
import io.basc.framework.json.JsonElement;

public class JacksonJSONSupport extends AbstractJsonSupport {
	public static final JsonSupport INSTANCE = new JacksonJSONSupport();

	private ObjectMapper mapper;

	@Order
	public JacksonJSONSupport() {
		this.mapper = Sys.getEnv().getServiceLoader(ObjectMapper.class).first();
		if (mapper == null) {
			mapper = new ObjectMapper();
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
		}
	}

	public JacksonJSONSupport(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public JsonElement parseJson(String text) {
		JsonNode jsonNode;
		try {
			jsonNode = mapper.readTree(text);
		} catch (JsonMappingException e) {
			throw new JsonException(e);
		} catch (JsonProcessingException e) {
			throw new JsonException(e);
		}
		return new JacksonJsonElement(jsonNode, mapper);
	}

	@Override
	protected String toJsonStringInternal(Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new JsonException(e);
		}
	}

}
