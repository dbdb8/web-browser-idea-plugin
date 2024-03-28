package xyz.read1314.web_browser.core.renderer;

import cn.hutool.core.util.ObjectUtil;
import xyz.read1314.web_browser.core.entity.BookMarkDto;

import javax.swing.*;
import java.awt.*;


/**
 * Bookmark List Cell Renderer
 */
public class BookMarkListCellRenderer extends DefaultListCellRenderer {

    public BookMarkListCellRenderer() {
    }


    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel renderer = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof BookMarkDto) {
            BookMarkDto bookMarkDto = (BookMarkDto) value;
            String text = "";
            if (ObjectUtil.notEqual(bookMarkDto.getName(), bookMarkDto.getUrl())) {
                text = bookMarkDto.getName() + " | " + bookMarkDto.getUrl();
            } else {
                text = bookMarkDto.getName();
            }
            if (text.length() > 64) {
                text = text.substring(0, 64) + "...";
            }
            setText(text);
        }
        return renderer;
    }
}
