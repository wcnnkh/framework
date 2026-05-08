package run.soeasy.framework.buffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.CharBuffer;
import java.util.Iterator;

import org.junit.Test;

public class DynamicBufferTest {
    private static final int SEGMENT_CAPACITY = 16;
    private static final int DEFAULT_INIT_CAPACITY = 32; // 默认扩容容量，避免初始容量为0

    /**
     * 创建已扩容的有效DynamicBuffer，规避初始容量为0导致的越界问题
     */
    private DynamicBuffer<CharBuffer> createValidDynamicBuffer() {
        DynamicBuffer<CharBuffer> buffer = new DynamicBuffer<>(SEGMENT_CAPACITY, CharBuffer::allocate);
        buffer.setCapacity(DEFAULT_INIT_CAPACITY); // 先扩容，确保后续操作合法
        return buffer;
    }

    @Test
    public void testConstructor() {
        // 测试构造方法1：指定分段容量
        DynamicBuffer<CharBuffer> buffer1 = new DynamicBuffer<>(SEGMENT_CAPACITY, CharBuffer::allocate);
        assertNotNull(buffer1);
        assertEquals(SEGMENT_CAPACITY, buffer1.getBufferPool().getBufferCapacity());
        assertEquals(0, buffer1.capacity());
        assertEquals(0, buffer1.position());
        assertEquals(0, buffer1.limit());

        // 测试构造方法2：传入BufferPool
        BufferPool<CharBuffer> pool = new GenericBufferPool<>(SEGMENT_CAPACITY, CharBuffer::allocate, CharBuffer::clear);
        DynamicBuffer<CharBuffer> buffer2 = new DynamicBuffer<>(pool);
        assertNotNull(buffer2);
        assertEquals(pool, buffer2.getBufferPool());
        assertEquals(0, buffer2.capacity());
    }

    @Test
    public void testDuplicate() {
        DynamicBuffer<CharBuffer> original = createValidDynamicBuffer();
        // 先设置合法状态（capacity=32 → limit=10 → position=5）
        original.limit(10);
        original.position(5);
        original.mark();

        DynamicBuffer<CharBuffer> duplicate = original.duplicate();
        // 验证副本状态与原对象一致
        assertEquals(original.position(), duplicate.position());
        assertEquals(original.limit(), duplicate.limit());
        assertEquals(original.markValue(), duplicate.markValue());
        assertEquals(original.getBufferPool(), duplicate.getBufferPool());
        assertEquals(original.capacity(), duplicate.capacity());

        // 验证修改副本不影响原对象
        duplicate.position(8);
        duplicate.limit(15);
        assertNotEquals(duplicate.position(), original.position());
        assertNotEquals(duplicate.limit(), original.limit());
    }

    @Test
    public void testRemainingAndHasRemaining() {
        DynamicBuffer<CharBuffer> buffer = createValidDynamicBuffer();
        // 设置合法状态
        buffer.limit(20);
        buffer.position(5);

        // 验证有剩余长度
        assertEquals(15, buffer.remaining());
        assertTrue(buffer.hasRemaining());

        // 验证无剩余长度
        buffer.position(20);
        assertEquals(0, buffer.remaining());
        assertFalse(buffer.hasRemaining());
    }

