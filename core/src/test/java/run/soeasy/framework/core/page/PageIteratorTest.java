package run.soeasy.framework.core.page;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * PageIterator 的 JUnit4 单元测试
 * 1. 适配 getPageNumber() = long 类型（无 longValue() 调用）
 * 2. 移除所有超时等待（永久阻塞，直观暴露线程卡死/死循环/死锁问题）
 * 3. 仅用 CountDownLatch 控制线程（无 join()）
 *
 * @author soeasy.run
 */
public class PageIteratorTest {

    private static final List<String> TEST_ELEMENTS = Arrays.asList(
            "elem1", "elem2", "elem3", "elem4", "elem5",
            "elem6", "elem7", "elem8", "elem9", "elem10"
    );

    private static <T> Iterator<T> emptyIterator() {
        return new ArrayList<T>().iterator();
    }

    private static <T> Iterator<T> dataIterator(List<T> data) {
        return new ArrayList<>(data).iterator();
    }

    /**
     * 测试构造函数：每页大小≤0 抛出 IllegalArgumentException
     */
    @Test
    public void testConstructor_InvalidPageSize_ThrowIllegalArgumentException() {
        System.out.println("=== 开始测试：构造函数-无效页大小校验 ===");
        Iterator<String> emptyIter = emptyIterator();

        // 验证页大小为0
        System.out.println("验证页大小为0的异常场景...");
        IllegalArgumentException exception0 = Assert.assertThrows(
                IllegalArgumentException.class,
                () -> new PageIterator<>(0, emptyIter)
        );
        Assert.assertEquals("Page size must be greater than 0, current value: 0", exception0.getMessage());

        // 验证页大小为负数
        System.out.println("验证页大小为负数(-5)的异常场景...");
        IllegalArgumentException exceptionNeg = Assert.assertThrows(
                IllegalArgumentException.class,
                () -> new PageIterator<>(-5, emptyIter)
        );
        Assert.assertEquals("Page size must be greater than 0, current value: -5", exceptionNeg.getMessage());

        System.out.println("=== 测试完成：构造函数-无效页大小校验 通过 ===");
    }

    /**
     * 测试 hasNext()：空源迭代器 返回 false
     */
    @Test
    public void testHasNext_EmptySource_ReturnFalse() {
        System.out.println("=== 开始测试：hasNext()-空源迭代器校验 ===");
        PageIterator<String> pageIterator = new PageIterator<>(5, emptyIterator());
        boolean hasNext = pageIterator.hasNext();
        System.out.println("空源迭代器调用hasNext()结果：" + hasNext);
        Assert.assertFalse(hasNext);
        System.out.println("=== 测试完成：hasNext()-空源迭代器校验 通过 ===");
    }

    /**
     * 测试 hasNext()：有数据时预加载并返回 true
     */
    @Test
    public void testHasNext_WithData_PreloadAndReturnTrue() {
        System.out.println("=== 开始测试：hasNext()-有数据预加载校验 ===");
        PageIterator<String> pageIterator = new PageIterator<>(5, dataIterator(TEST_ELEMENTS));
        boolean hasNext = pageIterator.hasNext();
        System.out.println("有数据迭代器调用hasNext()结果：" + hasNext);
        Assert.assertTrue(hasNext);
        System.out.println("=== 测试完成：hasNext()-有数据预加载校验 通过 ===");
    }

    /**
     * 测试 next()：正常分页返回正确的 Page 对象（适配 getPageNumber() = long）
     */
    @Test
    public void testNext_ValidData_ReturnCorrectPage() {
        System.out.println("=== 开始测试：next()-正常分页逻辑校验 ===");
        PageIterator<String> pageIterator = new PageIterator<>(5, dataIterator(TEST_ELEMENTS));

        System.out.println("==== 处理第一页数据 ====");
        Assert.assertTrue(pageIterator.hasNext());
        Page<Long, String> firstPage = pageIterator.next();
        System.out.println("第一页页码：" + firstPage.getPageNumber() + "，页大小：" + firstPage.getPageSize());
        List<String> firstPageElements = firstPage.toList();
        System.out.println("第一页元素数量：" + firstPageElements.size() + "，元素内容：" + firstPageElements);
        // 直接断言 long 类型（无 longValue()）
        Assert.assertEquals(1L, firstPage.getPageNumber());
        Assert.assertEquals(5, firstPage.getPageSize());
        Assert.assertEquals(5, firstPageElements.size());
        Assert.assertEquals(TEST_ELEMENTS.subList(0, 5), firstPageElements);

        System.out.println("==== 处理第二页数据 ====");
        Assert.assertTrue(pageIterator.hasNext());
        Page<Long, String> secondPage = pageIterator.next();
        System.out.println("第二页页码：" + secondPage.getPageNumber() + "，页大小：" + secondPage.getPageSize());
        List<String> secondPageElements = secondPage.toList();
        System.out.println("第二页元素数量：" + secondPageElements.size() + "，元素内容：" + secondPageElements);
        // 直接断言 long 类型（无 longValue()）
        Assert.assertEquals(2L, secondPage.getPageNumber());
        Assert.assertEquals(5, secondPage.getPageSize());
        Assert.assertEquals(5, secondPageElements.size());
        Assert.assertEquals(TEST_ELEMENTS.subList(5, 10), secondPageElements);

        boolean hasNext = pageIterator.hasNext();
        System.out.println("所有数据处理完成后，hasNext()结果：" + hasNext);
        Assert.assertFalse(hasNext);
        System.out.println("=== 测试完成：next()-正常分页逻辑校验 通过 ===");
    }

