package xyz.read1314.web_browser.menu;

import cn.hutool.core.util.ObjectUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import xyz.read1314.web_browser.core.enums.BackGroundColor;
import xyz.read1314.web_browser.core.util.StorageUtil;
import xyz.read1314.web_browser.message.ChangeActionNotifier;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;


public class WebBrowserConfig extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        produceMessage(project);
    }


    private static boolean showInputDialog(Project project) {
        Box panel = Box.createVerticalBox();

        String homePageStr = PropertiesComponent.getInstance().getValue("web_browser_homePage");
        System.out.println("old homePage: " + homePageStr);
        String bgStr = PropertiesComponent.getInstance().getValue("web_browser_bg");
        System.out.println("old bg: " + bgStr);

        //Home page settings
        JTextField homePage = wrapperOneLineInput(panel, "Home Page: ", StorageUtil.getDefaultHomePage(project));
        //Background color settings
        ComboBox backGroundColorBox = wrapperSelectInput(panel, "BackGround Color: ", StorageUtil.getDefaultBgColor(project));
        //hide top settings
        ButtonGroup hideTopAreaButtonGroup = wrapperRadioButtonInput(panel, "Hide top area: ", StorageUtil.getDefaultHideTopArea(project));
        //hide bottom settings
        ButtonGroup hideBottomAreaButtonGroup = wrapperRadioButtonInput(panel, "Hide bottom area: ", StorageUtil.getDefaultHideBottomArea(project));

        int result = JOptionPane.showConfirmDialog(null, panel, "Web Browser Config", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            System.out.println("input homePage: " + homePage.getText());
            StorageUtil.setDefaultHomePage(project, homePage.getText());

            String selectBgColor = BackGroundColor.getColorByName(String.valueOf(backGroundColorBox.getSelectedItem())).getValue();
            StorageUtil.setDefaultBgColor(project, selectBgColor);

            String hideTopAreaStr =  getSelectedButtonText(hideTopAreaButtonGroup);
            System.out.println("input hideTopAreaStr: " + hideTopAreaStr);
            StorageUtil.setDefaultHideTopArea(project, hideTopAreaStr);

            String hideBottomAreaStr =  getSelectedButtonText(hideBottomAreaButtonGroup);
            System.out.println("input hideBottomAreaStr: " + hideBottomAreaStr);
            StorageUtil.setDefaultHideBottomArea(project, hideBottomAreaStr);

            return true;
        }

        return false;
    }

    /**
     * wrapperOneLineInput
     *
     * @param panel        Box
     * @param tips         String
     * @param defaultValue String
     * @return JTextField
     */
    private static JTextField wrapperOneLineInput(Box panel, String tips, String defaultValue) {
        Box box = Box.createHorizontalBox();
        JTextField input = new JTextField();
        if (ObjectUtil.isNotEmpty(defaultValue)) {
            input.setText(defaultValue);
        }
        box.add(new JLabel(tips), BorderLayout.NORTH);
        box.add(input, BorderLayout.SOUTH);
        panel.add(box);
        return input;
    }


    /**
     * wrapperSelectInput
     *
     * @param panel        panel
     * @param tips         tips
     * @param defaultValue defaultValue
     * @return ComboBox
     */
    private static ComboBox wrapperSelectInput(Box panel, String tips, String defaultValue) {
        Box box = Box.createHorizontalBox();
        ComboBox backGroundColorBox = new ComboBox();
        backGroundColorBox.setPreferredSize(new Dimension(100, 30));
        for (BackGroundColor color : BackGroundColor.values()) {
            backGroundColorBox.addItem(color.getName());
        }
        backGroundColorBox.setSelectedItem(BackGroundColor.getNameByValue(defaultValue).getName());
        box.add(new JLabel(tips), BorderLayout.NORTH);
        box.add(backGroundColorBox, BorderLayout.SOUTH);
        panel.add(box);
        return backGroundColorBox;
    }

    /**
     * wrapperRadioButtonInput
     *
     * @param panel        Box
     * @param tips         String
     * @param defaultValue String
     * @return JTextField
     */
    private static ButtonGroup wrapperRadioButtonInput(Box panel, String tips, String defaultValue) {
        Box box = Box.createHorizontalBox();
        ButtonGroup buttonGroup = new ButtonGroup();

        JRadioButton radioButton1 = new JRadioButton("Yes");
        JRadioButton radioButton2 = new JRadioButton("No");

        if (ObjectUtil.equals(defaultValue, "Yes")) {
            radioButton1.setSelected(true);
        } else {
            radioButton2.setSelected(true);
        }

        buttonGroup.add(radioButton1);
        buttonGroup.add(radioButton2);


        box.add(new JLabel(tips), BorderLayout.NORTH);

        box.add(radioButton1, BorderLayout.SOUTH);
        box.add(Box.createHorizontalStrut(5));
        box.add(radioButton2, BorderLayout.SOUTH);

        panel.add(Box.createVerticalStrut(5));
        panel.add(box);
        return buttonGroup;
    }

    private static String getSelectedButtonText( ButtonGroup buttonGroup){
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                System.out.println("Selected radio button: " + button.getText());
                return button.getText();
            }
        }
        return "";
    }


    /**
     * Send event message
     *
     * @param project Project
     */
    public static void produceMessage(Project project) {
        ChangeActionNotifier publisher = project.getMessageBus()
                .syncPublisher(ChangeActionNotifier.CHANGE_ACTION_TOPIC);
        publisher.beforeAction(project);
        boolean result = false;
        try {
            result = showInputDialog(project);
        } finally {
            if (result) {
                publisher.afterAction(project);
            }
        }
    }

}
