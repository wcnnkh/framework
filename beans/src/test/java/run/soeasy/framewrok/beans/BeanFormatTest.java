package run.soeasy.framewrok.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import run.soeasy.framework.beans.BeanFormat;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.core.collection.LinkedMultiValueMap;
import run.soeasy.framework.core.collection.MultiValueMap;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.streaming.Mapping;

/**
 * KeyValueFormat 核心功能单元测试（JUnit4 原生断言 + 流程打印）
 * 覆盖：多值处理、对象<->字符串转换、编解码、边界场景
 *
 * @author soeasy.run
 */
public class BeanFormatTest {

    // 测试基础参数：分隔符&，连接符=，透传编解码器（无实际编解码）
    private static final CharSequence DELIMITER = "&";
    private static final CharSequence CONNECTOR = "=";
    private static final Codec<String, String> PASSTHROUGH_CODEC = new Codec<String, String>() {
        @Override
        public String encode(String source) throws CodecException {
            return source;
        }

        @Override
        public String decode(String source) throws CodecException {
            return source;
        }
    };

    // 测试实例
    private BeanFormat keyValueFormat;

    /**
     * 每个测试方法执行前初始化
     */
    @Before
    public void setUp() {
        keyValueFormat = new BeanFormat(DELIMITER, CONNECTOR, PASSTHROUGH_CODEC, PASSTHROUGH_CODEC);
        System.out.println("=====================================");
        System.out.println("测试环境初始化完成，KeyValueFormat 实例已创建");
        System.out.println("分隔符：" + DELIMITER + "，连接符：" + CONNECTOR);
        System.out.println("=====================================");
    }

    // ------------------------------ join方法测试（多值处理） ------------------------------
    @Test
    public void testJoin_IterableValue() throws Exception {
        System.out.println("\n【开始测试】testJoin_IterableValue（Iterable类型多值拼接）");
        // 测试Iterable类型值（List）
        KeyValue<String, Iterable<String>> element = KeyValue.of("hobby", Arrays.asList("reading", "coding"));
        StringWriter writer = new StringWriter();
        System.out.println("准备测试数据：键=hobby，值=List[reading, coding]");

        long count = keyValueFormat.join(writer, 0, element);
        System.out.println("执行join方法结果：拼接字符串=" + writer.toString() + "，计数=" + count);

        // 验证：拼接结果为 "hobby=reading&hobby=coding"，计数为2
        assertEquals("hobby=reading&hobby=coding", writer.toString());
        assertEquals(2, count);
        System.out.println("【验证通过】testJoin_IterableValue 测试完成");
    }

    @Test
    public void testJoin_ArrayValue() throws Exception {
        System.out.println("\n【开始测试】testJoin_ArrayValue（数组类型多值拼接）");
        // 测试数组类型值
        KeyValue<String, Integer[]> element = KeyValue.of("age", new Integer[]{18, 20});
        StringWriter writer = new StringWriter();
        System.out.println("准备测试数据：键=age，值=Integer[18, 20]");

        long count = keyValueFormat.join(writer, 0, element);
        System.out.println("执行join方法结果：拼接字符串=" + writer.toString() + "，计数=" + count);

        // 验证：拼接结果为 "age=18&age=20"，计数为2
        assertEquals("age=18&age=20", writer.toString());
        assertEquals(2, count);
        System.out.println("【验证通过】testJoin_ArrayValue 测试完成");
    }

    @Test
    public void testJoin_SingleValue() throws Exception {
        System.out.println("\n【开始测试】testJoin_SingleValue（普通单值拼接）");
        // 测试普通单值
        KeyValue<String, String> element = KeyValue.of("name", "test");
        StringWriter writer = new StringWriter();
        System.out.println("准备测试数据：键=name，值=test");

        long count = keyValueFormat.join(writer, 0, element);
        System.out.println("执行join方法结果：拼接字符串=" + writer.toString() + "，计数=" + count);

        // 验证：拼接结果为 "name=test"，计数为1
        assertEquals("name=test", writer.toString());
        assertEquals(1, count);
        System.out.println("【验证通过】testJoin_SingleValue 测试完成");
    }

