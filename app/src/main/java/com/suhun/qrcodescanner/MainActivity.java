package com.suhun.qrcodescanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private String tag = MainActivity.class.getSimpleName();
    private ZXingScannerView mScannerView;
    private Button sendMessageBtn;
    private TextView showScanResult;
    private ListView scanListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)==
                PackageManager.PERMISSION_GRANTED){
            initQRCode();
        }else{
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 123);
        }
    }
    private void initView(){
        sendMessageBtn = findViewById(R.id.sendMessage);
        showScanResult = findViewById(R.id.showScan);
        mScannerView = findViewById(R.id.zxingView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==123){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                initQRCode();
            }else{
                finish();
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
        sendMessageBtn.setEnabled(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    public void sendMessageFun(View view){
        String textContent = showScanResult.getText().toString();
        String[] resultSplit = textContent.split(":");
        Uri uri = Uri.parse("smsto:"+resultSplit[1]);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", resultSplit[2]);
        startActivity(intent);
    }

    private void initQRCode(){
//        mScannerView = new ZXingScannerView(this);
        mScannerView = findViewById(R.id.zxingView);
    }

    @Override
    public void handleResult(Result rawResult) {
        String[] result;
        // Do something with the result here
        Log.v(tag, rawResult.getText()); // Prints scan results
        Log.v(tag, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
        result = rawResult.getText().split(":");
        if(result!=null && result.length==3 && result[0].equals("SMSTO")){
            sendMessageBtn.setEnabled(true);
            showScanResult.setText( rawResult.getText());
        }
        // If you would like to resume scanning, call this method below:
//        mScannerView.resumeCameraPreview(this);
    }
}