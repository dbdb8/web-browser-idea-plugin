package xyz.read1314.web_browser.core.service;


import cn.hutool.core.util.StrUtil;
import com.intellij.ui.jcef.JBCefBrowser;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.*;
import org.cef.misc.BoolRef;
import org.cef.network.CefRequest;
import xyz.read1314.web_browser.core.enums.UserAgent;
import xyz.read1314.web_browser.core.listener.BrowserEventListener;

import javax.swing.*;

public class JcefBrowserService implements BrowserService {

    private final JBCefBrowser jbCefBrowser;

    private final CefBrowser cefBrowser;

    private final CefClient client;

    private UserAgent userAgent;

    private BrowserEventListener eventListener;

    public JcefBrowserService(String url) {
        this.jbCefBrowser = new JBCefBrowser(url);
        this.cefBrowser = this.jbCefBrowser.getCefBrowser();
        this.client = this.cefBrowser.getClient();
        this.userAgent = UserAgent.IPHONE;
        this.eventListener = new BrowserEventListener() {
        };
        initAddEvent();
    }

    private void initAddEvent() {
        this.client.removeRequestHandler();
        this.client.addRequestHandler(new CefRequestHandlerAdapter() {
            @Override
            public CefResourceRequestHandler getResourceRequestHandler(CefBrowser browser, CefFrame frame, CefRequest request, boolean isNavigation, boolean isDownload, String requestInitiator, BoolRef disableDefaultHandling) {
                return new CefResourceRequestHandlerAdapter() {
                    @Override
                    public boolean onBeforeResourceLoad(CefBrowser browser, CefFrame frame, CefRequest request) {
                        String ua = userAgent.getValue();
                        if (ua != null) {
                            request.setHeaderByName("User-Agent", ua, true);
                        }
                        return false;
                    }
                };
            }
        });

        this.client.addDisplayHandler(new CefDisplayHandlerAdapter() {
            @Override
            public void onAddressChange(CefBrowser browser, CefFrame frame, String url) {
                if (!StrUtil.startWith(url, "http")) {
                    return;
                }
                System.out.println("url="+url);
                eventListener.onAddressChange(url);
            }
        });

        this.client.removeLifeSpanHandler();
        this.client.addLifeSpanHandler(new CefLifeSpanHandlerAdapter() {
            @Override
            public boolean onBeforePopup(CefBrowser browser, CefFrame frame, String target_url, String target_frame_name) {
                if (StrUtil.endWithAnyIgnoreCase(target_url, "jpg", "png", "gif", "svg", "pdf", "bmp", "webp")) {
                    return false;
                }

                loadURL(target_url);
                return true;
            }

            @Override
            public void onBeforeClose(CefBrowser browser) {
                close();
                eventListener.onBeforeClose();
            }
        });
    }

    @Override
    public JComponent getUI() {
        return this.jbCefBrowser.getComponent();
    }

    @Override
    public void loadURL(String url) {
        this.jbCefBrowser.loadURL(url);
    }

    @Override
    public void goBack() {
        if (this.cefBrowser.canGoBack()) {
            this.cefBrowser.goBack();
        }
    }

    @Override
    public void goForward() {
        if (this.cefBrowser.canGoForward()) {
            this.cefBrowser.goForward();
        }
    }

    @Override
    public void reload() {
        this.cefBrowser.reload();
//        this.jbCefBrowser.setPageBackgroundColor("#000000");
    }

    @Override
    public void close() {
        this.client.dispose();
        this.jbCefBrowser.dispose();
    }

    @Override
    public void setUserAgent(UserAgent userAgent) {
        if (userAgent == null) {
            return;
        }

        this.userAgent = userAgent;
    }

    @Override
    public void addEventListener(BrowserEventListener listener) {
        if (listener == null) {
            return;
        }

        this.eventListener = listener;
    }

    @Override
    public void setPageBackgroundColor(String cssColor) {
        this.jbCefBrowser.setPageBackgroundColor(cssColor);
    }


    @Override
    public String getWebPageTile() {
        CefFrame frame = jbCefBrowser.getCefBrowser().getMainFrame();
        return frame.getName();
    }
}
