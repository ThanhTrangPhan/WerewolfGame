-- phpMyAdmin SQL Dump
-- version 5.1.0
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Sep 11, 2021 at 03:13 PM
-- Server version: 10.4.18-MariaDB
-- PHP Version: 8.0.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `GameDB`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `addGroupMember` (IN `timeJoined` TEXT, IN `groupId` INT, IN `playerId` INT)  BEGIN
INSERT INTO tblGroupMember(tblGroupMember.timeJoined,tblGroupMember.GroupID,tblGroupMember.playerID) VALUES (timeJoined, groupId,playerId);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `cancleMember` (IN `idMember` INT)  BEGIN
DELETE FROM tblGroupMember WHERE tblGroupMember.id = idMember;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `createGroup` (IN `name` TEXT, IN `founderName` TEXT, IN `timeStarted` DATETIME)  BEGIN
INSERT INTO tblGroup(tblGroup.name,tblGroup.founderName,tblGroup.timeStarted) VALUES (name, founderName,timeStarted);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getMostUsedRole` (IN `playerid` INT)  BEGIN
 SELECT tblRecord.role as r, COUNT(*) FROM tblRecord WHERE tblRecord.playerID=playerid GROUP BY r  ORDER BY 2 DESC LIMIT 1 ;
 END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getPlayerStatistic` (IN `playerid` INT)  BEGIN
