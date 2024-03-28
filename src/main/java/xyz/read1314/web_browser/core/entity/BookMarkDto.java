package xyz.read1314.web_browser.core.entity;


public class BookMarkDto {

    private String name;

    private String url;

    // Sort from small to large
    private Integer sort = 100;

    public BookMarkDto(String name, String url, Integer sort) {
        this.name = name;
        this.url = url;
        this.sort = sort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "BookMarkDto{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", sort=" + sort +
                '}';
    }
}
