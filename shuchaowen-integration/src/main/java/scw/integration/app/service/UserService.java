package scw.integration.app.service;

import scw.beans.annotation.AutoImpl;
import scw.integration.app.enums.UnionIdType;
import scw.integration.app.pojo.UnionId;
import scw.integration.app.pojo.User;
import scw.integration.app.service.impl.UserServiceImpl;

@AutoImpl(UserServiceImpl.class)
public interface UserService {
	User getUser(long uid);
	
	UnionId getUnionId(UnionIdType unionIdType, String unionId);

	UnionId getUnionId(String unionIdType, String unionId);
	
	User getUser(UnionIdType unionIdType, String unionId);
	
	User getUser(String unionIdType, String unionId);
}
