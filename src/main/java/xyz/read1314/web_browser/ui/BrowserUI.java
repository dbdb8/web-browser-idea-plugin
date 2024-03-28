package xyz.read1314.web_browser.ui;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBList;
import xyz.read1314.web_browser.core.entity.BookMarkDto;
import xyz.read1314.web_browser.core.enums.BackGroundColor;
import xyz.read1314.web_browser.core.enums.UserAgent;
import xyz.read1314.web_browser.core.enums.WindowMode;
import xyz.read1314.web_browser.core.listener.BrowserEventListener;
import xyz.read1314.web_browser.core.renderer.BookMarkListCellRenderer;
import xyz.read1314.web_browser.core.service.BrowserService;
import xyz.read1314.web_browser.core.service.JcefBrowserService;
import xyz.read1314.web_browser.core.util.StorageUtil;
import xyz.read1314.web_browser.message.ChangeActionNotifier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;


public class BrowserUI extends JPanel {

    private BrowserService browserService;

    private String lastUrl;

    private WindowMode windowMode;

    private UserAgent userAgent;

    private Component browserUI;

    private JTextField urlField;

    private final Project project;

    public BrowserUI(Project project) {
        this.windowMode = WindowMode.SMALL;
        this.userAgent = UserAgent.IPHONE;
        this.project = project;
        initPanel();
    }

