package scw.application;

/**
 * 初始化后的事件
 * @author shuchaowen
 *
 */
public class ApplicationAfterInitEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	public ApplicationAfterInitEvent(Application application) {
		super(application);
	}

	@Override
	public Application getSource() {
		return (Application) super.getSource();
	}
}
