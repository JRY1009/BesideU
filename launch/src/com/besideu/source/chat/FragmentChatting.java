package com.besideu.source.chat;

import java.io.FileNotFoundException;


import com.besideu.source.R;
import com.besideu.source.chat.FragmentBiaoqing.BqCallBackEvent;
import com.besideu.source.chat.FragmentElement.EmCallBackEvent;
import com.besideu.source.ctrl.PullToRefreshListView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class FragmentChatting extends Fragment implements EmCallBackEvent,
		BqCallBackEvent {
	
	private Button mBtnSend;
	private EditText mEditContent;

	private ImageView mImgElement;
	private ImageView mImgFace;

	private PullToRefreshListView mListView;
	private RelativeLayout mRlFmContent;
	private RelativeLayout mRlBottomRBtn;

	private Fragment mFmContent;
	private FragmentBiaoqing mFmBiaoqing;
	private FragmentElement mFmElement;
	private FragmentEmpty mFmEmpty;
	private FragmentManager mFmManager;

	private OnClickListener mBtnListener = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_send:
				ChatMsgMgr.getInstance().sendText();
				break;
			case R.id.img_element:
				showFm(mFmElement);
				break;
			case R.id.img_face:
				showFm(mFmBiaoqing);
				break;
			}
		}
	};
	
	private TextWatcher mWatcher = new TextWatcher() {
		@Override
		public void afterTextChanged(Editable arg0) {
			if (mEditContent.getText().length() > 0) {
				mBtnSend.setVisibility(View.VISIBLE);
				mImgElement.setVisibility(View.GONE);
			} else {
				mBtnSend.setVisibility(View.GONE);
				mImgElement.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mFmElement != null) {
			mFmElement.onActivityResult(requestCode, resultCode, data);
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.chat_fragment, container, false);
		mBtnSend = (Button) view.findViewById(R.id.btn_send);
		mBtnSend.setOnClickListener(mBtnListener);

		mImgElement = (ImageView) view.findViewById(R.id.img_element);
		mImgElement.setOnClickListener(mBtnListener);

		mImgFace = (ImageView) view.findViewById(R.id.img_face);
		mImgFace.setOnClickListener(mBtnListener);

		mEditContent = (EditText) view.findViewById(R.id.et_sendmessage);
		mEditContent.addTextChangedListener(mWatcher);

		mListView = (PullToRefreshListView) view.findViewById(R.id.listview);

		mRlFmContent = (RelativeLayout) view.findViewById(R.id.rl_fragment_content);
		mRlBottomRBtn = (RelativeLayout) view.findViewById(R.id.rl_bottom_rbtn);
		// ---------------------------------------------------------------------
		mFmManager = getActivity().getSupportFragmentManager();
		mFmElement = new FragmentElement();
		mFmElement.setListener(this);

		mFmBiaoqing = new FragmentBiaoqing();
		mFmBiaoqing.setListener(this);

		mFmEmpty = new FragmentEmpty();
		switchContent(mFmEmpty);
		
		ChatMsgMgr.getInstance().init(getActivity(), mListView, mEditContent);
		return view;
	}
	
	public void onRefreshPlace() {
		ChatMsgMgr.getInstance().refreshState();
	}
	

	public void OnDispatchTouch(View v, MotionEvent ev) {
		if (isHideKeyboard((int) ev.getX(), (int) ev.getY())) {
			InputMethodManager im = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(mEditContent.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		} else {
			mEditContent.requestFocus();
		}

		if (isHideElement((int) ev.getX(), (int) ev.getY())) {
			switchContent(mFmEmpty);
			mRlFmContent.setVisibility(View.GONE);
		}
	}

	private void showFm(Fragment fragment) {
		if (mRlFmContent.getVisibility() == View.GONE) {
			switchContent(fragment);
			mRlFmContent.setVisibility(View.VISIBLE);
		} else {
			if (mFmContent == fragment) {
				switchContent(mFmEmpty);
				mRlFmContent.setVisibility(View.GONE);
			} else
				switchContent(fragment);
		}
	}

	private boolean isHideKeyboard(int x, int y) {
		if (mImgElement.getVisibility() == View.GONE) {
			return (!isPtInView(x, y, mEditContent) && !isPtInView(x, y,
					mBtnSend));
		} else
			return !isPtInView(x, y, mEditContent);
	}

	private boolean isHideElement(int x, int y) {
		return (!isPtInView(x, y, mRlFmContent)
				&& !isPtInView(x, y, mRlBottomRBtn)
				&& !isPtInView(x, y, mBtnSend) && !isPtInView(x, y, mImgFace));
	}

	private boolean isPtInView(int x, int y, View view) {
		int[] l = { 0, 0 };
		view.getLocationInWindow(l);
		Rect rcView = new Rect(l[0], l[1], l[0] + view.getWidth(), l[1]
				+ view.getHeight());

		return rcView.contains(x, y);
	}

	public void switchContent(Fragment fragment) {
		if (mFmContent != fragment) {
			if (fragment.isAdded()) {
				if (mFmContent != null && mFmContent.isAdded()) {
					mFmManager.beginTransaction().hide(mFmContent)
							.show(fragment).commit();
				} else {
					mFmManager.beginTransaction().show(fragment).commit();
				}
			} else {
				if (mFmContent != null && mFmContent.isAdded()) {
					mFmManager.beginTransaction().hide(mFmContent)
							.add(R.id.rl_fragment_content, fragment).commit();
				} else {
					mFmManager.beginTransaction()
							.add(R.id.rl_fragment_content, fragment).commit();
				}
			}

			mFmContent = fragment;
		}
	}

	@Override
	public void OnPreviewOKEvent(String strPath) {
		try {
			ChatMsgMgr.getInstance().uploadPicture(strPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void OnBiaoqingSelectEvent(int nId, int nIndex) {
		if (nId == R.drawable.chatting_biaoqing_del_btn) {
			UtilBiaoqing.backSpace(mEditContent);
		} else {
			UtilBiaoqing.inputBiaoqing(this.getActivity(), nId, nIndex, mEditContent);
		}
	}

}
