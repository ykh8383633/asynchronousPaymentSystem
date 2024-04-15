create table `orders` (
    `id`            bigint NOT NULL AUTO_INCREMENT,
    `user_id`       bigint,
    `product_id`    bigint,
    `quantity`      integer,
    `price`         bigint,
    `created_dt` timestamp   not null default current_timestamp,
    `updated_dt` timestamp   not null default current_timestamp on update current_timestamp,
    PRIMARY KEY (`id`)
)
;

create table `payment` (
    `id`            bigint NOT NULL AUTO_INCREMENT,
    `order_id`      bigint,
    `user_id`       bigint,
    `status`        varchar(50),
    `created_dt` timestamp   not null default current_timestamp,
    `updated_dt` timestamp   not null default current_timestamp on update current_timestamp,
    PRIMARY KEY (`id`)
)
;