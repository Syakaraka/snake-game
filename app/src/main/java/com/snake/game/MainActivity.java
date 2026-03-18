package com.snake.game;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebChromeClient;
import android.webkit.JavascriptInterface;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bytedance.sdk.openadsdk.*;
import com.bytedance.sdk.openadsdk.reward.RewardVideoAd;

public class MainActivity extends AppCompatActivity {
    
    private WebView webView;
    private TTAdSdk ttAdSdk;
    private RewardVideoAd rewardVideoAd;
    private String adSlotId = "946061972"; // 测试广告位 ID
    private String appId = "5093868"; // 测试 App ID
    private boolean isAdReady = false;
    private boolean isWatchingAd = false;
    private String pendingReward = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 初始化穿山甲 SDK
        ttAdSdk = TTAdSdk.init(this, new TTAdSdkConfig.Builder()
                .setAppId(appId)
                .setUseTextureView(true)
                .setAllowShowNotify(true)
                .setDebugBuild(true)
                .build());
        
        TTAdSdk.getAdManager().requestPermissionIfNecessary(this);
        
        webView = new WebView(this);
        setContentView(webView);
        
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(false);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        
        webView.addJavascriptInterface(new AdInterface(), "AdInterface");
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("file:///android_asset/index.html");
        
        new Handler(Looper.getMainLooper()).postDelayed(this::loadRewardAd, 2000);
    }
    
    private void loadRewardAd() {
        if (ttAdSdk == null) return;
        
        TTAdNative rewardVideoAdNative = ttAdSdk.createAdNative(this);
        
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adSlotId)
                .setSupportDeepLink(true)
                .setRewardName("金币")
                .setRewardAmount(10)
                .setUserID("user123")
                .setMediaExtra("extra_data")
                .setOrientation(TTAdConstant.VERTICAL)
                .build();
        
        rewardVideoAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.e("TTAdSdk", "Error: " + code + " - " + message);
                isAdReady = false;
            }
            
            @Override
            public void onRewardVideoAdLoad(RewardVideoAd ad) {
                Log.d("TTAdSdk", "Reward video ad loaded");
                rewardVideoAd = ad;
                isAdReady = true;
                
                rewardVideoAd.setRewardAdInteractionListener(new RewardVideoAd.RewardVideoAdInteractionListener() {
                    @Override
                    public void onAdShow() {
                        Log.d("TTAdSdk", "Ad showing");
                        isWatchingAd = true;
                    }
                    
                    @Override
                    public void onAdVideoBarClick() {
                        Log.d("TTAdSdk", "Ad bar clicked");
                    }
                    
                    @Override
                    public void onAdClose() {
                        Log.d("TTAdSdk", "Ad closed");
                        isWatchingAd = false;
                        isAdReady = false;
                        new Handler(Looper.getMainLooper()).postDelayed(MainActivity.this::loadRewardAd, 3000);
                    }
                    
                    @Override
                    public void onVideoComplete() {
                        Log.d("TTAdSdk", "Video complete");
                        if (pendingReward != null) {
                            grantReward(pendingReward);
                            pendingReward = null;
                        }
                    }
                    
                    @Override
                    public void onVideoError() {
                        Log.e("TTAdSdk", "Video error");
                        isWatchingAd = false;
                    }
                    
                    @Override
                    public void onAdShowedByOther() {
                        Log.d("TTAdSdk", "Ad showed by other");
                    }
                    
                    @Override
                    public void onSkippedVideo() {
                        Log.d("TTAdSdk", "Video skipped");
                        isWatchingAd = false;
                    }
                });
            }
        });
    }
    
    public class AdInterface {
        @JavascriptInterface
        public boolean isAdReady() {
            return isAdReady && !isWatchingAd;
        }
        
        @JavascriptInterface
        public void showRewardAd(String rewardType) {
            runOnUiThread(() -> {
                if (isAdReady && !isWatchingAd && rewardVideoAd != null) {
                    pendingReward = rewardType;
                    rewardVideoAd.showRewardVideoAd(MainActivity.this, RewardVideoAd.RitScenes.CUSTOMIZE_SCENES, "scenes_test");
                } else {
                    Toast.makeText(MainActivity.this, "广告加载中，请稍后", Toast.LENGTH_SHORT).show();
                }
            });
        }
        
        @JavascriptInterface
        public void loadAd() {
            runOnUiThread(MainActivity.this::loadRewardAd);
        }
    }
    
    private void grantReward(String rewardType) {
        runOnUiThread(() -> {
            webView.post(() -> {
                webView.evaluateJavascript(
                    "javascript:onRewardGranted('" + rewardType + "')",
                    null
                );
            });
            Toast.makeText(this, "奖励已发放！", Toast.LENGTH_SHORT).show();
        });
    }
    
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
        }
    }
}
