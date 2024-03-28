package xyz.read1314.web_browser.core.enums;


public enum BackGroundColor {

    BLACK("BLACK","#000000"),
    DARK("DARK","#343638"),
    WHITE("WHITE","#FFFFFF"),
    RED("RED","#FF0000"),
    GREEN("GREEN","#00FF00"),
    BLUE("BLUE","#0000FF"),
    YELLOW("YELLOW","#FFFF00"),
    PURPLE("PURPLE","#FF00FF"),
    ORANGE("ORANGE","#FFA500"),
    PINK("PINK","#FFC0CB"),
    LIGHT_GREY("LIGHT_GREY","#D3D3D3"),
    DARK_GRAY("DARK_GRAY","#696969"),
    ;

    private final String name;
    private final String value;

    BackGroundColor(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static BackGroundColor getColorByName(String name) {
        for (BackGroundColor colors : values()) {
            if (colors.getName().equals(name)) {
                return colors;
            }
        }
        return WHITE;
    }

    public static BackGroundColor getNameByValue(String value) {
        for (BackGroundColor colors : values()) {
            if (colors.getValue().equals(value)) {
                return colors;
            }
        }
        return WHITE;
    }

}
