package com.feytuo.laoxianghao;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.feytuo.laoxianghao.fragment.MyInvitationFragment;
import com.umeng.analytics.MobclickAgent;
/**
 * 个人中心的我的帖子
 * @author feytuo
 *
 */
public class PersonInvitationActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_invitation);
		getSupportActionBar().hide();
		
		initView(savedInstanceState);
	}

	private void initView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Fragment fragment ;
		if(savedInstanceState == null){
			fragment = new  MyInvitationFragment();
		}else{
			fragment = getSupportFragmentManager().findFragmentByTag("fragment1");
		}
		getSupportFragmentManager().beginTransaction()
					.add(R.id.person_invitation_container, fragment,"fragment1")
					.show(fragment)
					.commit();
	}

	public void messagecollectReturnRelative(View v) {
		finish();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
