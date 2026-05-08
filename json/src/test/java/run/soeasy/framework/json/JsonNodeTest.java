package run.soeasy.framework.json;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.tree.TreeNode;

/**
 * JsonNode 单测（适配修复后的 JsonTokenizer）
 */
public class JsonNodeTest {

    /**
     * 辅助方法：创建合法的 JsonTokenizer
     */
    private JsonTokenizer createTokenizer(String json) {
        if (json == null || json.isEmpty()) {
            throw new IllegalArgumentException("JSON must not be null or empty");
        }
        return JsonTokenizer.create(json);
    }

    /**
     * 用例2：getValue 延迟初始化 + 单例特性
     */
    @Test
    public void testGetValue_LazyInitialize_SameInstance() {
        String json = "\"testValue\"";
        try (JsonTokenizer tokenizer = createTokenizer(json)) {
            JsonNode jsonNode = new JsonNode(tokenizer);

            JsonValue firstValue = jsonNode.getValue();
            System.out.println(firstValue);
            Assert.assertNotNull("JsonValue must not be null", firstValue);
            Assert.assertEquals(JsonToken.STRING, firstValue.getToken());
            Assert.assertEquals("testValue", firstValue.getAsString());

            JsonValue secondValue = jsonNode.getValue();
            Assert.assertSame("Two calls should return the same JsonValue", firstValue, secondValue);
        }
    }

    /**
     * 用例3：isArray / isObject 节点类型判断
     */
    @Test
    public void testIsArray_IsObject_CorrectIdentify() {
        // 数组节点
        try (JsonTokenizer arrayTokenizer = createTokenizer("[1,2,3]")) {
            JsonNode arrayNode = new JsonNode(arrayTokenizer);
            Assert.assertTrue(arrayNode.isArray());
            Assert.assertFalse(arrayNode.isObject());
            Assert.assertTrue(arrayNode.getValue().isJsonArray());
        }

        // 对象节点
        try (JsonTokenizer objectTokenizer = createTokenizer("{\"key\":\"val\"}")) {
            JsonNode objectNode = new JsonNode(objectTokenizer);
            Assert.assertTrue(objectNode.isObject());
            Assert.assertFalse(objectNode.isArray());
            Assert.assertTrue(objectNode.getValue().isJsonObject());
        }

        // 普通节点
        try (JsonTokenizer normalTokenizer = createTokenizer("123.45")) {
            JsonNode normalNode = new JsonNode(normalTokenizer);
            Assert.assertFalse(normalNode.isArray());
            Assert.assertFalse(normalNode.isObject());
            Assert.assertTrue(normalNode.getValue().isNumber());
        }
    }

    /**
     * 用例4：arrayStream 单例性 + 子节点遍历
     */
    @Test
    public void testArrayStream_SameInstance_Iterate() {
        String json = "[\"a\", 123, true]";
        try (JsonTokenizer tokenizer = createTokenizer(json)) {
            JsonNode arrayNode = new JsonNode(tokenizer);

            Stream<TreeNode<JsonValue>> stream1 = arrayNode.arrayStream();
            Stream<TreeNode<JsonValue>> stream2 = arrayNode.arrayStream();
            Assert.assertSame("Streams should be the same instance", stream1, stream2);

            List<TreeNode<JsonValue>> childNodes = stream1.collect(Collectors.toList());
            Assert.assertEquals(3, childNodes.size());

            for (TreeNode<JsonValue> node : childNodes) {
                Assert.assertNotNull(node.getValue());
                Assert.assertNotNull(node.getValue().getToken());
            }
        }
    }

    /**
     * 用例5：objectStream 单例性 + 键值对遍历
     */
    @Test
    public void testObjectStream_SameInstance_Iterate() {
        String json = "{\"name\":\"test\", \"age\":20}";
        try (JsonTokenizer tokenizer = createTokenizer(json)) {
            JsonNode objectNode = new JsonNode(tokenizer);

            Stream<KeyValue<String, TreeNode<JsonValue>>> stream1 = objectNode.objectStream();
            Stream<KeyValue<String, TreeNode<JsonValue>>> stream2 = objectNode.objectStream();
            Assert.assertSame("Streams should be the same instance", stream1, stream2);

            List<KeyValue<String, TreeNode<JsonValue>>> kvList = stream1.collect(Collectors.toList());
            Assert.assertEquals(2, kvList.size());

            for (KeyValue<String, TreeNode<JsonValue>> kv : kvList) {
                Assert.assertNotNull(kv.getKey());
                Assert.assertFalse(kv.getKey().isEmpty());
                Assert.assertNotNull(kv.getValue().getValue());
            }
        }
    }

    /**
     * 用例6：非数组/对象节点返回空流
     */
    @Test
    public void testNonArrayNonObject_ReturnEmptyStream() {
        String json = "false";
        try (JsonTokenizer tokenizer = createTokenizer(json)) {
            JsonNode normalNode = new JsonNode(tokenizer);

            // 验证数组流为空
            Stream<TreeNode<JsonValue>> arrayStream = normalNode.arrayStream();
            Iterator<TreeNode<JsonValue>> arrayIter = arrayStream.iterator();
            Assert.assertFalse(arrayIter.hasNext());

            // 验证对象流为空
            Stream<KeyValue<String, TreeNode<JsonValue>>> objectStream = normalNode.objectStream();
            Iterator<KeyValue<String, TreeNode<JsonValue>>> objectIter = objectStream.iterator();
            Assert.assertFalse(objectIter.hasNext());
        }
    }
}