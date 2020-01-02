package scw.integration.app.service.impl;

import scw.db.DB;
import scw.integration.app.enums.UnionIdType;
import scw.integration.app.pojo.UnionId;
import scw.integration.app.pojo.User;
import scw.integration.app.service.UserService;

public class UserServiceImpl extends BaseServiceImpl implements UserService {

	public UserServiceImpl(DB db) {
		super(db);
	}

	public User getUser(long uid) {
		return db.getById(User.class, uid);
	}

	public User getUser(UnionIdType unionIdType, String unionId) {
		UnionId union = getUnionId(unionIdType, unionId);
		return union == null ? null : getUser(union.getUid());
	}

	public User getUser(String unionIdType, String unionId) {
		UnionId union = getUnionId(unionIdType, unionId);
		return union == null ? null : getUser(union.getUid());
	}

	public UnionId getUnionId(UnionIdType unionIdType, String unionId) {
		return db.getById(UnionId.class, unionIdType, unionId);
	}

	public UnionId getUnionId(String unionIdType, String unionId) {
		return db.getById(UnionId.class, unionIdType, unionId);
	}

}
