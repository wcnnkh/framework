package run.soeasy.framework.core.exchange;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Stage类JUnit4单测（覆盖所有核心场景 + Bug验证）
 */
public class StageTest {

    // 全局超时规则：所有测试方法超时时间1秒（避免线程永久阻塞）
    @Rule
    public Timeout globalTimeout = Timeout.millis(1000);

    private Stage stage;

    @Before
    public void setUp() {
        // 每个测试方法前初始化全新的Stage实例
        stage = new Stage();
    }

    @After
    public void tearDown() {
        // 清理资源（可选）
        stage = null;
    }

    // ========== 1. 基础状态转换测试（默认方法） ==========
    @Test
    public void testDefaultStateTransition() {
        // 1.1 初始状态校验
        assertEquals(Stage.State.NEW, stage.getCurrentState());
        assertFalse(stage.isDone());
        assertFalse(stage.isSuccess());
        assertFalse(stage.isCancelled());
        assertFalse(stage.isRollback());

        // 1.2 尝试从NEW转SUCCESS（默认方法）
        boolean success = stage.trySuccess();
        assertTrue(success);
        assertEquals(Stage.State.SUCCESS, stage.getCurrentState());
        assertTrue(stage.isDone());
        assertTrue(stage.isSuccess());

        // 1.3 重复转SUCCESS（失败）
        boolean repeatSuccess = stage.trySuccess();
        assertFalse(repeatSuccess);

        // 1.4 重置状态为NEW
        stage.reset();
        assertEquals(Stage.State.NEW, stage.getCurrentState());

        // 1.5 尝试从NEW转FAILURE（默认方法）
        RuntimeException ex = new RuntimeException("test failure");
        boolean failure = stage.tryFailure(ex);
        assertTrue(failure);
        assertEquals(Stage.State.FAILURE, stage.getCurrentState());
        assertEquals(ex, stage.cause());
        assertTrue(stage.isDone());
        assertFalse(stage.isSuccess());

        // 1.6 重置状态为NEW
        stage.reset();

        // 1.7 尝试从NEW转CANCELLED（默认方法）
        boolean cancel = stage.tryCancel();
        assertTrue(cancel);
        assertEquals(Stage.State.CANCELLED, stage.getCurrentState());
        assertTrue(stage.isDone());
        assertTrue(stage.isCancelled());
    }

    // ========== 2. 自定义旧状态转换测试 ==========
    @Test
    public void testCustomOldStateTransition() {
        // 2.1 从NEW转FAILURE（自定义旧状态）
        RuntimeException ex = new RuntimeException("custom failure");
        boolean failure = stage.tryFailure(Stage.State.NEW, ex);
        assertTrue(failure);
        assertEquals(Stage.State.FAILURE, stage.getCurrentState());

        // 2.2 从FAILURE转CANCELLED（特殊场景）
        boolean cancel = stage.tryCancel(Stage.State.FAILURE);
        assertTrue(cancel);
        assertEquals(Stage.State.CANCELLED, stage.getCurrentState());

        // 2.3 从CANCELLED转SUCCESS（失败，状态不可逆）
        boolean success = stage.trySuccess(Stage.State.CANCELLED);
        assertFalse(success);
    }

    // ========== 3. 回滚逻辑测试（核心场景） ==========
    @Test
    public void testRollback() {
        // 3.1 无回滚逻辑时，tryRollback返回false
        stage.trySuccess();
        boolean rollback1 = stage.tryRollback();
        assertFalse(rollback1);
        assertEquals(Stage.State.SUCCESS, stage.getCurrentState());

        // 3.2 有回滚逻辑且执行成功
        AtomicBoolean rollbackExecuted = new AtomicBoolean(false);
        Stage rollbackStage = new Stage(() -> {
            rollbackExecuted.set(true);
            return true;
        });
        rollbackStage.trySuccess();
        boolean rollback2 = rollbackStage.tryRollback();
        assertTrue(rollback2);
        assertTrue(rollbackExecuted.get());
        assertEquals(Stage.State.ROLLBACKED, rollbackStage.getCurrentState());
        assertTrue(rollbackStage.isRollback());
        assertTrue(rollbackStage.isDone());

        // 3.3 回滚逻辑执行失败（状态恢复为SUCCESS）
        AtomicBoolean rollbackFailed = new AtomicBoolean(false);
        Stage rollbackFailStage = new Stage(() -> {
            rollbackFailed.set(true);
            return false;
        });
        rollbackFailStage.trySuccess();
        boolean rollback3 = rollbackFailStage.tryRollback();
        assertFalse(rollback3);
        assertTrue(rollbackFailed.get());
        assertEquals(Stage.State.SUCCESS, rollbackFailStage.getCurrentState());

        // 3.4 回滚逻辑抛异常（修复后：状态恢复为SUCCESS）
        Stage rollbackExceptionStage = new Stage(() -> {
            throw new RuntimeException("rollback exception");
        });
        rollbackExceptionStage.trySuccess();
        try {
            rollbackExceptionStage.tryRollback();
            fail("预期抛出RuntimeException");
        } catch (RuntimeException e) {
            assertEquals("rollback exception", e.getMessage());
            // 验证：状态恢复为SUCCESS（修复后的关键）
            assertEquals(Stage.State.SUCCESS, rollbackExceptionStage.getCurrentState());
        }
    }

