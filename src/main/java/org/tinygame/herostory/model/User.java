package org.tinygame.herostory.model;

public class User {

    /**
     * 用户
     */
    public int userId;

    /**
     * 英雄形象
     */
    public String heroAvatar;


    public String userName;

    /**
     * 移动状态
     */
    public final MoveState moveState = new MoveState();

    public int currHp;

}