    @Test
    public void testMarkAndReset() {
        DynamicBuffer<CharBuffer> buffer = createValidDynamicBuffer();
        buffer.limit(20);
        buffer.position(8);
        buffer.mark(); // 设置有效标记

        // 验证重置到标记位
        buffer.position(15);
        buffer.reset();
        assertEquals(8, buffer.position());

        // 验证标记无效时抛出异常
        buffer.clear(); // clear会将mark置为-1
        try {
            buffer.reset();
            fail("Expected IllegalStateException not thrown");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("Mark is invalid"));
        }
    }

    @Test
    public void testClearFlipRewind() {
        DynamicBuffer<CharBuffer> buffer = createValidDynamicBuffer();
        // 先设置合法初始状态
        buffer.limit(20);
        buffer.position(10);
        buffer.mark();

        // 测试clear：position=0，limit=capacity，mark=-1
        buffer.clear();
        assertEquals(0, buffer.position());
        assertEquals(DEFAULT_INIT_CAPACITY, buffer.limit());
        assertEquals(-1, buffer.markValue());

        // 测试flip：limit=position，position=0，mark=-1
        buffer.position(15);
        buffer.flip();
        assertEquals(0, buffer.position());
        assertEquals(15, buffer.limit());
        assertEquals(-1, buffer.markValue());

        // 测试rewind：position=0，mark=-1，limit不变
        buffer.position(10);
        buffer.rewind();
        assertEquals(0, buffer.position());
        assertEquals(15, buffer.limit());
        assertEquals(-1, buffer.markValue());
    }

    @Test
    public void testPositionAndLimit() {
        DynamicBuffer<CharBuffer> buffer = createValidDynamicBuffer();
        int maxLimit = DEFAULT_INIT_CAPACITY;

        // 测试合法limit设置
        buffer.limit(maxLimit);
        assertEquals(maxLimit, buffer.limit());

        // 测试合法position设置
        buffer.position(10);
        assertEquals(10, buffer.position());

        // 测试position越界（超过limit）
        try {
            buffer.position(maxLimit + 1);
            fail("Expected IndexOutOfBoundsException not thrown");
        } catch (IndexOutOfBoundsException e) {
            assertTrue(e.getMessage().contains("Invalid position"));
        }

        // 测试limit < position时，position自动调整为limit
        buffer.position(10);
        buffer.limit(5);
        assertEquals(5, buffer.position());
        assertEquals(5, buffer.limit());

        // 测试limit越界（超过capacity）
        try {
            buffer.limit(maxLimit + 1);
            fail("Expected IndexOutOfBoundsException not thrown");
        } catch (IndexOutOfBoundsException e) {
            assertTrue(e.getMessage().contains("Invalid limit"));
        }
    }

    @Test
    public void testSetCapacity() {
        DynamicBuffer<CharBuffer> buffer = new DynamicBuffer<>(SEGMENT_CAPACITY, CharBuffer::allocate);

        // 测试扩容：从0→32（2个分段）
        buffer.setCapacity(32);
        assertEquals(32, buffer.capacity());
        
        // 关键修正：扩容后设置合法的position和limit，让迭代器能遍历到分段
        buffer.clear(); // clear()会将position=0，limit=capacity（32），此时迭代器可正常遍历
        // 统计分段数量
        Iterator<CharBuffer> iterator = buffer.iterator();
        int segmentCount = 0;
        while (iterator.hasNext()) {
            segmentCount++;
            iterator.next();
        }
        assertEquals(2, segmentCount);

        // 测试缩容：32→16（合法，limit=16）
        buffer.limit(16); // 先设置合法limit（不超过后续缩容容量）
        buffer.setCapacity(16);
        assertEquals(16, buffer.capacity());
        // 统计分段数量
        Iterator<CharBuffer> iterator2 = buffer.iterator();
        int segmentCount2 = 0;
        while (iterator2.hasNext()) {
            segmentCount2++;
            iterator2.next();
        }
        assertEquals(1, segmentCount2);

        // 测试缩容非法：新容量 < limit
        try {
            buffer.setCapacity(10);
            fail("Expected IllegalArgumentException not thrown");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Shrink failed"));
        }
    }

    @Test
    public void testExpandAndShrink() {
        DynamicBuffer<CharBuffer> buffer = createValidDynamicBuffer();
        int originalCapacity = buffer.capacity();

        // 测试expand：扩容16，总容量=32+16=48
        buffer.setCapacity(48);
        assertEquals(originalCapacity + 16, buffer.capacity());

        // 先设置合法的limit（不小于要设置的position）
        buffer.clear(); // 快速将limit置为capacity（48），position置为0
        // 再设置position(20)，此时20 <= 48（limit），合法无越界
        buffer.position(20);
        buffer.flip(); // limit=20，position=0
        buffer.shrink();
        assertEquals(20, buffer.capacity());
        assertEquals(20, buffer.limit());

        // 测试空buffer shrink
        DynamicBuffer<CharBuffer> emptyBuffer = new DynamicBuffer<>(SEGMENT_CAPACITY, CharBuffer::allocate);
        emptyBuffer.shrink();
        assertEquals(0, emptyBuffer.capacity());
        assertEquals(0, emptyBuffer.limit());
        assertEquals(0, emptyBuffer.position());
    }

    @Test
    public void testFreeCapacityAndIsEmpty() {
        DynamicBuffer<CharBuffer> buffer = createValidDynamicBuffer();
        buffer.limit(10);

        // 验证空闲容量
        assertEquals(buffer.capacity() - 10, buffer.freeCapacity());
        assertFalse(buffer.isEmpty());

        // 验证空buffer
        buffer.clear();
        buffer.limit(0);
        assertTrue(buffer.isEmpty());
        assertEquals(buffer.capacity(), buffer.freeCapacity());
    }
}