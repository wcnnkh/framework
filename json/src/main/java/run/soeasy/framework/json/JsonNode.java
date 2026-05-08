package run.soeasy.framework.json;

import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.NonNull;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.tree.TreeNode;

/**
 * JSON 节点实现类（流式数据专属），适配 {@link TreeNode<JsonValue>}
 * 接口，无条件跳过子节点嵌套内容（无回退流式特性），仅缓存 Stream 保证多次调用返回同一实例。
 *
 * @author soeasy.run
 */
public class JsonNode implements TreeNode<JsonValue> {
	// ===================== 核心字段（仅保留 Stream 缓存，无消费标记） =====================
	/** JSON 分词器（核心解析器，非空，父子节点共用，流式单向读取） */
	private final JsonTokenizer jsonTokenizer;
	/** JSON 节点值（延迟初始化，首次使用时解析） */
	private JsonValue jsonValue;
	/** 是否为数组节点（延迟初始化，依赖 jsonValue） */
	private boolean isArrayNode;
	/** 是否为对象节点（延迟初始化，依赖 jsonValue） */
	private boolean isObjectNode;

	/** 数组流缓存（首次调用创建后缓存，多次调用返回同一个实例） */
	private Stream<TreeNode<JsonValue>> cachedArrayStream;
	/** 对象流缓存（首次调用创建后缓存，多次调用返回同一个实例） */
	private Stream<KeyValue<String, TreeNode<JsonValue>>> cachedObjectStream;

	// ===================== 构造器（仅赋值，不执行任何初始化逻辑） =====================
	public JsonNode(@NonNull JsonTokenizer jsonTokenizer) {
		this.jsonTokenizer = jsonTokenizer;
	}

	// ===================== 私有延迟初始化方法（简化版，无线程安全） =====================
	private void lazyInitialize() {
		if (this.jsonValue == null) {
			JsonToken initialToken = this.jsonTokenizer.readNextToken();
			if (initialToken == JsonToken.EOF) {
				throw new JsonParseException("Unexpected EOF when initializing JsonNode (no valid JSON token)");
			}
			this.jsonValue = new JsonValue(this.jsonTokenizer.getTokenValue(), initialToken);
			this.isArrayNode = JsonToken.BEGIN_ARRAY == initialToken;
			this.isObjectNode = JsonToken.BEGIN_OBJECT == initialToken;
		}
	}

	// ===================== 私有核心：无条件跳过子节点嵌套内容（适配流式无回退） =====================
	/**
	 * 无条件跳过目标节点的所有嵌套内容（流式数据无回退，必须强制跳过避免Token错乱）
	 * 
	 * @param targetNode 目标子节点（数组/对象节点才会触发跳过逻辑）
	 */
	private void skipSubNodesUnconditionally(JsonNode targetNode) {
		targetNode.lazyInitialize();
		JsonToken nodeToken = targetNode.getValue().getToken();

		// 仅对数组/对象节点执行跳过（普通值节点无嵌套内容，无需处理）
		if (nodeToken != JsonToken.BEGIN_ARRAY && nodeToken != JsonToken.BEGIN_OBJECT) {
			return;
		}

		JsonToken endToken = (nodeToken == JsonToken.BEGIN_ARRAY) ? JsonToken.END_ARRAY : JsonToken.END_OBJECT;
		int nestedCount = 1; // 嵌套计数器，处理多层嵌套（如数组包含对象、对象包含数组）

		// 循环读取Token，直到匹配结束标记且嵌套计数器归0（流式单向读取，无回退）
		while (nestedCount > 0) {
			JsonToken currentToken = this.jsonTokenizer.readNextToken();
			if (currentToken == JsonToken.EOF) {
				throw new JsonParseException("Unexpected EOF when skipping sub nodes, missing end token: " + endToken);
			}

			// 嵌套层级增加：遇到子嵌套的开始标记
			if (currentToken == JsonToken.BEGIN_ARRAY || currentToken == JsonToken.BEGIN_OBJECT) {
				nestedCount++;
			}
			// 嵌套层级减少：遇到对应结束标记
			else if (currentToken == endToken) {
				nestedCount--;
			}
		}
	}

	// ===================== TreeNode 接口完整实现 =====================
	@Override
	public boolean isStreamNode() {
		return true; // 流式数据节点，始终返回true
	}

	@Override
	public JsonValue getValue() {
		lazyInitialize();
		return this.jsonValue;
	}

	@Override
	public boolean isArray() {
		lazyInitialize();
		return this.isArrayNode;
	}

	@Override
	public boolean isObject() {
		lazyInitialize();
		return this.isObjectNode;
	}

	/**
	 * 获取数组节点子流（多次调用返回同一个缓存实例，流式一次性消费）
	 * 
	 * @return 缓存的子节点流（非数组节点返回空流单例）
	 */
	@Override
	public Stream<TreeNode<JsonValue>> arrayStream() {
		lazyInitialize();

		// 非数组节点返回 Stream.empty()（天然单例，满足多次调用返回同一实例）
		if (!this.isArrayNode) {
			return Stream.empty();
		}

		// 首次调用创建流并缓存，后续调用直接返回缓存实例（无消费标记，仅靠缓存字段判断）
		if (this.cachedArrayStream == null) {
			Iterator<TreeNode<JsonValue>> arrayNodeIterator = new ArrayNodeIterator();
			this.cachedArrayStream = StreamSupport
					.stream(Spliterators.spliteratorUnknownSize(arrayNodeIterator, java.util.Spliterator.ORDERED), false // 强制串行，适配流式单向特性
					);
		}

		return this.cachedArrayStream;
	}

