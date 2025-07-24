package run.soeasy.framework.io.watch;

import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.ElementsWrapper;
import run.soeasy.framework.core.concurrent.Poller;
import run.soeasy.framework.core.exchange.Lifecycle;
import run.soeasy.framework.core.exchange.Publisher;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.container.RegistrationException;
import run.soeasy.framework.core.exchange.container.Registry;
import run.soeasy.framework.core.exchange.event.ChangeEvent;
import run.soeasy.framework.io.FileResource;
import run.soeasy.framework.io.PathResource;

/**
 * 路径监控器，继承自{@link Poller}，实现{@link Registry<T>}、{@link Lifecycle}和{@link ElementsWrapper}接口，
 * 用于集中管理文件资源（{@link FileResource}）的监控逻辑，通过协调{@link PathPoller}和{@link WatchService}实现文件系统事件的批量处理，
 * 并将资源变更事件通过{@link Publisher}发布，适用于需要监控多个文件资源变化的场景（如目录下多文件监控、批量文件变更追踪）。
 * 
 * <p>核心职责：
 * - 作为{@link T}（{@link FileResource}）的注册中心，管理所有需要监控的文件资源；
 * - 通过{@link Poller}的轮询能力，定期从{@link WatchService}获取文件系统事件（{@link WatchKey}）；
 * - 协调内部{@link PathPoller}处理具体资源的事件，并汇总变更通过{@link Publisher}发布；
 * - 实现{@link Lifecycle}接口，提供启动监控的入口（{@link #start()}）。
 * 
 * @param <T> 监控的资源类型，必须继承自{@link FileResource}（文件系统资源）
 * @author soeasy.run
 * @see Poller
 * @see Registry
 * @see Lifecycle
 * @see PathPoller
 * @see WatchService
 */
@RequiredArgsConstructor
public class PathWatcher<T extends PathResource> extends Poller
		implements Registry<T>, Lifecycle, ElementsWrapper<T, Elements<T>> {

    /**
     * 内部注册中心，用于管理{@link PathPoller<T>}实例（每个文件资源对应一个PathPoller），
     * 负责PathPoller的注册、移除等生命周期管理，实现监控逻辑的分发。
     */
    @NonNull
    private final Registry<PathPoller<T>> registry;

    /**
     * 变更事件发布者（非空），用于将文件资源的变更事件（{@link ChangeEvent<T>}）发布给订阅者，
     * 实现监控结果的对外通知（如业务层可订阅事件进行后续处理）。
     */
    @NonNull
    private final Publisher<? super Elements<ChangeEvent<T>>> publisher;

    /**
     * 线程工厂（非空），用于创建轮询线程，控制线程的命名、优先级、守护线程属性等，
     * 确保监控线程的可管理性（如设置为守护线程避免阻塞JVM退出）。
     */
    @NonNull
    private final ThreadFactory threadFactory;

    /**
     * 文件系统监控服务（{@link WatchService}），用于接收操作系统的文件系统事件通知，
     * 是底层事件获取的核心依赖（需外部初始化并关联到当前监控器）。
     */
    private WatchService watchService;

    /**
     * 从{@link WatchService}获取事件的超时时间（默认5），结合{@link #timeUnit}使用，
     * 控制轮询时等待事件的阻塞时长（避免无限阻塞，确保线程可响应中断）。
     */
    private long timeout = 5;

    /**
     * 超时时间的单位（默认{@link TimeUnit#SECONDS}），配合{@link #timeout}使用。
     */
    private TimeUnit timeUnit = TimeUnit.SECONDS;

    /**
     * 获取所有已注册的文件资源（{@link T}）集合
     * 
     * <p>通过内部注册中心{@link #registry}的映射操作，将注册的{@link PathPoller<T>}转换为其关联的{@link T}资源，
     * 提供对监控目标资源的直接访问（如统计监控数量、展示监控列表）。
     * 
     * @return 已注册的文件资源集合（非空，可能为空集合）
     */
    @Override
    public Elements<T> getSource() {
        // 将PathPoller集合映射为其管理的FileResource集合
        return registry.map((pathPoller) -> pathPoller.getResource());
    }

    /**
     * 轮询执行的核心方法，从{@link WatchService}获取事件并分发到对应的{@link PathPoller}处理
     * 
     * <p>执行流程：
     * 1. 调用{@link WatchService#poll(long, TimeUnit)}获取文件系统事件键（{@link WatchKey}），超时则返回null；
     * 2. 若获取事件时被中断（{@link InterruptedException}），直接返回（结束本次轮询）；
     * 3. 遍历内部注册的所有{@link PathPoller<T>}，调用其{@code run(Elements.singleton(watchKey))}处理事件；
     * 4. 无论事件处理结果如何，在finally块中调用{@link PathPoller#run()}执行后续操作（如状态更新）。
     * 
     * <p>注：此方法通过轮询持续获取事件，确保文件系统的变更能被及时捕获并处理。
     */
    @Override
    public void run() {
        WatchKey watchKey;
        try {
            // 从WatchService获取事件键，超时时间为timeout和timeUnit指定的值
            watchKey = watchService.poll(timeout, timeUnit);
        } catch (InterruptedException e) {
            // 线程被中断，结束本次轮询
            return;
        }

        // 遍历所有注册的PathPoller，处理获取到的WatchKey事件
        for (PathPoller<T> poller : registry) {
            try {
                // 调用PathPoller的run方法处理当前WatchKey事件（包装为单元素集合）
                poller.run(Elements.singleton(watchKey));
            } finally {
                // 无论事件处理是否成功，执行PathPoller的常规run操作（如状态同步）
                poller.run();
            }
        }
    }

    /**
     * 启动监控器，开始轮询文件系统事件
     * 
     * <p>通过调用{@link Poller#startEndlessLoop(long, TimeUnit, ThreadFactory)}启动轮询线程，
     * 周期设为0秒（立即开始，轮询间隔由{@link #run()}中的超时控制），使用指定的{@link #threadFactory}创建线程，
     * 确保监控逻辑在独立线程中持续运行。
     */
    public void start() {
        // 启动循环轮询，初始延迟0秒，使用指定线程工厂创建线程
        startEndlessLoop(0, TimeUnit.SECONDS, threadFactory);
    }

    /**
     * 批量注册文件资源（{@link T}）到监控器
     * 
     * <p>注册逻辑：
     * 1. 对输入的每个{@link T}资源，创建对应的{@link PathPoller<T>}（关联资源和事件发布者{@link #publisher}）；
     * 2. 若监控器尚未运行（{@link #isRunning()}为false），调用{@link #start()}启动监控；
     * 3. 将创建的{@link PathPoller<T>}注册到内部{@link #registry}，完成资源与监控逻辑的绑定。
     * 
     * @param elements 待注册的文件资源集合（非空，包含一个或多个T实例）
     * @return 注册结果（{@link Registration}），表示批量注册的状态
     * @throws RegistrationException 注册过程中发生异常时抛出（如资源已注册、内部registry错误）
     */
    @Override
    public Registration registers(Elements<? extends T> elements) throws RegistrationException {
        // 批量处理每个元素：创建PathPoller并注册，若未启动则启动监控
        return Registration.registers(elements, (element) -> {
            PathPoller<T> pathPoller = new PathPoller<>(element, publisher);
            if (!isRunning()) {
                start();
            }
            return registry.register(pathPoller);
        });
    }

    // 以下方法为接口默认实现或未展示的Lifecycle/Registry方法，根据实际场景补充：
    // - 如stop()方法（实现Lifecycle）：停止轮询并清理资源
    // - 如unregister(T)方法（实现Registry）：移除指定资源的监控
}