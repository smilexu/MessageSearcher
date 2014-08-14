package com.smilestudio.messagesearcher.proxy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public interface Messager {
    public final  static int MODE_WEIBO = 0;
    public final static int MODE_WEIXIN = 1;

    void initAPI();
    void authorize(Activity activity, RequestUserCallback callback);
    void syncMessagesToDB();
    void searchMessage(String keywords, RequestSearchCallback callback);
    boolean ifNeedLogin(Context context);
    void requestUserName(RequestUserCallback callback);
    void authorizeCallBack(int requestCode, int resultCode, Intent data);
}

