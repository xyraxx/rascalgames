package app.win11.rascalriches

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import app.win11.rascalriches.databinding.ActivityPolicyBinding
import com.facebook.FacebookSdk
import com.facebook.FacebookSdk.setAdvertiserIDCollectionEnabled
import com.facebook.FacebookSdk.setAutoLogAppEventsEnabled

class PolicyActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPolicyBinding

    private lateinit var policyWV : WebView
    private lateinit var agree: Button
    private lateinit var decline: Button

    private var consentDialog : AlertDialog.Builder? = null
    private lateinit var pref : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = getSharedPreferences(GlobalConfig.appCode, MODE_PRIVATE)

        policyWV = binding.policyWV
        agree = binding.BtnAgree
        decline = binding.BtnDisagree

        policyWV.webViewClient = WebViewClient()
        policyWV.loadUrl(GlobalConfig.policyURL)

        agree.setOnClickListener {
            consentDialog = AlertDialog.Builder(this@PolicyActivity)
            consentDialog!!.setTitle("User Data Consent")
            consentDialog!!.setMessage("We may collect your information based on your activities during the usage of the app, to provide better user experience.")
            consentDialog!!.setPositiveButton(
                "Agree"
            ) { dialogInterface: DialogInterface, _: Int ->
                pref.edit().putBoolean("permitSendData", true).apply()
                FacebookSdk.setAutoInitEnabled(true)
                FacebookSdk.fullyInitialize()
                setAutoLogAppEventsEnabled(true);
                setAdvertiserIDCollectionEnabled(true);
                dialogInterface.dismiss()
            }
            consentDialog!!.setNegativeButton(
                "Disagree"
            ) { dialogInterface: DialogInterface, _: Int ->
                pref.edit().putBoolean("permitSendData", false).apply()
                FacebookSdk.setAutoInitEnabled(false)
                FacebookSdk.fullyInitialize()
                setAutoLogAppEventsEnabled(false);
                setAdvertiserIDCollectionEnabled(false);
                dialogInterface.dismiss()
            }

            consentDialog!!.setOnDismissListener {
                if (pref.getBoolean("permitSendData",true)) {
                    openGame()
                } else openGame()
            }
            consentDialog!!.show()

        }
        decline.setOnClickListener {
            finishAffinity()
        }

    }

    private fun openGame() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}