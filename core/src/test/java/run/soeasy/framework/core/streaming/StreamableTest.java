package run.soeasy.framework.core.streaming;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import org.junit.Test;

import run.soeasy.framework.core.domain.KeyValue;

/**
 * Streamable 核心功能单测（JUnit4）
 * 覆盖静态工厂、元素判断、元素获取、集合转换、比较逻辑、流操作、拉链/拼接/缓存等核心场景
 *
 * @author soeasy.run
 */
public class StreamableTest {

    // ======================== 静态工厂方法测试 ========================
    @Test
    public void testStaticFactories() {
        // 1. empty() 空流测试
        Streamable<String> empty = Streamable.empty();
        assertTrue(empty.isEmpty());
        assertEquals(0, empty.count());

        // 2. array() 可变参数测试
        Streamable<String> arrayStream = Streamable.array("a", "b", "c");
        assertFalse(arrayStream.isEmpty());
        assertEquals(3, arrayStream.count());
        assertTrue(arrayStream.contains("b"));

        // 3. singleton() 单元素测试
        Streamable<Integer> singletonStream = Streamable.singleton(100);
        assertEquals(1, singletonStream.count());
        assertEquals(Integer.valueOf(100), singletonStream.first());

        // 4. of(Iterable) 可迭代对象测试
        List<String> list = Arrays.asList("x", "y");
        Streamable<String> iterableStream = Streamable.of(list);
        assertEquals(2, iterableStream.count());
        assertTrue(iterableStream.contains("x"));

        // 5. of(Supplier<Stream>) 动态流测试
        AtomicInteger counter = new AtomicInteger(0);
        Streamable<Integer> supplierStream = Streamable.of(() -> {
            counter.incrementAndGet();
            return Stream.of(1, 2);
        });
        assertEquals(2, supplierStream.count());
        assertEquals(1, counter.get()); // 仅生成一次流
        assertEquals(2, supplierStream.count());
        assertEquals(2, counter.get()); // 动态流每次调用都会生成新流
    }

    // ======================== 元素匹配判断测试 ========================
    @Test
    public void testMatchPredicates() {
        Streamable<Integer> numberStream = Streamable.array(1, 2, 3, 4, 5);
        Streamable<Integer> emptyStream = Streamable.empty();

        // allMatch：所有元素匹配
        assertTrue(numberStream.allMatch(n -> n > 0));
        assertFalse(numberStream.allMatch(n -> n > 3));
        assertTrue(emptyStream.allMatch(n -> n > 0)); // 空流默认true

        // anyMatch：任意元素匹配
        assertTrue(numberStream.anyMatch(n -> n == 3));
        assertFalse(numberStream.anyMatch(n -> n == 6));
        assertFalse(emptyStream.anyMatch(n -> n > 0)); // 空流默认false

        // noneMatch：无元素匹配
        assertTrue(numberStream.noneMatch(n -> n == 6));
        assertFalse(numberStream.noneMatch(n -> n == 3));
        assertTrue(emptyStream.noneMatch(n -> n > 0)); // 空流默认true
    }

    // ======================== 元素获取测试（含边界/异常场景） ========================
    @Test
    public void testElementRetrieval() {
        Streamable<String> strStream = Streamable.array("a", "b", "c", null);
        Streamable<String> emptyStream = Streamable.empty();
        Streamable<String> singletonStream = Streamable.singleton("test");

        // at()：宽松索引（越界返回empty）
        assertEquals(Optional.of("a"), strStream.at(0));
        assertEquals(Optional.ofNullable(null), strStream.at(3));
        assertEquals(Optional.empty(), strStream.at(-1));
        assertEquals(Optional.empty(), strStream.at(10));
        assertEquals(Optional.empty(), emptyStream.at(0));

        // getAt()：严格索引（越界抛异常）
        assertEquals("a", strStream.getAt(0));
        assertNull(strStream.getAt(3));
        try {
            strStream.getAt(-1);
            fail("未抛出索引越界异常");
        } catch (IndexOutOfBoundsException e) {
            assertTrue(e.getMessage().contains("negative index"));
        }
        
        boolean outOfBounds = false;
        try {
            strStream.getAt(10);
            fail("未抛出索引越界异常");
        } catch (IndexOutOfBoundsException e) {
        	outOfBounds = true;
        }
        assertTrue(outOfBounds);

        // first()/last()
        assertEquals("a", strStream.first());
        assertNull(emptyStream.first());
        assertEquals(null, strStream.last());
        assertEquals("test", singletonStream.last());

        // unique()/getUnique()
        assertTrue(singletonStream.isUnique());
        assertFalse(strStream.isUnique());
        assertEquals(Optional.of("test"), singletonStream.unique());
        assertEquals(Optional.empty(), strStream.unique());
        assertEquals(Optional.empty(), emptyStream.unique());

        // getUnique() 异常场景
        assertEquals("test", singletonStream.getUnique());
        try {
            emptyStream.getUnique();
            fail("空流未抛出NoSuchElementException");
        } catch (NoSuchElementException e) {
            // 预期异常
        }
        try {
            strStream.getUnique();
            fail("多元素流未抛出NoUniqueElementException");
        } catch (NoUniqueElementException e) {
            // 预期异常
        }
    }

