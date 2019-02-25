package com.soullistener.nfcdemo;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

/**
 * @author kuan
 * Created on 2019/2/25.
 * @description
 */
public class NfcActivity extends AppCompatActivity {
    private NfcIoUtil nfcIoUtil ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nfcIoUtil =  NfcIoUtil.getInstance(this);
        nfcIoUtil.isNfcAble();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

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

        nfcIoUtil.readCard();


        Log.e("NfcActivity","onNewIntent");
        nfcIoUtil = NfcIoUtil.getInstance(this);
        if (nfcIoUtil.isNfcAble()){
            nfcIoUtil.onNewIntent(intent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        nfcIoUtil.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        nfcIoUtil.onResume();
    }

}