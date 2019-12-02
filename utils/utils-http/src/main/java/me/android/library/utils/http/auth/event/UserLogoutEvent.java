package me.android.library.utils.http.auth.event;

import me.android.library.utils.http.auth.User;
import me.java.library.utils.base.guava.AbstractEvent;

/**
 * 用户注销登录事件
 *
 * @author sylar
 */
public class UserLogoutEvent extends AbstractEvent<Void, User> {

    public UserLogoutEvent(User user) {
        super(user);
    }

    public User getUser() {
        return getContent();
    }
}