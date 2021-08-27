package io.basc.framework.util.page;

public class JumpPages<P extends Page<T>, PS extends Pages<T>, T> extends
		JumpPageables<P, PS, Long, T> implements Pages<T> {

	public JumpPages(P page, PS pages) {
		super(page, pages);
	}

	@Override
	public long getTotal() {
		return wrappedTarget.getTotal();
	}

	@Override
	public Pages<T> jumpTo(Long cursorId) {
		return pageables.jumpTo(cursorId);
	}
}
