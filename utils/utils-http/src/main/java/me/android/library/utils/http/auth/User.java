package me.android.library.utils.http.auth;


import me.java.library.common.Identifiable;

/**
 * Created by sylar on 16/8/17.
 */
public interface User extends Identifiable<String> {

    String getAccount();

    String getPassword();
}