    /**
     * 测试 next()：无更多数据时抛出 NoSuchElementException（适配 long 类型 PageNumber）
     */
    @Test
    public void testNext_NoMoreData_ThrowNoSuchElementException() {
        System.out.println("=== 开始测试：next()-无更多数据异常校验 ===");
        PageIterator<String> pageIterator = new PageIterator<>(5, emptyIterator());
        System.out.println("空迭代器调用next()，预期抛出NoSuchElementException...");

        NoSuchElementException exception = Assert.assertThrows(
                NoSuchElementException.class,
                pageIterator::next
        );
        // 异常消息需与 PageIterator 抛出的完全一致（PageNumber 为 long 不影响字符串）
        Assert.assertEquals("No more pagination data available, current traversed to page 1", exception.getMessage());

        System.out.println("=== 测试完成：next()-无更多数据异常校验 通过 ===");
    }

    /**
     * 测试 calculateNextCursorId()：数值溢出抛出 ArithmeticException
     * 修复：解包 InvocationTargetException，适配 long 类型溢出
     */
    @Test
    public void testCalculateNextCursorId_Overflow_ThrowArithmeticException() {
        System.out.println("=== 开始测试：calculateNextCursorId()-数值溢出异常校验 ===");
        long maxOffset = Long.MAX_VALUE - 10;
        int pageSize = 20;
        System.out.println("构造溢出场景：当前偏移量=" + maxOffset + "，页大小=" + pageSize);
        PageIterator<String> pageIterator = new PageIterator<>(pageSize, dataIterator(TEST_ELEMENTS));

        try {
            java.lang.reflect.Method method = PageIterator.class.getDeclaredMethod("calculateNextCursorId", long.class);
            method.setAccessible(true);
            System.out.println("调用calculateNextCursorId()方法，预期抛出ArithmeticException...");

            // 反射调用会包装异常，需解包
            try {
                method.invoke(pageIterator, maxOffset);
                Assert.fail("Expected ArithmeticException was not thrown");
            } catch (java.lang.reflect.InvocationTargetException e) {
                Throwable targetEx = e.getTargetException();
                System.out.println("捕获到预期异常：" + targetEx.getClass().getSimpleName() + "，异常信息：" + targetEx.getMessage());
                Assert.assertTrue(targetEx instanceof ArithmeticException);
                Assert.assertEquals(
                        String.format("Failed to calculate next cursor ID: numeric overflow (current offset=%d, page size=%d)", maxOffset, pageSize),
                        targetEx.getMessage()
                );
            }
        } catch (NoSuchMethodException | IllegalAccessException e) {
            Assert.fail("Unexpected exception: " + e.getMessage());
        }
        System.out.println("=== 测试完成：calculateNextCursorId()-数值溢出异常校验 通过 ===");
    }

    /**
     * 测试 remove()：不支持移除操作，抛出 UnsupportedOperationException
     */
    @Test
    public void testRemove_Unsupported_ThrowException() {
        System.out.println("=== 开始测试：remove()-不支持操作异常校验 ===");
        PageIterator<String> pageIterator = new PageIterator<>(5, dataIterator(TEST_ELEMENTS));
        System.out.println("调用remove()方法，预期抛出UnsupportedOperationException...");

        UnsupportedOperationException exception = Assert.assertThrows(
                UnsupportedOperationException.class,
                pageIterator::remove
        );
        Assert.assertEquals("Page iterator does not support remove operation", exception.getMessage());

        System.out.println("=== 测试完成：remove()-不支持操作异常校验 通过 ===");
    }

