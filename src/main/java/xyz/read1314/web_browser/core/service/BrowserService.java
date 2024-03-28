package xyz.read1314.web_browser.core.service;


import org.jetbrains.annotations.NotNull;
import xyz.read1314.web_browser.core.enums.UserAgent;
import xyz.read1314.web_browser.core.listener.BrowserEventListener;

import javax.swing.*;

public interface BrowserService {

    JComponent getUI();


    void loadURL(String url);


    void goBack();


    void goForward();


    void reload();


    void close();


    void setUserAgent(UserAgent userAgent);


    void addEventListener(BrowserEventListener listener);



    void setPageBackgroundColor(@NotNull String cssColor);


    String getWebPageTile();

}
