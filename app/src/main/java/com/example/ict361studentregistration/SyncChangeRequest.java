package com.example.ict361studentregistration.network;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

public class SyncChangeRequest {

    @SerializedName("operation_id")
    private String operationId;

    @SerializedName("account_id")
    private String accountId;

    @SerializedName("entity_type")
    private String entityType;

    @SerializedName("entity_id")
    private String entityId;

    @SerializedName("operation_type")
    private String operationType;

    @SerializedName("payload")
    private JsonElement payload;

    @SerializedName("base_version")
    private String baseVersion;

    public SyncChangeRequest(
            String operationId,
            String accountId,
            String entityType,
            String entityId,
            String operationType,
            JsonElement payload,
            String baseVersion
    ) {
        this.operationId = operationId;
        this.accountId = accountId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.operationType = operationType;
        this.payload = payload;
        this.baseVersion = baseVersion;
    }

    public String getOperationId() {
        return operationId;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getOperationType() {
        return operationType;
    }

    public JsonElement getPayload() {
        return payload;
    }

    public String getBaseVersion() {
        return baseVersion;
    }
}