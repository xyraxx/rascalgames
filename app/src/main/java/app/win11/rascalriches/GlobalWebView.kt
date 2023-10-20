package app.win11.rascalriches

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

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
    }
}
