//package me.android.library.utils.http.auth.pojo;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//
//import me.android.library.utils.http.auth.AuthPlatType;
//import me.android.library.utils.http.auth.User;
//import me.android.library.utils.http.auth.open.OpenUser;
//
///**
// * Created by sylar on 16/8/18.
// */
//public class LastUserInfo {
//    private int authPlatType;
//    private String userId;
//    private String account;
//    private String password;
//
//    private String oauthUserId;
//    private String oauthNickname;
//    private String oauthFigureUrl;
//    private String oauthToken;
//
//    private User user;
//    private OpenUser openUser;
//
//    public LastUserInfo() {
//    }
//
//    public LastUserInfo(User user) {
//        this.user = user;
//        setAccount(user.getAccount());
//        setPassword(user.getPassword());
//    }
//
//    public void setAuthPLatUser(OpenUser openUser) {
//        this.openUser = openUser;
//        setOauthUserId(openUser.getId());
//        setOauthNickname(openUser.getNickname());
//        setOauthFigureUrl(openUser.getFigureUrl());
//        setOauthToken(openUser.getToken());
//    }
//
//    public boolean isSelfUser() {
//        return authPlatType == AuthPlatType.Plat_Self;
//    }
//
//    @JsonIgnore
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
//
//    @JsonIgnore
//    public OpenUser getOpenUser() {
//        return openUser;
//    }
//
//    public void setOpenUser(OpenUser openUser) {
//        this.openUser = openUser;
//    }
//
//    public int getAuthPlatType() {
//        return authPlatType;
//    }
//
//    public void setAuthPlatType(int authPlatType) {
//        this.authPlatType = authPlatType;
//    }
//
//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
//
//    public String getAccount() {
//        return account;
//    }
//
//    public void setAccount(String account) {
//        this.account = account;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public String getOauthUserId() {
//        return oauthUserId;
//    }
//
//    public void setOauthUserId(String oauthUserId) {
//        this.oauthUserId = oauthUserId;
//    }
//
//    public String getOauthNickname() {
//        return oauthNickname;
//    }
//
//    public void setOauthNickname(String oauthNickname) {
//        this.oauthNickname = oauthNickname;
//    }
//
//    public String getOauthFigureUrl() {
//        return oauthFigureUrl;
//    }
//
//    public void setOauthFigureUrl(String oauthFigureUrl) {
//        this.oauthFigureUrl = oauthFigureUrl;
//    }
//
//    public String getOauthToken() {
//        return oauthToken;
//    }
//
//    public void setOauthToken(String oauthToken) {
//        this.oauthToken = oauthToken;
//    }
//
//}