    // ========== 4. await()方法测试（修复后验证） ==========
    @Test(timeout = 1000) // 单独设置超时，确保不阻塞
    public void testAwait_AfterRollback() throws InterruptedException {
        // 4.1 ROLLBACKED态await（修复后：正常唤醒，无阻塞）
        Stage rollbackStage = new Stage(() -> true);
        rollbackStage.trySuccess();
        rollbackStage.tryRollback();
        rollbackStage.await(); // 修复后：正常返回，无阻塞

        // 4.2 所有完成态await验证
        Stage successStage = new Stage();
        successStage.trySuccess();
        successStage.await();

        Stage failureStage = new Stage();
        failureStage.tryFailure(new RuntimeException("test"));
        failureStage.await();

        Stage cancelStage = new Stage();
        cancelStage.tryCancel();
        cancelStage.await();
    }

    // ========== 5. reset()方法测试（修复后验证） ==========
    @Test(timeout = 1000)
    public void testReset_NoBlock() throws InterruptedException {
        CountDownLatch testLatch = new CountDownLatch(1);
        AtomicBoolean awaitBlocked = new AtomicBoolean(true);

        // 5.1 启动线程await()
        Thread awaitThread = new Thread(() -> {
            try {
                stage.await();
                awaitBlocked.set(false);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                testLatch.countDown();
            }
        });
        awaitThread.start();

        // 5.2 重置状态（修复后：旧latch被countDown，线程不会阻塞）
        stage.reset();
        // 5.3 标记状态为SUCCESS
        stage.trySuccess();

        // 5.4 等待线程执行，验证是否阻塞
        assertTrue(testLatch.await(1, TimeUnit.SECONDS));
        assertFalse(awaitBlocked.get()); // 修复后：线程已唤醒，非阻塞

        // 5.5 清理线程
        if (awaitThread.isAlive()) {
            awaitThread.interrupt();
        }
    }

    // ========== 6. 多线程并发状态转换测试（原子性） ==========
    @Test(timeout = 2000)
    public void testConcurrentStateTransition() throws InterruptedException {
        int threadCount = 100;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        AtomicReference<Stage.State> finalState = new AtomicReference<>();
        AtomicInteger successCount = new AtomicInteger(0);

        // 6.1 启动100个线程并发调用trySuccess()
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    startLatch.await(); // 等待统一启动
                    boolean success = stage.trySuccess();
                    if (success) {
                        successCount.incrementAndGet();
                        finalState.set(stage.getCurrentState());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            }).start();
        }

        // 6.2 统一启动所有线程
        startLatch.countDown();
        assertTrue(endLatch.await(1, TimeUnit.SECONDS));

        // 6.3 验证原子性：仅1个线程成功转换
        assertEquals(1, successCount.get());
        assertEquals(Stage.State.SUCCESS, finalState.get());
    }

    // ========== 7. 边界条件测试 ==========
    @Test
    public void testBoundaryConditions() {
        // 7.1 tryFailure传入null异常（抛NPE）
        try {
            stage.tryFailure(null);
            fail("预期抛出NullPointerException");
        } catch (NullPointerException e) {
            assertEquals("Failure cause must not be null", e.getMessage());
        }

        // 7.2 isCancellable仅NEW态返回true
        assertTrue(stage.isCancellable());
        stage.trySuccess();
        assertFalse(stage.isCancellable());

        // 7.3 rollback()方法复用tryRollback()
        Stage rollbackStage = new Stage(() -> true);
        rollbackStage.trySuccess();
        boolean rollback = rollbackStage.rollback();
        assertTrue(rollback);
        assertEquals(Stage.State.ROLLBACKED, rollbackStage.getCurrentState());

        // 7.4 cancel()方法复用tryCancel()
        Stage cancelStage = new Stage();
        boolean cancel = cancelStage.cancel();
        assertTrue(cancel);
        assertEquals(Stage.State.CANCELLED, cancelStage.getCurrentState());
    }
}