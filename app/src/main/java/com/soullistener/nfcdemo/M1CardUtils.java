package com.soullistener.nfcdemo;

import android.app.Activity;
import android.app.PendingIntent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;


/**
 * @author kuan
 * Created on 2019/2/26.
 * @description MifareClassic卡片读写工具类
 */
public class M1CardUtils {

    private static PendingIntent pendingIntent;
    public static PendingIntent getPendingIntent(){
        return pendingIntent;
    }

    public static void setPendingIntent(PendingIntent pendingIntent){
        M1CardUtils.pendingIntent = pendingIntent;
    }

    /**
     * 判断是否支持NFC
     * @return
     */
    public static NfcAdapter isNfcAble(Activity mContext){
        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(mContext);
        if (mNfcAdapter == null) {
            Toast.makeText(mContext, "设备不支持NFC！", Toast.LENGTH_LONG).show();
        }
        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(mContext, "请在系统设置中先启用NFC功能！", Toast.LENGTH_LONG).show();
        }
        return mNfcAdapter;
    }

    /**
     * 监测是否支持MifareClassic
     * @param tag
     * @param activity
     * @return
     */
    public static boolean isMifareClassic(Tag tag,Activity activity){
        String[] techList = tag.getTechList();
        boolean haveMifareUltralight = false;
        for (String tech : techList) {
            if (tech.contains("MifareClassic")) {
                haveMifareUltralight = true;
                break;
            }
        }
        if (!haveMifareUltralight) {
            Toast.makeText(activity, "不支持MifareClassic", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * 读取卡片信息
     * @return
     */
    public static String[][] readCard(Tag tag)  throws IOException{
        MifareClassic mifareClassic = MifareClassic.get(tag);
        try {
            mifareClassic.connect();
            String[][] metaInfo = new String[16][4];
            // 获取TAG中包含的扇区数
            int sectorCount = mifareClassic.getSectorCount();
            for (int j = 0; j < sectorCount; j++) {
                int bCount;//当前扇区的块数
                int bIndex;//当前扇区第一块
                if (m1Auth(mifareClassic,j)) {
                    bCount = mifareClassic.getBlockCountInSector(j);
                    bIndex = mifareClassic.sectorToBlock(j);
                    for (int i = 0; i < bCount; i++) {
                        byte[] data = mifareClassic.readBlock(bIndex);
                        String dataString = bytesToHexString(data);
                        metaInfo[j][i] = dataString;
                        Log.e("获取到信息",dataString);
                        bIndex++;
                    }
                } else {
                    Log.e("readCard","密码校验失败");
                }
            }
            return metaInfo;
        } catch (IOException e){
            throw new IOException(e);
        } finally {
            try {
                mifareClassic.close();
            }catch (IOException e){
                throw new IOException(e);
            }
        }
    }

    /**
     * 改写数据
     * @param block
     * @param blockbyte
     */
    public static boolean writeBlock(Tag tag, int block, byte[] blockbyte) throws IOException {
        MifareClassic mifareClassic = MifareClassic.get(tag);
        try {
            mifareClassic.connect();
            if (m1Auth(mifareClassic,block/4)) {
                mifareClassic.writeBlock(block, blockbyte);
                Log.e("writeBlock","写入成功");
            } else {
                Log.e("密码是", "没有找到密码");
                return false;
            }
        } catch (IOException e){
            throw new IOException(e);
        } finally {
            try {
                mifareClassic.close();
            }catch (IOException e){
                throw new IOException(e);
            }
        }
        return true;

    }

    /**
     * 密码校验
     * @param mTag
     * @param position
     * @return
     * @throws IOException
     */
    public static boolean m1Auth(MifareClassic mTag,int position) throws IOException {
        if (mTag.authenticateSectorWithKeyA(position, MifareClassic.KEY_DEFAULT)) {
            return true;
        } else if (mTag.authenticateSectorWithKeyB(position, MifareClassic.KEY_DEFAULT)) {
            return true;
        }
        return false;
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            System.out.println(buffer);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }
}
