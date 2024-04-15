create table `products` (
    `id`                bigint NOT NULL AUTO_INCREMENT,
    `name`              varchar(100),
    `price`             bigint,
    `created_dt` timestamp   not null default current_timestamp,
    `updated_dt` timestamp   not null default current_timestamp on update current_timestamp,
    PRIMARY KEY (`id`)
)
;

create table `inventory` (
    `id`                bigint NOT NULL AUTO_INCREMENT,
    `product_id`        bigint,
    `quantity`          INTEGER,
    `created_dt` timestamp   not null default current_timestamp,
    `updated_dt` timestamp   not null default current_timestamp on update current_timestamp,
    PRIMARY KEY (`id`)
)
;