	/**
	 * 获取对象节点键值对流（多次调用返回同一个缓存实例，流式一次性消费）
	 * 
	 * @return 缓存的键值对流（非对象节点返回空流单例）
	 */
	@Override
	public Stream<KeyValue<String, TreeNode<JsonValue>>> objectStream() {
		lazyInitialize();

		// 非对象节点返回 Stream.empty()（天然单例，满足多次调用返回同一实例）
		if (!this.isObjectNode) {
			return Stream.empty();
		}

		// 首次调用创建流并缓存，后续调用直接返回缓存实例（无消费标记，仅靠缓存字段判断）
		if (this.cachedObjectStream == null) {
			Iterator<KeyValue<String, TreeNode<JsonValue>>> objectNodeIterator = new ObjectNodeIterator();
			this.cachedObjectStream = StreamSupport.stream(
					Spliterators.spliteratorUnknownSize(objectNodeIterator, java.util.Spliterator.ORDERED), false // 强制串行，适配流式单向特性
			);
		}

		return this.cachedObjectStream;
	}

	// ===================== 内部迭代器：数组节点遍历（无条件跳过子节点，适配流式无回退） =====================
	private class ArrayNodeIterator implements Iterator<TreeNode<JsonValue>> {
		private boolean hasNextNode;
		private JsonToken nextToken;

		public ArrayNodeIterator() {
			this.fetchNextToken(); // 预读取第一个Token
		}

		@Override
		public boolean hasNext() {
			// 未到数组结尾且非EOF，即为有下一个子节点
			return this.hasNextNode && this.nextToken != JsonToken.END_ARRAY;
		}

		@Override
		public TreeNode<JsonValue> next() {
			if (!this.hasNext()) {
				throw new java.util.NoSuchElementException("No more child nodes in JSON array (stream exhausted)");
			}

			// 1. 构建子节点（仅获取子节点基础信息，嵌套内容将被无条件跳过）
			JsonNode childNode = new JsonNode(JsonNode.this.jsonTokenizer);
			// 2. 无条件跳过子节点所有嵌套内容（流式无回退，避免Token错乱）
			JsonNode.this.skipSubNodesUnconditionally(childNode);
			// 3. 预读取下一个Token，供父节点下一次迭代使用
			this.fetchNextToken();

			return childNode;
		}

		/** 预读取下一个Token，更新迭代状态 */
		private void fetchNextToken() {
			this.nextToken = JsonNode.this.jsonTokenizer.readNextToken();
			this.hasNextNode = this.nextToken != JsonToken.EOF && this.nextToken != JsonToken.END_ARRAY;
		}
	}

	// ===================== 内部迭代器：对象节点遍历（无条件跳过子节点，适配流式无回退） =====================
	private class ObjectNodeIterator implements Iterator<KeyValue<String, TreeNode<JsonValue>>> {
		private boolean hasNextEntry;
		private String nextKey;
		private JsonNode nextValueNode;

		public ObjectNodeIterator() {
			this.fetchNextKeyValue(); // 预读取第一个键值对
		}

		@Override
		public boolean hasNext() {
			return this.hasNextEntry;
		}

		@Override
		public KeyValue<String, TreeNode<JsonValue>> next() {
			if (!this.hasNext()) {
				throw new java.util.NoSuchElementException(
						"No more key-value entries in JSON object (stream exhausted)");
			}

			// 1. 构建键值对
			KeyValue<String, TreeNode<JsonValue>> keyValue = KeyValue.of(this.nextKey, this.nextValueNode);
			// 2. 无条件跳过值节点的所有嵌套内容（流式无回退，避免Token错乱）
			JsonNode.this.skipSubNodesUnconditionally(this.nextValueNode);
			// 3. 预读取下一个键值对，供父节点下一次迭代使用
			this.fetchNextKeyValue();

			return keyValue;
		}

		/** 预读取下一个键值对，更新迭代状态 */
		private void fetchNextKeyValue() {
			// 1. 读取键（必须是字符串类型，符合JSON规范）
			JsonToken keyToken = JsonNode.this.jsonTokenizer.readNextToken();
			if (keyToken == JsonToken.END_OBJECT || keyToken == JsonToken.EOF) {
				this.hasNextEntry = false;
				return;
			}
			if (keyToken != JsonToken.STRING) {
				throw new JsonParseException("Expected string key in JSON object, but got: " + keyToken);
			}

			// 2. 提取键字符串
			this.nextKey = JsonNode.this.jsonTokenizer.getTokenValue().toString();

			// 3. 读取键值分隔符（必须是冒号，符合JSON规范）
			JsonToken separatorToken = JsonNode.this.jsonTokenizer.readNextToken();
			if (separatorToken != JsonToken.NAME_SEPARATOR) {
				throw new JsonParseException("Expected colon separator in JSON object, but got: " + separatorToken);
			}

			// 4. 构建值节点
			this.nextValueNode = new JsonNode(JsonNode.this.jsonTokenizer);

			// 5. 标记存在下一个键值对
			this.hasNextEntry = true;
		}
	}
}