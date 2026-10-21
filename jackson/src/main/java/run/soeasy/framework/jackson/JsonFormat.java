package run.soeasy.framework.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

import lombok.Getter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.strings.StringFormat;
import run.soeasy.framework.io.AppendableWriter;
import run.soeasy.framework.io.ReadableReader;
import run.soeasy.framework.json.JsonConverter;

/**
 * 基于Jackson的JSON格式化工具，继承自{@link JsonMapper}并实现双向转换接口，
 * 提供标准化的JSON序列化与反序列化功能。该类预配置了工业级常用参数，
 * 并通过静态实例{@link #DEFAULT}和{@link #SNAKE_CASE}提供两种典型配置方案。
 * 
 * <h2>核心特性</h2>
 * <ul>
 * <li><strong>兼容性配置</strong>：忽略未知属性、支持单引号和大小写不敏感属性</li>
 * <li><strong>数据精简</strong>：自动过滤null值字段，避免冗余数据传输</li>
 * <li><strong>类型安全</strong>：通过{@link TypeDescriptor}实现泛型类型精确转换</li>
 * <li><strong>流式处理</strong>：支持{@link ReadableReader}/{@link AppendableWriter}流式IO操作</li>
 * <li><strong>命名策略</strong>：预定义驼峰式(默认)和蛇形(SNAKE_CASE)两种命名转换</li>
 * </ul>
 * 
 * <h2>使用场景</h2>
 * 
 * <pre>{@code
 * // 基础对象转换
 * User user = new User("张三", 18);
 * String json = JsonFormat.DEFAULT.to(user);
 * User parsed = JsonFormat.DEFAULT.from(json, User.class);
 * 
 * // 流式JSON处理
 * try (FileReader fileReader = new FileReader("data.json")) {
 * 	List<Order> orders = (List<Order>) JsonFormat.SNAKE_CASE.from(fileReader,
 * 			TypeDescriptor.valueOf(new TypeReference<List<Order>>() {
 * 			}));
 * }
 * 
 * // 自定义配置扩展
 * JsonFormat customFormat = JsonFormat.DEFAULT.copy().configure(SerializationFeature.INDENT_OUTPUT, true) // 格式化输出
 * 		.setSerializationInclusion(JsonInclude.Include.ALWAYS); // 包含所有字段
 * }</pre>
 * 
 * @see JsonConverter
 * @see StringFormat
 * @see JsonMapper
 */
@Getter
public class JsonFormat extends JsonMapper implements JacksonFormat, JsonConverter {
	private static final long serialVersionUID = 1L;

	/**
	 * 默认JSON格式实例，采用驼峰式命名策略，适用于Java生态系统的标准JSON交互。
	 * <p>
	 * 配置包含：忽略未知属性、过滤null值、支持单引号等工业级常用设置。
	 */
	public static final JsonFormat DEFAULT = new JsonFormat();

	/**
	 * 蛇形命名JSON格式实例，采用{@link PropertyNamingStrategy#SNAKE_CASE}策略，
	 * 适用于与Python/Ruby等下划线命名规范的系统进行数据交互。
	 */
	public static final JsonFormat SNAKE_CASE = new JsonFormat();

	static {
		SNAKE_CASE.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
	}

	/**
	 * 初始化默认配置的JSON格式处理器，自动调用{@link #defaultProperties()}完成参数配置。
	 */
	public JsonFormat() {
		super();
		defaultProperties();
	}

	/**
	 * 基于现有JsonMapper创建新实例，用于配置继承或定制扩展。
	 * 
	 * @param jsonMapper 基础JsonMapper实例，将复制其配置
	 */
	public JsonFormat(JsonMapper jsonMapper) {
		super(jsonMapper);
	}

	/**
	 * 标准化配置方法，设置工业级JSON处理参数：
	 * <ul>
	 * <li>禁用{@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES}忽略未知字段</li>
	 * <li>启用{@link JsonInclude.Include#NON_NULL}过滤null值字段</li>
	 * <li>启用{@link JsonParser.Feature#ALLOW_SINGLE_QUOTES}支持单引号</li>
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
	 * 创建配置副本，生成独立的JSON处理器实例，允许自定义配置而不影响原实例。
	 * 
	 * @return 新的JsonFormat实例，复制当前所有配置
	 */
	@Override
	public JsonFormat copy() {
		_checkInvalidCopy(JsonFormat.class);
		return new JsonFormat(this);
	}

	@Override
	public ObjectMapper getObjectMapper() {
		return this;
	}
}