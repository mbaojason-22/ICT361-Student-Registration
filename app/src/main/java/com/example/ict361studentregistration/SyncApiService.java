
package com.example.ict361studentregistration;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface SyncApiService {

    @POST("sync/")
    Call<JsonObject> createSyncChange(
            @Body JsonObject request
    );

    @GET("sync/account/{account_id}")
    Call<JsonObject> getSyncChanges(
            @Path("account_id") String accountId
    );

    @GET("sync/account/{account_id}/pending")
    Call<JsonObject> getPendingSyncChanges(
            @Path("account_id") String accountId
    );

    @PUT("sync/{sync_change_id}/applied")
    Call<JsonObject> markSyncChangeApplied(
            @Path("sync_change_id") String syncChangeId,
            @Body JsonObject request
    );

    @PUT("sync/{sync_change_id}/conflict")
    Call<JsonObject> markSyncChangeConflict(
            @Path("sync_change_id") String syncChangeId,
            @Body JsonObject request
    );

    @PUT("sync/{sync_change_id}/rejected")
    Call<JsonObject> markSyncChangeRejected(
            @Path("sync_change_id") String syncChangeId,
            @Body JsonObject request
    );

    @POST("sync/receipts")
    Call<JsonObject> createOperationReceipt(
            @Body JsonObject request
    );

    @GET("sync/receipts/{operation_id}")
    Call<JsonObject> getOperationReceipt(
            @Path("operation_id") String operationId
    );
}
