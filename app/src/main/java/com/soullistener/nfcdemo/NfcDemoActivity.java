package com.soullistener.nfcdemo;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author kuan
 * Created on 2019/2/25.
 * @description
 */
public class NfcDemoActivity extends AppCompatActivity {
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "设备不支持NFC！", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "请在系统设置中先启用NFC功能！", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()), 0);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "设备不支持NFC！", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "请在系统设置中先启用NFC功能！", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()), 0);
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
            Toast.makeText(this, "不支持MifareClassic", Toast.LENGTH_LONG).show();
            return;
        }

//        try {
//            writeBlock(tag, 4*6+2, "9999999999      ".getBytes());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        ArrayList data = new ArrayList();
        try {
            data = readTag(tag);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取卡片信息
     *
     * @return
     */
    public ArrayList readTag(Tag tag) throws IOException {
        MifareClassic mTag = MifareClassic.get(tag);
        mTag.connect();
        ArrayList metaInfo = new ArrayList();
        // 读取TAG
        try {
            // 获取TAG中包含的扇区数
            int sectorCount = mTag.getSectorCount();
            for (int j = 5; j < 7; j++) {
                int bCount;//当前扇区的块数
                int bIndex;//当前扇区第一块

                boolean isAuth = false;
                if (mTag.authenticateSectorWithKeyA(j, MifareClassic.KEY_DEFAULT)) {
                    isAuth = true;
                } else if (mTag.authenticateSectorWithKeyB(j, MifareClassic.KEY_DEFAULT)) {
                    isAuth = true;
                } else {
                    Log.e("扇区", sectorCount + "没有找到密码");
                }
                if (isAuth) {
                    bCount = mTag.getBlockCountInSector(j);
                    bIndex = mTag.sectorToBlock(j);
                    for (int i = 0; i < bCount; i++) {
                        byte[] data = mTag.readBlock(bIndex);
                        Log.e("第"+i+"数据:",convertHexToString(bytesToHexString(data)));
                        metaInfo.add(bytesToHexString(data));
                        bIndex++;
                    }
                } else {
                    metaInfo.add("                ");
                    metaInfo.add("                ");
                    metaInfo.add("                ");
                    metaInfo.add("                ");
                }
            }
            return metaInfo;
        } catch (Exception e) {
            Log.e("读取数据错误", e.getMessage());
            e.printStackTrace();
        } finally {
            //释放卡片
            mTag.close();
        }
        return null;
    }

    /**
     * 写卡数据
     *
     * @param tag
     * @param block
     * @param blockbyte
     * @throws IOException
     */
    public void writeBlock(Tag tag, int block, byte[] blockbyte) throws IOException {
        MifareClassic mTag = MifareClassic.get(tag);
        mTag.connect();
        try {
            boolean isAuth = false;
            if (mTag.authenticateSectorWithKeyA(block / 4, MifareClassic.KEY_DEFAULT)) {
                isAuth = true;
            } else if (mTag.authenticateSectorWithKeyB(block / 4, MifareClassic.KEY_DEFAULT)) {
                isAuth = true;
            } else {
                Log.e("writeBlock", "没有找到密码");
            }

            if (isAuth) {
                mTag.writeBlock(block, blockbyte);

                Toast.makeText(this, "写入成功", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("writeBlock", "写入失败");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //释放卡片
            mTag.close();

        }
    }



    @Override
    public void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null,
                    null);
        }
    }

    /**
     * 10进制字符序列转换为16进制字符串
     */
    private String bytesToHexString(byte[] src) {
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

    /**
     * 16进制byte数组转换为10进制字符串
     * @param hex
     * @return
     */
    public String convertHexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        for (int i = 0; i < hex.length() - 1; i += 2) {
            String output = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char) decimal);
            temp.append(decimal);
        }

        return sb.toString();
    }
}
