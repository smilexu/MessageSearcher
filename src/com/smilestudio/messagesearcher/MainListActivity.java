package com.smilestudio.messagesearcher;

import com.smilestudio.messagesearcher.R;
import com.smilestudio.messagesearcher.model.Message;
import com.smilestudio.messagesearcher.proxy.RequestSearchCallback;
import com.smilestudio.messagesearcher.proxy.RequestUserCallback;
import com.smilestudio.messagesearcher.proxy.Messager;
import com.smilestudio.messagesearcher.proxy.MessagerFactory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainListActivity extends FragmentActivity implements InputListener {

    protected static final String TAG_SEARCH_DIALOG = "search_dialog";
    private View mLoginBtn;
    private TextView mAccountLabel;
    private ImageView mSearchBtn;
    private Messager mMessager = null;
    private RequestUserCallback mRequestUserCallback;
    private SearchDialogFragment mSearchFragment;
    private ListView mList;
    private MessageListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        mMessager = MessagerFactory.getFactory().getMessager(getApplicationContext(), Messager.MODE_WEIBO);

        mLoginBtn = findViewById(R.id.header_login_btn);
        mAccountLabel = (TextView)findViewById(R.id.header_account_name);
        mSearchBtn = (ImageView)findViewById(R.id.header_search_btn);
        mAccountLabel.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                TextView tv = (TextView)v;
                if (!TextUtils.isEmpty(tv.getText())) {
                    mMessager.syncMessagesToDB();
                }
            }
        });

        mLoginBtn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                mMessager.authorize(MainListActivity.this, mRequestUserCallback);
            }
        });

        mSearchBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null == mSearchFragment) {
                    mSearchFragment = new SearchDialogFragment();
                    mSearchFragment.setInputListener(MainListActivity.this);
                }
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                mSearchFragment.show(ft, TAG_SEARCH_DIALOG);
            }
        });

        mRequestUserCallback = new RequestUserCallback() {

            @Override
            public void onUserRequested(String name) {
                showAccountView(name);
            }};

        if (!mMessager.ifNeedLogin(getApplicationContext()) ) {
            mMessager.initAPI();
            mMessager.requestUserName(mRequestUserCallback);
        }

        mList = (ListView)findViewById(R.id.content_list);
        mAdapter = new MessageListAdapter(getApplicationContext());
        mList.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        mMessager.authorizeCallBack(requestCode, resultCode, data);
    }

    private void showAccountView(String name) {
        mLoginBtn.setVisibility(View.GONE);
        mAccountLabel.setVisibility(View.VISIBLE);
        mAccountLabel.setText(name);
    }

    @Override
    public void onInputed(String input) {
        mAdapter.clearMessageList();

        mMessager.searchMessage(input, new RequestSearchCallback() {
            
            @Override
            public void onSearched(String keywords, Message msg) {
                mAdapter.addMessage(msg);
            }
        });
    }

}