SELECT tblPlayer.name, (SELECT COUNT(*) FROM tblRecord WHERE tblPlayer.id = playerid AND tblPlayer.id=tblRecord.playerID) AS totalMatch,(SELECT COUNT(*) FROM tblRecord WHERE tblPlayer.id = playerid AND tblPlayer.id=tblRecord.playerID AND tblRecord.status LIKE 'win') AS totalWin,(SELECT tblRecord.role as roles, COUNT(*) as numberUsed FROM tblRecord WHERE tblRecord.playerID=playerid GROUP BY roles  ORDER BY 2 DESC LIMIT 1) FROM tblPlayer WHERE tblPlayer.id=playerid; 
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getPlayerStatWin` (IN `playerid` INT)  BEGIN
SELECT COUNT(tblRecord.id) FROM tblRecord,tblPlayer WHERE tblPlayer.id = playerid AND tblPlayer.id=tblRecord.playerID AND tblRecord.status LIKE 'win';
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getTop5` ()  BEGIN
SELECT tblPlayer.name, (SELECT COUNT(*) FROM tblRecord WHERE tblPlayer.id=tblRecord.playerID) AS totalMatch,(SELECT COUNT(*) FROM tblRecord WHERE tblPlayer.id = playerid AND tblPlayer.id=tblRecord.playerID AND tblRecord.status LIKE 'win') AS totalWin FROM tblPlayer; 
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getTotalMatch` (IN `playerid` INT)  BEGIN
SELECT COUNT(tblRecord.id) FROM tblRecord,tblPlayer WHERE tblPlayer.id = playerid AND tblPlayer.id=tblRecord.playerID;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `searchGroup` (IN `keyword` VARCHAR(255))  BEGIN
SELECT * FROM tblGroup WHERE tblGroup.name LIKE keyword;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `searchRecordBasedOnGroup` (IN `tgg` INT)  BEGIN
SELECT tblRecord.* FROM ((tblGroupMember  INNER JOIN tblRecord ON  tblGroupMember.GroupID = tgg AND tblGroupMember.id = tblRecord.groupMemberID)
INNER JOIN tblPlayer on tblRecord.playerID=tblPlayer.id) ORDER BY tblRecord.id ASC;
                                                                                                       
end$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `setting` (IN `maxPlayer` INT, IN `description` TEXT, IN `type` TEXT, IN `gameId` INT)  BEGIN
UPDATE tblGameMatch SET tblGameMatch.maxPlayer = maxPlayer, tblGameMatch.description=description, tblGameMatch.type=type WHERE tblGameMatch.id = gameId;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateGameMatch` (IN `maxPlayer` INT, IN `description` TEXT, IN `type` TEXT, IN `timeStarted` TEXT, IN `timeEnded` TEXT, IN `winnerSide` TEXT)  BEGIN
INSERT INTO tblGameMatch(tblGameMatch.maxPlayer,tblGameMatch.description,tblGameMatch.type,tblGameMatch.timeStarted,tblGameMatch.timeEnded, tblGameMatch.winnerSide) VALUES (maxPlayer,description,type,timeStarted,timeEnded,winnerSide);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateRecord` (IN `role` INT, IN `stat` INT, IN `playerId` INT, IN `groupMemberID` INT, IN `gameID` INT)  BEGIN
INSERT INTO tblRecord(tblRecord.role,tblRecord.status,tblRecord.playerID,tblRecord.groupMemberID,tblRecord.gameMatchID) VALUES (role,stat,playerId,groupMemberID,gameID );
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `viewRecordBasedOnMatch` (IN `keyword` INT)  BEGIN
SELECT tblRecord.* FROM tblRecord,tblGameMatch WHERE tblRecord.gameMatchID=keyword AND tblRecord.gameMatchID = tblGameMatch.id;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `tblFriendList`
--

CREATE TABLE `tblFriendList` (
  `player1` int(11) NOT NULL,
  `player2` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `tblFriendList`
--

INSERT INTO `tblFriendList` (`player1`, `player2`) VALUES
(1, 2),
(1, 4),
(2, 3),
(2, 4);

-- --------------------------------------------------------

--
-- Table structure for table `tblGameMatch`
--

CREATE TABLE `tblGameMatch` (
  `id` int(11) NOT NULL,
  `maxPlayer` int(11) NOT NULL,
  `description` text NOT NULL,
  `type` varchar(255) NOT NULL,
  `timeStarted` text NOT NULL,
  `timeEnded` text NOT NULL,
  `winnerSide` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `tblGameMatch`
--

INSERT INTO `tblGameMatch` (`id`, `maxPlayer`, `description`, `type`, `timeStarted`, `timeEnded`, `winnerSide`) VALUES
(1, 3, 'Anyone ', 'Random', '2021-09-10 14:32:10', '2021-09-10 14:40:12', 'Villagers'),
(2, 3, 'Open for everyone', 'Random', '2021-09-10 14:34:54', '2021-09-10 14:59:51', 'Werewolves'),
(3, 4, 'One time per week', 'Group Match', '2021-09-11 9:21:18', '2021-09-11 9:49:22', 'Villagers');

-- --------------------------------------------------------

--
-- Table structure for table `tblGroup`
--

CREATE TABLE `tblGroup` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `timeStarted` text NOT NULL,
  `founderName` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `tblGroup`
--

INSERT INTO `tblGroup` (`id`, `name`, `timeStarted`, `founderName`) VALUES
(1, 'Winner', '2021-09-10 10:16:23', 'chicken'),
(2, 'vietnamWW', '2021-09-10 22:29:11', 'CadyGiang'),
(4, 'wereWolves', '2021-09-10 22:34:14', 'duongAnh'),
(6, 'LaLaLa', '2021-09-10 22:49:45', 'luckyMan'),
(7, 'HQ', '2021-09-11T18:47:29.234234', 'Tran'),
(9, 'Squad', '2021-09-11T18:49:29.139121', 'luckyMan');

-- --------------------------------------------------------

--
-- Table structure for table `tblGroupMember`
--

CREATE TABLE `tblGroupMember` (
  `id` int(11) NOT NULL,
  `timeJoined` text NOT NULL,
  `GroupID` int(11) NOT NULL,
  `playerID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `tblGroupMember`
--

INSERT INTO `tblGroupMember` (`id`, `timeJoined`, `GroupID`, `playerID`) VALUES
(1, '2021-09-10 13:32:11', 1, 5),
(2, '2021-09-10 13:45:12', 1, 3),
(3, '2021-09-11 23:01:22', 4, 8),
(4, '2021-09-10 23:10:09', 2, 3),
(5, '2021-09-10 23:09:44', 2, 5),
(6, '2021-09-10 17:21:05', 4, 7),
(7, '2021-09-10 23:13:02', 6, 8),
(9, '2021-09-11 9:00:18', 1, 2),
(10, '2021-09-11 6:21:18', 1, 7),
(11, '2021-09-11 12:07:42', 2, 6),
(12, '2021-09-11T18:47:29.292109', 7, 3),
(14, '2021-09-11T18:49:29.190719', 9, 8);

-- --------------------------------------------------------

