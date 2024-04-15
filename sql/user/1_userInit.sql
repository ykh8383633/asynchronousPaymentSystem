create table `users` (
    `id`        bigint NOT NULL AUTO_INCREMENT,
    `uid`       varchar(100),
    `email`     varchar(100),
    `created_dt` timestamp   not null default current_timestamp,
    `updated_dt` timestamp   not null default current_timestamp on update current_timestamp,
    PRIMARY KEY (`id`)
)
;