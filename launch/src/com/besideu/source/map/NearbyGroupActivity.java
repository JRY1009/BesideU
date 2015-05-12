package com.besideu.source.map;

import java.util.ArrayList;
import java.util.List;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.besideu.source.R;
import com.besideu.source.util.UtilLocationData;
import com.besideu.source.util.UtilUserData;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NearbyGroupActivity extends Activity implements
		OnPoiSearchListener {
	
	private ListView mListView;
	private NearbyGroupAdapter mAdapter;
	private List<NearbyGroupItem> mDataArrays = new ArrayList<NearbyGroupItem>();
	
	private ProgressDialog progDialog = null;// 搜索时进度条
	private PoiResult poiResult; // poi返回的结果
	private int currentPage = 0;// 当前页面，从0开始计数
	private PoiSearch.Query query;// Poi查询条件类
	private LatLonPoint lp;
	private PoiSearch poiSearch;
	private List<PoiItem> poiItems;// poi数据
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nearby_group);
		
		mListView = (ListView) findViewById(R.id.lv_nearby_group);
		mAdapter = new NearbyGroupAdapter(this, mDataArrays);
		mListView.setAdapter(mAdapter);
		
		doSearchQuery();
	}

	@Override
	protected void onDestroy() {
		dissmissProgressDialog();
		super.onDestroy();
	}

	public void onNearbyGroupItem(View view) {
		
		TextView txtName = (TextView) view.findViewById(R.id.txt_nearby_group_name);
		
		int count = mDataArrays.size();
		for (int i=0; i<count; i++) {
			NearbyGroupItem item = mDataArrays.get(i);
			if (item == null)
				continue;
			
			if (item.getLocDesc() == txtName.getText())
			{
				UtilUserData.leaveGroup(this, UtilUserData.getGid());
				
				UtilUserData.findGroup(this,
						Double.toString(item.getGeoLng()),
						Double.toString(item.getGeoLat()),
						item.getLocDescFull());
				
				break;
			}
		}
		
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();
	}
	
	public void onNearbyGroupBack(View view) {
		finish();
	}
	
	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(false);
		progDialog.setMessage("正在搜索中");
		progDialog.show();
	}

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}
	
	/**
	 * 开始进行poi搜索
	 */
	protected void doSearchQuery() {
		showProgressDialog();// 显示进度框
		currentPage = 0;
		query = new PoiSearch.Query("", "060400|060600|080100|080101|080400|080601|090101|" +
				"090100|100101|100102|100103|100104|100105|110000|110100|110101|110102|" +
				"110103|110105|110200|110201|110202|110203|110204|110205|110206|110208|" +
				"120100|120201|120301|120302|130102|130103|130201|130501|130502|130503|" +
				"140100|140300|140500|141101|141102|141103|141200|141201|141202|141203|" +
				"141204|141205|141206|150100|150200|150300|150301|150400|150500|150600|" +
				"170100|170300|180300|180301|190103|190104|190105|190106|190107|190108|" +
				"190200|190202|190203|190204|190205|190300|190301|190600", UtilLocationData.getCity());// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
		query.setPageSize(20);// 设置每页最多返回多少条poiitem
		query.setPageNum(currentPage);// 设置查第一页

		query.setLimitDiscount(false);
		query.setLimitGroupbuy(false);

		lp = new LatLonPoint(UtilLocationData.getGeoLat(), UtilLocationData.getGeoLng());
		if (lp != null) {
			poiSearch = new PoiSearch(this, query);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.setBound(new SearchBound(lp, 500, true));//
			// 设置搜索区域为以lp点为圆心，其周围2000米范围
			/*
			 * List<LatLonPoint> list = new ArrayList<LatLonPoint>();
			 * list.add(lp);
			 * list.add(AMapUtil.convertToLatLonPoint(Constants.BEIJING));
			 * poiSearch.setBound(new SearchBound(list));// 设置多边形poi搜索范围
			 */
			poiSearch.searchPOIAsyn();// 异步搜索
		}
	}

	@Override
	public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		dissmissProgressDialog();// 隐藏对话框
		if (rCode == 0) {
			if (result != null && result.getQuery() != null) {// 搜索poi的结果
				if (result.getQuery().equals(query)) {// 是否是同一条
					poiResult = result;
					poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
					if (poiItems != null && poiItems.size() > 0) {
						
						mDataArrays.clear();
						int count = poiItems.size();
						for (int i=0; i<count; i++) {
							PoiItem item = poiItems.get(i);
							if (item == null)
								continue;
							
							NearbyGroupItem nbItem = new NearbyGroupItem();
							nbItem.setLocDesc(item.toString());
							nbItem.setGeoLat(item.getLatLonPoint().getLatitude());
							nbItem.setGeoLng(item.getLatLonPoint().getLongitude());
							
							String province = item.getProvinceName();
							String city = item.getCityName();
							String ad = item.getAdName();
							String fulldesc = "神州大地";
							if (province != null && province.length() > 0 && !province.equals(city)) {
								fulldesc = fulldesc + " " + province;
							}
							
							if (city != null && city.length() > 0) {
								fulldesc = fulldesc + " " + city;
							}
							
							if (ad != null && ad.length() > 0) {
								fulldesc = fulldesc + " " + ad;
							}
							
							fulldesc = fulldesc + " " + item.toString();
							nbItem.setLocDescFull(fulldesc);
							
							mDataArrays.add(nbItem);
						}
						
						mAdapter.notifyDataSetChanged();
					} 
					else {
						Toast.makeText(this, "未找到周边信息", Toast.LENGTH_LONG).show();
					}
				}
			} else {
				Toast.makeText(this, "未找到周边信息", Toast.LENGTH_LONG).show();
			}
		} else if (rCode == 27) {
			Toast.makeText(this, "网络错误", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "未找到周边信息", Toast.LENGTH_LONG).show();
		}
	}
	
	public void onPrevPage(View view) {
		if (query != null && poiSearch != null && poiResult != null) {
			if (currentPage > 0) {
				currentPage--;

				query.setPageNum(currentPage);// 设置查后一页
				poiSearch.searchPOIAsyn();
			} else {
				Toast.makeText(this, "已经是第一页", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	public void onNextPage(View view) {
		if (query != null && poiSearch != null && poiResult != null) {
			if (poiResult.getPageCount() - 1 > currentPage) {
				currentPage++;

				query.setPageNum(currentPage);// 设置查后一页
				poiSearch.searchPOIAsyn();
			} else {
				Toast.makeText(this, "无更多信息", Toast.LENGTH_LONG).show();
			}
		}
	}
}
