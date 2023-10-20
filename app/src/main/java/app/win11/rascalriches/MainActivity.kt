package app.win11.rascalriches

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.win11.rascalriches.databinding.ActivityMainBinding
import com.adjust.sdk.webbridge.AdjustBridge

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AdjustBridge.registerAndGetInstance(getApplication(), binding.contentView);

        try {
            binding.contentView.loadUrl(GlobalConfig.gameURL)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}