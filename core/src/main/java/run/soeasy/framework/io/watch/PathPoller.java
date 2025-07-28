package run.soeasy.framework.io.watch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Publisher;
import run.soeasy.framework.core.exchange.Receipt;
import run.soeasy.framework.core.exchange.container.PayloadRegistration;
import run.soeasy.framework.core.exchange.event.ChangeEvent;
import run.soeasy.framework.core.exchange.event.ChangeType;
import run.soeasy.framework.io.PathResource;
import run.soeasy.framework.logging.LogManager;
import run.soeasy.framework.logging.Logger;

/**
 * 路径资源轮询器，继承自{@link ResourcePoller}，专为{@link PathResource}（文件或目录）提供基于{@link WatchService}的变更监控，
 * 结合文件系统事件（{@link WatchEvent}）与定期轮询机制，实现对路径资源的创建、删除、修改事件的捕获与转换，
 * 并通过事件发布者将标准化的{@link ChangeEvent}对外发布。
 * 
 * <p>核心设计：
 * - 双重监控机制：基于{@link WatchService}的事件驱动（实时捕获文件系统事件）+ 父类{@link ResourcePoller}的定期轮询（兜底检查）；
 * - 事件转换：将{@link StandardWatchEventKinds}（ENTRY_CREATE/DELETE/MODIFY）转换为统一的{@link ChangeType}；
 * - 生命周期管理：通过{@link WatchKeyRegistry}管理{@link WatchKey}的注册与清理，确保监控资源有效释放。
 * 
 * @param <T> 资源类型，必须继承自{@link PathResource}（支持路径操作的文件资源）
 * @author soeasy.run
 * @see ResourcePoller
 * @see PathResource
 * @see WatchService
 * @see WatchEvent
 * @see ChangeEvent
 */
public class PathPoller<T extends PathResource> extends ResourcePoller<T> {
    private static final Logger logger = LogManager.getLogger(PathPoller.class);

    /**
     * 可监控的路径（通常是目录），WatchService只能监控目录，因此需向上查找存在的父目录作为监控点，
     * 懒加载初始化（首次调用{@link #getWatchable()}时确定）。
     */
    private volatile Path watchable;

    /**
     * WatchKey注册中心，用于管理当前Poller关联的{@link WatchKey}（监控键），
     * 初始容量设为2（多数场景下仅需监控一个目录，预留扩展空间）。
     */
    private final WatchKeyRegistry watchKeyRegistry = new WatchKeyRegistry(2);

    /**
     * 初始化路径资源轮询器，指定待监控的资源和事件发布者
     * 
     * @param variable 待监控的路径资源（非空，文件或目录）
     * @param changeEventProducer 变更事件发布者（非空，用于发布转换后的{@link ChangeEvent}）
     */
    public PathPoller(@NonNull T variable, @NonNull Publisher<? super Elements<ChangeEvent<T>>> changeEventProducer) {
        super(variable, changeEventProducer);
    }

    /**
     * 将文件系统事件（{@link WatchEvent}）转换为资源变更事件（{@link ChangeEvent<T>}）
     * 
     * <p>转换逻辑：
     * 1. 根据{@link WatchEvent#kind()}映射{@link ChangeType}（CREATE/DELETE/UPDATE）；
     * 2. 路径匹配检查：
     *    - 若监控的是目录（{@link PathResource#getPath()}为目录），则子路径前缀匹配即视为相关事件；
     *    - 若监控的是文件，则需路径完全匹配才视为相关事件；
     * 3. 不匹配或未知事件类型返回null（过滤无关事件）。
     * 
     * @param watchEvent 文件系统事件（非空，包含事件类型和上下文路径）
     * @return 转换后的变更事件，不匹配则返回null
     */
    private ChangeEvent<T> eventConvert(WatchEvent<Path> watchEvent) {
        ChangeType eventType = null;
        // 映射WatchEvent类型到ChangeType
        if (StandardWatchEventKinds.ENTRY_CREATE.equals(watchEvent.kind())) {
            eventType = ChangeType.CREATE;
        } else if (StandardWatchEventKinds.ENTRY_DELETE.equals(watchEvent.kind())) {
            eventType = ChangeType.DELETE;
        } else if (StandardWatchEventKinds.ENTRY_MODIFY.equals(watchEvent.kind())) {
            eventType = ChangeType.UPDATE;
        }

        if (eventType == null) {
            return null; // 忽略未知事件类型（如OVERFLOW）
        }

        Path sourcePath = getResource().getPath();
        Path eventPath = watchEvent.context(); // 事件关联的路径（相对监控目录的路径）

        // 目录资源：子路径前缀匹配即视为相关事件
        if (Files.isDirectory(sourcePath)) {
            if (eventPath.startsWith(sourcePath)) {
                return new ChangeEvent<>(getResource(), eventType);
            }
        } 
        // 文件资源：路径完全匹配才视为相关事件
        else {
            if (eventPath.equals(sourcePath)) {
                return new ChangeEvent<>(getResource(), eventType);
            }
        }
        return null; // 路径不匹配，过滤该事件
    }

