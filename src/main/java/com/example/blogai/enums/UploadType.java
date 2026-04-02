package com.example.blogai.enums;


public enum UploadType {
    AVATAR("avatars");
//    BLOG_COVER("blogs/covers");

    private final String folder;

    UploadType(String folder) {
        this.folder = folder;
    }

    public String getFolder() {
        return folder;
    }
}
