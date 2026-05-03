package com.example.blogai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ===== SYSTEM =====
    UNCATEGORIZED_EXCEPTION(1000, "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST(1001, "Invalid request", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1002, "Unauthorized", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(1003, "You do not have permission to access this resource", HttpStatus.FORBIDDEN),
    NOT_FOUND(1004, "Resource not found", HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED(1005, "Method not allowed", HttpStatus.METHOD_NOT_ALLOWED),
    TOO_MANY_REQUESTS(1006, "Too many requests, please try again later", HttpStatus.TOO_MANY_REQUESTS),
    NOT_BLANK(1007, "This field isn't blank", HttpStatus.BAD_REQUEST),
    // ===== VALIDATION =====
    VALIDATION_ERROR(1010, "Validation failed", HttpStatus.BAD_REQUEST),
    INVALID_OTP(1011, "invalid otp", HttpStatus.BAD_REQUEST),
    TOTP_ALREADY_ENABLED(1012, "you already enable 2FA before", HttpStatus.BAD_REQUEST),
    TOTP_NOT_SETUP(1013, "TOTP not set up before", HttpStatus.BAD_REQUEST),
    TOTP_NOT_ENABLED(1014, "TOTP not enabled before", HttpStatus.BAD_REQUEST),
    // ===== USER =====
    USER_NOT_EXISTED(2001, "User not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTED(2002, "User already exists", HttpStatus.CONFLICT),
    USER_INACTIVE(2003, "User account is inactive", HttpStatus.FORBIDDEN),
    USER_BANNED(2004, "User account has been banned", HttpStatus.FORBIDDEN),

    // ===== EMAIL =====
    EMAIL_ALREADY_EXISTED(2010, "Email already exists", HttpStatus.CONFLICT),
    EMAIL_INVALID_FORMAT(2011, "Email is not in the correct format", HttpStatus.BAD_REQUEST),
    EMAIL_REQUIRED(2012, "Email is required", HttpStatus.BAD_REQUEST),

    // ===== PASSWORD =====
    PASSWORD_REQUIRED(2020, "Password is required", HttpStatus.BAD_REQUEST),
    PASSWORD_TOO_SHORT(2021, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INCORRECT(2022, "Incorrect password", HttpStatus.UNAUTHORIZED),
    PASSWORD_TOO_WEAK(2023, "Password is too weak", HttpStatus.BAD_REQUEST),
    CURRENT_PASSWORD_INCORRECT(2024, "current password incorrect", HttpStatus.UNAUTHORIZED),
    PASSWORD_CONFIRMATION_MISMATCH(2025, "Password confirmation does not match", HttpStatus.BAD_REQUEST),

    // ===== AUTH =====
    TOKEN_INVALID(3001, "Token is invalid", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(3002, "Token has expired", HttpStatus.UNAUTHORIZED),
    TOKEN_MISSING(3003, "Token is missing", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_INVALID(3004, "Refresh token is invalid", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED(3005, "Refresh token has expired", HttpStatus.UNAUTHORIZED),

    // ===== BLOG / POST =====
    POST_NOT_FOUND(4001, "Post not found", HttpStatus.NOT_FOUND),
    POST_ALREADY_EXISTED(4002, "Post already exists", HttpStatus.CONFLICT),
    POST_ACCESS_DENIED(4003, "You do not have permission to modify this post", HttpStatus.FORBIDDEN),

    // ===== COMMENT =====
    COMMENT_NOT_FOUND(5001, "Comment not found", HttpStatus.NOT_FOUND),
    COMMENT_ACCESS_DENIED(5002, "You do not have permission to modify this comment", HttpStatus.FORBIDDEN),

    //====== BLOG ======
    BLOG_NOT_EXISTED(6001, "blog not existed", HttpStatus.NOT_FOUND),
    BLOG_CONTENT_REQUIRED(6002, "content's blog is required", HttpStatus.BAD_REQUEST),
    BLOG_COVER_IMAGE_REQUIRED(6003, "cover image blog is required", HttpStatus.BAD_REQUEST),
    BLOG_AUTHOR_REQUIRED(6004, "author's blog is required", HttpStatus.BAD_REQUEST),
    BLOG_TAG_REQUIRED(6005, "tag's blog are required", HttpStatus.BAD_REQUEST),
    BLOG_TITLE_REQUIRED(6006, "blog title is required", HttpStatus.BAD_REQUEST),
    //====== TAG ======
    TAG_REQUIRED(7001, "Tag name is required", HttpStatus.BAD_REQUEST),
    TAG_GROUP_NAME_REQUIRED(7002, "Group name is required", HttpStatus.BAD_REQUEST),
    TAG_EXISTED(7003, "this tag is existed", HttpStatus.BAD_REQUEST),
    INVALID_TAG(7004, "tag invalid", HttpStatus.BAD_REQUEST),

    FILE_TOO_LARGE(8001, "File size exceeds the maximum allowed limit", HttpStatus.CONTENT_TOO_LARGE),

    CANNOT_FOLLOW_YOURSELF(9001, "you can't follow yourself", HttpStatus.BAD_REQUEST),
    NOT_MUTUAL_FOLLOW(9002, "user not mutual follow", HttpStatus.BAD_REQUEST),
    ALREADY_FOLLOWED(9003, "you already followed this user", HttpStatus.BAD_REQUEST),
    NOT_FOLLOWED(9004, "you not followed this user", HttpStatus.BAD_REQUEST),
    CHAT_NOT_ALLOWED(9005, "you hasn't been followed this user so can't message with them", HttpStatus.BAD_REQUEST),

    CONVERSATION_NOT_FOUND(10001, "Conversation not found", HttpStatus.NOT_FOUND),
    MESSAGE_SEND_FAILED(10002, "Failed to send message", HttpStatus.INTERNAL_SERVER_ERROR);
    private final int code;
    private final String message;
    private final HttpStatus httpStatus;




}
