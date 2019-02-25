package com.soullistener.nfcdemo;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class NfcIoUtil {
    private Activity mContext;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private MifareClassic mTag;

    public static NfcIoUtil nfcIoUtil;

    private NfcIoUtil(Activity context) {
        this.mContext = context;
    }

    public static NfcIoUtil getInstance(Activity context){

        if (nfcIoUtil == null){
            synchronized (NfcIoUtil.class){
                if (nfcIoUtil == null){
                    nfcIoUtil = new NfcIoUtil(context);
                }
            }
        }

        return nfcIoUtil;
    }

    /**
     * 判断是否支持NFC
     * @return
     */
    public boolean isNfcAble(){
        mNfcAdapter = NfcAdapter.getDefaultAdapter(mContext);
        if (mNfcAdapter == null) {
            Toast.makeText(mContext, "设备不支持NFC！", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(mContext, "请在系统设置中先启用NFC功能！", Toast.LENGTH_LONG).show();
            return false;
        }

        mPendingIntent = PendingIntent.getActivity(mContext, 0, new Intent(mContext,
                getClass()), 0);
        return true;
    }


    public void onNewIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String[] techList = tag.getTechList();

        boolean haveMifareUltralight = false;
        for (String tech : techList) {
            if (tech.contains("MifareClassic")) {
                haveMifareUltralight = true;
                break;
            }
        }

        if (!haveMifareUltralight) {
            Toast.makeText(mContext, "不支持MifareClassic", Toast.LENGTH_LONG).show();
            return;
        }

        mTag = MifareClassic.get(tag);
        if (mTag != null) {
            try {
                mTag.connect();
                if (mTag.isConnected()) {
                    readCard();
                } else {
                    Toast.makeText(mContext, "请贴卡后再操作",
                            Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(mContext, "请贴卡后再操作",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 读取卡片信息
     * @return
     */
    public String[][] readCard() {
        if (checkConnect()) {
            String[][] metaInfo = new String[16][4];
            // 读取TAG
            try {
                // 获取TAG中包含的扇区数
                int sectorCount = mTag.getSectorCount();
                for (int j = 0; j < sectorCount; j++) {
                    int bCount;//当前扇区的块数
                    int bIndex;//当前扇区第一块

                    boolean isAuth = false;
                    if (mTag.authenticateSectorWithKeyA(j, MifareClassic.KEY_DEFAULT)) {
                        isAuth = true;
                    } else if (mTag.authenticateSectorWithKeyB(j, MifareClassic.KEY_DEFAULT)) {
                        isAuth = true;
                    } else {
                        Log.e("密码是", "没有找到密码");
                    }
                    if (isAuth) {
                        bCount = mTag.getBlockCountInSector(j);
                        bIndex = mTag.sectorToBlock(j);
                        for (int i = 0; i < bCount; i++) {
                            byte[] data = mTag.readBlock(bIndex);
                            metaInfo[j][i] += bytesToHexString(data);
                            bIndex++;
                        }
                    }
                }
                return metaInfo;
            } catch (Exception e) {
                Log.e("读取数据错误", e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 改写数据
     * @param block
     * @param blockbyte
     */
    public void commit(int block, byte[] blockbyte) {
        if (checkConnect()) {
            try {
                boolean isAuth = false;
                if (mTag.authenticateSectorWithKeyA(block/4, MifareClassic.KEY_DEFAULT)) {
                    isAuth = true;
                } else if (mTag.authenticateSectorWithKeyB(block/4, MifareClassic.KEY_DEFAULT)) {
                    isAuth = true;
                } else {
                    Log.e("密码是", "没有找到密码");
                }

                if (isAuth) {
                    mTag.writeBlock(block, blockbyte);

                    Toast.makeText(mContext, "写入成功", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("密码是", "没有找到密码");
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断是否连接卡片
     * @return
     */
    public boolean checkConnect() {
        boolean flag = false;
        if (mTag != null) {
            if (mTag.isConnected()) {
                flag = true;
            } else {
                try {
                    mTag.connect();
                    checkConnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
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


    public void onResume(){
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(mContext, mPendingIntent, null,
                    null);
        }
    }

    public void onPause(){
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(mContext);
        }
    }

    public void finish() {
        if (mTag != null) {
            try {
                mTag.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
