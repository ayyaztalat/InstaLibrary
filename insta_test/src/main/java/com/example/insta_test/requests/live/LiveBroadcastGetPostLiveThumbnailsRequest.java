package com.example.insta_test.requests.live;

import com.example.insta_test.requests.IGGetRequest;
import com.example.insta_test.responses.live.LiveBroadcastThumbnailsResponse;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LiveBroadcastGetPostLiveThumbnailsRequest extends IGGetRequest<LiveBroadcastThumbnailsResponse> {
    @NonNull
    private String _broadcast_id;

    @Override
    public String path() {
        return "live/" + _broadcast_id + "/get_post_live_thumbnails/";
    }

    @Override
    public Class<LiveBroadcastThumbnailsResponse> getResponseType() {
        return LiveBroadcastThumbnailsResponse.class;
    }

}
