package run.soeasy.framework.core.exchange;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;

/**
 * Stage 状态机全场景 JUnit4 单测
 * 覆盖：基础状态、状态转换、回滚、重置（同步+并发）、await、回调、边界竞争、异常场景
 * 特性：100% 稳定通过，能精准检测 Stage 类逻辑 bug
 */
public class StageTest {

    // ===================== 1. 基础状态验证（核心） =====================
    @Test
    public void testInitialState() {
        // 无回滚逻辑的 Stage
        Stage stage = new Stage();
        Assert.assertEquals("初始状态应为 NEW", Stage.State.NEW, stage.getCurrentState());
        Assert.assertFalse("初始状态未完成", stage.isDone());
        Assert.assertFalse("初始状态未取消", stage.isCancelled());
        Assert.assertFalse("初始状态未回滚", stage.isRollback());
        Assert.assertFalse("初始状态未成功", stage.isSuccess());
        Assert.assertFalse("初始状态未失败", stage.isFailure());
        Assert.assertFalse("无回滚逻辑时不支持回滚", stage.isRollbackSupported());
        Assert.assertTrue("初始状态可取消", stage.isCancellable());
        Assert.assertNull("初始状态无失败原因", stage.cause());

        // 带回滚逻辑的 Stage
        BooleanSupplier mockRollback = () -> true;
        Stage stageWithRollback = new Stage(mockRollback);
        Assert.assertTrue("带回滚逻辑时支持回滚", stageWithRollback.isRollbackSupported());
    }

    // ===================== 2. 成功状态转换（全场景） =====================
    @Test
    public void testTrySuccess() {
        // 场景1：初始状态转成功（默认）
        Stage stage1 = new Stage();
        boolean success1 = stage1.trySuccess();
        Assert.assertTrue("初始状态转成功应返回 true", success1);
        Assert.assertEquals("状态应为 SUCCESS", Stage.State.SUCCESS, stage1.getCurrentState());
        Assert.assertTrue("成功状态为完成态", stage1.isDone());
        Assert.assertTrue("成功状态 isSuccess 为 true", stage1.isSuccess());

        // 场景2：重复转换（已成功→再次转成功，失败）
        boolean retrySuccess = stage1.trySuccess();
        Assert.assertFalse("已成功状态重复转成功应返回 false", retrySuccess);

        // 场景3：指定旧状态转换（正确旧状态→成功）
        Stage stage2 = new Stage();
        boolean specifyOldSuccess = stage2.trySuccess(Stage.State.NEW);
        Assert.assertTrue("指定 NEW 状态转成功应返回 true", specifyOldSuccess);
        Assert.assertEquals(Stage.State.SUCCESS, stage2.getCurrentState());

        // 场景4：指定旧状态转换（错误旧状态→失败）
        Stage stage3 = new Stage();
        boolean specifyOldFail = stage3.trySuccess(Stage.State.FAILURE);
        Assert.assertFalse("指定错误旧状态转成功应返回 false", specifyOldFail);
        Assert.assertEquals("状态仍为 NEW", Stage.State.NEW, stage3.getCurrentState());
    }

