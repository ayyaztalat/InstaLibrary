package com.example.insta_test.requests.direct;

import com.example.insta_test.IGClient;
import com.example.insta_test.models.IGPayload;
import com.example.insta_test.requests.IGPostRequest;
import com.example.insta_test.responses.IGResponse;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DirectThreadsMarkItemSeenRequest extends IGPostRequest<IGResponse> {
    @NonNull
    private String _thread_id;
    @NonNull
    private String _item_id;

    @Override
    protected IGPayload getPayload(IGClient client) {
        return new DirectThreadsMarkItemSeenPayload();
    }

    @Override
    public String path() {
        return String.format("direct_v2/threads/%s/items/%s/seen/", _thread_id, _item_id);
    }

    @Override
    public Class<IGResponse> getResponseType() {
        return IGResponse.class;
    }

    @Data
    public class DirectThreadsMarkItemSeenPayload extends IGPayload {
        private String thread_id = _thread_id;
        private String item_id = _item_id;
        private final String action = "mark_seen";
        private boolean use_unified_inbox = true;
    }

}
