package run.soeasy.framework.core.concurrent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 轮询器抽象类，提供基于不同调度机制的周期性任务执行能力，
 * 支持三种调度方式：线程循环休眠、ScheduledExecutorService、Timer，
 * 用于需要定期执行任务的场景（如资源变更检测、定时数据同步等）。
 * 
 * <p>核心特性：
 * - 统一管理任务的启动/停止状态，确保线程安全；
 * - 支持多种调度实现，可根据场景选择（如高并发场景优先ScheduledExecutorService）；
 * - 内置状态检查与资源释放逻辑，避免重复启动和资源泄漏。
 * 
 * @author soeasy.run
 * @see Runnable
 * @see ScheduledExecutorService
 * @see Timer
 */
public abstract class Poller implements Runnable {

    /**
     * 默认的Timer实例（守护线程模式），懒加载创建，用于通过Timer方式调度任务，
     * 全局共享以减少线程创建开销，并在JVM退出时自动取消。
     */
    private static volatile Timer defaultTimer;

    /**
     * 获取全局默认的Timer实例（单例），采用双重检查锁定确保线程安全，
     * 并注册JVM关闭钩子以在程序退出时释放资源。
     * 
     * @return 全局唯一的Timer实例（非空，守护线程模式）
     */
    private static Timer getDefaultTimer() {
        if (defaultTimer == null) {
            synchronized (Poller.class) {
                if (defaultTimer == null) {
                    // 创建守护线程的Timer，避免阻塞JVM退出
                    defaultTimer = new Timer(Poller.class.getName(), true);
                    // 注册关闭钩子，JVM退出时取消Timer任务
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> defaultTimer.cancel()));
                }
            }
        }
        return defaultTimer;
    }

    /**
     * 用于循环执行任务的线程（对应startEndlessLoop方式）
     */
    private volatile Thread endlessLoopThread;

    /**
     * ScheduledExecutorService的调度结果（对应startScheduled方式）
     */
    private volatile ScheduledFuture<?> scheduledFuture;

    /**
     * Timer的任务实例（对应startTimerTask方式）
     */
    private volatile TimerTask timerTask;

    /**
     * 判断当前轮询器是否处于运行状态（任一调度方式正在执行）
     * 
     * @return 运行中返回true，已停止返回false
     */
    public boolean isRunning() {
        synchronized (this) {
            // 任一调度组件非空即视为运行中
            return timerTask != null || scheduledFuture != null || endlessLoopThread != null;
        }
    }

    /**
     * 使用默认线程工厂启动循环休眠式轮询（基于Thread.sleep）
     * 
     * @param period 轮询周期（大于0）
     * @param timeUnit 周期单位（非空）
     */
    public void startEndlessLoop(long period, TimeUnit timeUnit) {
        startEndlessLoop(period, timeUnit, Executors.defaultThreadFactory());
    }

    /**
     * 使用指定线程工厂启动循环休眠式轮询（基于Thread.sleep），
     * 适用于简单场景，通过线程休眠控制执行间隔。
     * 
     * <p>实现逻辑：
     * 1. 创建新线程，在循环中休眠指定周期后执行{@link #run()}；
     * 2. 线程被中断时退出循环，任务停止；
     * 3. 线程设为守护线程，避免阻塞JVM退出。
     * 
     * @param period 轮询周期（大于0）
     * @param timeUnit 周期单位（非空）
     * @param threadFactory 线程工厂（非空，用于创建任务线程）
     */
    public void startEndlessLoop(long period, TimeUnit timeUnit, ThreadFactory threadFactory) {
        if (isRunning()) {
            return; // 已运行则直接返回，避免重复启动
        }

        synchronized (this) {
            if (endlessLoopThread == null) {
                endlessLoopThread = threadFactory.newThread(() -> {
                    // 循环执行，直到线程被中断
                    while (!endlessLoopThread.isInterrupted()) {
                        try {
                            // 休眠指定周期
                            timeUnit.sleep(period);
                        } catch (InterruptedException e) {
                            // 捕获中断异常，退出循环
                            break;
                        }
                        // 执行具体任务（子类实现）
                        Poller.this.run();
                    }
                });
                // 设置为守护线程，随主线程退出而终止
                endlessLoopThread.setDaemon(true);
                endlessLoopThread.start();
            }
        }
    }

    /**
     * 使用ScheduledExecutorService启动调度任务，适用于多任务并发场景，
     * 相比Timer具有更好的线程安全和异常处理机制。
     * 
     * @param period 轮询周期（大于0）
     * @param timeUnit 周期单位（非空）
     * @param scheduledExecutorService 调度线程池（非空，由外部管理生命周期）
     */
    public void startScheduled(long period, TimeUnit timeUnit, ScheduledExecutorService scheduledExecutorService) {
        if (isRunning()) {
            return; // 已运行则直接返回，避免重复启动
        }

        synchronized (this) {
            if (scheduledFuture == null) {
                // 使用固定延迟调度：以上一次任务结束时间为基准计算下一次执行时间
                scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(
                    this,   // 任务实例（当前Poller）
                    period, // 初始延迟
                    period, // 周期间隔
                    timeUnit // 时间单位
                );
            }
        }
    }

    /**
     * 使用默认Timer启动定时任务，适用于简单的定时场景
     * 
     * @param period 轮询周期（大于0）
     * @param timeUnit 周期单位（非空）
     */
    public void startTimerTask(long period, TimeUnit timeUnit) {
        startTimerTask(period, timeUnit, getDefaultTimer());
    }

    /**
     * 使用指定Timer启动定时任务，基于传统Timer实现，
     * 注意：Timer在任务抛出异常时会终止整个调度线程，需自行处理异常。
     * 
     * @param period 轮询周期（大于0）
     * @param timeUnit 周期单位（非空）
     * @param timer 外部Timer实例（非空，由外部管理生命周期）
     */
    public void startTimerTask(long period, TimeUnit timeUnit, Timer timer) {
        if (isRunning()) {
            return; // 已运行则直接返回，避免重复启动
        }

        synchronized (this) {
            if (timerTask == null) {
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        // 执行具体任务（子类实现）
                        Poller.this.run();
                    }
                };
                // 转换周期为毫秒，延迟period后开始，每period执行一次
                long time = timeUnit.toMillis(period);
                timer.schedule(timerTask, time, time);
            }
        }
    }

    /**
     * 停止当前轮询器的所有任务，释放相关资源，线程安全
     * 
     * <p>处理逻辑：
     * 1. 中断循环线程（endlessLoopThread）并置空；
     * 2. 取消ScheduledFuture（不中断正在执行的任务）并置空；
     * 3. 取消TimerTask并置空。
     */
    public void stop() {
        if (!isRunning()) {
            return; // 未运行则直接返回
        }

        synchronized (this) {
            // 停止循环线程
            if (endlessLoopThread != null) {
                endlessLoopThread.interrupt();
                endlessLoopThread = null;
            }

            // 停止Scheduled任务
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false); // 不中断正在执行的任务
                scheduledFuture = null;
            }

            // 停止Timer任务
            if (timerTask != null) {
                timerTask.cancel();
                timerTask = null;
            }
        }
    }
}