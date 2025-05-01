package studio.clashbuddy.clashaccess.utils;

public enum AccessType {
    PRIVATE("pr"),PUBLIC("pb");
    private final String accessType;
    AccessType(String accessType) {
        this.accessType = accessType;
    }
    public String getAccessType() {
        return accessType;
    }
    public static AccessType fromAccessType(String accessType) {
        for (AccessType type : AccessType.values()) {
            if (type.accessType.equals(accessType)) {
                return type;
            }
        }
        return AccessType.PUBLIC;
    }
}
