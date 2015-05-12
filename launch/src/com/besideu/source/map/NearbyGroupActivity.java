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
	
	private ProgressDialog progDialog = null;// ����ʱ������
	private PoiResult poiResult; // poi���صĽ��
	private int currentPage = 0;// ��ǰҳ�棬��0��ʼ����
	private PoiSearch.Query query;// Poi��ѯ������
	private LatLonPoint lp;
	private PoiSearch poiSearch;
	private List<PoiItem> poiItems;// poi����
	
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
	 * ��ʾ���ȿ�
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(false);
		progDialog.setMessage("����������");
		progDialog.show();
	}

	/**
	 * ���ؽ��ȿ�
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}
	
	/**
	 * ��ʼ����poi����
	 */
	protected void doSearchQuery() {
		showProgressDialog();// ��ʾ���ȿ�
		currentPage = 0;
		query = new PoiSearch.Query("", "060400|060600|080100|080101|080400|080601|090101|" +
				"090100|100101|100102|100103|100104|100105|110000|110100|110101|110102|" +
				"110103|110105|110200|110201|110202|110203|110204|110205|110206|110208|" +
				"120100|120201|120301|120302|130102|130103|130201|130501|130502|130503|" +
				"140100|140300|140500|141101|141102|141103|141200|141201|141202|141203|" +
				"141204|141205|141206|150100|150200|150300|150301|150400|150500|150600|" +
				"170100|170300|180300|180301|190103|190104|190105|190106|190107|190108|" +
				"190200|190202|190203|190204|190205|190300|190301|190600", UtilLocationData.getCity());// ��һ��������ʾ�����ַ������ڶ���������ʾpoi�������ͣ�������������ʾpoi�������򣨿��ַ�������ȫ����
		query.setPageSize(20);// ����ÿҳ��෵�ض�����poiitem
		query.setPageNum(currentPage);// ���ò��һҳ

		query.setLimitDiscount(false);
		query.setLimitGroupbuy(false);

		lp = new LatLonPoint(UtilLocationData.getGeoLat(), UtilLocationData.getGeoLng());
		if (lp != null) {
			poiSearch = new PoiSearch(this, query);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.setBound(new SearchBound(lp, 500, true));//
			// ������������Ϊ��lp��ΪԲ�ģ�����Χ2000�׷�Χ
			/*
			 * List<LatLonPoint> list = new ArrayList<LatLonPoint>();
			 * list.add(lp);
			 * list.add(AMapUtil.convertToLatLonPoint(Constants.BEIJING));
			 * poiSearch.setBound(new SearchBound(list));// ���ö����poi������Χ
			 */
			poiSearch.searchPOIAsyn();// �첽����
		}
	}

	@Override
	public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		dissmissProgressDialog();// ���ضԻ���
		if (rCode == 0) {
			if (result != null && result.getQuery() != null) {// ����poi�Ľ��
				if (result.getQuery().equals(query)) {// �Ƿ���ͬһ��
					poiResult = result;
					poiItems = poiResult.getPois();// ȡ�õ�һҳ��poiitem���ݣ�ҳ��������0��ʼ
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
							String fulldesc = "���ݴ��";
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
						Toast.makeText(this, "δ�ҵ��ܱ���Ϣ", Toast.LENGTH_LONG).show();
					}
				}
			} else {
				Toast.makeText(this, "δ�ҵ��ܱ���Ϣ", Toast.LENGTH_LONG).show();
			}
		} else if (rCode == 27) {
			Toast.makeText(this, "�������", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "δ�ҵ��ܱ���Ϣ", Toast.LENGTH_LONG).show();
		}
	}
	
	public void onPrevPage(View view) {
		if (query != null && poiSearch != null && poiResult != null) {
			if (currentPage > 0) {
				currentPage--;

				query.setPageNum(currentPage);// ���ò��һҳ
				poiSearch.searchPOIAsyn();
			} else {
				Toast.makeText(this, "�Ѿ��ǵ�һҳ", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	public void onNextPage(View view) {
		if (query != null && poiSearch != null && poiResult != null) {
			if (poiResult.getPageCount() - 1 > currentPage) {
				currentPage++;

				query.setPageNum(currentPage);// ���ò��һҳ
				poiSearch.searchPOIAsyn();
			} else {
				Toast.makeText(this, "�޸�����Ϣ", Toast.LENGTH_LONG).show();
			}
		}
	}
}