    /**
     * 测试分页逻辑：非整数页数据（适配 long 类型 PageNumber）
     */
    @Test
    public void testPagination_NonIntegerPage() {
        System.out.println("=== 开始测试：分页逻辑-非整数页数据校验 ===");
        List<String> nonIntegerData = Arrays.asList("a", "b", "c", "d", "e", "f", "g");
        System.out.println("测试数据：" + nonIntegerData + "，页大小=3");
        PageIterator<String> pageIterator = new PageIterator<>(3, dataIterator(nonIntegerData));

        System.out.println("==== 处理第一页 ====");
        Page<Long, String> page1 = pageIterator.next();
        System.out.println("第一页页码：" + page1.getPageNumber() + "，元素数量：" + page1.toList().size());
        // 直接断言 long 类型（无 longValue()）
        Assert.assertEquals(1L, page1.getPageNumber());
        Assert.assertEquals(3, page1.toList().size());

        System.out.println("==== 处理第二页 ====");
        Page<Long, String> page2 = pageIterator.next();
        System.out.println("第二页页码：" + page2.getPageNumber() + "，元素数量：" + page2.toList().size());
        // 直接断言 long 类型（无 longValue()）
        Assert.assertEquals(2L, page2.getPageNumber());
        Assert.assertEquals(3, page2.toList().size());

        System.out.println("==== 处理第三页 ====");
        Page<Long, String> page3 = pageIterator.next();
        System.out.println("第三页页码：" + page3.getPageNumber() + "，元素数量：" + page3.toList().size());
        // 直接断言 long 类型（无 longValue()）
        Assert.assertEquals(3L, page3.getPageNumber());
        Assert.assertEquals(1, page3.toList().size());

        boolean hasNext = pageIterator.hasNext();
        System.out.println("所有数据处理完成后，hasNext()结果：" + hasNext);
        Assert.assertFalse(hasNext);
        System.out.println("=== 测试完成：分页逻辑-非整数页数据校验 通过 ===");
    }

    /**
     * 测试线程安全：多线程调用无数据错乱（永久阻塞，直观暴露问题；无 join()，仅 CountDownLatch）
     */
    @Test
    public void testThreadSafety() throws InterruptedException {
        System.out.println("=== 开始测试：线程安全-多线程分页遍历校验 ===");
        // 构造测试数据：100条数据，每页10条 → 预期10页（PageNumber 为 long）
        List<Integer> largeData = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            largeData.add(i);
        }
        PageIterator<Integer> pageIterator = new PageIterator<>(10, dataIterator(largeData));

        // 1. CountDownLatch 控制（无超时，永久阻塞）
        CountDownLatch startLatch = new CountDownLatch(1);  // 启动门闩
        CountDownLatch finishLatch = new CountDownLatch(2); // 等待2个线程完成
        Set<Long> pageNumbers = Collections.synchronizedSet(new HashSet<>()); // 存储 long 类型页码

        // 2. 通用遍历逻辑（适配 long 类型 PageNumber）
        Runnable traversalTask = () -> {
            try {
                startLatch.await(); // 永久阻塞，直到启动门闩释放（直观暴露卡死问题）
                System.out.println(Thread.currentThread().getName() + "：开始遍历分页数据...");

                while (true) {
                    Page<Long, Integer> page;
                    synchronized (pageIterator) {
                        if (!pageIterator.hasNext()) {
                            break; // 无数据则退出，避免死循环
                        }
                        page = pageIterator.next();
                    }
                    // 直接获取 long 类型页码（无 longValue()）
                    long pageNum = page.getPageNumber();
                    pageNumbers.add(pageNum);
                    System.out.println(Thread.currentThread().getName() + " - 获取第" + pageNum + "页");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println(Thread.currentThread().getName() + "：被中断，终止遍历");
            } catch (Exception e) {
                System.err.println(Thread.currentThread().getName() + "：遍历异常：" + e.getMessage());
            } finally {
                finishLatch.countDown();
                System.out.println(Thread.currentThread().getName() + "：遍历完成");
            }
        };

        // 3. 启动线程（无 join()）
        Thread thread1 = new Thread(traversalTask, "分页线程-1");
        Thread thread2 = new Thread(traversalTask, "分页线程-2");
        thread1.start();
        thread2.start();
        System.out.println("所有线程已启动，释放启动门闩...");
        startLatch.countDown(); // 释放门闩，让线程开始遍历

        // 4. 永久阻塞等待线程完成（直观暴露卡死/死锁问题）
        finishLatch.await();

        // 5. 核心验证（适配 long 类型 PageNumber）
        System.out.println("\n=== 遍历完成，开始验证 ===");
        System.out.println("所有线程获取的页码：" + pageNumbers);
        System.out.println("获取的页码总数：" + pageNumbers.size());

        Assert.assertEquals("页码总数应为10", 10, pageNumbers.size());
        // 直接断言 long 类型页码（1L~10L）
        for (long i = 1L; i <= 10L; i++) {
            Assert.assertTrue("缺失第" + i + "页", pageNumbers.contains(i));
        }
        Assert.assertFalse("遍历完成后应无更多数据", pageIterator.hasNext());

        System.out.println("=== 测试完成：线程安全-多线程分页遍历校验 通过 ===");
    }
}