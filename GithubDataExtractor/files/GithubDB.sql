CREATE DATABASE  IF NOT EXISTS `githubdb` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;
USE `githubdb`;

SET NAMES utf8mb4 ;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;
SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '';

DROP TABLE IF EXISTS `Repositories`;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `Repositories` (
  `id` bigint(100) NOT NULL,
  `name` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL,
  `owner` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL,
  `url` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `language` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `Commits`;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `Commits` (
  `idSeq` int(50) NOT NULL AUTO_INCREMENT,
  `sha` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `url` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL,
  `message` longtext COLLATE utf8mb4_unicode_ci,
  `idRepository` bigint(100) NOT NULL,
  PRIMARY KEY (`idSeq`),
  KEY `fk_Commits_Repositories_idx` (`idRepository`),
  CONSTRAINT `fk_Commits_Repositories` FOREIGN KEY (`idRepository`) REFERENCES `Repositories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1839891 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `Files`;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `Files` (
  `idSeq` int(11) NOT NULL AUTO_INCREMENT,
  `sha` varchar(200) NOT NULL,
  `filename` varchar(700) NOT NULL,
  `status` varchar(50) DEFAULT NULL,
  `additions` int(10) DEFAULT '0',
  `deletions` int(10) DEFAULT '0',
  `changes` int(10) DEFAULT '0',
  `idCommit` int(20) NOT NULL,
  `shaCommit` varchar(100) NOT NULL,
  `idRepository` bigint(100) NOT NULL,
  PRIMARY KEY (`idSeq`),
  KEY `fk_Files_Commit_id_idx` (`idCommit`),
  KEY `fk_Files_Repositories_id_idx` (`idRepository`),
  CONSTRAINT `fk_Files_Commits` FOREIGN KEY (`idCommit`) REFERENCES `Commits` (`idseq`),
  CONSTRAINT `fk_Files_Repositories` FOREIGN KEY (`idRepository`) REFERENCES `Repositories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27703017 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `IssueEvents`;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `IssueEvents` (
  `idSeq` int(50) NOT NULL AUTO_INCREMENT,
  `id` bigint(100) NOT NULL,
  `commitId` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `url` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `idIssue` int(50) NOT NULL,
  `idRepository` bigint(100) NOT NULL,
  PRIMARY KEY (`idSeq`),
  KEY `fk_IssueEvents_Repositories1_idx` (`idRepository`),
  KEY `index_commit` (`commitId`) USING BTREE,
  CONSTRAINT `fk_IssueEvents_Repositories1` FOREIGN KEY (`idRepository`) REFERENCES `Repositories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=435709 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `Issues`;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `Issues` (
  `idSeq` int(50) NOT NULL AUTO_INCREMENT,
  `id` bigint(100) NOT NULL,
  `state` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `title` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `body` longtext COLLATE utf8mb4_unicode_ci,
  `url` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `idRepository` bigint(100) NOT NULL,
  PRIMARY KEY (`idSeq`),
  KEY `fk_Issues_Repositories1_idx` (`idRepository`),
  CONSTRAINT `fk_Issues_Repositories1` FOREIGN KEY (`idRepository`) REFERENCES `Repositories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=435709 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
