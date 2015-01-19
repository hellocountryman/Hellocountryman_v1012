package com.feytuo.laoxianghao.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.feytuo.laoxianghao.R;

public class OnloadDialog extends Dialog {
	Context context;
	private TextView messageTextView;

	public OnloadDialog(Context context) {
		super(context,R.style.LoadDialog);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public OnloadDialog(Context context, int theme, boolean cancelable) {
		super(context, theme);
		this.context = context;
		setCancelable(cancelable);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.onload_dialog);
		messageTextView = (TextView)findViewById(R.id.onload_dialog_message);
		Log.i("OnloadDialog","onCreate:"+messageTextView);
	}

	public void setMessage(String message){
		if(!TextUtils.isEmpty(message)){
			messageTextView.setText(message);
		}else{
			messageTextView.setText("正在加载，请稍候...");
		}
	}
	
	public void progressFinish(String message){
		if(isShowing()){
			findViewById(R.id.loadprogresabar_pro).setVisibility(View.GONE);
			messageTextView.setText(message);
		}
	}
}