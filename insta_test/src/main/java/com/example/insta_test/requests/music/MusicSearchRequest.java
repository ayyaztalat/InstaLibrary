package com.example.insta_test.requests.music;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.example.insta_test.IGClient;
import com.example.insta_test.models.IGPayload;
import com.example.insta_test.requests.IGPostRequest;
import com.example.insta_test.responses.music.MusicTrackResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
public class MusicSearchRequest extends IGPostRequest<MusicTrackResponse> {
    @NonNull
    private String _query;
    private String _cursor = "0";

    @Override
    protected IGPayload getPayload(IGClient client) {
        return new MusicQueryPayload(client.getSessionId(), _cursor, _query);
    }

    @Override
    public String path() {
        return "music/search/";
    }

    @Override
    public Class<MusicTrackResponse> getResponseType() {
        return MusicTrackResponse.class;
    }

    @Data
    @RequiredArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    protected static class MusicQueryPayload extends IGPayload {
        @NonNull
        private String browse_session_id;
        @NonNull
        private String cursor;
        private String q;
    }

}
