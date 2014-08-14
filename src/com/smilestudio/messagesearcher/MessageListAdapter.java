package com.smilestudio.messagesearcher;

import java.util.ArrayList;
import java.util.List;

import com.smilestudio.messagesearcher.model.Message;
import com.smilestudio.messagesearcher.utils.HttpHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageListAdapter extends BaseAdapter {

    private List<Message> mMessageList = new ArrayList<Message> ();
    private Context mContext;

    public MessageListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message holder = null;

        if (null == convertView) {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.content_list_item, null);
            holder = mMessageList.get(position);
            convertView.setTag(holder);
        } else {
            holder = (Message)convertView.getTag();
        }
        ImageView image = (ImageView)convertView.findViewById(R.id.list_item_my_thumb);
        new ThumbnailTask(holder.getAuthorThumb(), image).execute();
        TextView name = (TextView)convertView.findViewById(R.id.list_item_my_name);
        name.setText(holder.getAuthor());
        TextView time = (TextView)convertView.findViewById(R.id.list_item_post_time);
        time.setText(holder.getTime());
        TextView content = (TextView)convertView.findViewById(R.id.list_item_content);
        content.setText(holder.getContent());
        Message repost = holder.getRepost();
        if (holder.getRepost() != null) {
            TextView repoContent = (TextView)convertView.findViewById(R.id.list_item_original_post_content);
            repoContent.setText("@" + repost.getAuthor() + ":" + repost.getContent());
            repoContent.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(repost.getContentThumb())) {
                ImageView iv = (ImageView)convertView.findViewById(R.id.list_item_original_post_pic);
                new ThumbnailTask(repost.getContentThumb(), iv).execute();
            }
        }
        return convertView;
    }

    public void addMessage(Message msg) {
        mMessageList.add(msg);
        notifyDataSetChanged();
    }

    public void clearMessageList() {
        mMessageList.clear();
    }
    
    public int getMessageListSize() {
        return mMessageList.size();
    }

    private class ThumbnailTask extends AsyncTask<Void, Void, Bitmap> {

        private String mUrl;
        private ImageView mView;

        public ThumbnailTask(String url, ImageView view) {
            mUrl = url;
            mView = view;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            return HttpHelper.BitmapDownloader(mUrl);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if(result != null) {
                mView.setImageBitmap(result);
                mView.setVisibility(View.VISIBLE);
            }
        }
    }

}
