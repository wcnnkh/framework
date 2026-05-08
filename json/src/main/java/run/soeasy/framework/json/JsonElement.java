package run.soeasy.framework.json;

import java.io.IOException;
import java.util.Iterator;
import java.util.stream.Stream;

import run.soeasy.framework.buffer.JsonEscaping;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.io.Exportable;

/**
 * JSON元素的抽象根接口，定义所有JSON元素的通用行为契约，是整个JSON类型体系的顶层抽象。
 * <p>
 * 所有JSON相关元素（对象、数组、基本类型、空值）都必须实现此接口，用于统一JSON数据的操作标准，
 * 消除类型强制转换的安全风险，提供高效、合规的序列化能力。
 * </p>
 * 
 * <h3>核心设计目标</h3>
 * <ul>
 * <li>类型安全保障：提供统一的类型判定（如{@link #isJsonObject()}）和安全转换（如{@link #getAsJsonObject()}）方法，
 * 避免直接强制类型转换导致的{@link ClassCastException}，简化异常排查</li>
 * <li>高效序列化整合：继承{@link Exportable}接口，支持流式导出JSON数据，兼顾内存序列化（如{@link StringBuilder}）
 * 和文件/网络流式写入（如{@link java.io.FileWriter}），相比传统字节数组序列化更节省内存</li>
 * <li>语法合规保障：内置标准化的JSON字符串序列化方法（{@link #toJsonString()}），自动处理字符串转义、
 * 语法格式校验，确保输出结果严格遵循 <a href="https://datatracker.ietf.org/doc/html/rfc8259">RFC
 * 8259 JSON规范</a></li>
 * <li>扩展性兼容：默认方法实现核心通用逻辑，子类可按需重写，既保证接口的规范性，又保留实现类的灵活扩展空间</li>
 * </ul>
 * 
 * <h3>JSON元素类型体系</h3> 该接口定义了四类基础JSON元素类型，所有实现类必须归属其中一类，类型判定与转换通过接口默认方法统一实现：
 * <ul>
 * <li>{@link JsonObject}：JSON对象，无序字符串键值对集合（键唯一，值为任意{@link JsonElement}类型）</li>
 * <li>{@link JsonArray}：JSON数组，有序{@link JsonElement}元素集合（允许重复元素，索引从0开始）</li>
 * <li>{@link JsonPrimitive}：JSON基本类型，包含字符串（String）、数字（Integer/Long/Double等）、布尔值（Boolean）
 * 三种基础数据类型，不包含复杂结构</li>
 * <li>{@link JsonNull}：JSON显式空值，独立于Java的{@code null}，用于表示JSON数据中的显式null节点（如{"key":
 * null}）</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see JsonObject
 * @see JsonArray
 * @see JsonPrimitive
 * @see JsonNull
 * @see Exportable
 */
public interface JsonElement extends Exportable {
	public static final char BEGIN_OBJECT = '{';
	public static final char END_OBJECT = '}';
	public static final char BEGIN_ARRAY = '[';
	public static final char END_ARRAY = ']';
	public static final char NAME_SEPARATOR = ':';
	public static final char VALUE_SEPARATOR = ',';
	public static final char QUOTE = '"';
	public static final char SPACE = ' ';

	/**
	 * 判断当前JSON元素是否为{@link JsonObject}（JSON对象）类型
	 * <p>
	 * 默认实现基于{@code instanceof}关键字进行类型判定，无额外对象创建和性能损耗，
	 * 可安全用于循环、条件分支等高频执行场景，子类可按需重写但不推荐（避免破坏类型判定一致性）。
	 * </p>
	 *
	 * @return {@code true} - 当前元素是{@link JsonObject}的实例；{@code false} -
	 *         当前元素非JSON对象类型
	 */
	default boolean isJsonObject() {
		return this instanceof JsonObject;
	}

