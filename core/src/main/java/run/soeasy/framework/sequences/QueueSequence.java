package run.soeasy.framework.sequences;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.NoSuchElementException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * <p>
 * 一个将 {@link Queue} 适配为 {@link Sequence} 的实现。
 * </p>
 *
 * <p>
 * 这个类充当了队列和序列之间的桥梁，允许你将一个标准的 {@link Queue} 用作 {@link Sequence}。
 * 它特别适用于"生产者-消费者"模式，其中一个或多个生产者线程向队列中添加元素，而一个消费者线程通过
 * {@link Sequence} 接口的 {@link #next()} 方法来消费这些元素。
 * </p>
 *
 * <p>
 * {@code QueueSequence} 是一个 <b>有限序列</b>。当底层队列为空时，{@link #hasNext()} 方法会返回
 * {@code false}，并且后续调用 {@link #next()} 会抛出 {@link NoSuchElementException}。
 * </p>
 *
 * <p>
 * <b>线程安全性：</b>
 * 如果构造 {@code QueueSequence} 时使用的 {@link Queue} 是线程安全的（例如，默认使用的
 * {@link ConcurrentLinkedQueue}），那么这个 {@code Sequence} 的所有操作（{@link #hasNext()},
 * {@link #next()}）在多线程环境下都是安全的。
 * </p>
 *
 * <p>
 * <b>性能注意事项：</b>
 * 性能完全取决于底层 {@link Queue} 的实现。对于高并发场景，推荐使用
 * {@link ConcurrentLinkedQueue} 或其他高性能的并发队列。
 * </p>
 *
 * @author soeasy.run
 * @param <T> 队列中元素的类型
 * @see Queue
 * @see ConcurrentLinkedQueue
 * @see Sequence
 */
@RequiredArgsConstructor
@Getter
public class QueueSequence<T> implements Sequence<T> {

    /**
     * 存储元素的底层队列。
     */
    @NonNull
    private final Queue<T> queue;

    /**
     * <p>
     * 使用一个默认的、线程安全的 {@link ConcurrentLinkedQueue} 来创建一个空的 {@code QueueSequence}。
     * </p>
     * <p>
     * 这是创建 {@code QueueSequence} 的便捷方式，适用于大多数并发场景。
     * </p>
     */
    public QueueSequence() {
        this(new ConcurrentLinkedQueue<>());
    }

    /**
     * <p>
     * 判断序列是否还有下一个元素。
     * </p>
     * <p>
     * 这个方法会委托给底层 {@link Queue#isEmpty()}。如果队列为空，说明序列已耗尽。
     * </p>
     *
     * @return 如果队列不为空（即序列中还有元素），则返回 {@code true}；否则返回 {@code false}。
     */
    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    /**
     * <p>
     * 获取并移除序列中的下一个元素。
     * </p>
     * <p>
     * 这个方法会委托给底层 {@link Queue#poll()}。
     * </p>
     * <p>
     * <b>重要提示：</b> 根据 {@link Sequence} 接口契约，当 {@link #hasNext()} 返回 {@code false} 时，
     * 调用此方法会抛出 {@link NoSuchElementException}。这与 {@link Queue#poll()} 在队列为空时返回
     * {@code null} 的行为不同。
     * </p>
     *
     * @return 序列中的下一个元素。
     * @throws NoSuchElementException 如果序列已耗尽（即队列为空）。
     */
    @Override
    public @NonNull T next() {
        T element = queue.poll();
        if (element == null) {
            throw new NoSuchElementException("Queue is empty.");
        }
        return element;
    }
}