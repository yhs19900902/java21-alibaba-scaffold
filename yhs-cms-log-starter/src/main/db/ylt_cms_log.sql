-- yhd_service_quotation.enquiry_log definition

CREATE TABLE `ylt-service-system`.`ylt_cms_log`
(
    `id`                String COMMENT '主键',
    `log_type`          Nullable(Enum8('CREATED' = 0,
        'UPDATED' = 1,
        'DELETED' = 2)) COMMENT '日志类型',
    `operation_module`  Nullable(String) COMMENT '操作模块',
    `operation_table`   Nullable(String) COMMENT '操作的表',
    `operation_data`    Nullable(TEXT) COMMENT '操作的数据',
    `original_data`     Nullable(TEXT) COMMENT '原数据',
    `result`            Nullable(Enum8('true' = 0,
        'false' = 1)) COMMENT '结果',
    `result_data`       Nullable(TEXT) COMMENT '操作的结果',
    `ip`                Nullable(String) COMMENT '操作的ip',
    `uri`               Nullable(String) COMMENT '操作的uri',
    `ua`                Nullable(TEXT) COMMENT '浏览器信息',
    `created_day`       Date DEFAULT now() COMMENT '分区时间',
    `created_by`        Nullable(String) COMMENT '创建人',
    `created_date_time` DateTime('Asia/Shanghai') DEFAULT now() COMMENT '创建时间',
    `updated_by`        Nullable(String) COMMENT '修改人',
    `updated_date_time` Nullable(DateTime('Asia/Shanghai')) COMMENT '修改时间',
    INDEX               idx_log_type log_type TYPE minmax GRANULARITY 3,
    INDEX               idx_ip ip TYPE minmax GRANULARITY 3,
    INDEX               idx_uri uri TYPE minmax GRANULARITY 3,
    INDEX               idx_operation_module operation_module TYPE minmax GRANULARITY 3
) ENGINE = MergeTree
PARTITION BY created_day
PRIMARY KEY id
ORDER BY (id,
 created_date_time)
SETTINGS index_granularity = 8192
COMMENT '管理后台操作日志';