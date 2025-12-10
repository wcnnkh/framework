package run.soeasy.framework.io.watch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import lombok.NonNull;
import run.soeasy.framework.core.exchange.ChangeEvent;
import run.soeasy.framework.core.exchange.ChangeType;
import run.soeasy.framework.core.exchange.Operation;
import run.soeasy.framework.core.exchange.Publisher;
import run.soeasy.framework.core.streaming.Streamable;
import run.soeasy.framework.io.PathResource;
import run.soeasy.framework.logging.LogManager;
import run.soeasy.framework.logging.Logger;

/**
 * 路径资源轮询器，基于JDK {@link WatchService} 实现文件系统事件驱动监控，同时继承 {@link ResourcePoller}
 * 提供定期轮询兜底能力，专为 {@link PathResource}（文件/目录资源）实现创建、删除、修改事件的捕获、转换与发布。
 * 
 * <h3>核心特性</h3>
 * <ul>
 * <li><b>双重监控机制</b>：WatchService 事件驱动（实时捕获文件系统原生事件）+ 父类定期轮询（兜底检查资源状态，避免事件丢失）；</li>
 * <li><b>事件标准化</b>：将 {@link StandardWatchEventKinds}（ENTRY_CREATE/DELETE/MODIFY）转换为统一的 {@link ChangeType}；</li>
 * <li><b>路径适配</b>：自动向上查找有效父目录（WatchService 仅支持监控目录），适配文件/目录两种监控场景；</li>
 * <li><b>生命周期安全</b>：通过 {@link WatchKeyRegistry} 管理 {@link WatchKey} 注册/清理，避免资源泄漏；</li>
 * <li><b>线程安全</b>：懒加载的监控路径（watchable）采用volatile + 双重检查锁初始化，WatchKey 管理线程安全；</li>
 * </ul>
 * 
 * <h3>适用场景</h3>
 * 适用于需要实时监控文件/目录变更的场景（如配置文件热加载、目录文件新增/删除监听），兼顾实时性与可靠性。
 * 
 * @param <T> 资源类型，必须继承自 {@link PathResource}，确保具备路径操作能力
 * @author soeasy.run
 * @see ResourcePoller
 * @see PathResource
 * @see WatchService
 * @see WatchEvent
 * @see ChangeEvent
 * @see WatchKeyRegistry
 */
public class PathPoller<T extends PathResource> extends ResourcePoller<T> {
	private static final Logger logger = LogManager.getLogger(PathPoller.class);

	/**
	 * 可被WatchService监控的目录路径（WatchService仅支持监控目录，文件需关联其父目录）。
	 * <p>
	 * 懒加载初始化：首次调用 {@link #getWatchable()} 时确定，采用volatile + 双重检查锁保证线程安全，
	 * 后续调用直接返回缓存结果。
	 */
	private volatile Path watchable;

	/**
	 * WatchKey注册中心，管理当前Poller关联的所有{@link WatchKey}（监控键）。
	 * <p>
	 * 初始容量设为2（默认仅监控一个目录，预留扩展空间），线程安全管理WatchKey的注册/注销。
	 */
	private final WatchKeyRegistry watchKeyRegistry = new WatchKeyRegistry();

	/**
	 * 初始化路径资源轮询器，绑定待监控资源与事件发布者。
	 * 
	 * @param variable            待监控的路径资源（非空，支持文件/目录类型）
	 * @param changeEventProducer 变更事件发布者（非空，用于发布转换后的{@link ChangeEvent}）
	 * @throws NullPointerException variable 或 changeEventProducer 为null时抛出
	 */
	public PathPoller(@NonNull T variable, @NonNull Publisher<? super Streamable<ChangeEvent<T>>> changeEventProducer) {
		super(variable, changeEventProducer);
	}

	/**
	 * 将文件系统原生事件（{@link WatchEvent}）转换为标准化的资源变更事件（{@link ChangeEvent}）。
	 * <p>
	 * 核心转换规则：
	 * <ol>
	 * <li>事件类型映射：ENTRY_CREATE→CREATE、ENTRY_DELETE→DELETE、ENTRY_MODIFY→UPDATE，未知类型（如OVERFLOW）返回null；</li>
	 * <li>路径匹配规则（eventPath为相对监控目录的路径）：
	 *   <ul>
	 *   <li>监控目录：eventPath为该目录的子路径即视为相关事件；</li>
	 *   <li>监控文件：eventPath需与文件的绝对/相对路径完全匹配才视为相关事件；</li>
	 *   </ul>
	 * </li>
	 * <li>非匹配/未知事件返回null，实现无效事件过滤。</li>
	 * </ol>
	 * 
	 * @param watchEvent 文件系统原生事件（非空，包含事件类型与关联路径）
	 * @return 标准化的ChangeEvent，非匹配/未知事件返回null
	 * @throws NullPointerException watchEvent为null时抛出
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
	 * 获取可被WatchService监控的有效目录路径（懒加载+双重检查锁实现线程安全）。
	 * <p>
	 * 查找逻辑：
	 * <ol>
	 * <li>从监控资源的路径（{@link PathResource#getPath()}）开始向上遍历父目录；</li>
	 * <li>找到第一个存在且为目录的路径（WatchService仅支持监控目录）；</li>
	 * <li>无有效目录时返回null（监控失效）。</li>
	 * </ol>
	 * 
	 * @return 可监控的目录路径，无有效目录返回null
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
	 * 获取当前Poller的WatchKey注册中心，用于管理监控键的生命周期。
	 * <p>
	 * 注册中心负责WatchKey的注册、注销与有效性检查，避免无效WatchKey导致的监控失效。
	 * 
	 * @return WatchKey注册中心（非空，线程安全）
	 */
	public WatchKeyRegistry getWatchKeyRegistry() {
		return watchKeyRegistry;
	}

