package com.besideu.source.chat;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.widget.EditText;

public class UtilBiaoqing {
	
	public static void  backSpace(EditText edit) {
		if (edit.getText().length() == 0)
			return;

		int selectionStart = edit.getSelectionStart();
		if (selectionStart == 0)
			return;

		String body = edit.getText().toString();
		String tempStr = body.substring(0, selectionStart);

		do {
			int i = tempStr.lastIndexOf("-");// 获取最后一个表情的位置
			if (i == -1)
				break;

			CharSequence cs = tempStr.subSequence(i, selectionStart);
			if (!cs.equals("-bq>"))
				break;

			int j = tempStr.lastIndexOf("<bq-");
			if (j == -1)
				break;

			edit.getEditableText().delete(j, selectionStart);
			return;

		} while (false);

		edit.getEditableText().delete(tempStr.length() - 1, selectionStart);
	}

	public static void inputBiaoqing(Context context, int nId, int nIndex, EditText edit) {
		Drawable d = context.getResources().getDrawable(nId);
		int nHeight = getFontHeight(edit.getTextSize());
		d.setBounds(0, 0, nHeight, nHeight);

		ImageSpan imageSpan = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);

		SpannableString spannableString = new SpannableString("<bq-"
				+ Integer.toString(nIndex) + "-bq>");
		spannableString.setSpan(imageSpan, 0, spannableString.length(),
				SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

		Editable e = edit.getText();
		int nStart = edit.getSelectionStart();
		int nEnd = edit.getSelectionEnd();
		e.replace(nStart, nEnd, spannableString);
	}

	public static int getFontHeight(float fontSize) {
		Paint paint = new Paint();
		paint.setTextSize(fontSize);
		FontMetrics fm = paint.getFontMetrics();
		return (int) Math.ceil(fm.descent - fm.top) + 2;
	}

	public static void parseEditable(Context context, Editable e, EditText edit) {
		String strText = e.toString();
		String strTemp = strText;

		int nStart = 0;
		do {
			int i = strTemp.indexOf("<bq-", nStart);
			if (i == -1)
				break;

			int j = strTemp.indexOf("-bq>", i + 4);
			if (j == -1)
				break;

			CharSequence csBq = strTemp.subSequence(i, j + 4);
			CharSequence csId = strTemp.subSequence(i + 4, j);
			int nIndex = Integer.parseInt(csId.toString());

			Drawable d = context.getResources().getDrawable(
					FragmentBiaoqing.imageIds[nIndex]);
			int nHeight = getFontHeight(edit.getTextSize());
			d.setBounds(0, 0, nHeight, nHeight);

			ImageSpan imageSpan = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);

			SpannableString spannableString = new SpannableString(
					csBq.toString());
			spannableString.setSpan(imageSpan, 0, spannableString.length(),
					SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

			e.replace(i, j + 4, spannableString);

			nStart = j + 4;

		} while (true);
	}
}
