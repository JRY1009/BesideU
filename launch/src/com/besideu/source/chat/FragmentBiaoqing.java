package com.besideu.source.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.besideu.source.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

public class FragmentBiaoqing extends Fragment {

	public interface BqCallBackEvent
	{
	    public void OnBiaoqingSelectEvent(int nId, int nIndex);
	}
	
	BqCallBackEvent mBqCallBackEvent = null;
	
	public static final int[] imageIds = new int[] { 
			R.drawable.bq_1, R.drawable.bq_2,
			R.drawable.bq_3, R.drawable.bq_4, R.drawable.bq_5,
			R.drawable.bq_6, R.drawable.bq_7, R.drawable.bq_8,
			R.drawable.bq_9, R.drawable.bq_10, R.drawable.bq_11,
			R.drawable.bq_12, R.drawable.bq_13, R.drawable.bq_14,
			R.drawable.bq_15, R.drawable.bq_16, R.drawable.bq_17,
			R.drawable.bq_18, R.drawable.bq_19, R.drawable.bq_20,
			R.drawable.chatting_biaoqing_del_btn, 
			R.drawable.bq_21, R.drawable.bq_22, R.drawable.bq_23,
			R.drawable.bq_24, R.drawable.bq_25, R.drawable.bq_26,
			R.drawable.bq_27, R.drawable.bq_28, R.drawable.bq_29,
			R.drawable.bq_30, R.drawable.bq_31, R.drawable.bq_32,
			R.drawable.bq_33, R.drawable.bq_34, R.drawable.bq_35,
			R.drawable.bq_36, R.drawable.bq_37, R.drawable.bq_38,
			R.drawable.bq_39, R.drawable.bq_40, 
			R.drawable.chatting_biaoqing_del_btn,
			R.drawable.bq_41,
			R.drawable.bq_42, R.drawable.bq_43, R.drawable.bq_44,
			R.drawable.bq_45, R.drawable.bq_46, R.drawable.bq_47,
			R.drawable.bq_48, R.drawable.bq_49, R.drawable.bq_50,
			R.drawable.bq_51, R.drawable.bq_52, R.drawable.bq_53,
			R.drawable.bq_54, R.drawable.bq_55, R.drawable.bq_56,
			R.drawable.bq_57, R.drawable.bq_58, R.drawable.bq_59,
			R.drawable.bq_60, 
			R.drawable.chatting_biaoqing_del_btn,
			R.drawable.bq_61, R.drawable.bq_62,
			R.drawable.bq_63, R.drawable.bq_64, R.drawable.bq_65,
			R.drawable.bq_66, R.drawable.bq_67, R.drawable.bq_68,
			R.drawable.bq_69, R.drawable.bq_70, R.drawable.bq_71,
			R.drawable.bq_72, R.drawable.bq_73, R.drawable.bq_74,
			R.drawable.bq_75, R.drawable.bq_76, R.drawable.bq_77,
			R.drawable.bq_78, R.drawable.bq_79, R.drawable.bq_80,
			R.drawable.chatting_biaoqing_del_btn, 
			R.drawable.bq_81, R.drawable.bq_82, R.drawable.bq_83,
			R.drawable.bq_84, R.drawable.bq_85, R.drawable.bq_86,
			R.drawable.bq_87, R.drawable.bq_88, R.drawable.bq_89,
			R.drawable.bq_90, R.drawable.bq_91, R.drawable.bq_92,
			R.drawable.bq_93, R.drawable.bq_94, R.drawable.bq_95,
			R.drawable.bq_96, R.drawable.bq_97, R.drawable.bq_98,
			R.drawable.bq_99, R.drawable.bq_100, 
			R.drawable.chatting_biaoqing_del_btn,
			R.drawable.bq_101,
			R.drawable.bq_102, R.drawable.bq_103, R.drawable.bq_104,
			R.drawable.bq_105, R.drawable.bq_106, R.drawable.bq_107, 
			R.drawable.bq_108, R.drawable.bq_109, R.drawable.bq_110, 
			R.drawable.bq_111, R.drawable.bq_112, R.drawable.bq_113, 
			R.drawable.bq_114, R.drawable.bq_115, R.drawable.bq_116,
			R.drawable.bq_117, R.drawable.bq_118, R.drawable.bq_119,
			R.drawable.bq_120, 
			R.drawable.chatting_biaoqing_del_btn};

	private final int MAX_BIAOQING_COUNT = 21;
	
	private ViewPager mViewPager;
	private ArrayList<ImageView> mDotList = new ArrayList<ImageView>();
	private ArrayList<View> mPageList = new ArrayList<View>();
	
	private int mSelectIndex = 0;
	
	public void setListener(BqCallBackEvent event)
	{
		mBqCallBackEvent = event;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.chat_bottom_biaoqing, container, false);
		mViewPager = (ViewPager) view.findViewById(R.id.vp_biaoqing);
		mViewPager.setOnPageChangeListener(new OnBQPageChangeListener());
		
		mDotList.add((ImageView) view.findViewById(R.id.img_dot1));
		mDotList.add((ImageView) view.findViewById(R.id.img_dot2));
		mDotList.add((ImageView) view.findViewById(R.id.img_dot3));
		mDotList.add((ImageView) view.findViewById(R.id.img_dot4));
		mDotList.add((ImageView) view.findViewById(R.id.img_dot5));
		mDotList.add((ImageView) view.findViewById(R.id.img_dot6));

		for (int i=0; i<imageIds.length/MAX_BIAOQING_COUNT; ++i)
		{
			View page = inflater.inflate(R.layout.chat_bottom_biaoqing_page, null);
			mPageList.add(page);

			List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
			for (int n=i*MAX_BIAOQING_COUNT; n<imageIds.length && n<(i+1)*MAX_BIAOQING_COUNT; n++) 
			{
				Map<String, Object> listItem = new HashMap<String, Object>();
				listItem.put("image", imageIds[n]);
				listItems.add(listItem);
			}
	
			// 创建一个SimpleAdapter
			SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(),
					listItems, 
					R.layout.chat_biaoqing_cell, 
					new String[] { "image" },
					new int[] { R.id.image1 });
			
			GridView grid = (GridView) page.findViewById(R.id.gv_biaoqing);
			// 为GridView设置Adapter
			grid.setAdapter(simpleAdapter);
			grid.setTag(i);
			grid.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
				{
					int tag = (Integer) parent.getTag();
					int nIndex = tag * MAX_BIAOQING_COUNT + position;
					if (mBqCallBackEvent != null) {
						mBqCallBackEvent.OnBiaoqingSelectEvent(imageIds[nIndex % imageIds.length], nIndex % imageIds.length);
					}
				}
			});
		}
		
        PagerAdapter adapter = new PagerAdapter() 
        {
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				return mPageList.size();
			}
			
			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager)container).removeView(mPageList.get(position));
			}
			
			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager)container).addView(mPageList.get(position));
				return mPageList.get(position);
			}
		};
		
		mViewPager.setAdapter(adapter);
		return view;
	}

	public class OnBQPageChangeListener implements OnPageChangeListener
	{
		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		@Override
		public void onPageSelected(int arg0) 
		{
			mDotList.get(arg0).setImageDrawable(getResources().getDrawable(R.drawable.page_selected));
			mDotList.get(mSelectIndex).setImageDrawable(getResources().getDrawable(R.drawable.page_unselected));
			mSelectIndex = arg0;
		}	
	}
}
