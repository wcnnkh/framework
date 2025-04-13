package run.soeasy.framework.core;

/**
 * Strategy interface for processing Lifecycle beans within the
 * ApplicationContext.
 *
 */
public interface LifecycleProcessor extends Lifecycle {

	/**
	 * Notification of context refresh, e.g. for auto-starting components.
	 */
	void onRefresh();

	/**
	 * Notification of context close phase, e.g. for auto-stopping components.
	 */
	void onClose();
}