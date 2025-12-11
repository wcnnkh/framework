package run.soeasy.framework.core.function;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.exchange.CollectionContainer;
import run.soeasy.framework.core.exchange.Operation;

/**
 * 泛型谓词注册表 基于策略模式实现规则解耦，支持内置枚举规则/自定义规则，底层用CopyOnWriteArrayList保证并发安全。
 *
 * @param <T> 谓词匹配的目标类型
 * @author soeasy.run
 */
@Getter
public class PredicateRegistry<T> extends
		CollectionContainer<Predicate<? super T>, CopyOnWriteArrayList<Predicate<? super T>>> implements Predicate<T> {

	/**
	 * 匹配规则接口（策略模式核心） 自定义规则只需实现此接口，无需修改注册表核心逻辑。
	 */
	public static interface MatchRule {
		/**
		 * 规则匹配判断
		 * 
		 * @param totalCount 注册表中总谓词数量
		 * @param matchCount 匹配成功的谓词数量
		 * @return 符合规则返回true，否则返回false
		 */
		boolean match(long totalCount, long matchCount);
	}

	/**
	 * 内置常用匹配规则（枚举实现MatchRule，轻量化）
	 */
	@RequiredArgsConstructor
	@Getter
	public static enum MatchRules implements MatchRule {
		/** 全部匹配：匹配数量=总数量（空注册表返回false） */
		ALL((total, match) -> total > 0 && match == total),
		/** 任意匹配：匹配数量&gt;0（空注册表返回false） */
		ANY((total, match) -> match > 0),
		/** 无匹配：匹配数量=0（空注册表返回true） */
		NONE((total, match) -> match == 0),
		/** 恰好1个匹配：匹配数量=1 */
		EXACTLY_ONE((total, match) -> match == 1),
		/** 至少2个匹配：匹配数量≥2 */
		AT_LEAST_TWO((total, match) -> match >= 2),
		/** 最多2个匹配：匹配数量≤2 */
		AT_MOST_TWO((total, match) -> match <= 2),
		/** 多数匹配：匹配数量&gt;总数量的50%（空注册表返回false） */
		MAJORITY((total, match) -> total > 0 && match > total * 0.5),
		/** 非全部匹配：匹配数量≠总数量（空注册表返回true） */
		NOT_ALL((total, match) -> match != total),
		/** 除1个外全匹配：匹配数量=总数量-1（空注册表返回false） */
		ALL_BUT_ONE((total, match) -> total > 0 && match == total - 1);

		private final MatchRule matchRule;

		@Override
		public boolean match(long totalCount, long matchCount) {
			return matchRule.match(totalCount, matchCount);
		}
	}

	/** 核心匹配规则（支持内置枚举/自定义实现） */
	private final MatchRule matchRule;

	/**
	 * 构造PredicateRegistry（核心优势：可传任意MatchRule实现）
	 * 
	 * @param matchRule 匹配规则（不可为null）
	 */
	public PredicateRegistry(@NonNull MatchRule matchRule) {
		super(new CopyOnWriteArrayList<>());
		this.matchRule = matchRule;
	}

	/**
	 * 核心匹配逻辑：统计数量 + 规则判断（极简无冗余）
	 * 
	 * @param t 待匹配的目标对象
	 * @return 符合规则返回true，否则返回false
	 */
	@Override
	public boolean test(T t) {
		long totalCount = getContainer().size();
		long matchCount = getContainer().stream().filter(pred -> pred.test(t)).count();
		// 完全复用你的核心逻辑：仅调用规则的match方法
		return matchRule.match(totalCount, matchCount);
	}

	/**
	 * 注册指定对象的等值匹配谓词
	 * 
	 * @param object 等值匹配的目标对象（泛型T类型）
	 * @return 用于操作已注册谓词的{@link Operation}实例
	 */
	public Operation registerEqual(T object) {
		return register(Predicate.isEqual(object));
	}

	/**
	 * 快速创建“精准N个匹配”的自定义规则
	 * 
	 * @param n 精准匹配数量
	 * @return 自定义MatchRule实例
	 */
	public static MatchRule exactN(int n) {
		return (total, match) -> match == n;
	}

	/**
	 * 快速创建“区间匹配”的自定义规则
	 * 
	 * @param min 最小匹配数
	 * @param max 最大匹配数
	 * @return 自定义MatchRule实例
	 */
	public static MatchRule between(int min, int max) {
		return (total, match) -> match >= min && match <= max;
	}
}