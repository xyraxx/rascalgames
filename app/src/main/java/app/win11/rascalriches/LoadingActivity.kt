package app.win11.rascalriches

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.json.JSONObject

class LoadingActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 201235

    private lateinit var sharedPref : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
        window.setFlags(1024,1024)

        sharedPref = getSharedPreferences(GlobalConfig.appCode, MODE_PRIVATE)

        val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        mFirebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener { task: Task<Boolean?> ->
                if (task.isSuccessful) {
                    Log.d("Firebase Remote Config", "Connected")
                    GlobalConfig.apiURL = mFirebaseRemoteConfig.getString("apiURL")
                    GlobalConfig.policyURL = mFirebaseRemoteConfig.getString("policyURL")
                    GlobalConfig.gameURL = GlobalConfig.apiURL+"?appid="+GlobalConfig.appCode
                    GlobalConfig.facebookAppToken = mFirebaseRemoteConfig.getString("facebookAppId")
                    Log.d("apiURL", GlobalConfig.apiURL)
                    Log.d("policyURL", GlobalConfig.policyURL)
                    Log.d("facebookAppId", GlobalConfig.facebookAppToken)

                    if (!permissionChecker()){
                        requestPermission()
                    }else openActivity()
                }
            }
    }

    private fun permissionChecker(): Boolean {
        val location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val media =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            }

        return location == PackageManager.PERMISSION_GRANTED
                && camera == PackageManager.PERMISSION_GRANTED
                && media == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        val permissions =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            } else {
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }

        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val locationGranted = grantResults.getOrNull(0) == PackageManager.PERMISSION_GRANTED
            val cameraGranted = grantResults.getOrNull(1) == PackageManager.PERMISSION_GRANTED
            val mediaGranted = grantResults.getOrNull(2) == PackageManager.PERMISSION_GRANTED

            sharedPref.edit {
                putBoolean("locationGranted", locationGranted)
                putBoolean("cameraGranted", cameraGranted)
                putBoolean("mediaGranted", mediaGranted)
                putBoolean("runOnce", locationGranted && cameraGranted && mediaGranted)
                apply()
            }
        }

        openActivity()
    }

    private fun openActivity() {
        if(!sharedPref.getBoolean("permitSendData", false)){
            val policyIntent = Intent(this, PolicyActivity::class.java)
            policyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(policyIntent)
            finish()
        }else{
            val gameIntent = Intent(this, MainActivity::class.java)
            gameIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(gameIntent)
            finish()
        }

    }

}