package app.win11.rascalriches

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.util.AttributeSet
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat.startActivity
import org.json.JSONException
import org.json.JSONObject
import java.security.AccessController.getContext


@SuppressLint("SetJavaScriptEnabled")
class GlobalWebView  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0 ) : WebView(context, attrs, defStyle) {

    init {
        initWebViewSettings()
    }

    private fun initWebViewSettings() {
        val webSettings = settings
        webSettings.javaScriptEnabled = true
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false
        webSettings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        webSettings.domStorageEnabled = true
        webSettings.loadsImagesAutomatically = true
        webSettings.setSupportMultipleWindows(true)
        webSettings.javaScriptCanOpenWindowsAutomatically = true

        webViewClient = CustomWebClient()

        addJavascriptInterface(JSInterface(), "jsBridge")
    }

    private inner class CustomWebClient : WebViewClient() {
        override fun doUpdateVisitedHistory(view: WebView, url: String, isReload: Boolean) {
            Handler(view.context.mainLooper).postDelayed({
                view.evaluateJavascript(
                    """
                    (function() {
                        if(document.getElementById('pngPreloaderWrapper')) {
                            document.getElementById('pngPreloaderWrapper').removeChild(document.getElementById('pngLogoWrapper'));
                        }
                    })();
                    """.trimIndent()
                ) { }
            }, 600)

            Handler(view.context.mainLooper).postDelayed({
                view.evaluateJavascript(
                    """
                    (function() {
                        var myHome = document.getElementById('lobbyButtonWrapper');
                        if(document.getElementById('lobbyButtonWrapper')) {
                            document.getElementById('lobbyButtonWrapper').style = 'display:none;';
                        }
                    })();
                    """.trimIndent()
                ) { }
            }, 5000)
        }

        override fun onPageFinished(view: WebView, url: String?) {
            super.onPageFinished(view, url)
            Handler().postDelayed({
                view.evaluateJavascript(
                    "(function() { document.getElementById('suggest-download-h5_top').innerHTML = ''; document.getElementById('headerWrap').style = 'position:fixed; top:0px; width:100%';})();"
                ) { _: String? -> }
            }, 1800)
        }
    }

    private inner class JSInterface {
        @JavascriptInterface
        fun postMessage(name: String, data: String) {
            val eventValue: MutableMap<String, Any> = HashMap()
            if ("openWindow" == name) {
                try {
                    val extLink = JSONObject(data)
                    val newWindow = Intent(Intent.ACTION_VIEW)
                    newWindow.data = Uri.parse(extLink.getString("url"))
                    newWindow.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(newWindow)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                eventValue[name] = data
            }
        }
    }
}
