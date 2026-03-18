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
    private String rewardAdSlotId = "887249b0"; // 测试广告位 ID，上线需替换
    private boolean isAdReady = false;
    private boolean isWatchingAd = false;
    private String pendingReward = null;
    
    // 测试设备 ID（添加你的设备 ID 到白名单）
    private static final String TEST_DEVICE_ID = "YOUR_DEVICE_ID";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 初始化穿山甲 SDK
        initTTAdSdk();
        
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
        
        // 启用缩放（可选）
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        
        // 添加 JavaScript 接口
        webView.addJavascriptInterface(new AdInterface(), "AdInterface");
        
        // 配置 WebChromeClient 以支持更多功能
        webView.setWebChromeClient(new WebChromeClient());
        
        // 加载本地 HTML 游戏
        webView.loadUrl("file:///android_asset/index.html");
        
        // 延迟加载广告
        new Handler(Looper.getMainLooper()).postDelayed(this::loadRewardAd, 2000);
    }
    
    // 初始化穿山甲 SDK
    private void initTTAdSdk() {
        ttAdSdk = TTAdSdk.init(this, new TTAdSdkConfig.Builder()
                .setAppId("5093868") // 测试 App ID，上线需替换
                .setUseTextureView(true)
                .setAllowShowNotify(true)
                .setDebugBuild(true) // 测试时开启，上线关闭
                .build());
        
        // 请求权限（如果需要）
        TTAdSdk.getAdManager().requestPermissionIfNecessary(this);
    }
    
    // 加载激励视频广告
    private void loadRewardAd() {
        if (ttAdSdk == null) return;
        
        TTAdNative rewardVideoAdNative = ttAdSdk.createAdNative(this);
        
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(rewardAdSlotId)
                .setSupportDeepLink(true)
                .setRewardName("金币") // 奖励名称
                .setRewardAmount(10) // 奖励数量
                .setUserID("user123") // 用户 ID
                .setMediaExtra("extra_data")
                .setOrientation(TTAdConstant.VERTICAL)
                .build();
        
        rewardVideoAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.e("TTAdSdk", "Error loading ad: " + code + " - " + message);
                isAdReady = false;
            }
            
            @Override
            public void onRewardVideoAdLoad(RewardVideoAd ad) {
                Log.d("TTAdSdk", "Reward video ad loaded");
                rewardVideoAd = ad;
                isAdReady = true;
                
                // 设置广告回调
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
                        
                        // 3 秒后重新加载广告
                        new Handler(Looper.getMainLooper()).postDelayed(MainActivity.this::loadRewardAd, 3000);
                    }
                    
                    @Override
                    public void onVideoComplete() {
                        Log.d("TTAdSdk", "Video complete");
                        // 视频播放完成，发放奖励
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
    
    // JavaScript 接口
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
    
    // 发放奖励（调用 JavaScript）
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
