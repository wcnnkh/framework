package run.soeasy.framework.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * 基于Jackson的XML格式处理类，继承{@link XmlMapper}并实现{@link JacksonFormat}接口，
 * 提供XML数据的序列化与反序列化能力，包含默认配置以适配常见XML处理场景。
 * 
 * <p>核心特性：
 * <ul>
 *   <li>默认配置：包含一系列开箱即用的XML处理规则（如忽略未知属性、排除null值字段等）</li>
 *   <li>格式统一：实现{@link JacksonFormat}接口，提供与JSON格式处理一致的接口规范</li>
 *   <li>可复制性：支持实例复制，便于基于默认配置扩展自定义规则</li>
 * </ul>
 * 
 * <p>默认配置通过{@link #defaultProperties()}方法设置，主要包括：
 * <ul>
 *   <li>忽略反序列化时的未知属性（避免因字段不匹配导致的异常）</li>
 *   <li>序列化时排除null值字段（精简输出）</li>
 *   <li>日期字段序列化为时间戳（统一日期格式）</li>
 *   <li>允许单引号作为字符串分隔符（兼容更多XML格式）</li>
 *   <li>忽略属性名大小写（提高解析容错性）</li>
 * </ul>
 * 
 * @see XmlMapper
 * @see JacksonFormat
 * @see JsonFormat
 */
public class XmlFormat extends XmlMapper implements JacksonFormat {
	/**
	 * 默认XML格式处理实例，预配置了{@link #defaultProperties()}中的规则，
	 * 可直接用于大多数XML序列化/反序列化场景，避免重复配置。
	 */
	public static final XmlFormat DEFAULT = new XmlFormat();

	/**
	 * 序列化版本号，用于对象序列化时的版本控制，确保序列化与反序列化的兼容性。
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 无参构造方法，初始化XML映射器并应用默认配置。
	 * <p>调用{@link #defaultProperties()}方法设置默认的序列化/反序列化规则。
	 */
	public XmlFormat() {
		super();
		defaultProperties();
	}

	/**
	 * 基于已有{@link XmlMapper}实例创建XmlFormat对象。
	 * <p>用于在已有XML映射器配置的基础上扩展当前类的功能。
	 * 
	 * @param xmlMapper 已有的XmlMapper实例，不可为null
	 */
	public XmlFormat(XmlMapper xmlMapper) {
		super(xmlMapper);
	}

	/**
	 * 复制当前XmlFormat实例，创建一个新的实例并继承当前的配置。
	 * <p>用于基于默认配置创建自定义配置的实例，避免修改默认实例影响全局。
	 * 
	 * @return 新的XmlFormat实例，与当前实例配置一致
	 */
	@Override
	public XmlFormat copy() {
		_checkInvalidCopy(XmlFormat.class);
		return new XmlFormat(this);
	}

	/**
	 * 设置XML序列化/反序列化的默认属性，定义通用处理规则。
	 * <p>该方法在无参构造中自动调用，配置内容包括：
	 * <ul>
	 *   <li>{@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES}：关闭，忽略未知属性</li>
	 *   <li>{@link JsonInclude.Include#NON_NULL}：序列化时排除null值字段</li>
	 *   <li>{@link SerializationFeature#WRITE_DATE_KEYS_AS_TIMESTAMPS}：日期字段序列化为时间戳</li>
	 *   <li>{@link SerializationFeature#FAIL_ON_EMPTY_BEANS}：关闭，允许空对象序列化</li>
	 *   <li>{@link JsonParser.Feature#ALLOW_SINGLE_QUOTES}：允许单引号作为字符串分隔符</li>
	 * </ul>
	 */
	public void defaultProperties() {
		configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		setSerializationInclusion(JsonInclude.Include.NON_NULL);
		configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, true);
		configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
	}

	/**
	 * 获取当前实例作为{@link ObjectMapper}，实现{@link JacksonFormat}接口的规范方法。
	 * <p>用于统一格式处理接口，使XML和JSON格式处理可通过相同接口调用。
	 * 
	 * @return 当前XmlFormat实例（本身继承自ObjectMapper）
	 */
	@Override
	public ObjectMapper getObjectMapper() {
		return this;
	}
}