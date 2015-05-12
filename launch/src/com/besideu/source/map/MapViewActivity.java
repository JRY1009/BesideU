package com.besideu.source.map;

import java.util.List;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.AMap.OnCameraChangeListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
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

import android.graphics.Color;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

public class MapViewActivity extends Activity implements LocationSource,
	AMapLocationListener, OnPoiSearchListener, OnCameraChangeListener, 
	InfoWindowAdapter, OnMarkerClickListener {
	
	private AMap aMap;
	private MapView mapView;
	private LocationManagerProxy aMapLocManager = null;
	private OnLocationChangedListener mListener;
	
	private ProgressDialog progDialog = null;// ����ʱ������
	private PoiResult poiResult; // poi���صĽ��
	private int currentPage = 0;// ��ǰҳ�棬��0��ʼ����
	private PoiSearch.Query query;// Poi��ѯ������
	private PoiSearch poiSearch;
	private List<PoiItem> poiItems;// poi����
	private AMapLocation mLocation;
	
	private double mCurLat;
	private double mCurLng;
	
	private int mSearchType = 0; //0����������1�ؼ�������
	private EditText mEdit;
	private Button mSearch;
	private LayoutInflater mLayoutInflater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_view);
		
		mLayoutInflater = LayoutInflater.from(this);
		
		mapView = (MapView) findViewById(R.id.map_view);
		mapView.onCreate(savedInstanceState);// �˷���������д
		
		mEdit = (EditText) findViewById(R.id.et_mv_search);
		mSearch = (Button) findViewById(R.id.btn_mv_search);
		
		init();
	}
	
	// ����հ״����������
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) 
	{
        if (ev.getAction() == MotionEvent.ACTION_DOWN) 
        {
			if (!isPtInView((int) ev.getX(), (int) ev.getY(), mEdit) &&
					!isPtInView((int) ev.getX(), (int) ev.getY(), mSearch)) {
				InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				im.hideSoftInputFromWindow(mEdit.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			} else {
				mEdit.requestFocus();
			}

        }
        
        return super.dispatchTouchEvent(ev);
	}
	
	private boolean isPtInView(int x, int y, View view) {
		int[] l = { 0, 0 };
		view.getLocationInWindow(l);
		Rect rcView = new Rect(l[0], l[1], l[0] + view.getWidth(), l[1]
				+ view.getHeight());

		return rcView.contains(x, y);
	}
	
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
	}
	
	// ����һЩmap������
	private void setUpMap()
	{
		MyLocationStyle locStyle = new MyLocationStyle();
		locStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_mine_location));
		locStyle.strokeColor(Color.BLACK);
		locStyle.radiusFillColor(Color.argb(50, 180, 180, 0));
		locStyle.strokeWidth(0.1f);

		aMap.setMyLocationStyle(locStyle);
		aMap.setLocationSource(this);
		aMap.getUiSettings().setMyLocationButtonEnabled(false);
		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.setMyLocationEnabled(true);
		
		aMap.setOnCameraChangeListener(this);
		aMap.setOnMarkerClickListener(this);// ��ӵ��marker�����¼�
		aMap.setInfoWindowAdapter(this);// �����ʾinfowindow�����¼�
	}

	@SuppressWarnings("deprecation")
	public void startLocation() {
		if (aMapLocManager == null)
		{
			aMapLocManager = LocationManagerProxy.getInstance(this);
			aMapLocManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 2000, 10, this);
		}
	}

	@SuppressWarnings("deprecation")
	public void stopLocation() {
		if (aMapLocManager != null) {
			aMapLocManager.removeUpdates((AMapLocationListener) this);
			aMapLocManager.destory();
		}
		aMapLocManager = null;
	}

	@Override
	protected void onDestroy() 
	{
		dissmissProgressDialog();
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	protected void onPause() 
	{
		super.onPause();
		mapView.onPause();
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		mapView.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) 
	{
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}
	
	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(AMapLocation location) {
		if (mListener != null && location != null) {
			stopLocation();
			mLocation = location;
			mListener.onLocationChanged(location);// ��ʾϵͳС����
			aMap.animateCamera(CameraUpdateFactory.zoomTo(18));
			doSearchQuery(new LatLonPoint(location.getLatitude(), location
					.getLongitude()));
		}
	}
	
	public void onMapViewBack(View view) {
		finish();
	}

	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		startLocation();
	}

	@Override
	public void deactivate() {
		mListener = null;
		stopLocation();
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
						aMap.clear();// ����֮ǰ��ͼ��
						
						MyLocationStyle locStyle = new MyLocationStyle();
						locStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_mine_location));
						locStyle.strokeColor(Color.BLACK);
						locStyle.radiusFillColor(Color.argb(50, 180, 180, 0));
						locStyle.strokeWidth(0.1f);
						aMap.setMyLocationStyle(locStyle);
						mListener.onLocationChanged(mLocation);// ��ʾϵͳС����
						
						int count = poiItems.size();
						for (int i=0; i<count; i++) {
							PoiItem item = poiItems.get(i);
							if (item == null)
								continue;
							
							if (i == 0 && mSearchType == 1) {
								aMap.animateCamera(CameraUpdateFactory
										.newCameraPosition(new CameraPosition(
												new LatLng(item
														.getLatLonPoint()
														.getLatitude(), item
														.getLatLonPoint()
														.getLongitude()), 18,
												0, 0)), 1000, null);
							}
							
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
							
							View view = LayoutInflater.from(this).inflate(R.layout.view_map_mark, null);
							aMap.addMarker(new MarkerOptions()
									.anchor(0.5f, 1)
									.icon(BitmapDescriptorFactory.fromView(view))
									.position(new LatLng(item.getLatLonPoint().getLatitude(), item.getLatLonPoint().getLongitude()))
									.title(item.toString())
									.snippet(fulldesc));
						}

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

	@Override
	public void onCameraChange(CameraPosition cameraPosition) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCameraChangeFinish(CameraPosition cameraPosition) {
		mCurLat = cameraPosition.target.latitude;
		mCurLng = cameraPosition.target.longitude;
	}
	
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
	protected void doSearchQuery(LatLonPoint lp) {
		showProgressDialog();// ��ʾ���ȿ�
		mSearchType = 0;
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
	
	public void OnMapViewSearch(View view) {
		Editable et = mEdit.getText();
		if (et.length() == 0) {
			Toast.makeText(this, "�����������ؼ���", Toast.LENGTH_LONG).show();
			return;
		}
		
		showProgressDialog();
		mSearchType = 1;
		currentPage = 0;
		query = new PoiSearch.Query(et.toString(), "", UtilLocationData.getCity());// ��һ��������ʾ�����ַ������ڶ���������ʾpoi�������ͣ�������������ʾpoi�������򣨿��ַ�������ȫ����
		query.setPageSize(20);// ����ÿҳ��෵�ض�����poiitem
		query.setPageNum(currentPage);// ���ò��һҳ

		poiSearch = new PoiSearch(this, query);
		poiSearch.setOnPoiSearchListener(this);
		poiSearch.searchPOIAsyn();
	}
	
	public void OnMapViewLocate(View view) {
		if (mLocation != null) {
			aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
					new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 18, 0, 0)), 1000, null);	
		}
	}
	
	public void OnMapViewRefresh(View view) {
		if (mLocation != null) {
			doSearchQuery(new LatLonPoint(mCurLat, mCurLng));
			
			if (aMap.getCameraPosition().zoom > 18) {
				aMap.animateCamera(CameraUpdateFactory.zoomTo(18));
			}
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		if (poiItems != null && poiItems.size() > 0) {
			marker.showInfoWindow();
		}
		return false;
	}

	@Override
	public View getInfoContents(Marker marker) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		final Marker marker = arg0;
		
		View view = mLayoutInflater.inflate(R.layout.view_map_mark_window, null);
		TextView title = (TextView) view.findViewById(R.id.txt_mw_string);
		title.setText(marker.getTitle());
		
		Button button = (Button) view.findViewById(R.id.btn_mw_go);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UtilUserData.leaveGroup(MapViewActivity.this, UtilUserData.getGid());
				
				UtilUserData.findGroup(MapViewActivity.this,
						Double.toString(marker.getPosition().latitude),
						Double.toString(marker.getPosition().longitude),
						marker.getSnippet());
				
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		return view;
	}

}