    // ------------------------------ to方法测试（对象→字符串） ------------------------------
    @Test
    public void testTo_MapWithMultiValue() throws Exception {
        System.out.println("\n【开始测试】testTo_MapWithMultiValue（Map转键值对字符串）");
        // 测试含多值的Map转换为字符串
        Map<String, Object> sourceMap = new LinkedHashMap<>();
        sourceMap.put("name", "admin");
        sourceMap.put("roles", Arrays.asList("admin", "user"));
        sourceMap.put("age", 25);
        System.out.println("准备测试数据：Map=" + sourceMap);

        StringWriter writer = new StringWriter();
        TypeDescriptor sourceType = TypeDescriptor.forObject(sourceMap);
        keyValueFormat.to(sourceMap, sourceType, writer);
        System.out.println("执行to方法结果：拼接字符串=" + writer.toString());

        // 验证：拼接结果符合顺序（LinkedHashMap保证）
        String expected = "name=admin&roles=admin&roles=user&age=25";
        assertEquals(expected, writer.toString());
        System.out.println("【验证通过】testTo_MapWithMultiValue 测试完成（预期结果=" + expected + "）");
    }

    // ------------------------------ from方法测试（字符串→对象） ------------------------------
    @Test
    @SuppressWarnings("unchecked")
    public void testFrom_StringToMap() throws Exception {
        System.out.println("\n【开始测试】testFrom_StringToMap（键值对字符串转多值Map）");
        // 测试键值对字符串转换为多值Map
        String sourceStr = "fruit=apple&fruit=banana&color=red";
        StringReader reader = new StringReader(sourceStr);
        TypeDescriptor targetType = TypeDescriptor.map(Map.class, String.class, List.class);
        System.out.println("准备测试数据：键值对字符串=" + sourceStr);

        Map<String, List<String>> resultMap = (Map<String, List<String>>) keyValueFormat.from(reader, targetType);
        System.out.println("执行from方法结果：Map=" + resultMap);

        // 验证：多值键解析正确
        List<String> fruitList = resultMap.get("fruit");
        assertEquals(2, fruitList.size());
        assertEquals("apple", fruitList.get(0));
        assertEquals("banana", fruitList.get(1));
        System.out.println("验证多值键fruit：" + fruitList + "（符合预期）");

        // 验证：单值键解析正确
        List<String> colorList = resultMap.get("color");
        assertEquals(1, colorList.size());
        assertEquals("red", colorList.get(0));
        System.out.println("验证单值键color：" + colorList + "（符合预期）");

        // 验证Map大小
        assertEquals(2, resultMap.size());
        System.out.println("【验证通过】testFrom_StringToMap 测试完成（Map大小=" + resultMap.size() + "）");
    }

    @Test
    public void testFrom_StringToPojo() throws Exception {
        System.out.println("\n【开始测试】testFrom_StringToPojo（键值对字符串转POJO）");
        // 测试字符串转换为自定义POJO
        String sourceStr = "username=test&hobbies=reading&hobbies=coding&age=20";
        StringReader reader = new StringReader(sourceStr);
        TypeDescriptor targetType = TypeDescriptor.forType(User.class);
        System.out.println("准备测试数据：键值对字符串=" + sourceStr);

        User user = (User) keyValueFormat.from(reader, targetType);
        System.out.println("执行from方法结果：User=" + user.getUsername() + ", hobbies=" + user.getHobbies() + ", age=" + user.getAge());

        // 验证：POJO基础字段赋值正确
        assertNotNull(user);
        assertEquals("test", user.getUsername());
        assertEquals(Integer.valueOf(20), user.getAge());
        System.out.println("验证基础字段：username=" + user.getUsername() + "，age=" + user.getAge() + "（符合预期）");

        // 验证：多值List字段赋值正确
        List<String> hobbies = user.getHobbies();
        assertEquals(2, hobbies.size());
        assertEquals("reading", hobbies.get(0));
        assertEquals("coding", hobbies.get(1));
        System.out.println("验证多值字段hobbies：" + hobbies + "（符合预期）");
        System.out.println("【验证通过】testFrom_StringToPojo 测试完成");
    }

