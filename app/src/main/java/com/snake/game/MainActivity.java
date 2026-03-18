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

import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;
import com.qq.e.comm.constants.AdPlacementType;
import com.qq.e.comm.managers.GDTAdSdk;

public class MainActivity extends AppCompatActivity {
    
    private WebView webView;
    private RewardVideoAD rewardVideoAD;
    private String adSlotId = "887249b0"; // 测试广告位 ID，上线需替换
    private int appId = "1109652815"; // 测试 App ID，上线需替换
    private boolean isAdReady = false;
    private boolean isWatchingAd = false;
    private String pendingReward = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 初始化优量汇 SDK
        GDTAdSdk.init(this, appId);
        
        // 创建 WebView 并加载游戏
        webView = new WebView(this);
        setContentView(webView);
        
        // 配置 WebView 设置
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(false);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        
        // 添加 JavaScript 接口
        webView.addJavascriptInterface(new AdInterface(), "AdInterface");
        
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("file:///android_asset/index.html");
        
        // 延迟加载广告
        new Handler(Looper.getMainLooper()).postDelayed(this::loadRewardAd, 2000);
    }
    
    // 加载激励视频广告
    private void loadRewardAd() {
        VideoOption videoOption = new VideoOption.Builder()
                .setAutoPlayPolicy(VideoOption.AutoPlay.ALWAYS)
                .build();
        
        rewardVideoAD = new RewardVideoAD(
            this, 
            adSlotId, 
            appId,
            new RewardVideoADListener() {
                @Override
                public void onADLoad() {
                    Log.d("GDTAdSdk", "Ad loaded");
                    isAdReady = true;
                }
                
                @Override
                public void onVideoCached() {
                    Log.d("GDTAdSdk", "Ad cached");
                }
                
                @Override
                public void onADShow() {
                    Log.d("GDTAdSdk", "Ad showing");
                    isWatchingAd = true;
                }
                
                @Override
                public void onADExpose() {
                    Log.d("GDTAdSdk", "Ad exposed");
                }
                
                @Override
                public void onRewardVerify(boolean verify, int amount, String rewardName, int errorCode, String errorMsg) {
                    Log.d("GDTAdSdk", "Reward verify: " + verify);
                    if (verify) {
                        grantReward(pendingReward);
                        pendingReward = null;
                    }
                }
                
                @Override
                public void onADClick() {
                    Log.d("GDTAdSdk", "Ad clicked");
                }
                
                @Override
                public void onVideoComplete() {
                    Log.d("GDTAdSdk", "Video complete");
                }
                
                @Override
                public void onADClose() {
                    Log.d("GDTAdSdk", "Ad closed");
                    isWatchingAd = false;
                    isAdReady = false;
                    new Handler(Looper.getMainLooper()).postDelayed(MainActivity.this::loadRewardAd, 3000);
                }
                
                @Override
                public void onError(Throwable throwable) {
                    Log.e("GDTAdSdk", "Error: " + throwable.getMessage());
                    isAdReady = false;
                }
            },
            videoOption
        );
        
        rewardVideoAD.loadAD();
    }
    
    // JavaScript 接口
    public class AdInterface {
        @JavascriptInterface
        public boolean isAdReady() {
            return isAdReady && !isWatchingAd;
        }
        
        @JavascriptInterface
        public void showRewardAd(String rewardType) {
            runOnUiThread(() -> {
                if (isAdReady && !isWatchingAd && rewardVideoAD != null) {
                    pendingReward = rewardType;
                    rewardVideoAD.showAD(this);
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
    
    // 发放奖励
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
