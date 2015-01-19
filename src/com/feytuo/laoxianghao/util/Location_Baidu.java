package com.feytuo.laoxianghao.util;

import android.content.Context;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

/**
 * 利用百度地图对当前位置进行定位
 * @author feytuo
 *
 */
public class Location_Baidu {

	public LocationClient mLocationClient;
	private String mProvince;
	private String mCity;
	private String mPosition;
	private TextView mLocationTV;
	
	public Location_Baidu(Context context){
		mLocationClient = new LocationClient(context.getApplicationContext());
		SmallLocationListener myLocationListener = new SmallLocationListener();
		mLocationClient.registerLocationListener(myLocationListener);
		InitLocation();
	}
	
	public Location_Baidu(Context context,TextView locationTV) {
		// TODO Auto-generated constructor stub
		this.mLocationTV = locationTV;
		mLocationClient = new LocationClient(context.getApplicationContext());
		MyLocationListener myLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(myLocationListener);
		InitLocation();
	}
	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			mProvince = location.getProvince();
			mCity = location.getCity();
			if(mProvince != null && mCity != null){
				mLocationTV.setText(formatPosition(mProvince,mCity));
				stop();//已经有信息则关闭定位
			}else{
				mLocationTV.setText("火星");
			}
		}
	}
	/**
	 * 实现实位回调监听,只为得到地点的string
	 */
	public class SmallLocationListener implements BDLocationListener {
		
		@Override
		public void onReceiveLocation(BDLocation location) {
			mProvince = location.getProvince();
			mCity = location.getCity();
			if(mProvince != null && mCity != null){
				setmPosition(formatPosition(mProvince,mCity));
				stop();//已经有信息则关闭定位
			}else{
				setmPosition("火星");
			}
		}
	}
	
	/**
	 * 初始化定位设置
	 */
	private void InitLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
		option.setCoorType("gcj02");//返回的定位结果是百度经纬度，默认值gcj02
		option.setScanSpan(1000);//设置发起定位请求的间隔时间为1000ms
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
	}
	
	private String formatPosition(String province,String city){
		String provinceTemp = province.substring(0, province.length()-1);
		String cityTemp = city.substring(0, city.length()-1);
		if(!provinceTemp.equals(cityTemp)){//省份和城市为直辖市可能一直，只显示一个
			return provinceTemp+cityTemp;
		}else{
			return provinceTemp;
		}
	}

	public String getmPosition() {
		return mPosition;
	}

	public void setmPosition(String mPosition) {
		this.mPosition = mPosition;
	}

	/**
	 * 开始定位
	 */
	public void start(){
		if(!mLocationClient.isStarted()){
			mLocationClient.start();
		}
	}
	
	/**
	 * 停止定位
	 */
	public void stop(){
		if(mLocationClient.isStarted()){
			mLocationClient.stop();
		}
	}
}
