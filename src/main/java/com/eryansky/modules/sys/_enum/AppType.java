package com.eryansky.modules.sys._enum;

/**
 * 应用类型
 */
public enum AppType {

    /** Server(0) */
    Server(0, "服务器"),
    /** Android(1) */
    Android(1, "Android"),
    /** IOS(2) */
    IOS(2, "IOS"),
    /** Other(3) */
    Other(3, "其它");

    /**
     * 值 Integer型
     */
    private final Integer value;
    /**
     * 描述 String型
     */
    private final String description;

    AppType(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 获取值
     * @return value
     */
    public Integer getValue() {
        return value;
    }

    /**
     * 获取描述信息
     * @return description
     */
    public String getDescription() {
        return description;
    }

    public static AppType getAppType(Integer value) {
        if (null == value)
            return null;
        for (AppType _enum : AppType.values()) {
            if (value.equals(_enum.getValue()))
                return _enum;
        }
        return null;
    }

    public static AppType getAppType(String description) {
        if (null == description)
            return null;
        for (AppType _enum : AppType.values()) {
            if (description.equals(_enum.getDescription()))
                return _enum;
        }
        return null;
    }
}
