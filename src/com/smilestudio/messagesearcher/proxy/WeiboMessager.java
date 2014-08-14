package com.smilestudio.messagesearcher.proxy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.openapi.models.User;
import com.smilestudio.messagesearcher.model.Message;
import com.smilestudio.messagesearcher.utils.AccessTokenKeeper;
import com.smilestudio.messagesearcher.utils.Constants;

public class WeiboMessager implements Messager {

    private static final int DEFAULT_MESSAGE_COUNT = 100;
    public static final String TAG = "WeiboMessager";
    private WeiboAuth mWeiboAuth;
    private SsoHandler mSsoHandler;
    private Oauth2AccessToken mAccessToken;

    private RequestListener mRequestListener;
    private UsersAPI mUsersAPI;
    private StatusesAPI mStatusesAPI;
    private RequestUserCallback mRequestUserCallback;
    private Context mContext;
    private int mPage;

    public WeiboMessager(final Context context) {
        mContext = context;
        mWeiboAuth = new WeiboAuth(context, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        mRequestListener = new RequestListener() {

            @Override
            public void onWeiboException(WeiboException e) {
                ErrorInfo info = ErrorInfo.parse(e.getMessage());
                Toast.makeText(context, info.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onComplete(String response) {
                if (!TextUtils.isEmpty(response)) {
                    // 调用 User#parse 将JSON串解析成User对象
                    User user = User.parse(response);
                    if (user != null && mRequestUserCallback != null) {
                        mRequestUserCallback.onUserRequested(user.screen_name);
                    }
                }
            }
        };
    }

    @Override
    public void authorize(Activity activity, RequestUserCallback callback) {
        mRequestUserCallback = callback;

        if (null == mSsoHandler) {
            mSsoHandler = new SsoHandler(activity, mWeiboAuth);
        }
        mSsoHandler.authorize(new AuthListener());
    }

    @Override
    public void syncMessagesToDB() {
        mPage = 1;
        RequestListener listener = new RequestListener() {
            
            @Override
            public void onWeiboException(WeiboException e) {
                Log.w(TAG, "WeiboException : " + e.getMessage());
            }

            @Override
            public void onComplete(String response) {
                if (response.startsWith("{\"statuses\"")) {
                    StatusList statuses = StatusList.parse(response);

                    if (statuses.statusList != null && statuses.statusList.size() > 0) {

                        //TODO: need to store into db?
                        mPage++;
                        mStatusesAPI.userTimeLine(0, DEFAULT_MESSAGE_COUNT, mPage, this);
                    }
                }
            }
        };
        mStatusesAPI.userTimeLine(0, DEFAULT_MESSAGE_COUNT, mPage, listener);
    }

    @Override
    public void searchMessage(final String keywords, final RequestSearchCallback callback) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                searchKeywords(keywords, callback);
                return null;
            }
        }.execute();
    }

    private Message packageMessageData(Status status) {
        Message msg = new Message(status.id, status.text, status.created_at, status.thumbnail_pic, status.original_pic,
                status.user.screen_name, status.user.avatar_large);
        if (status.retweeted_status != null) {
            String userName = "";
            String userAvatar = "";
            if (status.retweeted_status.user != null) {
                userName = status.retweeted_status.user.screen_name;
                userAvatar = status.retweeted_status.user.avatar_large;
            }
            Message repostMsg = new Message(status.retweeted_status.id, status.retweeted_status.text,
                    status.retweeted_status.created_at, status.retweeted_status.thumbnail_pic,
                    status.retweeted_status.original_pic, userName, userAvatar);
            msg.setRepostMessage(repostMsg);
        }
        return msg;
    }

    @Override
    public boolean ifNeedLogin(Context context) {
        mAccessToken = AccessTokenKeeper.readAccessToken(context);
        return !mAccessToken.isSessionValid();
    }

    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(mContext, mAccessToken);

                initAPI();
                mUsersAPI.show(Long.valueOf(mAccessToken.getUid()), mRequestListener);
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = "auth failed";
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Log.w(TAG, message);
            }
        }

        @Override
        public void onCancel() {
            Log.w(TAG, "onCancel");
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Log.w(TAG, "Auth exception : " + e.getMessage());
        }
    }

    @Override
    public void initAPI() {
        mUsersAPI = new UsersAPI(mAccessToken);
        mStatusesAPI = new StatusesAPI(mAccessToken);
    }

    @Override
    public void requestUserName(RequestUserCallback callback) {
        mRequestUserCallback = callback;
        mUsersAPI.show(Long.valueOf(mAccessToken.getUid()), mRequestListener);
    }

    @Override
    public void authorizeCallBack(int requestCode, int resultCode, Intent data) {
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    private void searchKeywords(final String keywords, final RequestSearchCallback callback) {
        mPage = 1;
        RequestListener listener = new RequestListener() {
            
            @Override
            public void onWeiboException(WeiboException e) {
                Log.w(TAG, "WeiboException : " + e.getMessage());
            }

            @Override
            public void onComplete(String response) {
                if (response.startsWith("{\"statuses\"")) {
                    final StatusList statuses = StatusList.parse(response);

                    if (statuses.statusList != null && statuses.statusList.size() > 0) {

                        //store into db
                        for (com.sina.weibo.sdk.openapi.models.Status status : statuses.statusList) {
                            if (status.text.contains(keywords)
                                    || (status.retweeted_status != null && status.retweeted_status.text.contains(keywords))) {
                                callback.onSearched(keywords, packageMessageData(status));
                            }
                        }

                        mPage++;
                        //Remove it in develop period, to avoid out of rate limit for requesting
//                        mStatusesAPI.userTimeLine(0, DEFAULT_MESSAGE_COUNT, mPage, this);
                    }
                }
            }

        };
        mStatusesAPI.userTimeLine(0, DEFAULT_MESSAGE_COUNT, mPage, listener);
    }
}
