package org.example.config;

import org.apache.kafka.common.protocol.types.Field;

public class Constants {

    public static final String this_service_username = "txn-service";

    public static final String this_service_password = "txn@123";

    public static final String TRANSACTION_CREATED_TOPIC = "transaction_created";

    public static final String TRANSACTION_COMPLETED_TOPIC = "transaction_completed";

    public static final String AUTHORITIES_DELIMITER = "::";

    public static final String WALLET_UPDATED_TOPIC = "wallet_updated";

    public static final String USER_CACHE_KEY_PREFIX= "user::";

    public static final Integer USER_CACHE_KEY_EXPIRY = 600;
}