    /**
     * 获取可被WatchService监控的路径（必须是存在的目录）
     * 
     * <p>查找逻辑：
     * 1. 从监控资源的路径（{@link PathResource#getPath()}）开始，向上遍历父目录；
     * 2. 找到第一个存在且为目录的路径作为监控点（WatchService仅支持监控目录）；
     * 3. 懒加载初始化（首次调用时确定，后续直接返回缓存结果）。
     * 
     * @return 可监控的目录路径，若不存在则返回null
     */
    public Path getWatchable() {
        if (watchable == null) {
            synchronized (this) {
                if (watchable == null) {
                    Path currentPath = getResource().getPath();
                    // 向上查找存在的目录（WatchService只能监控目录）
                    while (currentPath != null) {
                        if (Files.exists(currentPath) && Files.isDirectory(currentPath)) {
                            break; // 找到有效监控目录
                        }
                        currentPath = currentPath.getParent(); // 向上级目录查找
                    }
                    this.watchable = currentPath;
                }
            }
        }
        return watchable;
    }

    /**
     * 获取当前Poller的WatchKey注册中心，用于管理监控键的生命周期
     * 
     * @return WatchKey注册中心（非空）
     */
    public WatchKeyRegistry getWatchKeyRegistry() {
        return watchKeyRegistry;
    }

    /**
     * 在指定的{@link WatchService}上注册监控，创建新的{@link WatchKey}
     * 
     * <p>注册逻辑：
     * 1. 通过{@link #getWatchable()}获取可监控目录；
     * 2. 向WatchService注册该目录，监控CREATE/DELETE/MODIFY事件；
     * 3. 注册失败（如目录不存在、权限不足）返回null，否则返回创建的WatchKey。
     * 
     * @param watchService 文件系统监控服务（非空）
     * @return 新创建的WatchKey，失败则返回null
     */
    protected WatchKey newWatchKey(WatchService watchService) {
        Path watchableDir = getWatchable();
        if (watchableDir == null) {
            return null; // 无有效监控目录，注册失败
        }

        try {
            // 注册监控事件：创建、删除、修改
            return watchableDir.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY
            );
        } catch (IOException e) {
            logger.error(e, "Failed to register watch for resource: {}", getResource().getDescription());
            return null;
        }
    }

    /**
     * 发布文件系统事件集合（{@link WatchEvent}），转换为{@link ChangeEvent}后通过发布者发布
     * 
     * @param watchEvents 待发布的文件系统事件集合（非空）
     * @return 发布结果（{@link Receipt}），表示事件发布的状态
     */
    private Receipt publishWatchEvents(Elements<WatchEvent<Path>> watchEvents) {
        // 过滤并转换为ChangeEvent，再发布
        Elements<ChangeEvent<T>> changeEvents = watchEvents
            .map(this::eventConvert)
            .filter(event -> event != null) // 过滤无效事件
            .toList();
        return getChangeEventProducer().publish(changeEvents);
    }

    /**
     * 将当前PathPoller注册到指定的{@link WatchService}，管理生成的{@link WatchKey}
     * 
     * @param watchService 文件系统监控服务（非空）
     * @return 注册结果（{@link PayloadRegistration}），成功则包含注册的WatchKey，失败则为failure
     */
    public PayloadRegistration<WatchKey> registerTo(WatchService watchService) {
        WatchKey watchKey = newWatchKey(watchService);
        if (watchKey == null) {
            return PayloadRegistration.failure(); // 注册失败
        }
        // 将WatchKey注册到内部管理中心
        return watchKeyRegistry.register(watchKey);
    }

    /**
     * 重写轮询方法，结合WatchKey事件处理与父类的定期检查
     * 
     * <p>执行流程：
     * 1. 调用{@link #run(Iterable)}处理注册中心中的所有WatchKey；
     * 2. 最终调用父类{@link ResourcePoller#run()}执行定期轮询（兜底检查资源状态）。
     */
    @Override
    public void run() {
        try {
            // 处理注册的WatchKey事件
            run(watchKeyRegistry);
        } finally {
            // 执行父类的轮询逻辑（基于lastModified的兜底检查）
            super.run();
        }
    }

    /**
     * 处理指定集合中的{@link WatchKey}，提取事件并发布
     * 
     * <p>处理逻辑：
     * 对每个WatchKey创建{@link WatchKeyPoller}，调用其{@link WatchKeyPoller#run()}提取事件，
     * 通过{@link #publishWatchEvents}发布转换后的变更事件。
     * 
     * @param watchKeys 待处理的WatchKey集合（非空）
     */
    public void run(Iterable<? extends WatchKey> watchKeys) {
        for (WatchKey watchKey : watchKeys) {
            // 创建WatchKeyPoller处理事件，使用当前类的publishWatchEvents作为发布者
            WatchKeyPoller<Path> keyPoller = new WatchKeyPoller<>(
                watchKey,
                Path.class,
                this::publishWatchEvents
            );
            keyPoller.run(); // 执行事件提取与发布
        }
    }
}