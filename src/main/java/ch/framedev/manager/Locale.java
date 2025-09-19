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

    // English Language
    ENGLISH("en-EN"),
    // German Language
    GERMAN("de-DE");

    /**
     * The language code for the locale.
     */
    private final String code;

    /**
     * Constructor for the Locale enum.
     * @param code the language code for the program
     */
    Locale(String code) {
        this.code = code;
    }

    /**
     * Returns the language code of the locale.
     *
     * @return the language code
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns the Locale enum based on the provided code.
     * If no matching locale is found, it defaults to ENGLISH.
     *
     * @param code the language code to match
     * @return the corresponding Locale enum or ENGLISH if not found
     */
    public static Locale getByCode(String code) {
        for(Locale locale : Locale.values())
            if(locale.getCode().equalsIgnoreCase(code)) {
                return locale;
            }
        return ENGLISH;
    }
}