	/**
	 * 将当前JSON元素安全转换为{@link JsonObject}类型
	 * <p>
	 * 转换前会通过{@code instanceof}进行类型校验，校验通过则直接返回转换后的实例；
	 * 校验失败时抛出明确的{@link IllegalStateException}，异常信息包含当前元素的实际类型和字符串表示， 便于快速定位类型不匹配问题。
	 * </p>
	 * <p>
	 * 推荐使用方式：先通过{link #isJsonObject()}判定类型，再调用此方法进行转换，确保类型安全。
	 * </p>
	 *
	 * @return 当前元素对应的{@link JsonObject}实例（非null，转换成功必然返回有效实例）
	 * @throws IllegalStateException 当当前元素不是{@link JsonObject}类型时抛出，包含具体的类型不匹配描述
	 */
	default JsonObject getAsJsonObject() {
		if (this instanceof JsonObject) {
			return (JsonObject) this;
		}
		throw new IllegalStateException("Not a JSON Object: " + this);
	}

	/**
	 * 判断当前JSON元素是否为{@link JsonArray}（JSON数组）类型
	 * <p>
	 * 默认实现基于{@code instanceof}关键字进行类型判定，无额外性能损耗，可直接用于高频执行场景，
	 * 子类无需重写，保证跨实现类的类型判定逻辑一致。
	 * </p>
	 *
	 * @return {@code true} - 当前元素是{@link JsonArray}的实例；{@code false} -
	 *         当前元素非JSON数组类型
	 */
	default boolean isJsonArray() {
		return this instanceof JsonArray;
	}

	/**
	 * 将当前JSON元素安全转换为{@link JsonArray}类型
	 * <p>
	 * 内置类型校验逻辑，避免强制类型转换导致的{@link ClassCastException}，校验失败时抛出的异常包含 当前元素的实际信息，便于问题排查。
	 * </p>
	 * <p>
	 * 推荐使用方式：先通过{@link #isJsonArray()}判定类型，再调用此方法转换，确保无异常抛出。
	 * </p>
	 *
	 * @return 当前元素对应的{@link JsonArray}实例（非null，转换成功必然返回有效实例）
	 * @throws IllegalStateException 当当前元素不是{@link JsonArray}类型时抛出，包含具体的类型不匹配描述
	 */
	default JsonArray getAsJsonArray() {
		if (this instanceof JsonArray) {
			return (JsonArray) this;
		}
		throw new IllegalStateException("Not a JSON Array: " + this);
	}

	/**
	 * 判断当前JSON元素是否为{@link JsonNull}（JSON显式空值）类型
	 * <p>
	 * 默认实现基于{@code instanceof}关键字进行类型判定，无额外性能损耗，可直接用于各类场景，
	 * 用于区分Java的{@code null}和JSON的显式null节点。
	 * </p>
	 *
	 * @return {@code true} - 当前元素是{@link JsonNull}的实例；{@code false} -
	 *         当前元素非JSON显式空值类型
	 */
	default boolean isJsonNull() {
		return this instanceof JsonNull;
	}

	/**
	 * 将当前JSON元素安全转换为{@link JsonNull}类型
	 * <p>
	 * 内置类型校验逻辑，转换失败时抛出的异常包含当前元素的字符串表示，便于快速定位问题。
	 * </p>
	 * <p>
	 * 推荐使用方式：先通过{@link #isJsonNull()}判定类型，再调用此方法转换，确保类型安全。
	 * </p>
	 *
	 * @return 当前元素对应的{@link JsonNull}实例（非null，转换成功必然返回有效实例）
	 * @throws IllegalStateException 当当前元素不是{@link JsonNull}类型时抛出，包含具体的类型不匹配描述
	 */
	default JsonNull getAsJsonNull() {
		if (this instanceof JsonNull) {
			return (JsonNull) this;
		}
		throw new IllegalStateException("Not a JSON Null: " + this);
	}

	/**
	 * 判断当前JSON元素是否为{@link JsonPrimitive}（JSON基本类型）类型
	 * <p>
	 * JSON基本类型仅包含字符串、数字、布尔值三种基础数据类型，不包含复杂结构（对象、数组）和空值，
	 * 默认实现基于{@code instanceof}关键字判定，无额外性能损耗，可安全用于高频场景。
	 * </p>
	 *
	 * @return {@code true} - 当前元素是{@link JsonPrimitive}的实例；{@code false} -
	 *         当前元素非JSON基本类型
	 */
	default boolean isJsonPrimitive() {
		return this instanceof JsonPrimitive;
	}

