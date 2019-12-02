package me.android.library.utils.http.auth.event;

import me.android.library.utils.http.auth.User;
import me.java.library.utils.base.guava.AbstractEvent;

/**
 * 用户信息变更事件
 *
 * @author sylar
 */
public class UserUpdatedEvent extends AbstractEvent<Void, User> {
    public UserUpdatedEvent(User user) {
        super(user);
    }

    public User getUser() {
        return getContent();
    }
}
