package org.example.hugmeexp.domain.mission.enums;

public enum FileUploadType {
    MISSION_UPLOADS("/mission-uploads/"),
    PROFILE_IMAGES("/profile-images/"),
    PRODUCT_IMAGES("/product-images/");

    private final String value;

    FileUploadType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}