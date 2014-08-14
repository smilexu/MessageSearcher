package com.smilestudio.messagesearcher.proxy;

import com.smilestudio.messagesearcher.model.Message;

public interface RequestSearchCallback {

    public void onSearched(String keywords, Message msg);
}
