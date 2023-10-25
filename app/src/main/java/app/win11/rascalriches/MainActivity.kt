package app.win11.rascalriches

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import app.win11.rascalriches.databinding.ActivityMainBinding
import com.adjust.sdk.webbridge.AdjustBridge
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(1024,1024)

        AdjustBridge.registerAndGetInstance(getApplication(), binding.contentView);

        adView = binding.adView

        try {
            binding.contentView.loadUrl(GlobalConfig.gameURL)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if(GlobalConfig.gameURL.contains(GlobalConfig.appCode)){
            Log.d("appCode", "true")
            MobileAds.initialize(this) {
                Log.d(
                    "AdMobs",
                    "Initialized Complete."
                )
            }

            val adRequest: AdRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }
        else{
            Log.d("appCode", "false")
        }

    }
}