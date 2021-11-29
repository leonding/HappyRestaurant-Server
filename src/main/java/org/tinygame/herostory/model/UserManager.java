package org.tinygame.herostory.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class UserManager {
    static private final Map<Integer, User> _userMap = new ConcurrentHashMap<>();

    private UserManager(){}

    static public void addUser(User newUser) {
        if(null != newUser){
            _userMap.put(newUser.userId, newUser);
        }
    }

    static public void removeUserById(int userId) {
        _userMap.remove(userId);
    }

    static public Collection<User> listUser(){
        return _userMap.values();
    }

    static public User getUserById(int userId) {
        return _userMap.get(userId);
    }
}
