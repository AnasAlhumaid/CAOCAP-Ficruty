package sa.gov.ksaa.dal

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewDeliverableFile
import sa.gov.ksaa.dal.ui.fragments.BaseFragment


class FileViewerFragment : BaseFragment(R.layout.fragment_file_viewer) {

    var deliverable: NewDeliverableFile? = null
    lateinit var webView: WebView
    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        webView = createdView.findViewById(R.id.webView)

        deliverable = activityVM.deliverable.value

        activityToolbar.title = deliverable!!.fileCategory

        webView.webViewClient = WebViewClient()
        webView.settings.setSupportZoom(true)
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = true
        webView.settings.javaScriptEnabled = true

        val url = deliverable!!.imageUrl!!.replace("\\", "")
        Log.w(javaClass.simpleName, "onViewCreated: url = $url")
        webView.loadUrl("https://docs.google.com/gview?embedded=true&url=$url")
    }
}