    private void initPanel() {
        removeAll();

        String url = StorageUtil.getDefaultHomePage(project);
        if (lastUrl != null) {
            url = lastUrl;
        }

        if (this.browserService != null) {
            this.browserService.close();
        }
        this.browserService = new JcefBrowserService(url);
        this.browserService.setUserAgent(userAgent);
        this.browserService.setPageBackgroundColor(StorageUtil.getDefaultBgColor(project));

        browserUI = browserService.getUI();
        urlField = new JTextField(url);
        resize();

        Dimension buttonDimension = new Dimension(50, 25);
        Box hbox = Box.createHorizontalBox();

        JButton exitButton = new JButton("✕");
        exitButton.setToolTipText("Close");
        exitButton.setPreferredSize(buttonDimension);
        exitButton.addActionListener(l -> exit());
        hbox.add(exitButton);

        JButton homeButton = new JButton("♨");
        homeButton.setToolTipText("Home Page");
        homeButton.setPreferredSize(buttonDimension);
        homeButton.addActionListener(l -> loadURL(StorageUtil.getDefaultHomePage(project)));
        hbox.add(homeButton);

        JButton bookmarkManageButton = new JButton("\uD83D\uDCC1");

        JButton bookmarkButton = new JButton("☆");
        bookmarkButton.setToolTipText("Add Bookmark");
        bookmarkButton.setPreferredSize(buttonDimension);
        bookmarkButton.addActionListener(l -> addBookmark(bookmarkButton, bookmarkManageButton, null));
        hbox.add(bookmarkButton);

        bookmarkManageButton.setToolTipText("Bookmark Management");
        bookmarkManageButton.setPreferredSize(buttonDimension);
        bookmarkManageButton.addActionListener(l -> bookmarkManage(bookmarkManageButton, bookmarkButton));
        hbox.add(bookmarkManageButton);

        JButton backButton = new JButton("←");
        backButton.setToolTipText("Back");
        backButton.setPreferredSize(buttonDimension);
        backButton.addActionListener(l -> goBack());
        hbox.add(backButton);

        JButton forwardButton = new JButton("→");
        forwardButton.setToolTipText("Forward");
        forwardButton.setPreferredSize(buttonDimension);
        forwardButton.addActionListener(l -> goForward());
        hbox.add(forwardButton);

        JButton refreshButton = new JButton("⟳");
        refreshButton.setToolTipText("Refresh");
        refreshButton.setPreferredSize(buttonDimension);
        refreshButton.addActionListener(l -> browserService.reload());
        hbox.add(refreshButton);
        hbox.add(urlField);


        JPanel urlPanel = new JPanel();
        urlPanel.add(hbox);

        Box h2Box = Box.createHorizontalBox();
        h2Box.add(new JLabel("Window："));
        ComboBox windowModeBox = new ComboBox();
        windowModeBox.setPreferredSize(buttonDimension);
        for (WindowMode mode : WindowMode.values()) {
            windowModeBox.addItem(mode.getName());
        }
        windowModeBox.setSelectedItem(windowMode.getName());
        windowModeBox.addItemListener(l -> {
            windowMode = WindowMode.getMode(l.getItem().toString());
            resize();
            updateUI();
        });
        h2Box.add(windowModeBox);

        h2Box.add(Box.createHorizontalStrut(5));
        h2Box.add(new JLabel("UA："));
        ComboBox uaBox = new ComboBox();
        uaBox.setPreferredSize(new Dimension(100, 30));
        for (UserAgent userAgent : UserAgent.values()) {
            uaBox.addItem(userAgent.getName());
        }
        uaBox.setSelectedItem(userAgent.getName());
        uaBox.addItemListener(l -> {
            userAgent = UserAgent.getUserAgent(l.getItem().toString());
            browserService.setUserAgent(userAgent);
            browserService.reload();
        });
        h2Box.add(uaBox);

        h2Box.add(Box.createHorizontalStrut(5));
        h2Box.add(new JLabel("BG ："));
        ComboBox backGroundColorBox = new ComboBox();
        backGroundColorBox.setPreferredSize(new Dimension(100, 30));
        for (BackGroundColor color : BackGroundColor.values()) {
            backGroundColorBox.addItem(color.getName());
        }
        backGroundColorBox.setSelectedItem(BackGroundColor.getNameByValue(StorageUtil.getDefaultBgColor(project)).getName());
        backGroundColorBox.addItemListener(l -> {
            BackGroundColor backGroundColor = BackGroundColor.getColorByName(l.getItem().toString());
            browserService.setPageBackgroundColor(backGroundColor.getValue());
            browserService.reload();
            StorageUtil.setDefaultBgColor(project, backGroundColor.getValue());
        });
        h2Box.add(backGroundColorBox);


        JPanel bottomPanel = new JPanel();
        bottomPanel.add(h2Box);

        setLayout(new BorderLayout());
        add(urlPanel, BorderLayout.NORTH);
        add(browserUI, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        add(Box.createHorizontalStrut(10), BorderLayout.EAST);

        updateUI();

        urlField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String url = urlField.getText();
                    if (!StrUtil.startWithAny(url, "https://", "http://")) {
                        url = "https://" + url;
                    }
                    browserService.loadURL(url);
                }
            }
        });

        this.browserService.addEventListener(new BrowserEventListener() {
            @Override
            public void onAddressChange(String url) {
                lastUrl = url;
                urlField.setText(url);
                updateBookmarkStatus(bookmarkButton, bookmarkManageButton);
            }

            @Override
            public void onBeforeClose() {
                SwingUtilities.invokeLater(() -> initPanel());
            }
        });

        //menu message listener
        project.getMessageBus().connect().subscribe(
                ChangeActionNotifier.CHANGE_ACTION_TOPIC,
                new ChangeActionNotifier() {
                    @Override
                    public void beforeAction(Project project) {
                        System.out.println("beforeAction --->");
                    }

                    @Override
                    public void afterAction(Project project) {
                        System.out.println("afterAction --->");
                        SwingUtilities.invokeLater(() -> initPanel());
                    }
                });

        //Whether to hide
        if (ObjectUtil.equals(StorageUtil.getDefaultHideTopArea(project), "Yes")) {
            urlPanel.setVisible(false);
        } else {
            urlPanel.setVisible(true);
        }
        if (ObjectUtil.equals(StorageUtil.getDefaultHideBottomArea(project), "Yes")) {
            bottomPanel.setVisible(false);
        } else {
            urlPanel.setVisible(true);
        }

        //Add bookmark button state handling
        updateBookmarkStatus(bookmarkButton, bookmarkManageButton);
    }

    private void resize() {
        int width = this.windowMode.getWidth();
        int height = this.windowMode.getHeight();

        urlField.setPreferredSize(new Dimension(width * 2 / 3, 30));
        urlField.updateUI();

        Dimension browserDimension = new Dimension(width, height);
        browserUI.setMinimumSize(null);
        browserUI.setPreferredSize(null);
        if (windowMode == WindowMode.SMALL) {
            browserUI.setPreferredSize(browserDimension);
        } else {
            browserUI.setMinimumSize(browserDimension);
        }
    }

    public void loadURL(String url) {
        browserService.loadURL(StorageUtil.getDefaultHomePage(project));
    }

    public void goBack() {
        browserService.goBack();
    }

    public void goForward() {
        browserService.goForward();
    }

    private void addBookmark(JButton bookmarkButton, JButton bookmarkManageButton, String name) {
        String url = urlField.getText();
        if (ObjectUtil.isEmpty(url)) {
            System.out.println("addBookmark url is empty");
            return;
        }
        if (ObjectUtil.isEmpty(name)) {
            name = browserService.getWebPageTile();
        }
        if (ObjectUtil.isEmpty(name)) {
            name = url;
        }
        if (ObjectUtil.equals(bookmarkButton.getText(), "☆")) {
            StorageUtil.setBookMark(project, new BookMarkDto(name, url, 100));
        } else {
            StorageUtil.delBookMark(project, new BookMarkDto(name, url, 100));
        }
        updateBookmarkStatus(bookmarkButton, bookmarkManageButton);
    }


    /**
     * Update bookmark status
     */
    private void updateBookmarkStatus(JButton bookmarkButton, JButton bookmarkManageButton) {
        // Get bookmark data source
        List<BookMarkDto> bookMarkDtoList = StorageUtil.getBookMark(project);
        if (ObjectUtil.isEmpty(bookMarkDtoList)) {
            bookmarkManageButton.setVisible(false);
        } else {
            bookmarkManageButton.setVisible(true);
        }

        String url = urlField.getText();
        if (ObjectUtil.isEmpty(url)) {
            System.out.println("updateBookmarkStatus url is empty");
            return;
        }
        BookMarkDto bookMarkDto = StorageUtil.getBookmarkByUrl(project, url, StorageUtil.getBookMark(project));
        if (bookMarkDto != null) {
            bookmarkButton.setText("★");
            bookmarkButton.setToolTipText("Bookmarked,Click To Delete");
        } else {
            bookmarkButton.setText("☆");
            bookmarkButton.setToolTipText("Add Bookmark");
        }
    }

    private void bookmarkManage(JButton bookmarkManageButton, JButton bookmarkButton) {
        SwingUtilities.invokeLater(() -> {
            // Get bookmark data source
            List<BookMarkDto> bookMarkDtoList = StorageUtil.getBookMark(project);

            // Create data model
            DefaultListModel<BookMarkDto> listModel = new DefaultListModel<>();
            listModel.addAll(bookMarkDtoList);

            // Create JBList instance
            JBList<BookMarkDto> jbList = new JBList<>(listModel);
            MouseListener mouseListener = new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e) && jbList.locationToIndex(e.getPoint()) != -1) {
                        // Get the currently selected index
                        int selectedIndex = jbList.locationToIndex(e.getPoint());

                        // Sets the entry with this index selected (not required, but helps with visual feedback)
                        jbList.setSelectedIndex(selectedIndex);

                        BookMarkDto selectedValue = listModel.getElementAt(selectedIndex);
                        System.out.println("Selected item: " + selectedValue);

                        JMenuItem menuItem2 = new JMenuItem("Jump URL");
                        JMenuItem menuItem3 = new JMenuItem("Remark");
                        JMenuItem menuItem1 = new JMenuItem("Delete");
                        menuItem1.addActionListener(e13 -> {
                            int result = JOptionPane.showConfirmDialog(jbList, "Do you want to delete this bookmark?", "tips", JOptionPane.YES_NO_OPTION);
                            if (result == JOptionPane.OK_OPTION) {
                                StorageUtil.delBookMark(project, selectedValue);
                                listModel.remove(selectedIndex);
                                updateBookmarkStatus(bookmarkButton, bookmarkManageButton);
                            }
                        });
                        menuItem2.addActionListener(e1 -> {
                            browserService.loadURL(selectedValue.getUrl());
                        });
                        menuItem3.addActionListener(e12 -> {
                            String result = JOptionPane.showInputDialog("Please enter bookmark remarks");
                            if (ObjectUtil.isNotEmpty(result)) {
                                selectedValue.setName(result);
                                StorageUtil.setBookMark(project, selectedValue);
                            }
                        });
                        JBPopupMenu popupMenu = new JBPopupMenu();
                        popupMenu.add(menuItem2);
                        popupMenu.addSeparator();
                        popupMenu.add(menuItem3);
                        popupMenu.addSeparator();
                        popupMenu.add(menuItem1);

                        popupMenu.show(jbList, e.getX(), e.getY());
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            };
            jbList.addMouseListener(mouseListener);
            jbList.setCellRenderer(new BookMarkListCellRenderer());
            DialogWrapper dialog = new DialogWrapper(project) {
                {
                    setTitle("Bookmark Management(right mouse button operation)");
                    init();
                }
                @Override
                protected JComponent createCenterPanel() {
                    JPanel panel = new JPanel(new BorderLayout());
                    panel.add(new JScrollPane(jbList), BorderLayout.CENTER);
                    return panel;
                }
            };
            dialog.show();
        });

    }

    private void exit() {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = toolWindowManager.getToolWindow("Web Browser");
        if (toolWindow != null) {
            toolWindow.hide(null);
        }
    }

}
