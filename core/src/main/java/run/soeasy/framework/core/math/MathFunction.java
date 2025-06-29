package run.soeasy.framework.core.math;

/**
 * 数学函数定义
 * 
 * @author soeasy.run
 *
 * @param <S>
 * @param <T>
 */
@FunctionalInterface
public interface MathFunction<S, T> {
	T eval(S left, S right);
}
