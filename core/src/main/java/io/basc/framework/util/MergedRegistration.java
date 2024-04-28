package io.basc.framework.util;

import lombok.Data;

@Data
public class MergedRegistration implements Registration {
	private final Registration left;
	private final Registration right;

	public MergedRegistration(Registration left, Registration right) {
		Assert.requiredArgument(left != null, "left");
		Assert.requiredArgument(right != null, "right");
		this.left = left;
		this.right = right;
	}

	@Override
	public void unregister() throws RegistrationException {
		try {
			left.unregister();
		} finally {
			right.unregister();
		}
	}

	@Override
	public boolean isInvalid() {
		return left.isInvalid() && right.isInvalid();
	}

	public static Registration merge(Registration left, Registration right) {
		if (right == null || right == EMPTY) {
			return left;
		}

		if (left == null || left == EMPTY) {
			return right;
		}

		return new MergedRegistration(left, right);
	}
}