	/**
	 * 将当前JSON元素安全转换为{@link JsonPrimitive}类型
	 * <p>
	 * 内置类型校验逻辑，避免强制类型转换的安全风险，转换失败时抛出的异常包含当前元素的实际信息， 便于快速排查问题。
	 * </p>
	 * <p>
	 * 推荐使用方式：先通过{@link #isJsonPrimitive()}判定类型，再调用此方法转换，确保无异常抛出。
	 * </p>
	 *
	 * @return 当前元素对应的{@link JsonPrimitive}实例（非null，转换成功必然返回有效实例）
	 * @throws IllegalStateException 当当前元素不是{@link JsonPrimitive}类型时抛出，包含具体的类型不匹配描述
	 */
	default JsonPrimitive getAsJsonPrimitive() {
		if (this instanceof JsonPrimitive) {
			return (JsonPrimitive) this;
		}
		throw new IllegalStateException("Not a JSON Primitive: " + this);
	}

	/**
	 * 将当前JSON元素序列化为标准合规的JSON格式字符串
	 * <p>
	 * 该方法基于{link Exportable#export(Appendable)}实现流式序列化，相比传统拼接字符串的方式，
	 * 具有更高的效率和更低的内存占用，同时自动处理各类特殊字符转义（如双引号、反斜杠、换行符等）， 确保输出结果符合RFC 8259 JSON规范。
	 * </p>
	 * <p>
	 * 因内部使用{@link StringBuilder}（内存缓冲区）进行序列化，理论上不会抛出{@link IOException}，
	 * 若意外抛出该异常，将被包装为{@link IllegalStateException}（非受检异常），避免上层代码强制处理不必要的受检异常。
	 * </p>
	 * <p>
	 * 不同类型元素的返回值说明：
	 * <ul>
	 * <li>{@link JsonObject}：返回带大括号的键值对字符串（如{"name":"soeasy","age":18}）</li>
	 * <li>{@link JsonArray}：返回带中括号的元素列表字符串（如[1,"test",true,null]）</li>
	 * <li>{@link JsonPrimitive}：字符串类型返回带双引号的转义字符串，数字/布尔值返回原始字符串表示（如"abc"、123、true）</li>
	 * <li>{@link JsonNull}：返回字符串"null"（符合JSON规范的显式空值表示）</li>
	 * </ul>
	 * </p>
	 *
	 * @return 标准化、合规的JSON格式字符串（非null，即使是空元素也会返回对应默认值，如空对象"{}"、空数组"[]"）
	 * @throws IllegalStateException 当序列化过程中意外发生{@link IOException}时抛出，包含原始异常信息，便于问题排查
	 */
	default String toJsonString() {
		StringBuilder sb = new StringBuilder();
		try {
			export(sb);
		} catch (IOException e) {
			throw new IllegalStateException("export error", e);
		}
		return sb.toString();
	}

	@Override
	default void export(Appendable target) throws IOException {
		if (isJsonObject()) {
			target.append(BEGIN_OBJECT);
			try (Stream<KeyValue<String, JsonElement>> stream = getAsJsonObject().stream()) {
				Iterator<KeyValue<String, JsonElement>> iterator = stream.iterator();
				while (iterator.hasNext()) {
					KeyValue<String, JsonElement> keyValue = iterator.next();
					CharSequence key = JsonEscaping.INSTANCE.encode(keyValue.getKey());
					target.append('"').append(key).append("\": ");
					keyValue.getValue().export(target);
					if (iterator.hasNext()) {
						target.append(", ");
					}
				}
			} finally {
				target.append(END_OBJECT);
			}
		} else if (isJsonArray()) {
			target.append(BEGIN_ARRAY);
			try (Stream<JsonElement> stream = getAsJsonArray().stream()) {
				Iterator<JsonElement> iterator = stream.iterator();
				while (iterator.hasNext()) {
					JsonElement jsonElement = iterator.next();
					jsonElement.export(target);
					if (iterator.hasNext()) {
						target.append(", ");
					}
				}
			} finally {
				target.append(END_ARRAY);
			}
		} else if (isJsonPrimitive()) {
			JsonPrimitive jsonPrimitive = getAsJsonPrimitive();
			CharSequence value = jsonPrimitive.getAsString();
			value = JsonEscaping.INSTANCE.encode(value);
			if (jsonPrimitive.isCharSequence()) {
				target.append('"').append(value).append('"');
			} else {
				target.append(value);
			}
		} else if (isJsonNull()) {
			target.append("null");
		}
	}
}