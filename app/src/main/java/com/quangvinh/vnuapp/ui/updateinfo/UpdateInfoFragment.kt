package com.quangvinh.vnuapp.ui.updateinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.quangvinh.vnuapp.R
import com.quangvinh.vnuapp.helper.INIT_SCALE
import com.quangvinh.vnuapp.helper.URL
import com.quangvinh.vnuapp.helper.onSessionExpired
import com.quangvinh.vnuapp.ui.home.HomeViewModel

class UpdateInfoFragment :Fragment(){

    private lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        webView = view.findViewById(R.id.webview_update_info)
        webView.settings.javaScriptEnabled = true
        webView.settings.builtInZoomControls = true
        webView.setInitialScale(INIT_SCALE)
        webView.webViewClient = WebViewClient()

        val viewModel = ViewModelProvider(this)[UpdateInfoViewModel::class.java]

        viewModel.sessionExpire.observe(requireActivity()) {
            if (it) onSessionExpired(requireActivity())
        }

        viewModel.doc.observe(requireActivity()) {
            webView.loadDataWithBaseURL(URL+"StdInfo/", it.html(), "text/html", "UTF-8", null)
        }

        viewModel.getSomethingAwesome()
        super.onViewCreated(view, savedInstanceState)
    }
}