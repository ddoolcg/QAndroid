package com.lcg.mylibrary.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.text.ClipboardManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import com.lcg.mylibrary.BaseFragment
import com.lcg.mylibrary.R
import com.lcg.mylibrary.utils.L
import com.lcg.mylibrary.utils.Token
import com.lcg.mylibrary.utils.UIUtils
import com.lcg.mylibrary.utils.getToken
import kotlinx.android.synthetic.main.fragment_webview.view.*
import java.util.*
import kotlin.collections.set
import kotlin.math.sqrt


/**
 * WebView的Fragment容器以及JS实现
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2015-3-12 上午11:59:22
 */
@SuppressLint("JavascriptInterface", "AddJavascriptInterface", "SetJavaScriptEnabled")
class WebViewFragment : BaseFragment() {
    private var root: View? = null
    private var url: String? = null
    private var mListener: OnCloseListener? = null
    private var isPad: Boolean = false
    private val jiMap = hashMapOf<String, Any>()
    private var titleObserver: ((String) -> Unit)? = null
    /**
     * JS调用关闭页面监听
     */
    fun setOnCloseListener(listener: OnCloseListener) {
        mListener = listener
    }

    /**
     * addJavascriptInterface
     */
    fun addJavascriptInterface(interfaceName: String, any: Any) {
        jiMap[interfaceName] = any
        if (root?.wv != null)
            root!!.wv.addJavascriptInterface(any, interfaceName)
    }

    /**removeJavascriptInterface*/
    fun removeJavascriptInterface(interfaceName: String) {
        jiMap.remove(interfaceName)
        if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB && root?.wv != null) {
            root!!.wv.removeJavascriptInterface(interfaceName)
        }
    }

    /**
     * H5的title监听
     */
    fun setTitleObserver(observer: (String) -> Unit) {
        titleObserver = observer
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_webview, container, false)
        this.root = root
        val bundle = arguments
        url = bundle!!.getString("url")
        isPad = bundle.getBoolean("isPad", false)
        root.wv.isHorizontalScrollBarEnabled = true
        root.wv.isVerticalScrollBarEnabled = true
        if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
            root.wv.isVerticalScrollBarEnabled = true
            root.wv.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
        val settings = root.wv.settings
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            // 跨域
            settings.allowUniversalAccessFromFileURLs = true
        }
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.javaScriptEnabled = true
        root.wv.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean { //
                // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                L.i(url)
                loadUrl(url)
                return true
            }
        }
        val client = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                L.i("$newProgress--")
                if (newProgress < 100) {
                    root.pb.visibility = View.VISIBLE
                    root.tv.visibility = View.VISIBLE
                }
                root.pb.progress = sqrt((100 * newProgress).toDouble()).toInt()
                if (newProgress == 100) {
                    root.pb.visibility = View.GONE
                    root.tv.visibility = View.GONE
                }
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                if (titleObserver != null) {
                    val forwardList: WebBackForwardList = root.wv.copyBackForwardList()
                    forwardList.currentItem?.title?.run {
                        titleObserver!!.invoke(this)
                    }
                }
            }
        }
        root.wv.webChromeClient = client
        root.wv.addJavascriptInterface(JsInterface(), "nm")
        for ((t, u) in jiMap) {
            root.wv.addJavascriptInterface(u, t)
        }
        if (isPad) {
            settings.defaultFontSize = 22
        } else {
            settings.defaultFontSize = 13
        }
        if (bundle.getBoolean("zoom", false)) {
            settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
        }
        root.wv.setBackgroundColor(0)
        loadUrl(url)
        return root
    }

    private fun loadUrl(url: String?) {
        root!!.pb.visibility = View.VISIBLE
        root!!.tv.visibility = View.VISIBLE
        root!!.pb.progress = 0
        val header = HashMap<String, String>()
        val token = getToken()
        header[Token.TOKEN] = token
        header["os"] = "android"
        root!!.wv.loadUrl(url, header)
    }

    /**
     * 监听JS调用关闭当前activity
     */
    interface OnCloseListener {
        fun onClose()
    }

    /**
     * js调用JAVA接口，调用方式window.nm.对应方法名。<br></br>
     * 例如：<br></br>
     * `window.nm.close();`<br></br>
     * `window.nm.log("打印日志");`<br></br>
     *
     * @author lei.chuguang Email:475825657@qq.com
     * @version 1.0
     * @since 2015-3-12 上午11:59:22
     */
    inner class JsInterface {
        /**
         * 关闭页面
         */
        @JavascriptInterface
        fun close() {
            if (mListener != null) {
                mListener!!.onClose()
            }
            activity?.finish()
        }

        /**
         * 调用原生日志输出
         *
         * @param s 输出内容
         */
        @JavascriptInterface
        fun log(s: String) {
            L.w("weblog hashCode=" + root!!.wv.hashCode() + "------------" + s)
        }

        /**
         * 调用toast显示
         *
         * @param s 显示内容
         */
        @JavascriptInterface
        fun toast(s: String) {
            UIUtils.showToastSafe(s)
        }

        /**
         * 返回上一次的网页，如果无则关闭该页面
         */
        @JavascriptInterface
        fun back() {
            UIUtils.runInMainThread {
                if (root!!.wv.canGoBack()) {
                    root!!.wv.goBack() // 返回前一个页面
                } else {
                    activity?.finish()
                }
            }
        }


        /**
         * 启用分享
         */
        @JavascriptInterface
        fun share(title: String, text: String) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, title)
            intent.putExtra(Intent.EXTRA_TEXT, text)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(Intent.createChooser(intent, title))
        }

        /**
         * 浏览器打开
         */
        @JavascriptInterface
        fun openWithBrowser(url: String) {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        /*
         * APP打开
         */
        /* @JavascriptInterface
        public void openWithApp(String url) {
            startActivity(new Intent(activity, WebFullLandscapeActivity.class).putExtra("url",
                    url));
        }*/

        /**
         * 复制
         *
         * @param text
         * @return -1表示复制失败、0表示成功、1表示粘贴板已经存在该内容。
         */
        @JavascriptInterface
        fun copy(text: String): Int {
            if (TextUtils.isEmpty(text)) {
                return -1
            }
            UIUtils.showToastSafe("复制成功")
            val clipboard = activity
                    ?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if (clipboard.hasText()) {
                if (text == clipboard.text.toString()) {
                    return 1
                }
            }
            clipboard.text = text
            return 0
        }

        /**
         * reload解决华为手机hash后reload进度卡在的问题
         */
        @JavascriptInterface
        fun reload() {
            UIUtils.runInMainThread { root!!.wv.reload() }
        }
    }

    fun onBackPressed(): Boolean {
        return if (root?.wv != null && root!!.wv.canGoBack()) {
            val url = root!!.wv.url
            if (TextUtils.isEmpty(url) || url.contains("noback=1")) {
                false
            } else {
                root!!.wv.goBack() // 返回前一个页面
                true
            }
        } else {
            false
        }
    }

    companion object {

        @JvmOverloads
        fun newInstance(url: String, isPad: Boolean = false, zoom: Boolean = false): WebViewFragment {
            val fragment = WebViewFragment()
            val bundle = Bundle()
            bundle.putString("url", url)
            bundle.putBoolean("isPad", isPad)
            bundle.putBoolean("zoom", zoom)
            fragment.arguments = bundle
            return fragment
        }
    }
}
