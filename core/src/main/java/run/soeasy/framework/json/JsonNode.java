package run.soeasy.framework.json;

import java.io.Reader;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.tree.TreeNode;

/**
 * JSON 节点实现类（流式数据专属）
 * 接口，无条件跳过子节点嵌套内容（无回退流式特性）。
 * 遵循接口约定：流为单例，遍历后即耗尽失效。
 *
 * @author soeasy.run
 */
@RequiredArgsConstructor
public class JsonNode implements TreeNode<JsonValue> {

    @NonNull
    private final JsonIterator jsonIterator;

    /** JSON 节点值（延迟初始化） */
    private JsonValue jsonValue;
    /** 是否为数组节点 */
    private boolean isArrayNode;
    /** 是否为对象节点 */
    private boolean isObjectNode;

    /** 数组流缓存（单例，遵循接口约定） */
    private Stream<TreeNode<JsonValue>> cachedArrayStream;
    /** 对象流缓存（单例，遵循接口约定） */
    private Stream<KeyValue<String, TreeNode<JsonValue>>> cachedObjectStream;

    public JsonNode(@NonNull Reader reader) {
        this(new JsonIterator(reader));
    }

    // ===================== 延迟初始化 =====================

    private void lazyInitialize() {
        if (this.jsonValue == null) {
            if (!jsonIterator.hasNext()) {
                throw new JsonParseException("Unexpected EOF when initializing JsonNode");
            }
            this.jsonValue = jsonIterator.next();
            this.isArrayNode = (this.jsonValue.getToken() == JsonToken.BEGIN_ARRAY);
            this.isObjectNode = (this.jsonValue.getToken() == JsonToken.BEGIN_OBJECT);
        }
    }

    // ===================== 跳过嵌套内容 =====================

    /**
     * 无条件跳过目标节点的所有嵌套内容。
     * 跳过完成后重置目标节点的类型标记，使其表现为普通值节点。
     */
    private void skipSubNodesUnconditionally(JsonNode targetNode) {
        targetNode.lazyInitialize();
        JsonToken nodeToken = targetNode.jsonValue.getToken();

        if (nodeToken != JsonToken.BEGIN_ARRAY && nodeToken != JsonToken.BEGIN_OBJECT) {
            return;
        }

        int nestedCount = 1;
        while (nestedCount > 0) {
            if (!jsonIterator.hasNext()) {
                throw new JsonParseException(
                    "Unexpected EOF when skipping sub nodes (nestedCount=" + nestedCount + ")");
            }
            JsonValue value = jsonIterator.next();
            JsonToken token = value.getToken();

            if (token == JsonToken.BEGIN_ARRAY || token == JsonToken.BEGIN_OBJECT) {
                nestedCount++;
            } else if (token == JsonToken.END_ARRAY || token == JsonToken.END_OBJECT) {
                nestedCount--;
            }
        }

        // 嵌套已消费完毕，重置类型标记
        targetNode.isArrayNode = false;
        targetNode.isObjectNode = false;
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

    @Override
    public Stream<TreeNode<JsonValue>> arrayStream() {
        lazyInitialize();
        if (!this.isArrayNode) {
            return Stream.empty();
        }
        if (this.cachedArrayStream == null) {
            this.cachedArrayStream = StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                    new ArrayNodeIterator(),
                    java.util.Spliterator.ORDERED),
                false);
        }
        return this.cachedArrayStream;
    }

    @Override
    public Stream<KeyValue<String, TreeNode<JsonValue>>> objectStream() {
        lazyInitialize();
        if (!this.isObjectNode) {
            return Stream.empty();
        }
        if (this.cachedObjectStream == null) {
            this.cachedObjectStream = StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                    new ObjectNodeIterator(),
                    java.util.Spliterator.ORDERED),
                false);
        }
        return this.cachedObjectStream;
    }

    // ===================== 数组迭代器 =====================

    private class ArrayNodeIterator implements Iterator<TreeNode<JsonValue>> {

        private JsonValue prefetched;
        private boolean endReached;

        ArrayNodeIterator() {
            fetchNext();
        }

        @Override
        public boolean hasNext() {
            return !endReached && prefetched != null
                && prefetched.getToken() != JsonToken.END_ARRAY;
        }

        @Override
        public TreeNode<JsonValue> next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException("No more array elements");
            }

            JsonNode child = new JsonNode(jsonIterator);
            child.jsonValue = prefetched;
            child.isArrayNode = (prefetched.getToken() == JsonToken.BEGIN_ARRAY);
            child.isObjectNode = (prefetched.getToken() == JsonToken.BEGIN_OBJECT);

            // 跳过嵌套内容，内部会重置 child 的类型标记
            skipSubNodesUnconditionally(child);

            fetchNext();

            return child;
        }

        private void fetchNext() {
            if (!jsonIterator.hasNext()) {
                endReached = true;
                prefetched = null;
                return;
            }
            JsonValue v = jsonIterator.next();
            if (v.getToken() == JsonToken.END_ARRAY) {
                endReached = true;
                prefetched = v;
            } else {
                endReached = false;
                prefetched = v;
            }
        }
    }

    // ===================== 对象迭代器 =====================

    private class ObjectNodeIterator implements Iterator<KeyValue<String, TreeNode<JsonValue>>> {

        private String nextKey;
        private JsonNode nextValueNode;
        private boolean hasMore;

        ObjectNodeIterator() {
            fetchNextEntry();
        }

        @Override
        public boolean hasNext() {
            return hasMore;
        }

        @Override
        public KeyValue<String, TreeNode<JsonValue>> next() {
            if (!hasMore) {
                throw new java.util.NoSuchElementException("No more object entries");
            }

            KeyValue<String, TreeNode<JsonValue>> result = KeyValue.of(nextKey, nextValueNode);

            // 跳过值节点的嵌套内容，内部会重置 nextValueNode 的类型标记
            skipSubNodesUnconditionally(nextValueNode);

            fetchNextEntry();

            return result;
        }

        private void fetchNextEntry() {
            // 1. 读键
            if (!jsonIterator.hasNext()) {
                hasMore = false;
                return;
            }
            JsonValue keyVal = jsonIterator.next();
            if (keyVal.getToken() == JsonToken.END_OBJECT) {
                hasMore = false;
                return;
            }
            if (keyVal.getToken() != JsonToken.STRING) {
                throw new JsonParseException("Expected string key, got: " + keyVal.getToken());
            }
            nextKey = keyVal.getValue().toString();

            // 2. 读冒号
            if (!jsonIterator.hasNext()) {
                throw new JsonParseException("Unexpected EOF after object key");
            }
            JsonValue sepVal = jsonIterator.next();
            if (sepVal.getToken() != JsonToken.NAME_SEPARATOR) {
                throw new JsonParseException("Expected ':', got: " + sepVal.getToken());
            }

            // 3. 读值
            if (!jsonIterator.hasNext()) {
                throw new JsonParseException("Unexpected EOF after ':'");
            }
            JsonValue valToken = jsonIterator.next();
            nextValueNode = new JsonNode(jsonIterator);
            nextValueNode.jsonValue = valToken;
            nextValueNode.isArrayNode = (valToken.getToken() == JsonToken.BEGIN_ARRAY);
            nextValueNode.isObjectNode = (valToken.getToken() == JsonToken.BEGIN_OBJECT);

            hasMore = true;
        }
    }
}