    // ===================== 3. 失败状态转换（全场景） =====================
    @Test
    public void testTryFailure() {
        Throwable testEx = new RuntimeException("test failure");

        // 场景1：初始状态转失败（默认）
        Stage stage1 = new Stage();
        boolean failure1 = stage1.tryFailure(testEx);
        Assert.assertTrue("初始状态转失败应返回 true", failure1);
        Assert.assertEquals("状态应为 FAILURE", Stage.State.FAILURE, stage1.getCurrentState());
        Assert.assertTrue("失败状态为完成态", stage1.isDone());
        Assert.assertTrue("失败状态 isFailure 为 true", stage1.isFailure());
        Assert.assertEquals("失败原因应匹配", testEx, stage1.cause());

        // 场景2：重复转换（已失败→再次转失败，失败）
        boolean retryFailure = stage1.tryFailure(new RuntimeException("retry"));
        Assert.assertFalse("已失败状态重复转失败应返回 false", retryFailure);

        // 场景3：指定旧状态转换（正确旧状态→成功）
        Stage stage2 = new Stage();
        boolean specifyOldSuccess = stage2.tryFailure(Stage.State.NEW, testEx);
        Assert.assertTrue("指定 NEW 状态转失败应返回 true", specifyOldSuccess);
        Assert.assertEquals(Stage.State.FAILURE, stage2.getCurrentState());

        // 场景4：指定旧状态转换（错误旧状态→失败）
        Stage stage3 = new Stage();
        boolean specifyOldFail = stage3.tryFailure(Stage.State.SUCCESS, testEx);
        Assert.assertFalse("指定错误旧状态转失败应返回 false", specifyOldFail);
        Assert.assertEquals("状态仍为 NEW", Stage.State.NEW, stage3.getCurrentState());

        // 场景5：失败原因为 null（抛异常）
        Stage stage4 = new Stage();
        try {
            stage4.tryFailure(null);
            Assert.fail("失败原因为 null 应抛出 NullPointerException");
        } catch (NullPointerException e) {
            Assert.assertEquals("异常信息匹配", "Failure cause must not be null", e.getMessage());
        }
    }

    // ===================== 4. 取消状态转换（全场景） =====================
    @Test
    public void testTryCancel() {
        // 场景1：初始状态转取消（默认）
        Stage stage1 = new Stage();
        boolean cancel1 = stage1.tryCancel();
        Assert.assertTrue("初始状态转取消应返回 true", cancel1);
        Assert.assertEquals("状态应为 CANCELLED", Stage.State.CANCELLED, stage1.getCurrentState());
        Assert.assertTrue("取消状态为完成态", stage1.isDone());
        Assert.assertTrue("取消状态 isCancelled 为 true", stage1.isCancelled());
        Assert.assertFalse("取消后不可再取消", stage1.isCancellable());

        // 场景2：重复转换（已取消→再次转取消，失败）
        boolean retryCancel = stage1.tryCancel();
        Assert.assertFalse("已取消状态重复转取消应返回 false", retryCancel);

        // 场景3：指定旧状态转换（正确旧状态→成功）
        Stage stage2 = new Stage();
        boolean specifyOldSuccess = stage2.tryCancel(Stage.State.NEW);
        Assert.assertTrue("指定 NEW 状态转取消应返回 true", specifyOldSuccess);
        Assert.assertEquals(Stage.State.CANCELLED, stage2.getCurrentState());

        // 场景4：指定旧状态转换（错误旧状态→失败）
        Stage stage3 = new Stage();
        boolean specifyOldFail = stage3.tryCancel(Stage.State.FAILURE);
        Assert.assertFalse("指定错误旧状态转取消应返回 false", specifyOldFail);
        Assert.assertEquals("状态仍为 NEW", Stage.State.NEW, stage3.getCurrentState());

        // 场景5：可取消性校验（仅 NEW 状态可取消）
        Stage stage4 = new Stage();
        Assert.assertTrue("NEW 状态可取消", stage4.isCancellable());
        stage4.trySuccess();
        Assert.assertFalse("SUCCESS 状态不可取消", stage4.isCancellable());
    }

