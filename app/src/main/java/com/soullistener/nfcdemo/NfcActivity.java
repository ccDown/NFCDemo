package com.soullistener.nfcdemo;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;

/**
 * @author kuan
 * Created on 2019/2/25.
 * @description
 */
public class NfcActivity extends AppCompatActivity {
    private NfcAdapter mNfcAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNfcAdapter = M1CardUtils.isNfcAble(this);
        M1CardUtils.setPendingIntent(PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()), 0));
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mNfcAdapter = M1CardUtils.isNfcAble(this);
        M1CardUtils.setPendingIntent(PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()), 0));

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        M1CardUtils.isMifareClassic(tag,this);
        try {
            if (M1CardUtils.writeBlock(tag, 25,"9966332211445566".getBytes())){
                Log.e("onNewIntent","写入成功");
            } else {
                Log.e("onNewIntent","写入失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            M1CardUtils.readCard(tag);
        } catch (IOException e) {
            e.printStackTrace();
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
            mNfcAdapter.enableForegroundDispatch(this, M1CardUtils.getPendingIntent(),
                    null, null);
        }
    }

}