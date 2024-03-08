package com.abciloveu.constant;

public class Constant {

    public static final String KAFKA_GROUP_ID = "${spring.kafka.consumer.group-id}";
    public static final String KAFKA_TOPIC_NAME = "#{'${spring.kafka.template.default-topic}'.split(',')}";

    public static final String NAMESPACE = "namespace";
    public static final String TRAN_CODE = "tranCode";
    public static final String TRAN_DESC = "tranDesc";
    
    public static final String MDC_TRAN_KEY = "tranKey";
    public static final String MDC_KEYREF = "keyRef";
    public static final String MDC_TOPIC = "topic";
    public static final String MDC_PARTITION = "partition";
    public static final String MDC_OFFSET = "offset";
    public static final String MDC_USED_TIME = "used_time";
    
    public static final String POST_FIX_WAKEUP = "-wakeup";
    
}
