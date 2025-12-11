package run.soeasy.framework.core.exchange;

/**
 * 可监听的注册操作接口，定义支持事件监听的注册操作。 该接口继承自{@link Listenable}和{@link Operation}，
 * 允许对注册操作的完成、成功或失败状态进行监听，提供异步操作的回调机制。
 *
 * <p>
 * 核心特性：
 * <ul>
 * <li>状态监听：支持监听注册操作的完成、成功和失败状态</li>
 * <li>链式调用：事件注册方法返回当前实例，支持链式调用</li>
 * <li>类型安全：通过泛型参数确保事件类型一致性</li>
 * </ul>
 *
 * <p>
 * 事件触发条件：
 * <ul>
 * <li>onComplete：当操作状态变为已完成（isDone()返回true）时触发</li>
 * <li>onSuccess：当操作状态变为已完成且成功（isSuccess()返回true）时触发</li>
 * <li>onFailure：当操作状态变为已完成且失败（isSuccess()返回false）时触发</li>
 * </ul>
 *
 * @param <T> 注册操作返回的回执类型，需实现{@link Operation}接口
 * 
 * @author soeasy.run
 * @see Listenable
 * @see Operation
 */
public interface ListenableOperation<T extends Operation> extends Listenable<T>, Operation {

	/**
	 * 注册完成事件监听器 当注册操作状态变为已完成（isDone()返回true）时触发
	 * 
	 * @param listener 完成事件监听器，接收操作回执作为参数
	 * @return 当前可监听注册实例，支持链式调用
	 */
	default ListenableOperation<T> onComplete(Listener<? super T> listener) {
		registerListener((event) -> {
			if (event.isDone()) {
				listener.accept(event);
			}
		});
		return this;
	}

	/**
	 * 注册失败事件监听器 当注册操作状态变为已完成且失败（isSuccess()返回false）时触发
	 * 
	 * @param listener 失败事件监听器，接收操作回执作为参数
	 * @return 当前可监听注册实例，支持链式调用
	 */
	default ListenableOperation<T> onFailure(Listener<? super T> listener) {
		return onComplete((event) -> {
			if (!event.isSuccess()) {
				listener.accept(event);
			}
		});
	}

	/**
	 * 注册成功事件监听器 当注册操作状态变为已完成且成功（isSuccess()返回true）时触发
	 * 
	 * @param listener 成功事件监听器，接收操作回执作为参数
	 * @return 当前可监听注册实例，支持链式调用
	 */
	default ListenableOperation<T> onSuccess(Listener<? super T> listener) {
		return onComplete((event) -> {
			if (event.isSuccess()) {
				listener.accept(event);
			}
		});
	}
}