package com.app.dz.quranapp.Api;


import com.app.dz.quranapp.Util.UserLocation;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    @GET("playlists")
    Observable<Object> getPlaylists(
            @Query("part") String part,
            @Query("key") String key,
            @Query("channelId") String channelId);

    @GET("collections/{collectionName}/books")
    Observable<Object> getBooks(
            @Path("collectionName") String collectionName,
             @Query("limit") Integer limit
    );

    @GET("collections/{collectionName}/books/{bookNumber}/chapters")
    Observable<Object> getChaptersOfBook(
            @Path("collectionName") String collectionName,
            @Path("bookNumber") String bookNumber
    );


    @GET("collections/{collectionName}/books/{bookNumber}/hadiths")
    Observable<Object> getHadithsOfBook(
            @Path("collectionName") String collectionName,
            @Path("bookNumber") String bookNumber,
            @Query("limit") Integer limit
    );

    @GET("/hadiths")
    Observable<Object> getHadithsOfCollection(
            @Query("collection") String collection,
            @Query("limit") Integer limit
    );

    @GET("/calendar")
    Observable<Object> getTime(@Query("month") int month,
                               @Query("year") int year,
                               @Query("method") int methode,
                               @Query("latitude") double latitude,
                               @Query("longitude") double longitude);

    /*
    @GET("playlistItems")
    Observable<PlaylistVideosResponse> getPlaylistItems(
            @Query("key") String key,
            @Query("playlistId") String playlistId,
            @Query("part") String part,
            @Query("fields") String fields,
            @Query("maxResults") Integer maxResults);*/

    /*https://www.googleapis.com/youtube/v3/search?key={your_key_here}&channelId={channel_id_here}&part=snippet,id&order=date&maxResults=20

     * */
}
