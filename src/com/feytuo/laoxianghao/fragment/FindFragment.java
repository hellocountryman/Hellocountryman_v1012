package com.feytuo.laoxianghao.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.feytuo.laoxianghao.FindDetailsActivity;
import com.feytuo.laoxianghao.R;
import com.umeng.analytics.MobclickAgent;

public class FindFragment extends Fragment {

	private RelativeLayout findTopicRelac;
	private RelativeLayout findDuanziRelac;
	private RelativeLayout findktvRelac;
	private RelativeLayout findtShowRelac;
//	private ImageButton indexTopicImg;//
//	private ImageButton indeDuanziImg;
//	private ImageButton indexKtvImg;//
//	private ImageButton indexShowImg;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.find_activity, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		initview();
		super.onActivityCreated(savedInstanceState);
	}

	public void initview() {
//		indexTopicImg = (ImageButton) getActivity().findViewById(
//				R.id.find_topic_img);
//		indeDuanziImg = (ImageButton) getActivity().findViewById(
//				R.id.find_duanzi_img);
//		indexKtvImg = (ImageButton) getActivity().findViewById(
//				R.id.find_ktv_img);
//		indexShowImg = (ImageButton) getActivity().findViewById(
//				R.id.find_show_img);

		findTopicRelac = (RelativeLayout) getActivity().findViewById(
				R.id.find_topic_linear);
		findDuanziRelac = (RelativeLayout) getActivity().findViewById(
				R.id.find_duanzi_linear);
		findktvRelac = (RelativeLayout) getActivity().findViewById(
				R.id.find_ktv_linear);
		findtShowRelac = (RelativeLayout) getActivity().findViewById(
				R.id.find_show_linear);
		listener lister = new listener();
		findTopicRelac.setOnClickListener(lister);
		findDuanziRelac.setOnClickListener(lister);
		findktvRelac.setOnClickListener(lister);
		findtShowRelac.setOnClickListener(lister);
	}

	class listener implements OnClickListener {

		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(getActivity(), FindDetailsActivity.class);
			switch (v.getId()) {
			case R.id.find_topic_linear:
				intent.putExtra("type", 1);
				getActivity().startActivity(intent);
				break;
			case R.id.find_duanzi_linear:
				intent.putExtra("type", 2);
				getActivity().startActivity(intent);
				break;
			case R.id.find_ktv_linear:
				intent.putExtra("type", 3);
				getActivity().startActivity(intent);
				break;
			case R.id.find_show_linear:
				intent.putExtra("type", 4);
				getActivity().startActivity(intent);
				break;
			default:
				break;

			}

		}
	};

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("FindFragment"); // 统计页面
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("FindFragment");
	}
}
