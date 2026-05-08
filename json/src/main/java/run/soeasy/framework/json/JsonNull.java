package run.soeasy.framework.json;

import java.io.Serializable;

/**
 * JSON空值类型实现类，实现{@link JsonElement}接口，用于表示JSON中的"null"值，
 * 采用单例模式确保全局唯一的null值实例，避免不必要的对象创建。
 * 
 * <p>
 * 该类通过重写{@link #export(Appendable)}方法输出"null"字符串， 并统一JSON
 * null值的序列化行为，确保在JSON处理中null值的一致性。
 * 
 * @author soeasy.run
 * @see JsonElement
 */
public final class JsonNull extends JsonValue implements JsonElement, Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * JSON null值的单例实例，全局唯一
	 */
	public static final JsonNull INSTANCE = new JsonNull();

	/**
	 * 私有构造方法，确保只能通过{@link #INSTANCE}获取实例（单例模式）
	 */
	private JsonNull() {
		super("null", JsonToken.NULL);
	}

	/**
	 * 重写类型判定方法：明确当前元素是JSON空值类型（直接返回true，比默认instanceof更高效）
	 */
	@Override
	public boolean isJsonNull() {
		return true;
	}

	/**
	 * 重写安全转换方法：直接返回自身单例，避免默认的类型校验逻辑，提升性能
	 */
	@Override
	public JsonNull getAsJsonNull() {
		return this;
	}

	/**
	 * 单例相等性判断：仅与自身实例相等，保证JSON null值的全局唯一性
	 * 
	 * @param obj 待比较的对象
	 * @return true - 仅当obj为{@link #INSTANCE}时；false - 其他所有情况
	 */
	@Override
	public boolean equals(Object obj) {
		return obj == this;
	}

	/**
	 * 单例哈希值：基于对象身份生成哈希码，保证哈希表中存储的唯一性
	 */
	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	/**
	 * 返回JSON null值的字符串表示（即"null"），与序列化结果一致
	 * 
	 * @return "null"字符串
	 */
	@Override
	public String toString() {
		return toJsonString();
	}

	/**
	 * 反序列化时返回单例实例（防止反序列化破坏单例模式）
	 * 
	 * @return 全局唯一的{@link #INSTANCE}
	 */
	private Object readResolve() {
		return INSTANCE;
	}
}