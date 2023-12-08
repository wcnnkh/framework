package io.basc.framework.observe;

/**
 * 可观察的(推拉结合)
 * 
 * @author wcnnkh
 *
 * @param <E> 事件类型
 */
public interface Observable<E> extends Push<E>, Pull {
}
