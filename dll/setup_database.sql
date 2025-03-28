-- This DLL is taken from an SQL-dump from original DB

--
-- Sequence structure for `hibernate_sequence`
--
CREATE SEQUENCE `hibernate_sequence` start with 1 minvalue 1 maxvalue 9223372036854775806 increment by 1 cache 1000 nocycle ENGINE=InnoDB;
SELECT SETVAL(`hibernate_sequence`, 2362001, 0);

--
-- Sequence structure for `takeover_sequence`
--
CREATE SEQUENCE `takeover_sequence` start with 1 minvalue 1 maxvalue 9223372036854775806 increment by 50 cache 1000 nocycle ENGINE=InnoDB;
SELECT SETVAL(`takeover_sequence`, 36800001, 0);

--
-- Table structure for table `broadcast_message`
--
CREATE TABLE `broadcast_message` (
                                     `id` bigint(20) NOT NULL,
                                     `import_needed_after` datetime(6) DEFAULT NULL,
                                     `message` longtext DEFAULT NULL,
                                     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_swedish_ci;

--
-- Table structure for table `error_takeover`
--
CREATE TABLE `error_takeover` (
                                  `id` bigint(20) NOT NULL,
                                  `created` datetime(6) DEFAULT NULL,
                                  `takeover_time` datetime(6) DEFAULT NULL,
                                  `user_id` bigint(20) DEFAULT NULL,
                                  `zone_id` bigint(20) DEFAULT NULL,
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_swedish_ci;

--
-- Table structure for table `error_zone`
--
CREATE TABLE `error_zone` (
                              `id` bigint(20) NOT NULL,
                              `created` datetime(6) DEFAULT NULL,
                              `latitude` double DEFAULT NULL,
                              `longitude` double DEFAULT NULL,
                              `takes` int(11) DEFAULT NULL,
                              `user_id` bigint(20) DEFAULT NULL,
                              `zone_name` varchar(255) DEFAULT NULL,
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_swedish_ci;

--
-- Table structure for table `feed_info`
--
CREATE TABLE `feed_info` (
                             `id` bigint(20) NOT NULL,
                             `feed_name` varchar(255) DEFAULT NULL,
                             `latest_feed_item_read` datetime(6) DEFAULT NULL,
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_swedish_ci;
INSERT INTO `feed_info` VALUES
                            (1,'takeover','2024-04-05 20:40:06.000000'),
                            (3,'zone','2025-02-18 12:00:00.000000');

--
-- Table structure for table `region_takes`
--
CREATE TABLE `region_takes` (
                                `id` bigint(20) NOT NULL,
                                `region_id` bigint(20) DEFAULT NULL,
                                `region_name` varchar(255) DEFAULT NULL,
                                `user_id` bigint(20) DEFAULT NULL,
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_swedish_ci;

--
-- Table structure for table `user`
--
CREATE TABLE `user` (
                        `id` bigint(20) NOT NULL,
                        `username` varchar(255) DEFAULT NULL,
                        `imported` bit(1) NOT NULL,
                        `last_import` datetime(6) DEFAULT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_swedish_ci;

--
-- Table structure for table `takeover`
--
CREATE TABLE `takeover` (
                            `id` bigint(20) NOT NULL,
                            `lost_time` datetime(6) DEFAULT NULL,
                            `pph` int(11) DEFAULT NULL,
                            `round_id` int(11) DEFAULT NULL,
                            `takeover_time` datetime(6) DEFAULT NULL,
                            `tp` int(11) DEFAULT NULL,
                            `type` varchar(255) DEFAULT NULL,
                            `zone_id` bigint(20) DEFAULT NULL,
                            `zonetype` varchar(255) DEFAULT NULL,
                            `assisting_user_id` bigint(20) DEFAULT NULL,
                            `next_user_id` bigint(20) DEFAULT NULL,
                            `previous_user_id` bigint(20) DEFAULT NULL,
                            `user_id` bigint(20) DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            KEY `FKk0glyg1y0kcts8eac72gir6of` (`assisting_user_id`),
                            KEY `FKtnxy1p934ohar58vxaso0ldbh` (`next_user_id`),
                            KEY `FKl7c1cbow6j065ymlsnqhrmtmw` (`previous_user_id`),
                            KEY `FKqy3jf2hr0uw6t56nannr6tojx` (`user_id`),
                            KEY `index_migrate` (`round_id`,`previous_user_id`,`lost_time`),
                            KEY `index_round_user_zone` (`round_id`,`user_id`,`zone_id`),
                            CONSTRAINT `FKk0glyg1y0kcts8eac72gir6of` FOREIGN KEY (`assisting_user_id`) REFERENCES `user` (`id`),
                            CONSTRAINT `FKl7c1cbow6j065ymlsnqhrmtmw` FOREIGN KEY (`previous_user_id`) REFERENCES `user` (`id`),
                            CONSTRAINT `FKqy3jf2hr0uw6t56nannr6tojx` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
                            CONSTRAINT `FKtnxy1p934ohar58vxaso0ldbh` FOREIGN KEY (`next_user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_swedish_ci;

--
-- Table structure for table `unique_zone`
--
CREATE TABLE `unique_zone` (
                               `id` bigint(20) NOT NULL,
                               `takes` int(11) DEFAULT NULL,
                               `area` varchar(255) DEFAULT NULL,
                               `area_id` bigint(20) DEFAULT NULL,
                               `latitude` double DEFAULT NULL,
                               `longitude` double DEFAULT NULL,
                               `name` varchar(255) DEFAULT NULL,
                               `zone_id` bigint(20) DEFAULT NULL,
                               `region_takes_id` bigint(20) DEFAULT NULL,
                               PRIMARY KEY (`id`),
                               KEY `FKj324i5ut1eu5immqi22lejcao` (`region_takes_id`),
                               KEY `index_unique_zone_zone_id` (`zone_id`,`region_takes_id`),
                               CONSTRAINT `FKj324i5ut1eu5immqi22lejcao` FOREIGN KEY (`region_takes_id`) REFERENCES `region_takes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_swedish_ci;

--
-- Table structure for table `zone`
--
CREATE TABLE `zone` (
                        `id` bigint(20) NOT NULL,
                        `area_id` bigint(20) DEFAULT NULL,
                        `area_name` varchar(255) DEFAULT NULL,
                        `country_code` varchar(255) DEFAULT NULL,
                        `latitude` double DEFAULT NULL,
                        `longitude` double DEFAULT NULL,
                        `name` varchar(255) DEFAULT NULL,
                        `region_id` bigint(20) NOT NULL,
                        `region_name` varchar(255) DEFAULT NULL,
                        PRIMARY KEY (`id`),
                        KEY `index_zone_region_id` (`region_id`),
                        KEY `index_zone_zone_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_swedish_ci;

--
-- Table structure for table `zone_updated`
--
CREATE TABLE `zone_updated` (
                                `id` bigint(20) NOT NULL,
                                `updated_time` datetime(6) DEFAULT NULL,
                                `zone_id` bigint(20) DEFAULT NULL,
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_swedish_ci;
