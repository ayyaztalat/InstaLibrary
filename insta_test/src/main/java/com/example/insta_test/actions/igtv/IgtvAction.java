package com.example.insta_test.actions.igtv;

import com.example.insta_test.IGClient;
import com.example.insta_test.actions.async.AsyncAction;
import com.example.insta_test.actions.feed.FeedIterable;
import com.example.insta_test.actions.media.MediaAction;
import com.example.insta_test.exceptions.IGResponseException.IGFailedResponse;
import com.example.insta_test.requests.igtv.IgtvBrowseFeedRequest;
import com.example.insta_test.requests.igtv.IgtvChannelRequest;
import com.example.insta_test.requests.igtv.IgtvSearchRequest;
import com.example.insta_test.requests.igtv.IgtvSeriesAddEpisodeRequest;
import com.example.insta_test.requests.igtv.IgtvSeriesCreateRequest;
import com.example.insta_test.responses.igtv.IgtvBrowseFeedResponse;
import com.example.insta_test.responses.igtv.IgtvChannelResponse;
import com.example.insta_test.responses.igtv.IgtvSearchResponse;
import com.example.insta_test.responses.igtv.IgtvSeriesResponse;
import com.example.insta_test.responses.media.MediaResponse.MediaConfigureToIgtvResponse;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class IgtvAction {
    @NonNull
    private IGClient client;

    public CompletableFuture<MediaConfigureToIgtvResponse> upload(byte[] data, byte[] cover,
            String title, String caption, boolean postToFeed) {
        String upload_id = String.valueOf(System.currentTimeMillis());

        return client.actions().upload()
                .chunkedVideoWithCover(data, cover, 10_000_000, upload_id)
                .thenCompose(res -> MediaAction.configureToIgtv(client, upload_id, title, caption,
                        postToFeed))
                .thenApply(CompletableFuture::completedFuture)
                .exceptionally(tr -> {
                    if (IGFailedResponse.of(tr.getCause()).getStatusCode() != 202)
                        throw new CompletionException(tr.getCause());
                    log.info("{} Transcode not finished. Retrying up to three times.", upload_id);
                    return AsyncAction.retry(
                            () -> MediaAction.configureToIgtv(client, upload_id, title, caption,
                                    postToFeed),
                            tr, 3, 10,
                            TimeUnit.SECONDS);
                })
                .thenCompose(Function.identity());
    }

    public CompletableFuture<MediaConfigureToIgtvResponse> upload(File videoFile, File coverFile,
            String title, String caption, boolean postToFeed) {
        try {
            return upload(Files.readAllBytes(videoFile.toPath()),
                    Files.readAllBytes(coverFile.toPath()), title, caption, postToFeed);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public CompletableFuture<MediaConfigureToIgtvResponse> upload(File videoFile, File coverFile,
            String title, String caption) {
        return upload(videoFile, coverFile, title, caption, false);
    }

    public CompletableFuture<IgtvSeriesResponse> createSeries(String title, String description) {
        return new IgtvSeriesCreateRequest(title, description).execute(client);
    }

    public CompletableFuture<IgtvSeriesResponse> addEpisode(String series_id, long pk) {
        return new IgtvSeriesAddEpisodeRequest(series_id, pk).execute(client);
    }

    public CompletableFuture<IgtvChannelResponse> getChannel(long pk) {
        return new IgtvChannelRequest("user_" + pk).execute(client);
    }

    public CompletableFuture<IgtvChannelResponse> getChannel(String userId) {
        return new IgtvChannelRequest(userId).execute(client);
    }

    public CompletableFuture<IgtvSearchResponse> search(String query) {
        return new IgtvSearchRequest(query).execute(client);
    }

    public FeedIterable<IgtvBrowseFeedRequest, IgtvBrowseFeedResponse> feed() {
        return new FeedIterable<>(client, IgtvBrowseFeedRequest::new);
    }
}
