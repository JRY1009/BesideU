package com.besideu.source.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageCompress {

	public static ByteArrayInputStream compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��
        int options = 100;
        while ( baos.toByteArray().length / 1024 > 256) {    //ѭ���ж����ѹ����ͼƬ�Ƿ����256kb,���ڼ���ѹ��        
            baos.reset();//����baos�����baos
            options -= 20;//ÿ�ζ�����10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//����ѹ��options%����ѹ��������ݴ�ŵ�baos��

        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//��ѹ���������baos��ŵ�ByteArrayInputStream��
        return isBm;
    }

	public static ByteArrayInputStream getImage(String srcPath, float ww, float hh) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //��ʼ����ͼƬ����ʱ��options.inJustDecodeBounds ���true��
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//��ʱ����bmΪ��

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��
        int be = 1;//be=1��ʾ������
        if (w > h && w > ww) {//�����ȴ�Ļ����ݿ�ȹ̶���С����
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//����߶ȸߵĻ����ݿ�ȹ̶���С����
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//�������ű���
        //���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//ѹ���ñ�����С���ٽ�������ѹ��
    }

}
