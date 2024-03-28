package xyz.read1314.web_browser.core.util;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import xyz.read1314.web_browser.core.entity.BookMarkDto;
import xyz.read1314.web_browser.core.enums.BackGroundColor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StorageUtil {

    private final static String default_HOME_PAGE = "https://www.google.com/";

    private final static String DEFAULT_BG_COLOR = BackGroundColor.WHITE.getValue();

    private final static String DEFAULT_HIDE_TOP_AREA = "No";

    private final static String DEFAULT_HIDE_BOTTOM_AREA = "No";


    //Set Default homepage
    public static String getDefaultHomePage(Project project) {
        String homePageStr = PropertiesComponent.getInstance(project).getValue("web_browser_homePage");
        System.out.println(" homePage: " + homePageStr);
        return ObjectUtil.isEmpty(homePageStr) ? default_HOME_PAGE : homePageStr;
    }


    //Set default background color
    public static String getDefaultBgColor(Project project) {
        String bgStr = PropertiesComponent.getInstance(project).getValue("web_browser_bg");
        System.out.println("bg: " + bgStr);
        return ObjectUtil.isEmpty(bgStr) ? DEFAULT_BG_COLOR : bgStr;
    }

    //Get the default hidden top
    public static String getDefaultHideTopArea(Project project) {
        String value = PropertiesComponent.getInstance(project).getValue("web_browser_hide_top_area");
        System.out.println("hide_top_area: " + value);
        return ObjectUtil.isEmpty(value) ? DEFAULT_HIDE_TOP_AREA : value;
    }

    //get Hide bottom by default
    public static String getDefaultHideBottomArea(Project project) {
        String value = PropertiesComponent.getInstance(project).getValue("web_browser_hide_bottom_area");
        System.out.println("hide_bottom_area: " + value);
        return ObjectUtil.isEmpty(value) ? DEFAULT_HIDE_BOTTOM_AREA : value;
    }

    //Set default homepage address
    public static void setDefaultHomePage(Project project, String value) {
        System.out.println("set homePage: " + value);
        PropertiesComponent.getInstance(project).setValue("web_browser_homePage", value);
    }


    //Set default background color
    public static void setDefaultBgColor(Project project, String value) {
        System.out.println("set bg: " + value);
        PropertiesComponent.getInstance(project).setValue("web_browser_bg", value);
    }

    //Set default to hide top
    public static void setDefaultHideTopArea(Project project, String value) {
        System.out.println("set hide_top_area: " + value);
        PropertiesComponent.getInstance(project).setValue("web_browser_hide_top_area", value);
    }

    //Set the default to hide the bottom
    public static void setDefaultHideBottomArea(Project project, String value) {
        System.out.println("set hide_bottom_area: " + value);
        PropertiesComponent.getInstance(project).setValue("web_browser_hide_bottom_area", value);
    }

    //Get bookmarks List
    public static List<BookMarkDto> getBookMark(Project project) {
        String value = PropertiesComponent.getInstance(project).getValue("web_browser_bookmark");
        System.out.println("bookMark: " + value);
        return ObjectUtil.isEmpty(value) ? new ArrayList<>() : JSONUtil.toList(value, BookMarkDto.class);
    }


    //Set bookmark
    public static void setBookMark(Project project, BookMarkDto bookMarkDto) {
        System.out.println("set bookMark: " + bookMarkDto);
        List<BookMarkDto> bookMarkDtoList = getBookMark(project);
        BookMarkDto oldBookMarkDto = getBookmarkByUrl(project, bookMarkDto.getUrl(), bookMarkDtoList);
        if (oldBookMarkDto == null) {
            bookMarkDtoList.add(bookMarkDto);
        } else {
            oldBookMarkDto.setName(bookMarkDto.getName());
            oldBookMarkDto.setSort(bookMarkDto.getSort());
        }
        bookMarkDtoList.sort(Comparator.comparingInt(BookMarkDto::getSort));
        System.out.println("bookMarkList: " + bookMarkDtoList);
        PropertiesComponent.getInstance(project).setValue("web_browser_bookmark", JSONUtil.toJsonStr(bookMarkDtoList));
    }

    //Get bookmarks based on url
    public static BookMarkDto getBookmarkByUrl(Project project, String url, List<BookMarkDto> bookMarkDtoList) {
        if (ObjectUtil.isEmpty(bookMarkDtoList)) {
            bookMarkDtoList = getBookMark(project);
        }
        for (BookMarkDto bookMarkDto : bookMarkDtoList) {
            if (ObjectUtil.equals(bookMarkDto.getUrl(), url)) {
                return bookMarkDto;
            }
        }
        return null;
    }


    /**
     * Delete bookmark
     */
    public static void delBookMark(Project project, BookMarkDto bookMarkDto) {
        System.out.println("del bookMark: " + bookMarkDto);
        List<BookMarkDto> bookMarkDtoList = getBookMark(project);
        BookMarkDto oldBookMarkDto = getBookmarkByUrl(project, bookMarkDto.getUrl(), bookMarkDtoList);
        if (oldBookMarkDto == null) {
            return;
        } else {
            bookMarkDtoList.remove(oldBookMarkDto);
        }
        System.out.println("bookMarkList: " + bookMarkDtoList);
        PropertiesComponent.getInstance(project).setValue("web_browser_bookmark", JSONUtil.toJsonStr(bookMarkDtoList));
    }
}
