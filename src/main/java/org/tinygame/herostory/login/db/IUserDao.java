package org.tinygame.herostory.login.db;

import org.apache.ibatis.annotations.Param;

public interface IUserDao {
    UserEntity getUserByName(@Param("userName") String userName);

    void insertInto(UserEntity newUserEntity);
}