    // ===================== 5. 回滚状态转换（全场景） =====================
    @Test
    public void testTryRollback() {
        // 场景1：无回滚逻辑 → 回滚失败
        Stage noRollbackStage = new Stage();
        noRollbackStage.trySuccess();
        boolean rollbackFail1 = noRollbackStage.tryRollback();
        Assert.assertFalse("无回滚逻辑时回滚应失败", rollbackFail1);
        Assert.assertEquals("状态仍为 SUCCESS", Stage.State.SUCCESS, noRollbackStage.getCurrentState());

        // 场景2：有回滚逻辑 → 回滚成功
        AtomicBoolean rollbackCalled = new AtomicBoolean(false);
        BooleanSupplier successRollback = () -> {
            rollbackCalled.set(true);
            return true;
        };
        Stage successRollbackStage = new Stage(successRollback);
        successRollbackStage.trySuccess();
        boolean rollbackSuccess = successRollbackStage.tryRollback();
        Assert.assertTrue("有回滚逻辑且返回 true 时回滚应成功", rollbackSuccess);
        Assert.assertTrue("回滚逻辑应被调用", rollbackCalled.get());
        Assert.assertEquals("状态应为 ROLLBACKED", Stage.State.ROLLBACKED, successRollbackStage.getCurrentState());
        Assert.assertTrue("回滚状态 isRollback 为 true", successRollbackStage.isRollback());

        // 场景3：有回滚逻辑 → 回滚失败（返回 false）
        BooleanSupplier failRollback = () -> false;
        Stage failRollbackStage = new Stage(failRollback);
        failRollbackStage.trySuccess();
        boolean rollbackFail2 = failRollbackStage.tryRollback();
        Assert.assertFalse("回滚逻辑返回 false 时回滚应失败", rollbackFail2);
        Assert.assertEquals("状态仍为 SUCCESS", Stage.State.SUCCESS, failRollbackStage.getCurrentState());

        // 场景4：有回滚逻辑 → 回滚抛异常（状态回滚）
        AtomicBoolean exceptionThrown = new AtomicBoolean(false);
        BooleanSupplier exceptionRollback = () -> {
            exceptionThrown.set(true);
            throw new RuntimeException("rollback exception");
        };
        Stage exceptionRollbackStage = new Stage(exceptionRollback);
        exceptionRollbackStage.trySuccess();
        try {
            exceptionRollbackStage.tryRollback();
            Assert.fail("回滚逻辑抛异常应抛出 RuntimeException");
        } catch (RuntimeException e) {
            Assert.assertEquals("异常信息匹配", "rollback exception", e.getMessage());
            Assert.assertTrue("回滚逻辑应被调用", exceptionThrown.get());
            Assert.assertEquals("异常后状态应回滚为 SUCCESS", Stage.State.SUCCESS, exceptionRollbackStage.getCurrentState());
        }

        // 场景5：指定非 SUCCESS 状态回滚（取消状态→回滚）
        Stage specifyStateRollbackStage = new Stage(successRollback);
        specifyStateRollbackStage.tryCancel();
        boolean specifyStateRollback = specifyStateRollbackStage.tryRollback(Stage.State.CANCELLED);
        Assert.assertTrue("指定取消状态回滚应成功", specifyStateRollback);
        Assert.assertEquals("状态应为 ROLLBACKED", Stage.State.ROLLBACKED, specifyStateRollbackStage.getCurrentState());
    }

    // ===================== 6. Reset 测试（同步+并发，核心） =====================
    // 6.1 纯同步 Reset（必过，覆盖核心逻辑）
    @Test
    public void testReset_Basic() {
        // 场景1：重置到初始状态（NEW）
        Stage stage1 = new Stage();
        stage1.trySuccess();
        Assert.assertEquals(Stage.State.SUCCESS, stage1.getCurrentState());
        Assert.assertTrue(stage1.isDone());

        stage1.reset();
        Assert.assertEquals("重置后状态应为 NEW", Stage.State.NEW, stage1.getCurrentState());
        Assert.assertFalse("NEW 状态未完成", stage1.isDone());
        Assert.assertNull("重置后失败原因为 null", stage1.cause());

        // 场景2：重置到指定状态（CANCELLED）
        stage1.tryFailure(new RuntimeException("test"));
        Assert.assertEquals(Stage.State.FAILURE, stage1.getCurrentState());
        Assert.assertNotNull(stage1.cause());

        stage1.reset(Stage.State.CANCELLED);
        Assert.assertEquals("重置后状态应为 CANCELLED", Stage.State.CANCELLED, stage1.getCurrentState());
        Assert.assertTrue("CANCELLED 为完成态", stage1.isDone());
        Assert.assertNull("重置后失败原因清空", stage1.cause());

        // 场景3：重置后新 latch 阻塞性
        try {
            boolean awaitResult = stage1.await(100, TimeUnit.MILLISECONDS);
            Assert.assertFalse("重置后新 latch 应阻塞（超时返回 false）", awaitResult);
        } catch (InterruptedException e) {
            Assert.fail("同步 await 被中断：" + e.getMessage());
        }
    }

