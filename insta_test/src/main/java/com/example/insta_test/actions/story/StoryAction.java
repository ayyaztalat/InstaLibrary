package com.example.insta_test.actions.story;

import com.example.insta_test.IGClient;
import com.example.insta_test.models.media.UploadParameters;
import com.example.insta_test.models.media.reel.item.ReelMetadataItem;
import com.example.insta_test.requests.feed.FeedReelsTrayRequest;
import com.example.insta_test.requests.feed.FeedUserStoryRequest;
import com.example.insta_test.requests.media.MediaConfigureToStoryRequest;
import com.example.insta_test.responses.feed.FeedReelsTrayResponse;
import com.example.insta_test.responses.feed.FeedUserStoryResponse;
import com.example.insta_test.responses.media.MediaResponse.MediaConfigureToStoryResponse;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StoryAction {
    @NonNull
    private IGClient client;

    public CompletableFuture<MediaConfigureToStoryResponse> uploadPhoto(byte[] data,
            List<ReelMetadataItem> metadata) {
        String upload_id = String.valueOf(System.currentTimeMillis());
        return client.actions().upload()
                .photo(data, upload_id)
                .thenCompose(response -> {
                    return new MediaConfigureToStoryRequest(response.getUpload_id(), metadata)
                            .execute(client);
                });
    }

    public CompletableFuture<MediaConfigureToStoryResponse> uploadVideo(byte[] video, byte[] cover,
            List<ReelMetadataItem> metadata) {
        String upload_id = String.valueOf(System.currentTimeMillis());
        return client.actions().upload()
                .videoWithCover(video, cover, UploadParameters.forAlbumVideo(upload_id))
                .thenCompose(Response -> {
                    return new MediaConfigureToStoryRequest(upload_id, metadata).execute(client);
                });
    }
    
    public CompletableFuture<MediaConfigureToStoryResponse> uploadPhoto(File file, List<ReelMetadataItem> metadata) {
        try {
            return uploadPhoto(Files.readAllBytes(file.toPath()), metadata);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    public CompletableFuture<MediaConfigureToStoryResponse> uploadPhoto(File file) {
        return uploadPhoto(file, Collections.emptyList());
    }
    
    public CompletableFuture<MediaConfigureToStoryResponse> uploadVideo(File videoFile, File coverFile, List<ReelMetadataItem> metadata) {
        try {
            return uploadVideo(Files.readAllBytes(videoFile.toPath()), Files.readAllBytes(coverFile.toPath()), metadata);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    public CompletableFuture<MediaConfigureToStoryResponse> uploadVideo(File videoFile, File coverFile) {
        return uploadVideo(videoFile, coverFile, Collections.emptyList());
    }

    public CompletableFuture<FeedReelsTrayResponse> tray() {
        return new FeedReelsTrayRequest().execute(client);
    }

    public CompletableFuture<FeedUserStoryResponse> userStory(long pk) {
        return new FeedUserStoryRequest(pk).execute(client);
    }

}