--
-- Table structure for table `tblPlayer`
--

CREATE TABLE `tblPlayer` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(255) NOT NULL,
  `status` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `tblPlayer`
--

INSERT INTO `tblPlayer` (`id`, `name`, `password`, `phone`, `status`) VALUES
(1, 'andy', '1111', '1244', 'Okay'),
(2, 'Palm ', '1111', '4233', 'Hurrrrray '),
(3, 'Tran', '1111', '6345', ''),
(4, 'crazylady', '1111', '1241', 'Im the wolf'),
(5, 'chicken', '1111', '1234', NULL),
(6, 'CadyGiag', '1111', '2423', NULL),
(7, 'duongAnh', '1111', '3553', NULL),
(8, 'luckyMan', '1111', '5323', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `tblRecord`
--

CREATE TABLE `tblRecord` (
  `id` int(11) NOT NULL,
  `role` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `playerID` int(11) NOT NULL,
  `groupMemberID` int(11) DEFAULT NULL,
  `gameMatchID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `tblRecord`
--

INSERT INTO `tblRecord` (`id`, `role`, `status`, `playerID`, `groupMemberID`, `gameMatchID`) VALUES
(1, 'Villager', 'win', 5, NULL, 1),
(2, 'Werewolf', 'loose', 1, NULL, 1),
(3, 'Seer', 'win', 4, NULL, 1),
(4, 'Werewolf', 'win', 2, NULL, 2),
(5, 'Villager', 'loose', 6, NULL, 2),
(6, 'Seeker', 'loose', 3, NULL, 2),
(7, 'Seeker', 'win', 2, 9, 3),
(8, 'Werewolf', 'loose', 3, 2, 3),
(9, 'Villager', 'win', 7, 10, 3),
(10, 'Villagers', 'win', 5, 1, 3);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `tblFriendList`
--
ALTER TABLE `tblFriendList`
  ADD PRIMARY KEY (`player1`,`player2`) USING BTREE;

--
-- Indexes for table `tblGameMatch`
--
ALTER TABLE `tblGameMatch`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tblGroup`
--
ALTER TABLE `tblGroup`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indexes for table `tblGroupMember`
--
ALTER TABLE `tblGroupMember`
  ADD PRIMARY KEY (`id`),
  ADD KEY `GroupID` (`GroupID`),
  ADD KEY `playerID` (`playerID`);

--
-- Indexes for table `tblPlayer`
--
ALTER TABLE `tblPlayer`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tblRecord`
--
ALTER TABLE `tblRecord`
  ADD PRIMARY KEY (`id`),
  ADD KEY `gameMatchID` (`gameMatchID`),
  ADD KEY `groupMemberID` (`groupMemberID`),
  ADD KEY `playerID` (`playerID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `tblGameMatch`
--
ALTER TABLE `tblGameMatch`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `tblGroup`
--
ALTER TABLE `tblGroup`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `tblGroupMember`
--
ALTER TABLE `tblGroupMember`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `tblPlayer`
--
ALTER TABLE `tblPlayer`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `tblRecord`
--
ALTER TABLE `tblRecord`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `tblFriendList`
--
ALTER TABLE `tblFriendList`
  ADD CONSTRAINT `tblFriendList_ibfk_1` FOREIGN KEY (`playerID`) REFERENCES `tblPlayer` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `tblFriendList_ibfk_2` FOREIGN KEY (`FriendID`) REFERENCES `tblPlayer` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `tblGroupMember`
--
ALTER TABLE `tblGroupMember`
  ADD CONSTRAINT `tblGroupMember_ibfk_1` FOREIGN KEY (`GroupID`) REFERENCES `tblGroup` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `tblGroupMember_ibfk_2` FOREIGN KEY (`playerID`) REFERENCES `tblPlayer` (`id`);

--
-- Constraints for table `tblRecord`
--
ALTER TABLE `tblRecord`
  ADD CONSTRAINT `tblRecord_ibfk_1` FOREIGN KEY (`gameMatchID`) REFERENCES `tblGameMatch` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `tblRecord_ibfk_2` FOREIGN KEY (`groupMemberID`) REFERENCES `tblGroupMember` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `tblRecord_ibfk_3` FOREIGN KEY (`playerID`) REFERENCES `tblPlayer` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