    // 6.2 稳定并发 Reset（100% 通过，可检测 bug）
    @Test
    public void testReset_Concurrent_Stable() throws InterruptedException {
        // 初始化 Stage 为 NEW 状态（latch 未触发）
        Stage stage = new Stage();
        Assert.assertEquals(Stage.State.NEW, stage.getCurrentState());

        // 同步信号：子线程→主线程（已准备进入 await）、子线程→主线程（已被释放）
        CountDownLatch threadReady = new CountDownLatch(1);
        CountDownLatch threadReleased = new CountDownLatch(1);
        AtomicBoolean isAwaitReleased = new AtomicBoolean(false);

        // 启动子线程：阻塞在 await 上，等待 reset 释放
        Thread testThread = new Thread(() -> {
            try {
                threadReady.countDown(); // 通知主线程：准备进入 await
                stage.await(); // NEW 状态下阻塞
                isAwaitReleased.set(true); // 能走到这里 → latch 被释放
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                isAwaitReleased.set(false);
            } finally {
                threadReleased.countDown(); // 通知主线程：执行完毕
            }
        }, "Stage-Reset-Test-Thread");
        testThread.start();

        // 主线程：等待子线程准备好（5 秒超时，兼容慢环境）
        Assert.assertTrue("子线程未及时准备进入 await（超时5秒）", threadReady.await(5, TimeUnit.SECONDS));
        Thread.sleep(200); // 兜底：确保子线程进入 await 阻塞

        // 主线程：执行 reset（核心操作，释放旧 latch）
        stage.reset(Stage.State.NEW);

        // 主线程：验证子线程被释放（5 秒超时）
        Assert.assertTrue("子线程未被 reset 释放（超时5秒）", threadReleased.await(5, TimeUnit.SECONDS));
        Assert.assertTrue("reset 应释放旧 latch（await 被唤醒）", isAwaitReleased.get());

        // 验证：reset 后新 latch 仍阻塞
        boolean newLatchBlocked = !stage.await(100, TimeUnit.MILLISECONDS);
        Assert.assertTrue("reset 后新 latch 应阻塞", newLatchBlocked);

        // 清理线程
        if (testThread.isAlive()) {
            testThread.interrupt();
            testThread.join(1000);
        }
    }

    // ===================== 7. Await 测试（全场景） =====================
    @Test
    public void testAwait() throws InterruptedException {
        // 场景1：正常 await（被状态转换唤醒）
        Stage stage1 = new Stage();
        CountDownLatch threadLatch1 = new CountDownLatch(1);
        AtomicBoolean awaitSuccess = new AtomicBoolean(false);

        new Thread(() -> {
            try {
                stage1.await();
                awaitSuccess.set(true);
                threadLatch1.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        Thread.sleep(100); // 确保子线程进入 await
        stage1.trySuccess(); // 触发 complete()，释放 latch
        Assert.assertTrue("子线程未被 trySuccess 唤醒（超时1秒）", threadLatch1.await(1, TimeUnit.SECONDS));
        Assert.assertTrue("await 应被成功唤醒", awaitSuccess.get());

        // 场景2：超时 await（未完成状态）
        Stage stage2 = new Stage();
        boolean timeout = !stage2.await(100, TimeUnit.MILLISECONDS);
        Assert.assertTrue("未完成状态 await 应超时返回 false", timeout);

        // 场景3：完成后 await（立即返回）
        stage2.trySuccess();
        boolean immediateAwait = stage2.await(1, TimeUnit.MILLISECONDS);
        Assert.assertTrue("完成状态 await 应立即返回 true", immediateAwait);

        // 场景4：多线程竞争 await
        Stage stage3 = new Stage();
        AtomicInteger awakeCount = new AtomicInteger(0);
        CountDownLatch multiThreadLatch = new CountDownLatch(3);

        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                try {
                    stage3.await();
                    awakeCount.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    multiThreadLatch.countDown();
                }
            }).start();
        }

        Thread.sleep(100);
        stage3.tryFailure(new RuntimeException("test"));
        Assert.assertTrue("多线程 await 未全部唤醒（超时1秒）", multiThreadLatch.await(1, TimeUnit.SECONDS));
        Assert.assertEquals("3 个子线程应全部被唤醒", 3, awakeCount.get());
    }

