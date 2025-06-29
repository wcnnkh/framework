package run.soeasy.framework.json;

import run.soeasy.framework.core.convert.strings.StringConverter;

/**
 * JSON转换器
 * 
 * @author soeasy.run
 *
 */
public interface JsonConverter extends StringConverter<Object> {

	/**
	 * 将对象转成一个JSON元素，默认的实现性能较差，建议子类重写此方法
	 * 
	 * @param json
	 * @return
	 */
	default JsonElement toJsonElement(Object json) {
		if (json instanceof JsonElement) {
			return (JsonElement) json;
		}
		return new ConvertibleJsonElement(this, json);
	}
}
