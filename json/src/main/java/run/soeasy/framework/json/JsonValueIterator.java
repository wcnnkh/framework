package run.soeasy.framework.json;

import java.util.NoSuchElementException;

import run.soeasy.framework.core.collection.CloseableIterator;

/**
 * JsonValue 迭代器，基于 JsonTokenizer 实现，遵循 Java Iterator 规范 支持自动预取元素、资源关闭，只读迭代（不支持
 * remove 操作）
 */
class JsonValueIterator implements CloseableIterator<JsonValue> {

	// 依赖的 JSON 分词器（核心数据来源）
	private final JsonTokenizer jsonTokenizer;
	// 预取的下一个 JsonValue（用于协调 hasNext() 和 next() 方法）
	private JsonValue nextValue;
	// 迭代器是否已关闭标记
	private boolean closed;

	/**
	 * 构造器：传入 JsonTokenizer 实例
	 * 
	 * @param jsonTokenizer JSON 分词器（非空）
	 */
	public JsonValueIterator(JsonTokenizer jsonTokenizer) {
		if (jsonTokenizer == null) {
			throw new NullPointerException("JsonTokenizer cannot be null");
		}
		this.jsonTokenizer = jsonTokenizer;
		this.nextValue = null;
		this.closed = false;
	}

	/**
	 * 预取下一个 JsonValue（核心辅助方法，避免重复读取 Token）
	 * 
	 * @return 下一个 JsonValue，无元素时返回 null
	 */
	private JsonValue fetchNextValue() {
		checkClosed();
		// 调用 JsonTokenizer 读取下一个 Token
		JsonToken token = jsonTokenizer.readNextToken();
		// 若读取到 EOF，说明无更多元素
		if (token == JsonToken.EOF) {
			return null;
		}
		// 封装 Token 为 JsonValue（tokenValue 为 CharBuffer，实现了 CharSequence 接口）
		return new JsonValue(jsonTokenizer.getTokenValue(), token);
	}

	/**
	 * 判断是否还有下一个 JsonValue
	 * 
	 * @return 有下一个元素返回 true，否则返回 false
	 */
	@Override
	public boolean hasNext() {
		checkClosed();
		// 若未预取元素，则先预取
		if (nextValue == null) {
			nextValue = fetchNextValue();
		}
		// 预取结果非空，说明还有下一个元素
		return nextValue != null;
	}

	/**
	 * 获取下一个 JsonValue
	 * 
	 * @return 下一个 JsonValue 实例
	 * @throws NoSuchElementException 当无更多元素时抛出
	 */
	@Override
	public JsonValue next() {
		// 先调用 hasNext() 确保预取了元素
		if (!hasNext()) {
			throw new NoSuchElementException("No more JsonValue elements to iterate");
		}
		// 保存要返回的元素
		JsonValue currentValue = nextValue;
		// 清空预取标记，为下一次 hasNext() 做准备
		nextValue = null;
		return currentValue;
	}

	/**
	 * 移除元素（不支持该操作，因为 JSON 迭代为只读迭代）
	 * 
	 * @throws UnsupportedOperationException 调用时始终抛出该异常
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException(
				"JsonValueIterator does not support remove operation (read-only iteration)");
	}

	/**
	 * 检查迭代器是否已关闭
	 * 
	 * @throws IllegalStateException 若已关闭则抛出该异常
	 */
	private void checkClosed() {
		if (closed) {
			throw new IllegalStateException("JsonValueIterator has been closed and cannot be reused");
		}
	}

	/**
	 * 关闭迭代器，释放底层 JsonTokenizer 资源
	 */
	@Override
	public void close() {
		if (!closed) {
			closed = true;
			// 关闭底层 JsonTokenizer
			jsonTokenizer.close();
			// 释放预取元素引用
			nextValue = null;
		}
	}
}