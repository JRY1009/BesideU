package com.besideu.source.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;

public class UtilSystem {
	
    public static void createShortCut(Activity act, int iconResId,  
            int appnameResId) {
  
        Intent shortcutintent = new Intent(  
                "com.android.launcher.action.INSTALL_SHORTCUT");  
        // 不允许重复创建  
        shortcutintent.putExtra("duplicate", false);  
        // 需要现实的名称  
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME,  
                act.getString(appnameResId));  
        // 快捷图片  
        Parcelable icon = Intent.ShortcutIconResource.fromContext(  
                act.getApplicationContext(), iconResId);  
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);  
        // 点击快捷图片，运行的程序主入口  
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,  
                new Intent(act.getApplicationContext(), act.getClass()));  
        // 发送广播  
        act.sendBroadcast(shortcutintent);  
    } 
}
