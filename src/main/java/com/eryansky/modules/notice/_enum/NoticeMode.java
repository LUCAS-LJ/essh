package com.eryansky.modules.notice._enum;

/**
 * 邮件状态
 */
public enum NoticeMode {
    /**
     * 未发布(0)
     */
    UnPublish(0, "未发布"),
    /**
     * 生效(1)
     */
    Effective(1, "已发布"),
    /**
     * 待生效(1)
     */
    Invalidated(2, "已失效");
    /**
     * 值 Integer型
     */
    private final Integer value;
    /**
     * 描述 String型
     */
    private final String description;

    NoticeMode(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 获取值
     *
     * @return value
     */
    public Integer getValue() {
        return value;
    }

    /**
     * 获取描述信息
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    public static NoticeMode getNoticeMode(Integer value) {
        if (null == value)
            return null;
        for (NoticeMode _enum : NoticeMode.values()) {
            if (value.equals(_enum.getValue()))
                return _enum;
        }
        return null;
    }

    public static NoticeMode getNoticeMode(String description) {
        if (null == description)
            return null;
        for (NoticeMode _enum : NoticeMode.values()) {
            if (description.equals(_enum.getDescription()))
                return _enum;
        }
        return null;
    }
}