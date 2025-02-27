package ch.framedev.manager;



/*
 * ch.framedev.manager
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 26.02.2025 22:17
 */

public enum Locale {

    ENGLISH("en-EN"),
    GERMAN("de-DE");

    private final String code;

    Locale(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Locale getByCode(String code) {
        for(Locale locale : Locale.values())
            if(locale.getCode().equalsIgnoreCase(code)) {
                return locale;
            }
        return ENGLISH;
    }
}