	/**
	 * 向指定WatchService注册监控目录，创建新的WatchKey。
	 * <p>
	 * 注册的事件类型：CREATE（创建）、DELETE（删除）、MODIFY（修改），覆盖文件系统核心变更场景。
	 * <p>
	 * 注：注册过程中发生的IO异常会被捕获并打印日志，最终返回null。
	 * 
	 * @param watchService 文件系统监控服务（非空）
	 * @return 新创建的WatchKey，无有效监控目录/注册失败（如权限不足、目录不存在）返回null
	 * @throws NullPointerException watchService为null时抛出
	 */
	protected WatchKey newWatchKey(WatchService watchService) {
		Path watchableDir = getWatchable();
		if (watchableDir == null) {
			return null; // 无有效监控目录，注册失败
		}

		try {
			// 注册监控事件：创建、删除、修改
			return watchableDir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
		} catch (IOException e) {
			logger.error(e, "Failed to register watch for resource: {}", getResource().getDescription());
			return null;
		}
	}

	/**
	 * 将文件系统事件集合转换为标准化ChangeEvent并发布。
	 * <p>
	 * 处理流程：
	 * <ol>
	 * <li>通过{@link #eventConvert}将WatchEvent转换为ChangeEvent；</li>
	 * <li>过滤null事件（无效/不匹配事件）；</li>
	 * <li>通过事件发布者发布转换后的事件流。</li>
	 * </ol>
	 * 
	 * @param watchEvents 待处理的文件系统事件集合（非空）
	 * @return 事件发布结果（{@link Operation}），包含发布状态/结果
	 * @throws NullPointerException watchEvents为null时抛出
	 */
	private Operation publishWatchEvents(Streamable<WatchEvent<Path>> watchEvents) {
		// 过滤并转换为ChangeEvent，再发布
		return getChangeEventProducer().publish(watchEvents.map(this::eventConvert).filter(event -> event != null));
	}

	/**
	 * 将当前PathPoller注册到指定WatchService，管理生成的WatchKey。
	 * <p>
	 * 注册成功后，WatchKey会被加入{@link #watchKeyRegistry}统一管理；注册失败返回failure类型的Operation。
	 * 
	 * @param watchService 文件系统监控服务（非空）
	 * @return 注册结果：成功返回{@link WatchKeyRegistry#register(WatchKey)}的执行结果，失败返回含异常的failure
	 * @throws NullPointerException watchService为null时抛出
	 */
	public Operation registerTo(WatchService watchService) {
		WatchKey watchKey = newWatchKey(watchService);
		if (watchKey == null) {
			return Operation.failure(new RuntimeException("注册失败：无有效监控目录或WatchService注册失败")); // 注册失败
		}
		// 将WatchKey注册到内部管理中心
		return watchKeyRegistry.register(watchKey);
	}

	/**
	 * 重写父类轮询核心方法，整合WatchKey事件处理与定期兜底检查。
	 * <p>
	 * 执行逻辑（保证兜底检查必执行）：
	 * <ol>
	 * <li>先处理注册中心中所有WatchKey的待处理事件（实时事件驱动）；</li>
	 * <li>finally块调用父类{super.run()}执行定期轮询（兜底检查资源状态，避免事件丢失）。</li>
	 * </ol>
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
	 * 批量处理WatchKey集合，提取事件并发布标准化ChangeEvent。
	 * <p>
	 * 处理逻辑：对每个WatchKey创建{@link WatchKeyPoller}，提取待处理事件，
	 * 通过{@link #publishWatchEvents}完成事件转换与发布；空集合无任何处理逻辑。
	 * 
	 * @param watchKeys 待处理的WatchKey集合（非空，空集合无操作）
	 * @throws NullPointerException watchKeys为null时抛出
	 */
	public void run(Streamable<? extends WatchKey> watchKeys) {
		watchKeys.forEach((watchKey) -> {
			// 创建WatchKeyPoller处理事件，使用当前类的publishWatchEvents作为发布者
			WatchKeyPoller<Path> keyPoller = new WatchKeyPoller<>(watchKey, Path.class, this::publishWatchEvents);
			keyPoller.run(); // 执行事件提取与发布
		});
	}
}