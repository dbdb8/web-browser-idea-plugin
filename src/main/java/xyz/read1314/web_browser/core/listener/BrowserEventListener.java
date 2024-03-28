package xyz.read1314.web_browser.core.listener;


public interface BrowserEventListener {


    /**
     * Browser address change
     *
     * @param url
     */
    default void onAddressChange(String url) {
    }

    /**
     * before browser closes
     */
    default void onBeforeClose() {
    }

}
