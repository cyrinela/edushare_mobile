package iset.dsi.myapplication;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface NotificationApi {

    @GET("/notifications/user/{userId}")
    Call<List<Notification>> getNotificationsByUser(@Path("userId") Long userId);
}