package com.besideu.source.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;

public class UtilSystem {
	
    public static void createShortCut(Activity act, int iconResId,  
            int appnameResId) {
  
        Intent shortcutintent = new Intent(  
                "com.android.launcher.action.INSTALL_SHORTCUT");  
        // �������ظ�����  
        shortcutintent.putExtra("duplicate", false);  
        // ��Ҫ��ʵ������  
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME,  
                act.getString(appnameResId));  
        // ���ͼƬ  
        Parcelable icon = Intent.ShortcutIconResource.fromContext(  
                act.getApplicationContext(), iconResId);  
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);  
        // ������ͼƬ�����еĳ��������  
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,  
                new Intent(act.getApplicationContext(), act.getClass()));  
        // ���͹㲥  
        act.sendBroadcast(shortcutintent);  
    } 
}
