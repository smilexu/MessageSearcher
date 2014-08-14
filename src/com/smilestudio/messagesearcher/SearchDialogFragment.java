package com.smilestudio.messagesearcher;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.webkit.WebView.FindListener;
import android.widget.EditText;

public class SearchDialogFragment extends DialogFragment{

    private InputListener mInputListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);

        super.onCreate(savedInstanceState);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater().inflate(R.layout.search_dialog, null);
        return new AlertDialog.Builder(getActivity())
//        .setTitle("Search")
        .setPositiveButton("search", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mInputListener != null) {
                    EditText et = (EditText)view.findViewById(R.id.keywordsInput);
                    mInputListener.onInputed(et.getText().toString());
                }
            }})
        .setNegativeButton("cancel", new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        })
        .setView(view)
        .create();
    }

    public void setInputListener (InputListener listener) {
        mInputListener = listener;
    }
}
