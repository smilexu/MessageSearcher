package com.smilestudio.messagesearcher.proxy;

import android.content.Context;

public class MessagerFactory {

    static MessagerFactory INSTANCE = null;

    public synchronized static MessagerFactory getFactory() {
        if (null == INSTANCE) {
            INSTANCE = new MessagerFactory();
        }
        return INSTANCE;
    }

    public Messager getMessager(Context context, int mode) {
        if(Messager.MODE_WEIBO == mode) {
            return new WeiboMessager(context);
        }
        return null;
    }
}