    // ------------------------------ encode/decode方法测试（编解码） ------------------------------
    @Test
    public void testEncode_Mapping() throws CodecException {
        System.out.println("\n【开始测试】testEncode_Mapping（Mapping编码为字符串）");
        // 测试Mapping编码为字符串
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("k1", "v1");
        multiValueMap.add("k1", "v2");
        multiValueMap.add("k2", "v3");
        Mapping<String, String> mapping = Mapping.ofMultiMapped(multiValueMap);
        System.out.println("准备测试数据：MultiValueMap=" + multiValueMap);

        String encoded = keyValueFormat.encode(mapping);
        System.out.println("执行encode方法结果：编码字符串=" + encoded);

        // 验证：编码结果正确
        assertEquals("k1=v1&k1=v2&k2=v3", encoded);
        System.out.println("【验证通过】testEncode_Mapping 测试完成（预期结果=" + encoded + "）");
    }

    @Test
    public void testDecode_StringToMapping() throws CodecException {
        System.out.println("\n【开始测试】testDecode_StringToMapping（字符串解码为Mapping）");
        // 测试字符串解码为Mapping
        String sourceStr = "k1=v1&k1=v2&k2=v3";
        System.out.println("准备测试数据：解码字符串=" + sourceStr);

        Mapping<String, String> mapping = keyValueFormat.decode(sourceStr);
        System.out.println("执行decode方法结果：Mapping集合=" + mapping);
        // 验证：多值键解码正确
        List<String> k1Values = mapping.getValues("k1").toList();
        assertEquals(2, k1Values.size());
        assertEquals("v1", k1Values.get(0));
        assertEquals("v2", k1Values.get(1));
        System.out.println("验证多值键k1：" + k1Values + "（符合预期）");

        // 验证：单值键解码正确
        List<String> k2Values = mapping.getValues("k2").toList();
        assertEquals(1, k2Values.size());
        assertEquals("v3", k2Values.get(0));
        System.out.println("验证单值键k2：" + k2Values + "（符合预期）");

        // 验证键的顺序（LinkedHashMap保证）
        System.out.println(mapping.keys().toList());
        Iterator<String> keyIterator = mapping.keys().toList().iterator();
        assertEquals("k1", keyIterator.next());
        assertEquals("k2", keyIterator.next());
        assertFalse(keyIterator.hasNext());
        System.out.println("验证键顺序：k1 → k2（符合预期）");
        System.out.println("【验证通过】testDecode_StringToMapping 测试完成");
    }

    // ------------------------------ 边界场景测试 ------------------------------
    @Test
    @SuppressWarnings("unchecked")
    public void testEdge_EmptyString() throws Exception {
        System.out.println("\n【开始测试】testEdge_EmptyString（空字符串转换）");
        // 测试空字符串转换为Map
        StringReader reader = new StringReader("");
        TypeDescriptor targetType = TypeDescriptor.map(Map.class, String.class, List.class);
        System.out.println("准备测试数据：空字符串");

        Map<String, List<String>> resultMap = (Map<String, List<String>>) keyValueFormat.from(reader, targetType);
        System.out.println("执行from方法结果：Map=" + resultMap);

        assertTrue(resultMap.isEmpty());
        System.out.println("【验证通过】testEdge_EmptyString 测试完成（Map为空）");
    }

    @Test
    public void testEdge_NullValue() throws Exception {
        System.out.println("\n【开始测试】testEdge_NullValue（null值拼接）");
        // 测试值为null的场景
        Map<String, Object> sourceMap = new LinkedHashMap<>();
        sourceMap.put("nullKey", null);
        System.out.println("准备测试数据：Map={nullKey: null}");

        StringWriter writer = new StringWriter();
        keyValueFormat.to(sourceMap, TypeDescriptor.forObject(sourceMap), writer);
        System.out.println("执行to方法结果：拼接字符串=" + writer.toString());

        // 验证：null值拼接为 "nullKey="
        assertEquals("nullKey=", writer.toString());
        System.out.println("【验证通过】testEdge_NullValue 测试完成（预期结果=nullKey=）");
    }

    // ------------------------------ 自定义测试POJO ------------------------------
    /**
     * 测试用POJO（用于from方法转换测试）
     */
    public static class User {
        private String username;
        private List<String> hobbies;
        private Integer age;

        // 必须提供无参构造（类型转换要求）
        public User() {}

        // Getter & Setter
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public List<String> getHobbies() {
            return hobbies;
        }

        public void setHobbies(List<String> hobbies) {
            this.hobbies = hobbies;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }
}