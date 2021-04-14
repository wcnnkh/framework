package scw.logger;

import java.util.SortedMap;
import java.util.logging.Level;

import scw.event.ChangeEvent;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.lang.Nullable;

public abstract class AbstractLogger implements Logger {
	protected final String placeholder;
	private volatile Level level;
	private final String name;
	private EventRegistration eventRegistration;

	public AbstractLogger(String name, @Nullable String placeholder) {
		this.level = LoggerLevelManager.getInstance().getLevel(name);
		this.name = name;
		this.placeholder = placeholder;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	protected void finalize() throws Throwable {
		if (eventRegistration != null) {
			eventRegistration.unregister();
		}
		eventRegistration = null;
		super.finalize();
	}

	/**
	 * 注册对日志Level变更的监听
	 * 
	 * @return 如果已经注册过了就返回false, 否则返回true
	 */
	public synchronized boolean registerLevelListener() {
		if (eventRegistration != null) {
			// 已经注册过吧
			return false;
		}

		eventRegistration = LoggerLevelManager
				.getInstance()
				.getRegistry()
				.registerListener(
						new EventListener<ChangeEvent<SortedMap<String, Level>>>() {

							public void onEvent(
									ChangeEvent<SortedMap<String, Level>> event) {
								Level level = LoggerLevelManager.getInstance()
										.getLevel(getName());
								if (!level.equals(getLevel())) {
									setLevel(level);
								}
							}
						});
		return true;
	}

	@Nullable
	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		// 这里使用off是为了任意日志级别都会显示该日志
		log(Level.OFF, "Level [{}] change to [{}]", getLevel(), level);
		this.level = level;
	}

	public String getPlaceholder() {
		return placeholder;
	}
	
	public boolean isLoggable(Level level) {
		return CustomLevel.isGreaterOrEqual(level, getLevel());
	}
}
