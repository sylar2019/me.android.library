package me.android.library.utils.http.auth.open;


import me.java.library.common.Identifiable;

/**
 * Created by sylar on 16/8/17.
 */
public interface OpenUser extends Identifiable<String> {

    String getNickname();

    String getFigureUrl();

    String getToken();

}
