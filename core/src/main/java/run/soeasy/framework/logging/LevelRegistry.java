package run.soeasy.framework.logging;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;

import lombok.NonNull;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.match.StringMatcher;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 日志级别注册表，实现{@link LevelFactory} 提供日志级别配置的管理、匹配和动态加载功能，支持按名称规则批量设置日志级别。
 * 
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>级别管理：存储和管理日志级别配置，支持按名称匹配</li>
 * <li>匹配策略：通过{@link StringMatcher}定义名称匹配规则（默认前缀匹配）</li>
 * <li>默认级别：支持设置默认日志级别，当无匹配时使用</li>
 * </ul>
 * 
 * <p>
 * <b>匹配规则说明：</b>
 * <ul>
 * <li>精确匹配：名称完全相同则直接返回对应级别</li>
 * <li>规则匹配：通过StringMatcher实现自定义匹配（如前缀、后缀、正则等）</li>
 * <li>优先级：精确匹配 &gt; 规则匹配 &gt; 默认级别</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see LevelFactory
 * @see Reloadable
 * @see StringMatcher
 */
public class LevelRegistry implements LevelFactory {
	/** 名称匹配策略（volatile保证可见性），默认使用前缀匹配 */
	@NonNull
	private volatile StringMatcher nameMatcher = StringMatcher.prefix();
	/** 级别配置映射表（线程安全访问），按匹配规则排序 */
	private volatile Map<String, Level> levelMap;
	/** 默认日志级别（无匹配时使用），不可为null */
	@NonNull
	private volatile Level defaultLevel = CustomLevel.INFO;

	/**
	 * 获取名称匹配策略。
	 * 
	 * @return 当前名称匹配器
	 */
	public StringMatcher getNameMatcher() {
		return nameMatcher;
	}

	/**
	 * 设置名称匹配策略并触发配置重新加载。
	 * <p>
	 * 同步更新匹配器并重新加载级别配置，确保一致性。
	 * 
	 * @param nameMatcher 新匹配策略，不可为null
	 */
	public void setNameMatcher(@NonNull StringMatcher nameMatcher) {
		synchronized (this) {
			this.nameMatcher = nameMatcher;
			updateRegistry();
		}
	}

	protected synchronized void updateRegistry() {
		if (levelMap == null) {
			return;
		}
		Map<String, Level> backMap = new LinkedHashMap<>(levelMap);
		levelMap = new TreeMap<String, Level>(nameMatcher);
		levelMap.putAll(backMap);
	}

	/**
	 * 获取所有级别配置（不可变视图）。
	 * <p>
	 * 返回Elements包装的键值对集合，键为名称，值为对应级别。
	 * 
	 * @return 级别配置元素集合
	 */
	public Streamable<KeyValue<String, Level>> getLevels() {
		synchronized (this) {
			if (levelMap == null) {
				return Streamable.empty();
			}
			return Streamable.of(levelMap.entrySet()).map((e) -> KeyValue.of(e.getKey(), e.getValue()));
		}
	}

	/**
	 * 设置指定名称的日志级别（支持匹配规则）。
	 * <p>
	 * 同步操作确保线程安全，先移除所有匹配旧名称的配置， 再添加新配置，避免旧规则干扰。
	 * 
	 * @param name  日志器名称（支持匹配模式），不可为null
	 * @param level 目标级别，不可为null
	 */
	public void setLevel(@NonNull String name, Level level) {
		synchronized (this) {
			if (levelMap == null) {
				levelMap = new TreeMap<String, Level>(nameMatcher);
			} else {
				Iterator<Entry<String, Level>> iterator = levelMap.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<String, Level> entry = iterator.next();
					if (StringUtils.equals(entry.getKey(), name)) {
						continue;
					}
					if (match(name, entry.getKey())) {
						iterator.remove();
					}
				}
			}
			levelMap.put(name, level);
		}
	}

	/**
	 * 判断名称是否匹配模式。
	 * <p>
	 * 先检查精确匹配，再使用匹配器判断规则匹配， 支持自定义匹配策略。
	 * 
	 * @param pattern 匹配模式
	 * @param name    待匹配名称
	 * @return true表示匹配成功
	 */
	public boolean match(String pattern, String name) {
		return StringUtils.equals(pattern, name) || nameMatcher.match(pattern, name);
	}

	/**
	 * 内部获取匹配级别的方法。
	 * <p>
	 * 按顺序检查精确匹配、规则匹配，返回第一个匹配的级别。
	 * 
	 * @param name 日志器名称
	 * @return 匹配的级别，未找到返回null
	 */
	private Level internalGetLevel(String name) {
		Level level = levelMap.get(name);
		if (level != null) {
			return level;
		}

		for (Entry<String, Level> entry : levelMap.entrySet()) {
			if (match(entry.getKey(), name)) {
				return entry.getValue();
			}
		}
		return null;
	}

	/**
	 * 获取指定名称的日志级别。
	 * <p>
	 * 先查找匹配的级别配置，未找到时返回默认级别， 确保不会返回null。
	 * 
	 * @param name 日志器名称，不可为null
	 * @return 匹配的级别或默认级别
	 */
	@Override
	public Level getLevel(@NonNull String name) {
		Level level = levelMap == null ? null : internalGetLevel(name);
		return level == null ? defaultLevel : level;
	}
}