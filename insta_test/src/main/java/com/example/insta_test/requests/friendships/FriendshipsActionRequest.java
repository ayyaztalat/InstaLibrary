package com.example.insta_test.requests.friendships;

import com.example.insta_test.IGClient;
import com.example.insta_test.models.IGPayload;
import com.example.insta_test.requests.IGPostRequest;
import com.example.insta_test.responses.friendships.FriendshipStatusResponse;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FriendshipsActionRequest extends IGPostRequest<FriendshipStatusResponse> {
    @NonNull
    private Long _pk;
    @NonNull
    private FriendshipsAction action;

    @Override
    protected IGPayload getPayload(IGClient client) {
        return new IGPayload() {
            @Getter
            private long user_id = _pk;
        };
    }

    @Override
    public String path() {
        return String.format("friendships/%s/%s/", action.name().toLowerCase(), _pk);
    }

    @Override
    public Class<FriendshipStatusResponse> getResponseType() {
        return FriendshipStatusResponse.class;
    }

    public static enum FriendshipsAction {
        BLOCK, UNBLOCK, CREATE, DESTROY, APPROVE, IGNORE, REMOVE_FOLLOWER;
    }
}
