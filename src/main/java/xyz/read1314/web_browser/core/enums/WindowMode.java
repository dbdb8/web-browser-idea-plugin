package xyz.read1314.web_browser.core.enums;

public enum WindowMode {
    SMALL("S", 220, 250),
    MEDIUM("M", 400, 300);

    String name;

    int width;

    int height;

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    WindowMode(String name, int width, int height) {
        this.name = name;
        this.width = width;
        this.height = height;
    }

    public static WindowMode getMode(String name) {
        for (WindowMode mode : values()) {
            if (mode.getName().equals(name)) {
                return mode;
            }
        }
        return WindowMode.SMALL;
    }
}
