package me.android.library.utils.http.auth.event;

import me.android.library.utils.http.auth.User;
import me.java.library.utils.base.guava.AbstractEvent;

/**
 * 用户登录事件
 *
 * @author sylar
 */
public class UserLoginEvent extends AbstractEvent<Void, User> {

    public UserLoginEvent(User user) {
        super(user);
    }

    public User getUser() {
        return getContent();
    }
}
