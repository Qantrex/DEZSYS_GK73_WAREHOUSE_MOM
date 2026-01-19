package com.example.demo.mapping;

public enum PayloadFormat {
    JSON("application/json"),
    XML("application/xml");

    private final String mediaType;

    PayloadFormat(String mediaType) {
        this.mediaType = mediaType;
    }

    public String mediaType() {
        return mediaType;
    }

    public static PayloadFormat fromContentType(String contentType) {
        if (contentType == null) return JSON;
        String ct = contentType.toLowerCase();
        if (ct.contains("xml")) return XML;
        return JSON;
    }
}
