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

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class MainActivity extends AppCompatActivity {
    
    private WebView webView;
    private RewardedAd rewardedAd;
    private String adUnitId = "ca-app-pub-3940256099942544/5224354917"; // 测试广告位 ID
    private boolean isAdReady = false;
    private boolean isWatchingAd = false;
    private String pendingReward = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 初始化 AdMob
        MobileAds.initialize(this, initializationStatus -> {
            Log.d("AdMob", "Initialized: " + initializationStatus);
        });
        
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
        AdRequest adRequest = new AdRequest.Builder().build();
        
        RewardedAd.load(this, adUnitId, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(RewardedAd ad) {
                Log.d("AdMob", "Ad loaded");
                rewardedAd = ad;
                isAdReady = true;
                
                rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdShow() {
                        Log.d("AdMob", "Ad showing");
                        isWatchingAd = true;
                    }
                    
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        Log.d("AdMob", "Ad dismissed");
                        isWatchingAd = false;
                        isAdReady = false;
                        new Handler(Looper.getMainLooper()).postDelayed(MainActivity.this::loadRewardAd, 3000);
                    }
                    
                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        Log.e("AdMob", "Ad failed to show: " + adError.getMessage());
                        isWatchingAd = false;
                        isAdReady = false;
                    }
                });
            }
            
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                Log.e("AdMob", "Ad failed to load: " + loadAdError.getMessage());
                isAdReady = false;
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
                if (isAdReady && !isWatchingAd && rewardedAd != null) {
                    pendingReward = rewardType;
                    rewardedAd.show(MainActivity.this, rewardItem -> {
                        Log.d("AdMob", "Reward earned: " + rewardItem.getAmount() + " " + rewardItem.getType());
                        if (pendingReward != null) {
                            grantReward(pendingReward);
                            pendingReward = null;
                        }
                    });
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
