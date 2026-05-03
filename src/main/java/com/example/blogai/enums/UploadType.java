package com.example.blogai.enums;


public enum UploadType {
    AVATAR("avatars");

    private final String folder;

    UploadType(String folder) {
        this.folder = folder;
    }

    public String getFolder() {
        return folder;
    }
}
