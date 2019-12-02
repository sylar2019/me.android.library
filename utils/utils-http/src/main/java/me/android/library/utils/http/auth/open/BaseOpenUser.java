package me.android.library.utils.http.auth.open;

import me.java.library.common.model.pojo.AbstractIdPojo;

public class BaseOpenUser extends AbstractIdPojo<String> implements OpenUser {

    private String nickname;
    private String figureUrl;
    private String token;

    public BaseOpenUser() {
    }

    public BaseOpenUser(String id, String nickname, String figureUrl, String token) {
        this.id = id;
        this.nickname = nickname;
        this.figureUrl = figureUrl;
        this.token = token;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String getFigureUrl() {
        return figureUrl;
    }

    public void setFigureUrl(String figureUrl) {
        this.figureUrl = figureUrl;
    }

    @Override
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