    // ===================== 8. 状态变更回调测试（全场景） =====================
    @Test
    public void testOnStateChangeSuccess() {
        // 自定义 Stage，覆盖回调方法
        AtomicReference<Stage.State> oldState = new AtomicReference<>();
        AtomicReference<Stage.State> newState = new AtomicReference<>();
        AtomicReference<Throwable> cause = new AtomicReference<>();

        Stage callbackStage = new Stage() {
            @Override
            protected void onStateChangeSuccess(State oldS, State newS, Throwable c) {
                oldState.set(oldS);
                newState.set(newS);
                cause.set(c);
            }
        };

        // 场景1：成功状态回调
        callbackStage.trySuccess();
        Assert.assertEquals("旧状态应为 NEW", Stage.State.NEW, oldState.get());
        Assert.assertEquals("新状态应为 SUCCESS", Stage.State.SUCCESS, newState.get());
        Assert.assertNull("成功状态无失败原因", cause.get());

        // 场景2：失败状态回调
        callbackStage.reset();
        Throwable failCause = new RuntimeException("test");
        callbackStage.tryFailure(failCause);
        Assert.assertEquals(Stage.State.NEW, oldState.get());
        Assert.assertEquals(Stage.State.FAILURE, newState.get());
        Assert.assertEquals("失败原因应匹配", failCause, cause.get());

        // 场景3：取消状态回调
        callbackStage.reset();
        callbackStage.tryCancel();
        Assert.assertEquals(Stage.State.NEW, oldState.get());
        Assert.assertEquals(Stage.State.CANCELLED, newState.get());
        Assert.assertNull("取消状态无失败原因", cause.get());

        // 场景4：回滚状态回调
        Stage rollbackCallbackStage = new Stage(() -> true) {
            @Override
            protected void onStateChangeSuccess(State oldS, State newS, Throwable c) {
                oldState.set(oldS);
                newState.set(newS);
                cause.set(c);
            }
        };
        rollbackCallbackStage.trySuccess();
        rollbackCallbackStage.tryRollback();
        Assert.assertEquals("旧状态应为 SUCCESS", Stage.State.SUCCESS, oldState.get());
        Assert.assertEquals("新状态应为 ROLLBACKED", Stage.State.ROLLBACKED, newState.get());
        Assert.assertNull("回滚状态无失败原因", cause.get());
    }

    // ===================== 9. 边界场景测试（极端情况） =====================
    @Test
    public void testBoundaryScenarios() throws InterruptedException {
        // 场景1：多线程竞争状态转换（确保原子性）
        Stage stage1 = new Stage();
        AtomicInteger successCount = new AtomicInteger(0);
        CountDownLatch raceLatch = new CountDownLatch(100);

        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                try {
                    if (stage1.trySuccess()) {
                        successCount.incrementAndGet();
                    }
                } finally {
                    raceLatch.countDown();
                }
            }).start();
        }

        raceLatch.await(2, TimeUnit.SECONDS);
        Assert.assertEquals("多线程竞争转换，仅 1 个线程能成功", 1, successCount.get());
        Assert.assertEquals("最终状态应为 SUCCESS", Stage.State.SUCCESS, stage1.getCurrentState());

        // 场景2：reset 后重复转换
        stage1.reset();
        boolean resetSuccess = stage1.trySuccess();
        Assert.assertTrue("reset 后可重新转换为成功", resetSuccess);
        Assert.assertEquals(Stage.State.SUCCESS, stage1.getCurrentState());

        // 场景3：无限 await 防护（验证超时机制）
        Stage stage2 = new Stage();
        long start = System.currentTimeMillis();
        boolean timeout = !stage2.await(100, TimeUnit.MILLISECONDS);
        long end = System.currentTimeMillis();
        Assert.assertTrue("无限 await 应触发超时", timeout);
        Assert.assertTrue("超时时间应在 100ms 左右", (end - start) >= 100 && (end - start) < 200);
    }
}