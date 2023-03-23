package com.example.insta_test.requests.media;

import com.example.insta_test.IGClient;
import com.example.insta_test.requests.IGGetRequest;
import com.example.insta_test.requests.IGPaginatedRequest;
import com.example.insta_test.responses.media.MediaGetCommentsResponse;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
public class MediaGetCommentsRequest extends IGGetRequest<MediaGetCommentsResponse>
        implements IGPaginatedRequest {
    @NonNull
    private String _id;
    @Setter
    private String max_id;

    @Override
    public String path() {
        return "media/" + _id + "/comments/";
    }

    @Override
    public String getQueryString(IGClient client) {
        return mapQueryString("max_id", max_id);
    }

    @Override
    public Class<MediaGetCommentsResponse> getResponseType() {
        return MediaGetCommentsResponse.class;
    }
}
