package com.example.insta_test.responses;

public interface IGPaginatedResponse {
    String getNext_max_id();

    boolean isMore_available();
}