    // ======================== 集合转换测试 ========================
    @Test
    public void testCollectionConversion() {
        Streamable<String> strStream = Streamable.array("a", "b", "a", "c");
        Streamable<String> emptyStream = Streamable.empty();

        // toList()
        List<String> list = strStream.toList();
        assertEquals(4, list.size());
        assertEquals(Arrays.asList("a", "b", "a", "c"), list);
        assertTrue(emptyStream.toList().isEmpty());

        // toSet()
        Set<String> set = strStream.toSet();
        assertEquals(3, set.size());
        assertTrue(set.containsAll(Arrays.asList("a", "b", "c")));
        assertTrue(emptyStream.toSet().isEmpty());

        // toMap()（唯一键/重复键）
        Streamable<KeyValue<String, Integer>> kvStream = Streamable.array(
                KeyValue.of("k1", 1), KeyValue.of("k2", 2)
        );
        Map<String, KeyValue<String, Integer>> map = kvStream.toMap(KeyValue::getKey);
        assertEquals(2, map.size());
        assertEquals(1, map.get("k1").getValue().intValue());

        // 重复键抛异常
        Streamable<KeyValue<String, Integer>> duplicateKvStream = Streamable.array(
                KeyValue.of("k1", 1), KeyValue.of("k1", 2)
        );
        try {
            duplicateKvStream.toMap(KeyValue::getKey);
            fail("重复键未抛出IllegalStateException");
        } catch (IllegalStateException e) {
            // 预期异常
        }

        // toArray()
        Object[] objArray = strStream.toArray();
        assertEquals(4, objArray.length);
        String[] strArray = strStream.toArray(String[]::new);
        assertEquals(4, strArray.length);
        assertEquals("a", strArray[0]);
    }

    // ======================== 比较逻辑测试（有序/无序） ========================
    @Test
    public void testEqualsLogic() {
        Streamable<Integer> stream1 = Streamable.array(1, 2, 3);
        Streamable<Integer> stream2 = Streamable.array(3, 2, 1);
        Streamable<Integer> stream3 = Streamable.array(1, 2, 4);
        Streamable<Integer> emptyStream = Streamable.empty();

        BiPredicate<Integer, Integer> eqPredicate = Integer::equals;

        // equalsInAnyOrder：无序相等
        assertTrue(stream1.equalsInAnyOrder(stream2, eqPredicate));
        assertFalse(stream1.equalsInAnyOrder(stream3, eqPredicate));
        assertTrue(emptyStream.equalsInAnyOrder(emptyStream, eqPredicate));
        assertFalse(stream1.equalsInAnyOrder(emptyStream, eqPredicate));

        // equalsInOrder：有序相等
        assertFalse(stream1.equalsInOrder(stream2, eqPredicate));
        assertTrue(stream1.equalsInOrder(Streamable.array(1, 2, 3), eqPredicate));
        assertTrue(emptyStream.equalsInOrder(emptyStream, eqPredicate));
    }

    // ======================== 核心流操作测试（过滤/映射/去重/排序） ========================
    @Test
    public void testStreamOperations() {
        Streamable<Integer> numberStream = Streamable.array(3, 1, 2, 2, 4, null);

        // filter()：过滤非空且>2的元素
        Streamable<Integer> filtered = numberStream.filter(n -> n != null && n > 2);
        assertEquals(Arrays.asList(3, 4), filtered.toList());

        // map()：映射为字符串
        Streamable<String> mapped = numberStream.map(n -> n == null ? "null" : n.toString());
        assertEquals(Arrays.asList("3", "1", "2", "2", "4", "null"), mapped.toList());

        // distinct()：去重
        Streamable<Integer> distinct = numberStream.distinct();
        assertEquals(Arrays.asList(3, 1, 2, 4, null), distinct.toList());

        // sorted()：自然排序
        Streamable<Integer> sorted = numberStream.filter(Objects::nonNull).sorted();
        assertEquals(Arrays.asList(1, 2, 2, 3, 4), sorted.toList());

        // limit()/skip()：分页
        Streamable<Integer> page = numberStream.skip(1).limit(2);
        assertEquals(Arrays.asList(1, 2), page.toList());
    }

    // ======================== 拼接/缓存/拉链测试 ========================
    @Test
    public void testConcatCacheZip() {
        Streamable<String> stream1 = Streamable.array("a", "b");
        Streamable<String> stream2 = Streamable.array("c", "d");
        Streamable<String> emptyStream = Streamable.empty();

        // concat()：拼接
        Streamable<String> concat = stream1.concat(stream2);
        assertEquals(Arrays.asList("a", "b", "c", "d"), concat.toList());
        assertSame(stream1, stream1.concat(emptyStream)); // 空流拼接优化

        // cached()：缓存（验证重复访问不重新生成流）
        AtomicInteger counter = new AtomicInteger(0);
        Streamable<Integer> dynamicStream = Streamable.of(() -> {
            counter.incrementAndGet();
            return Stream.of(1, 2);
        });
        Streamable<Integer> cached = dynamicStream.cached();
        assertEquals(2, cached.count());
        assertEquals(2, cached.count());
        assertEquals(1, counter.get()); // 仅生成一次流

        // zip()：拉链
        Streamable<Integer> numStream = Streamable.array(1, 2, 3);
        Streamable<String> strStream = Streamable.array("a", "b");
        // ANY_HAS_NEXT 策略：补null
        Streamable<String> zipResult = numStream.zip(strStream, (n, s) -> n + "-" + s);
        assertEquals(Arrays.asList("1-a", "2-b", "3-null"), zipResult.toList());
        // BOTH_HAS_NEXT 策略：仅匹配共同长度
        Streamable<String> zipBoth = numStream.zip(strStream, ZipIterator.Rule.BOTH_HAS_NEXT, (n, s) -> n + "-" + s);
        assertEquals(Arrays.asList("1-a", "2-b"), zipBoth.toList());
    }
}