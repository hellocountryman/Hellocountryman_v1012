package com.feytuo.laoxianghao.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.feytuo.laoxianghao.PublishActivity;
import com.feytuo.laoxianghao.R;

public class PositionChooseDialog extends Dialog {
	Context context;

	private ListView mListView;
	private SimpleAdapter adapter;
	private List<Map<String, String>> data;
	
	private String location;

	public PositionChooseDialog(Context context,String position) {
		super(context, R.style.MyDialog);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.location =position;
	}

	public PositionChooseDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.position_choose_dialog);

		initData();
		initListView();
	}

	private void initData() {
		// TODO Auto-generated method stub
		data = new ArrayList<Map<String, String>>();
		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("position", "火星");
		HashMap<String, String> map2 = new HashMap<String, String>();
		map2.put("position", "喵星");
		HashMap<String, String> map3 = new HashMap<String, String>();
		map3.put("position", "汪星");
		HashMap<String, String> map4 = new HashMap<String, String>();
		map4.put("position", "M78星云");
		HashMap<String, String> map5 = new HashMap<String, String>();
		map5.put("position", "塞博坦星球");
		HashMap<String, String> map6 = new HashMap<String, String>();
		map6.put("position", "地球");
		HashMap<String, String> map7 = new HashMap<String, String>();
		map7.put("position", "壕星");
		HashMap<String, String> map8 = new HashMap<String, String>();
		map8.put("position", location);
		data.add(map1);
		data.add(map2);
		data.add(map3);
		data.add(map4);
		data.add(map5);
		data.add(map6);
		data.add(map7);
		if(!"火星".equals(location)){
			data.add(map8);
		}
	}

	private void initListView() {
		// TODO Auto-generated method stub
		mListView = (ListView)findViewById(R.id.position_choose_listview);
		adapter = new SimpleAdapter(context, data,
				R.layout.position_choose_list_item, new String[] { "position" },
				new int[] { R.id.position_choose_textview });
		mListView.setAdapter(adapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				location = data.get(position).get("position").trim();
				if(context instanceof PublishActivity){
					((PublishActivity)context).setPosition(location);
				}
				dismiss();
			}
			
		});
	}
	
}