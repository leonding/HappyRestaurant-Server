package org.tinygame.herostory.login.db;

import org.apache.ibatis.annotations.Param;

public interface IUserDao {
    UserEntity getUserByName(@Param("puid") String puid);

    void insertInto(UserEntity newUserEntity);
}
