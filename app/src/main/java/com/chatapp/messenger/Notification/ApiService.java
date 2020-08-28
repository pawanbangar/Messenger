package com.chatapp.messenger.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers({
        "Content-type:application/json",
        "Authorization:key=AAAAPSX9YSc:APA91bHqmMjT2Vx6UBo8EMauyln84jKlsvtc4TW4OkkEMdP_oMzLYYTgaZ9nC56YYE0F1Fyb0W-NVSin50T48VYeforpRXq3GbGYhz0m3jc0aO7dsWKMEt40sDQhJ4TEEwcepBeN3ruc"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
