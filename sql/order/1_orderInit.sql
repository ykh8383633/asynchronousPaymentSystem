create table `orders` (
    `id`            bigint NOT NULL AUTO_INCREMENT,
    `user_id`       bigint,
    `product_id`    bigint,
    `quantity`      integer,
    `price`         bigint,
    `status`        varchar(50),
    `created_dt` timestamp   not null default current_timestamp,
    `updated_dt` timestamp   not null default current_timestamp on update current_timestamp,
    PRIMARY KEY (`id`)
)
;

create table `payment` (
    `id`            bigint NOT NULL AUTO_INCREMENT,
    `pg_order_id`   varchar(50),
    `pg_payment_id` varchar(50),
    `reason`        varchar(1000),
    `order_id`      bigint,
    `user_id`       bigint,
    `status`        varchar(50),
    `created_dt` timestamp   not null default current_timestamp,
    `updated_dt` timestamp   not null default current_timestamp on update current_timestamp,
    PRIMARY KEY (`id`)
)
;