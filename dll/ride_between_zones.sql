CREATE TABLE ride_between_zones (
                                    `id` bigint(20) NOT NULL,
                                    `from_zone_id` bigint(20) NOT NULL,
                                    `to_zone_id` bigint(20) NOT NULL,
                                    `user_id` bigint(20) NOT NULL,
                                    `kmph` double NOT NULL,
                                    `round_id` int(11) NOT NULL,
                                    PRIMARY KEY (`id`),
                                    CONSTRAINT `FK_RIDE_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
                                    CONSTRAINT `FK_RIDE_FROM_ZONE` FOREIGN KEY (`from_zone_id`) REFERENCES `zone` (`id`),
                                    CONSTRAINT `FK_RIDE_TO_ZONE` FOREIGN KEY (`to_zone_id`) REFERENCES `zone` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_swedish_ci;