-- 개발 테스트용 MySQL 도커 컨테이너 쿼리(유출되도 상관 X)
ALTER USER 'goorm_exp'@'%' IDENTIFIED BY '00000000';
GRANT ALL PRIVILEGES ON goorm_exp.* TO 'goorm_exp'@'%';
FLUSH PRIVILEGES;

USE goorm_exp;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS comment_emoji_reaction;
DROP TABLE IF EXISTS praise_comment;
DROP TABLE IF EXISTS praise_emoji_reaction;
DROP TABLE IF EXISTS praise_receiver;
DROP TABLE IF EXISTS study_diary_comment;
DROP TABLE IF EXISTS study_diary_like;
DROP TABLE IF EXISTS submission;
DROP TABLE IF EXISTS mission_reward_exp_log;
DROP TABLE IF EXISTS mission_reward_point_log;
DROP TABLE IF EXISTS user_mission_task;
DROP TABLE IF EXISTS user_mission_state_log;
DROP TABLE IF EXISTS user_quest;
DROP TABLE IF EXISTS user_mission;
DROP TABLE IF EXISTS mission_task;
DROP TABLE IF EXISTS user_mission_group;
DROP TABLE IF EXISTS bookmark;

DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS praise;
DROP TABLE IF EXISTS quest;
DROP TABLE IF EXISTS study_diary;

DROP TABLE IF EXISTS mission;
DROP TABLE IF EXISTS mission_group;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS product_image;

DROP TABLE IF EXISTS profile_image;
DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;


-- 기본 엔티티들
CREATE TABLE profile_image (
                               id BIGINT NOT NULL AUTO_INCREMENT,
                               uuid VARCHAR(36) NOT NULL,
                               extension VARCHAR(255) NOT NULL,
                               path VARCHAR(255) NOT NULL,
                               PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE product_image (
                               id BIGINT NOT NULL AUTO_INCREMENT,
                               uuid VARCHAR(36) NOT NULL,
                               extension VARCHAR(255) NOT NULL,
                               path VARCHAR(255) NOT NULL,
                               PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE quest (
                       is_deleted BIT NOT NULL,
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       name VARCHAR(255) NOT NULL,
                       url VARCHAR(255) NOT NULL,
                       type ENUM('ATTENDANCE','MISSION_REWARD','PRAISE_COMMENT','QUEST_CLEAR','WRITE_DIARY') NOT NULL,
                       PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE users (
                       exp INTEGER NOT NULL,
                       point INTEGER NOT NULL,
                       created_at DATETIME(6),
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       modified_at DATETIME(6),
                       profile_image_id BIGINT,
                       phone_number VARCHAR(13) NOT NULL,
                       name VARCHAR(32) NOT NULL,
                       username VARCHAR(32) NOT NULL,
                       password VARCHAR(60) NOT NULL,
                       description VARCHAR(255),
                       role ENUM('ADMIN','LECTURER','USER') NOT NULL,
                       PRIMARY KEY (id),
                       UNIQUE (profile_image_id),
                       UNIQUE (phone_number),
                       UNIQUE (username),
                       FOREIGN KEY (profile_image_id) REFERENCES profile_image(id)
) ENGINE=InnoDB;

CREATE TABLE bookmark (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          user_id BIGINT NOT NULL,
                          title VARCHAR(255) NOT NULL,
                          link VARCHAR(255) NOT NULL,
                          created_at DATETIME NOT NULL,
                          modified_at DATETIME NOT NULL,
                          PRIMARY KEY (id),
                          FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;


CREATE TABLE product (
                         is_deleted BIT NOT NULL,
                         price INTEGER NOT NULL,
                         quantity INTEGER NOT NULL,
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         product_image_id BIGINT,
                         brand VARCHAR(255) NOT NULL,
                         name VARCHAR(255) NOT NULL,
                         PRIMARY KEY (id),
                         UNIQUE (product_image_id),
                         FOREIGN KEY (product_image_id) REFERENCES product_image(id)
) ENGINE=InnoDB;

CREATE TABLE attendance (
                            attendance_date DATE NOT NULL,
                            created_at DATETIME(6),
                            id BIGINT NOT NULL AUTO_INCREMENT,
                            modified_at DATETIME(6),
                            user_id BIGINT NOT NULL,
                            PRIMARY KEY (id),
                            UNIQUE (user_id, attendance_date),
                            FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE mission_group (
                               created_at DATETIME(6),
                               id BIGINT NOT NULL AUTO_INCREMENT,
                               modified_at DATETIME(6),
                               teacher_id BIGINT NOT NULL,
                               name VARCHAR(127) NOT NULL,
                               PRIMARY KEY (id),
                               FOREIGN KEY (teacher_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE mission (
                         mission_line INTEGER NOT NULL,
                         mission_order INTEGER NOT NULL,
                         reward_exp INTEGER NOT NULL,
                         reward_point INTEGER NOT NULL,
                         created_at DATETIME(6),
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         mission_group_id BIGINT NOT NULL,
                         modified_at DATETIME(6),
                         name VARCHAR(127) NOT NULL,
                         description VARCHAR(1023) NOT NULL,
                         difficulty ENUM('EASY','HARD','NORMAL') NOT NULL,
                         PRIMARY KEY (id),
                         FOREIGN KEY (mission_group_id) REFERENCES mission_group(id)
) ENGINE=InnoDB;

CREATE TABLE mission_task (
                              score INTEGER NOT NULL,
                              created_at DATETIME(6),
                              id BIGINT NOT NULL AUTO_INCREMENT,
                              mission_id BIGINT NOT NULL,
                              modified_at DATETIME(6),
                              name VARCHAR(127) NOT NULL,
                              PRIMARY KEY (id),
                              FOREIGN KEY (mission_id) REFERENCES mission(id)
) ENGINE=InnoDB;

CREATE TABLE user_mission_group (
                                    created_at DATETIME(6),
                                    id BIGINT NOT NULL AUTO_INCREMENT,
                                    mission_group_id BIGINT NOT NULL,
                                    modified_at DATETIME(6),
                                    user_id BIGINT NOT NULL,
                                    PRIMARY KEY (id),
                                    UNIQUE (user_id, mission_group_id),
                                    FOREIGN KEY (mission_group_id) REFERENCES mission_group(id),
                                    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE user_mission (
                              created_at DATETIME(6),
                              id BIGINT NOT NULL AUTO_INCREMENT,
                              mission_id BIGINT,
                              modified_at DATETIME(6),
                              user_id BIGINT,
                              user_mission_group_id BIGINT,
                              progress ENUM('ABORTED','COMPLETED','FEEDBACK_COMPLETED','IN_FEEDBACK','IN_PROGRESS','NOT_STARTED','REWARD_RECEIVED'),
                              PRIMARY KEY (id),
                              UNIQUE (user_id, mission_id, user_mission_group_id),
                              FOREIGN KEY (mission_id) REFERENCES mission(id),
                              FOREIGN KEY (user_id) REFERENCES users(id),
                              FOREIGN KEY (user_mission_group_id) REFERENCES user_mission_group(id)
) ENGINE=InnoDB;

CREATE TABLE user_mission_task (
                                   id BIGINT NOT NULL AUTO_INCREMENT,
                                   mission_task_id BIGINT NOT NULL,
                                   user_mission_id BIGINT NOT NULL,
                                   state ENUM('COMPLETED', 'IN_PROGRESS', 'NOT_STARTED') NOT NULL,
                                   PRIMARY KEY (id),
                                   FOREIGN KEY (mission_task_id) REFERENCES mission_task(id),
                                   FOREIGN KEY (user_mission_id) REFERENCES user_mission(id)
) ENGINE=InnoDB;

-- 로그 및 리액션

CREATE TABLE mission_reward_exp_log (
                                        next_exp INTEGER,
                                        prev_exp INTEGER,
                                        created_at DATETIME(6),
                                        id BIGINT NOT NULL AUTO_INCREMENT,
                                        modified_at DATETIME(6),
                                        user_mission_id BIGINT,
                                        note VARCHAR(255),
                                        PRIMARY KEY (id),
                                        FOREIGN KEY (user_mission_id) REFERENCES user_mission(id)
) ENGINE=InnoDB;

CREATE TABLE mission_reward_point_log (
                                          next_point INTEGER,
                                          prev_point INTEGER,
                                          created_at DATETIME(6),
                                          id BIGINT NOT NULL AUTO_INCREMENT,
                                          modified_at DATETIME(6),
                                          user_mission_id BIGINT,
                                          note VARCHAR(255),
                                          PRIMARY KEY (id),
                                          FOREIGN KEY (user_mission_id) REFERENCES user_mission(id)
) ENGINE=InnoDB;

CREATE TABLE user_mission_state_log (
                                        created_at DATETIME(6),
                                        id BIGINT NOT NULL AUTO_INCREMENT,
                                        modified_at DATETIME(6),
                                        user_mission_id BIGINT,
                                        note VARCHAR(255),
                                        next_state ENUM('ABORTED','COMPLETED','FEEDBACK_COMPLETED','IN_FEEDBACK','IN_PROGRESS','NOT_STARTED','REWARD_RECEIVED'),
                                        prev_state ENUM('ABORTED','COMPLETED','FEEDBACK_COMPLETED','IN_FEEDBACK','IN_PROGRESS','NOT_STARTED','REWARD_RECEIVED'),
                                        PRIMARY KEY (id),
                                        FOREIGN KEY (user_mission_id) REFERENCES user_mission(id)
) ENGINE=InnoDB;

CREATE TABLE submission (
                            id BIGINT NOT NULL AUTO_INCREMENT,
                            user_mission_id BIGINT NOT NULL,
                            file_name VARCHAR(255) NOT NULL,
                            original_file_name VARCHAR(255) NOT NULL,
                            comment TEXT NOT NULL,
                            feedback TEXT,
                            PRIMARY KEY (id),
                            UNIQUE (user_mission_id),
                            FOREIGN KEY (user_mission_id) REFERENCES user_mission(id)
) ENGINE=InnoDB;

-- 기타 유저 기반 테이블

CREATE TABLE user_quest (
                            is_completable BIT NOT NULL,
                            is_completed BIT NOT NULL,
                            id BIGINT NOT NULL AUTO_INCREMENT,
                            quest_id BIGINT NOT NULL,
                            user_id BIGINT NOT NULL,
                            PRIMARY KEY (id),
                            FOREIGN KEY (quest_id) REFERENCES quest(id),
                            FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE praise (
                        created_at DATETIME(6),
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        modified_at DATETIME(6),
                        sender_id BIGINT NOT NULL,
                        content VARCHAR(255) NOT NULL,
                        praise_type ENUM('CHEER','RECOGNIZE','THANKS') NOT NULL,
                        PRIMARY KEY (id),
                        FOREIGN KEY (sender_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE praise_comment (
                                comment_writer_id BIGINT NOT NULL,
                                created_at DATETIME(6),
                                id BIGINT NOT NULL AUTO_INCREMENT,
                                modified_at DATETIME(6),
                                praise_id BIGINT NOT NULL,
                                content VARCHAR(255) NOT NULL,
                                PRIMARY KEY (id),
                                FOREIGN KEY (comment_writer_id) REFERENCES users(id),
                                FOREIGN KEY (praise_id) REFERENCES praise(id)
) ENGINE=InnoDB;

CREATE TABLE praise_emoji_reaction (
                                       created_at DATETIME(6),
                                       id BIGINT NOT NULL AUTO_INCREMENT,
                                       modified_at DATETIME(6),
                                       praise_id BIGINT NOT NULL,
                                       reaction_writer_id BIGINT NOT NULL,
                                       emoji VARCHAR(10) NOT NULL,
                                       PRIMARY KEY (id),
                                       FOREIGN KEY (praise_id) REFERENCES praise(id),
                                       FOREIGN KEY (reaction_writer_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE praise_receiver (
                                 created_at DATETIME(6),
                                 id BIGINT NOT NULL AUTO_INCREMENT,
                                 modified_at DATETIME(6),
                                 praise_id BIGINT,
                                 receiver_id BIGINT,
                                 PRIMARY KEY (id),
                                 FOREIGN KEY (praise_id) REFERENCES praise(id),
                                 FOREIGN KEY (receiver_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE comment_emoji_reaction (
                                        comment_id BIGINT NOT NULL,
                                        created_at DATETIME(6),
                                        id BIGINT NOT NULL AUTO_INCREMENT,
                                        modified_at DATETIME(6),
                                        reaction_writer_id BIGINT NOT NULL,
                                        emoji VARCHAR(10) NOT NULL,
                                        PRIMARY KEY (id),
                                        FOREIGN KEY (comment_id) REFERENCES praise_comment(id),
                                        FOREIGN KEY (reaction_writer_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- 공부 일지

CREATE TABLE study_diary (
                             is_created BIT NOT NULL,
                             like_count INTEGER NOT NULL,
                             created_at DATETIME(6),
                             modified_at DATETIME(6),
                             studydiary_id BIGINT NOT NULL AUTO_INCREMENT,
                             user_id BIGINT,
                             content VARCHAR(255),
                             title VARCHAR(255),
                             PRIMARY KEY (studydiary_id),
                             FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE study_diary_comment (
                                     created_at DATETIME(6),
                                     modified_at DATETIME(6),
                                     studydiary_comment_id BIGINT NOT NULL AUTO_INCREMENT,
                                     studydiary_id BIGINT,
                                     user_id BIGINT,
                                     content VARCHAR(255),
                                     PRIMARY KEY (studydiary_comment_id),
                                     FOREIGN KEY (studydiary_id) REFERENCES study_diary(studydiary_id),
                                     FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE study_diary_like (
                                  created_at DATETIME(6),
                                  modified_at DATETIME(6),
                                  studydiary_id BIGINT,
                                  studydiary_like_id BIGINT NOT NULL AUTO_INCREMENT,
                                  user_id BIGINT,
                                  PRIMARY KEY (studydiary_like_id),
                                  FOREIGN KEY (studydiary_id) REFERENCES study_diary(studydiary_id),
                                  FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;


CREATE TABLE orders (
                        created_at DATETIME(6),
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        modified_at DATETIME(6),
                        product_id BIGINT NOT NULL,
                        purchaser_id BIGINT NOT NULL,
                        receiver_phone_number VARCHAR(13) NOT NULL,
                        PRIMARY KEY (id),
                        FOREIGN KEY (product_id) REFERENCES product(id),
                        FOREIGN KEY (purchaser_id) REFERENCES users(id)
) ENGINE=InnoDB;



INSERT INTO `users` (`exp`, `point`, `created_at`, `id`, `modified_at`, `profile_image_id`, `phone_number`, `name`, `username`, `password`, `description`, `role`) VALUES
                                                                                                                                                                       (0, 500, '2025-06-28 18:27:53.817615', 1, '2025-06-28 18:27:53.817615', NULL, '010-0000-0001', '테스트01', 'test01', '$2a$10$kj/BjnH/Fy/r/8vBD.7aKebtfwnzSYK2UPdG1unkpCxTzjTLwONb.', NULL, 'USER'),
                                                                                                                                                                       (0, 500, '2025-06-28 18:28:20.789758', 2, '2025-06-28 18:28:20.789758', NULL, '010-0000-0002', '테스트02', 'test02', '$2a$10$pTwCJL/gxKdFLCGq/0fBa.5iQnN2Zy3y.NOnIxzrpUmDb/Rz0rXhG', NULL, 'USER'),
                                                                                                                                                                       (0, 500, '2025-06-28 18:28:37.324012', 3, '2025-06-28 18:28:37.324012', NULL, '010-0000-0003', '테스트03', 'test03', '$2a$10$s3S/X56jsjuWuBvZ3Tto3.d7ps5jRB1/iceAlKG7Tf9IdiHltZWVK', NULL, 'USER'),
                                                                                                                                                                       (0, 500, '2025-06-28 18:28:49.511950', 4, '2025-06-28 18:28:49.511950', NULL, '010-0000-0004', '테스트04', 'test04', '$2a$10$qJAlXIrBKMq/W6cAZVBLpOOLUOWafyBuJmwhtirFihJMdlLNlamim', NULL, 'USER'),
                                                                                                                                                                       (0, 500, '2025-06-28 18:28:58.882583', 5, '2025-06-28 18:28:58.882583', NULL, '010-0000-0005', '테스트05', 'test05', '$2a$10$eeKrOVXBnXVhiRA.HJwldu9AD1bkAucpEPLr0InNnFimIgFxY9syO', NULL, 'USER'),
                                                                                                                                                                       (0, 500, '2025-06-28 18:38:07.750071', 6, '2025-06-28 18:38:07.750071', NULL, '010-0001-0001', '관리자01', 'admin01', '$2a$10$1UGuGYTqi.lLSIuvfadBOun5P3U/GQmVB5Och/hwI6cAP0mowxzES', NULL, 'ADMIN'),
                                                                                                                                                                       (0, 500, '2025-06-28 18:38:32.935902', 7, '2025-06-28 18:38:32.935902', NULL, '010-0002-0001', '강사01', 'teacher01', '$2a$10$QPjenkRVoYBFV6lXeXjcfe4H9GNGDr/TdXb.iFUl4fgC3sHNAUynq', NULL, 'LECTURER');

INSERT INTO `mission_group` (`created_at`, `id`, `modified_at`, `teacher_id`, `name`) VALUES
                                                                                          ('2025-06-28 18:41:39.882646', 1, '2025-06-28 18:41:39.882646', 7, '허그톤 미션'),
                                                                                          ('2025-06-28 18:41:47.096493', 2, '2025-06-28 18:41:47.096493', 7, '허그톤 알고리즘');


INSERT INTO `user_mission_group` (`created_at`, `id`, `mission_group_id`, `modified_at`, `user_id`) VALUES
                                                                                                        ('2025-06-28 18:43:16.614022', 1, 1, '2025-06-28 18:43:16.614022', 1),
                                                                                                        ('2025-06-28 18:43:18.849122', 2, 1, '2025-06-28 18:43:18.849122', 2),
                                                                                                        ('2025-06-28 18:43:20.249681', 3, 1, '2025-06-28 18:43:20.249681', 3),
                                                                                                        ('2025-06-28 18:43:21.690030', 4, 1, '2025-06-28 18:43:21.690030', 4),
                                                                                                        ('2025-06-28 18:43:23.280028', 5, 1, '2025-06-28 18:43:23.280028', 5),
                                                                                                        ('2025-06-28 18:43:24.998023', 6, 1, '2025-06-28 18:43:24.998023', 6),
                                                                                                        ('2025-06-28 18:43:26.754535', 7, 1, '2025-06-28 18:43:26.754535', 7),
                                                                                                        ('2025-06-28 18:43:30.722564', 8, 2, '2025-06-28 18:43:30.722564', 1),
                                                                                                        ('2025-06-28 18:43:32.565030', 9, 2, '2025-06-28 18:43:32.565030', 2),
                                                                                                        ('2025-06-28 18:43:34.665942', 10, 2, '2025-06-28 18:43:34.665942', 3),
                                                                                                        ('2025-06-28 18:43:36.530726', 11, 2, '2025-06-28 18:43:36.530726', 4),
                                                                                                        ('2025-06-28 18:43:38.015032', 12, 2, '2025-06-28 18:43:38.015032', 5),
                                                                                                        ('2025-06-28 18:43:39.685884', 13, 2, '2025-06-28 18:43:39.685884', 6),
                                                                                                        ('2025-06-28 18:43:41.059188', 14, 2, '2025-06-28 18:43:41.059188', 7);

INSERT INTO `mission` (`mission_line`, `mission_order`, `reward_exp`, `reward_point`, `created_at`, `id`, `mission_group_id`, `modified_at`, `name`, `description`, `difficulty`) VALUES
                                                                                                                                                                                      (1, 1, 140, 14, '2025-06-28 18:46:54.612682', 1, 1, '2025-06-28 18:46:54.612682', '오리엔테이션', '구름EXP 사용 방법을 미션을 통해 살펴 봅시다.\n이 미션을 통해 구름EXP뿐만 아니라, 교육과정 전반에서 활용하게 될 디스코드와 노션 페이지의 접속 방법도 함께 익힐 수 있습니다.\n\n결과물\n하위 태스크를 수행한 뒤, 수행 과정의 캡처 이미지 또는 산출물 파일이 있다면 함께 제출해주세요.', 'EASY'),
                                                                                                                                                                                      (1, 2, 60, 6, '2025-06-28 18:47:33.468372', 2, 1, '2025-06-28 18:47:33.468372', 'Git 과 Git허브 기초', 'Git 명령어와 GitHub 웹사이트를 사용하여 Git 저장소를 생성하고 파일을 커밋 및 Push하는 과정을 학습합니다. Intellij IDE를 사용하여 Java 프로그램을 작성, 실행하고 결과를 확인하는 과정을 학습합니다.  **결과물** 1. 태스크별 코드 및 캡쳐 파일 제출', 'EASY'),
                                                                                                                                                                                      (1, 4, 240, 24, '2025-06-28 18:48:35.383869', 3, 1, '2025-06-28 18:48:35.383869', '자바 기초2', 'Java 프로그램을 작성하여 사용자 입력을 받고, 다양한 조건과 배열을 처리하는 과정을 학습합니다. 입력된 데이터를 정렬, 산술 연산, 평균 계산 등 다양한 연산을 수행하여 결과를 출력하는 프로그램을 작성합니다.  **결과물** 1. 태스크별 코드 및 캡쳐 파일 제출', 'NORMAL'),
                                                                                                                                                                                      (1, 8, 720, 72, '2025-06-28 18:49:25.333924', 4, 1, '2025-06-28 18:49:25.333924', 'Spring 프레임워크 고급', 'Spring 프레임워크를 사용해 Spring Security 등 다양한 고급 기능을 학습하고 구현합니다.\nSpring Boot와 Spring MVC를 활용하여 웹 애플리케이션을 개발하는 방법을 학습합니다.\n\n**결과물**\n1. 태스크별 코드 및 캡쳐 파일 제출', 'NORMAL'),
                                                                                                                                                                                      (1, 12, 120, 12, '2025-06-28 18:49:54.813136', 5, 1, '2025-06-28 18:49:54.813136', '[특강] 끊임없이 성장하는 개발자로서의 관성 만들기', '개발자로서 지속적으로 성장하기 위한 자기 계발 방법과 동기 부여 방법에 대해 배우는 특강입니다.  이 강의는 개발자의 경력 관리를 위한 심리적, 기술적 방법론을 다룹니다.   **결과물** 1. 태스크별 코드 및 캡쳐 파일 제출', 'NORMAL'),
                                                                                                                                                                                      (1, 14, 120, 12, '2025-06-28 18:50:28.340710', 6, 1, '2025-06-28 18:50:28.340710', '[특강] 카카오 클라우드 엔지니어 특강 - 쿠버네티스 배포와 운영', '카카오 클라우드 엔지니어가 설명하는 쿠버네티스 배포와 운영에 대한 실제 사례와 경험을 공유하는 특강입니다.  **결과물** 1. 설정 및 배포된 쿠버네티스 환경의 캡쳐 파일 제출', 'NORMAL'),
                                                                                                                                                                                      (1, 16, 120, 12, '2025-06-28 18:50:48.601688', 7, 1, '2025-06-28 18:50:48.601688', '[특강] 모던 소프트웨어 개발 프로세스', '현대 소프트웨어 개발에서 널리 사용되는 애자일(Scrum) 및 DevOps 프로세스에 대해 배우는 특강입니다.  **결과물**  1. 애자일 방법론 적용 및 CI/CD 파이프라인 설정 캡쳐 파일 제출', 'NORMAL'),
                                                                                                                                                                                      (2, 2, 60, 6, '2025-06-28 18:51:23.849710', 8, 1, '2025-06-28 18:51:23.849710', '자바 기초1', 'Java 프로그램을 작성하여 사용자 입력을 받아 출력하는 과정을 학습합니다.  **결과물** 1. 태스크별 코드 및 캡쳐 파일 제출', 'EASY'),
                                                                                                                                                                                      (2, 4, 400, 40, '2025-06-28 18:52:11.282593', 9, 1, '2025-06-28 18:52:11.282593', '자바 중급', 'Java를 사용하여 객체지향 프로그래밍, 예외 처리, 스레드, 람다 표현식 등 중급 개념을 학습하고 다양한 프로그램을 작성합니다. HTML과 CSS를 이용해 기본 웹 페이지를 작성하고 스타일을 지정하는 방법을 학습합니다.  **결과물** 1. 태스크별 코드 및 캡쳐 파일 제출', 'NORMAL'),
                                                                                                                                                                                      (2, 8, 480, 48, '2025-06-28 18:53:12.745156', 10, 1, '2025-06-28 18:53:12.745156', 'Spring 프레임워크 기초', 'Spring 프레임워크를 사용하여 빈 정의, 의존성 주입, AOP, 웹 스코프 등 다양한 기능을 학습하고 구현합니다. Spring Boot와 Spring MVC를 활용하여 웹 애플리케이션을 개발하는 방법을 학습합니다.  **결과물** 1. 태스크별 코드 및 캡쳐 파일 제출', 'NORMAL'),
                                                                                                                                                                                      (2, 12, 120, 12, '2025-06-28 18:54:14.125388', 11, 1, '2025-06-28 18:54:14.125388', '[특강] 이슈 트래커, 협업 도구, 생산성 도구 기초', 'GitHub Issues, Trello, JIRA와 같은 이슈 트래커와 협업 도구를 기본적으로 사용하고,  이를 통해 프로젝트 관리를 효율적으로 하는 방법을 배우는 특강입니다.   **결과물** 1. 설정한 이슈 트래커, 협업 도구 및 생산성 도구의 사용 캡쳐 파일 제출', 'NORMAL'),
                                                                                                                                                                                      (2, 14, 120, 12, '2025-06-28 18:54:34.559478', 12, 1, '2025-06-28 18:54:34.559478', '[특강] 포트폴리오 세미나', '개발자 포트폴리오 작성법과 이력서 작성 요령, 그리고 자신을 효과적으로 브랜딩하는 방법을 배우는 세미나입니다.  **결과물** 1. 작성한 포트폴리오 및 자기소개서 파일 제출', 'NORMAL'),
                                                                                                                                                                                      (2, 16, 120, 12, '2025-06-28 18:54:58.771965', 13, 1, '2025-06-28 18:54:58.771965', '[특강] 협업을 위한 세미나', '효율적인 팀 협업을 위한 커뮤니케이션 및 팀워크 전략을 배우는 세미나입니다.  **결과물** 1. 팀 협업 활동 기록 및 캡쳐 파일 제출', 'NORMAL'),
                                                                                                                                                                                      (3, 8, 360, 36, '2025-06-28 18:55:28.756228', 14, 1, '2025-06-28 18:55:28.756228', '데이터베이스 및 ORM', 'MariaDB에서 DDL, DML 쿼리를 사용하여 테이블 생성 및 데이터 조작을 수행하고, 쿼리 성능 최적화와 인덱스 활용 방법을 학습합니다. Spring Boot 프레임워크에서 MyBatis와 JPA를 사용해 데이터베이스와의 CRUD 작업을 구현하며, React를 사용해 웹 애플리케이션을 개발하는 과정을 학습합니다.  **결과물** 1. 태스크별 코드 및 캡쳐 파일 제출', 'NORMAL'),
                                                                                                                                                                                      (1, 6, 120, 12, '2025-06-28 18:59:32.684214', 15, 1, '2025-06-28 18:59:32.684214', '웹 개발 기초', 'HTML과 CSS를 사용하여 웹 페이지의 구조를 작성하고 스타일을 지정하는 방법을 학습합니다. 그래들 프로젝트를 생성하고 스프링 컨텍스트 설정 파일을 작성하여 빈을 추가하고 활용하는 과정을 학습합니다.  **결과물** 1. 태스크별 코드 및 캡쳐 파일 제출', 'NORMAL'),
                                                                                                                                                                                      (1, 1, 540, 20, '2025-06-28 19:02:51.527076', 16, 2, '2025-06-28 19:02:51.527076', '오리엔테이션', '알고리즘 강좌를 접속하고 수강하는 방법에 대해 학습합니다.\n\n\n결과물\n\n수강 화면 캡처', 'EASY'),
                                                                                                                                                                                      (1, 2, 810, 30, '2025-06-28 19:03:32.063225', 17, 2, '2025-06-28 19:03:32.063225', '기초 구현', '이 미션을 통해, 기초 구현 문제를 풀어볼 수 있습니다.\n\n\n결과물\n\n수강 화면 캡처', 'EASY'),
                                                                                                                                                                                      (1, 3, 810, 30, '2025-06-28 19:03:54.654803', 18, 2, '2025-06-28 19:03:54.654803', '기초 시뮬레이션', '이 미션을 통해, 기초 시뮬레이션 문제를 풀어볼 수 있습니다.\n\n\n결과물\n\n수강 화면 캡처', 'EASY'),
                                                                                                                                                                                      (1, 4, 810, 30, '2025-06-28 19:04:22.006311', 19, 2, '2025-06-28 19:04:22.006311', '기초 알고리즘 챌린지', '이 미션은 기초 수학, 구현, 시뮬레이션이 실제 코딩테스트에 출제되었을 때 문제를 변형한 문제들을 풀어볼 수 있습니다.\n\n\n결과물\n\n수강 화면 캡처', 'EASY'),
                                                                                                                                                                                      (1, 5, 1080, 40, '2025-06-28 19:05:03.394090', 20, 2, '2025-06-28 19:05:03.394090', '재귀 알고리즘', '함수의 재귀적 표현을 통해 해결하는 알고리즘을 공부할 수 있습니다.\n\n\n결과물\n\n수강 화면 캡처', 'NORMAL'),
                                                                                                                                                                                      (1, 6, 1620, 60, '2025-06-28 19:05:51.342769', 21, 2, '2025-06-28 19:05:51.342769', '완전 탐색 알고리즘', '모든 경우의 수 중에서, 조건에 맞는 경우를 찾는 완전 탐색 알고리즘 문제를 해결하는 알고리즘을 공부할 수 있습니다.\n\n\n결과물\n\n수강 화면 캡처', 'EASY'),
                                                                                                                                                                                      (1, 7, 2160, 80, '2025-06-28 19:06:21.453892', 22, 2, '2025-06-28 19:06:21.453892', '이분 탐색 알고리즘', '모든 경우가 아닌, 조건에 맞춰진 데이터에서 조건에 맞는 값을 빠르게 찾을 수 있는 알고리즘을 공부할 수 있습니다.\n\n\n결과물\n\n수강 화면 캡처', 'NORMAL'),
                                                                                                                                                                                      (1, 8, 1620, 60, '2025-06-28 19:06:50.836974', 23, 2, '2025-06-28 19:06:50.836974', '그래프와 트리', '그래프와 트리의 개념을 이해하여 해결하는 알고리즘을 공부할 수 있습니다.\n\n\n결과물\n\n수강 화면 캡처', 'NORMAL'),
                                                                                                                                                                                      (1, 10, 2700, 90, '2025-06-28 19:07:20.126111', 24, 2, '2025-06-28 19:07:20.126111', '그래프 탐색', '그래프, 트리에서의 기초적인 탐색 알고리즘을 이해하고 활용하여 해결하는 알고리즘을 공부할 수 있습니다.\n\n\n결과물\n\n수강 화면 캡처', 'NORMAL'),
                                                                                                                                                                                      (1, 11, 1620, 60, '2025-06-28 19:07:49.214110', 25, 2, '2025-06-28 19:07:49.214110', '그리디 알고리즘', '그리디 알고리즘을 이해하여 해결하는 알고리즘을 공부할 수 있습니다.\n\n\n결과물\n\n수강 화면 캡처', 'NORMAL'),
                                                                                                                                                                                      (1, 12, 4040, 140, '2025-06-28 19:08:19.282302', 26, 2, '2025-06-28 19:08:19.282302', '동적 프로그래밍', '문제 상황 해결을 위한 최적화를 이해하고, 이를 동적 프로그래밍으로 활용하여 해결하는 알고리즘을 공부할 수 있습니다.\n\n\n결과물\n\n수강 화면 캡처', 'HARD'),
                                                                                                                                                                                      (1, 13, 2020, 70, '2025-06-28 19:08:47.229073', 27, 2, '2025-06-28 19:08:47.229073', '그래프 탐색 응용', '그래프 탐색 알고리즘을 바탕으로, 다양한 상황에 응용된 알고리즘을 공부할 수 있습니다.\n\n\n결과물\n\n수강 화면 캡처', 'HARD'),
                                                                                                                                                                                      (1, 14, 1080, 40, '2025-06-28 19:09:31.228313', 28, 2, '2025-06-28 19:09:31.228313', '누적합 알고리즘', '미리 계산된 배열을 구성하여, 빠른 시간에 여러 개의 쿼리를 수행하는 알고리즘을 공부할 수 있습니다.\n\n\n결과물\n\n수강 화면 캡처', 'NORMAL'),
                                                                                                                                                                                      (1, 15, 1350, 50, '2025-06-28 19:09:57.277777', 29, 2, '2025-06-28 19:09:57.277777', '세그먼트 트리', '미리 계산된 배열이 지속적으로 업데이트되는 것을 반영하는 트리 구조를 이해하고, 다양한 쿼리를 수행하는 알고리즘을 공부할 수 있습니다.\n\n\n결과물\n\n수강 화면 캡처', 'HARD'),
                                                                                                                                                                                      (2, 1, 1220, 50, '2025-06-28 19:10:35.607612', 30, 2, '2025-06-28 19:10:35.607612', '기초 수학', '이 미션을 통해, 기초 수학문제를 풀어볼 수 있습니다.\n\n\n결과물\n\n수강 화면 캡처', 'EASY'),
                                                                                                                                                                                      (2, 5, 1080, 40, '2025-06-28 19:12:15.619198', 31, 2, '2025-06-28 19:12:15.619198', '정렬 알고리즘', '데이터를 조건에 맞게 정렬하여 푸는 문제를 해결하는 알고리즘을 공부할 수 있습니다.\n\n\n결과물\n\n수강 화면 캡처', 'NORMAL'),
                                                                                                                                                                                      (2, 6, 2160, 80, '2025-06-28 19:12:47.188489', 32, 2, '2025-06-28 19:12:47.188489', '자료 구조', '단순한 배열이 아닌, 각 자료구조의 특징을 활용하여 해결하는 알고리즘을 공부할 수 있습니다.\n\n\n결과물\n\n수강 화면 캡처', 'NORMAL'),
                                                                                                                                                                                      (2, 13, 2020, 70, '2025-06-28 19:13:16.239471', 33, 2, '2025-06-28 19:13:16.239471', '다익스트라 알고리즘', '단순한 그래프 탐색이 아닌, 최소 비용으로 경로를 탐색하는 알고리즘을 공부할 수 있습니다.\n\n\n결과물\n\n수강 화면 캡처', 'HARD'),
                                                                                                                                                                                      (2, 15, 1350, 50, '2025-06-28 19:13:41.275281', 34, 2, '2025-06-28 19:13:41.275281', '최소 스패닝 트리', '간선의 비용이 최소가 되도록, 트리를 구성하는 알고리즘을 공부할 수 있습니다.\n\n\n결과물\n\n수강 화면 캡처', 'HARD');

INSERT INTO `mission_task` (`score`, `created_at`, `id`, `mission_id`, `modified_at`, `name`) VALUES
                                                                                                  (1, '2025-06-28 19:33:59.357014', 1, 1, '2025-06-28 19:33:59.357014', '(노션) 줌 강의 링크 확인하기'),
                                                                                                  (1, '2025-06-28 19:34:08.310621', 2, 1, '2025-06-28 19:34:08.310621', '(노션) 노션 메인페이지 접속하기'),
                                                                                                  (1, '2025-06-28 19:34:18.752477', 3, 1, '2025-06-28 19:34:18.752477', '(디스코드) 딥다이브 커뮤니티 채널에 인사말 쓰기'),
                                                                                                  (1, '2025-06-28 19:34:29.637620', 4, 1, '2025-06-28 19:34:29.637620', '(디스코드) 딥다이브 디스코드 채널에 접속하고, 채널 과정명 확인하기'),
                                                                                                  (1, '2025-06-28 19:34:42.653506', 5, 1, '2025-06-28 19:34:42.653506', '(구름EXP) 구름 EXP 일일 퀘스트 완료하기'),
                                                                                                  (1, '2025-06-28 19:34:52.040631', 6, 1, '2025-06-28 19:34:52.040631', '(구름EXP) 프로필 이미지 변경 및 마이페이지 설정하기'),
                                                                                                  (1, '2025-06-28 19:35:01.867910', 7, 1, '2025-06-28 19:35:01.867910', '(구름EXP) 구름EXP 사용법 알아보기'),
                                                                                                  (1, '2025-06-28 19:35:33.868035', 8, 2, '2025-06-28 19:35:33.868035', '(Git) GitHub에서 저장소 Clone 받기'),
                                                                                                  (1, '2025-06-28 19:35:41.941667', 9, 2, '2025-06-28 19:35:41.941667', '(Git) GitHub에 Push 하기'),
                                                                                                  (1, '2025-06-28 19:35:51.293800', 10, 2, '2025-06-28 19:35:51.293800', '(Git) Git 계정을 생성하고 로컬 저장소에 간단한 텍스트 파일 커밋하기'),
                                                                                                  (2, '2025-06-28 19:36:16.107479', 11, 3, '2025-06-28 19:36:16.107479', '(Java) 배열의 평균을 계산하고 배열 요소를 변경하는 메서드 작성하기'),
                                                                                                  (2, '2025-06-28 19:36:26.904680', 12, 3, '2025-06-28 19:36:26.904680', '(Java) 사용자로부터 직사각형의 가로와 세로를 입력받아 넓이 출력하기'),
                                                                                                  (2, '2025-06-28 19:36:37.235688', 13, 3, '2025-06-28 19:36:37.235688', '(Java) 두 개의 정수를 더하고 빼는 메서드 작성하기'),
                                                                                                  (2, '2025-06-28 19:36:47.196562', 14, 3, '2025-06-28 19:36:47.196562', '(Java) 5개의 정수 오름차순 정렬하기'),
                                                                                                  (2, '2025-06-28 19:36:58.594030', 15, 3, '2025-06-28 19:36:58.594030', '(Java) 1부터 10까지의 숫자 배열 출력하기'),
                                                                                                  (2, '2025-06-28 19:37:10.598728', 16, 3, '2025-06-28 19:37:10.598728', '(Java) 홀수/짝수 판별 프로그램 작성하기'),
                                                                                                  (2, '2025-06-28 19:37:37.330956', 17, 15, '2025-06-28 19:37:37.330956', '그래들 프로젝트를 생성하고 스프링 컨텍스트에 새로운 빈 추가하기'),
                                                                                                  (2, '2025-06-28 19:37:47.047228', 18, 15, '2025-06-28 19:37:47.047228', 'CSS를 사용하여 웹 페이지 스타일링하기'),
                                                                                                  (2, '2025-06-28 19:37:57.148821', 19, 15, '2025-06-28 19:37:57.148821', 'HTML 기본 태그를 사용하여 웹 페이지 구조 만들기'),
                                                                                                  (2, '2025-06-28 19:38:17.276444', 20, 4, '2025-06-28 19:38:17.276444', '스프링 시큐리티를 사용하여 로그아웃 기능 구현하기'),
                                                                                                  (2, '2025-06-28 19:38:27.426744', 21, 4, '2025-06-28 19:38:27.426744', '스프링 시큐리티를 사용하여 회원가입 기능 구현하기'),
                                                                                                  (2, '2025-06-28 19:38:38.964226', 22, 4, '2025-06-28 19:38:38.964226', '스프링 시큐리티를 사용하여 로그인 기능 구현하기'),
                                                                                                  (2, '2025-06-28 19:38:47.904699', 23, 4, '2025-06-28 19:38:47.904699', '게시판 화면 구현하기'),
                                                                                                  (2, '2025-06-28 19:38:57.979251', 24, 4, '2025-06-28 19:38:57.979251', '스프링 부트를 사용하여 웹 애플리케이션 프로젝트 생성하기'),
                                                                                                  (2, '2025-06-28 19:39:07.894125', 25, 4, '2025-06-28 19:39:07.894125', '스프링 MVC를 이용하여 간단한 웹 페이지 구현하기'),
                                                                                                  (2, '2025-06-28 19:39:20.337772', 26, 4, '2025-06-28 19:39:20.337772', '스프링 부트와 스프링 MVC를 활용하여 웹 애플리케이션 개발하기'),
                                                                                                  (2, '2025-06-28 19:39:29.565532', 27, 4, '2025-06-28 19:39:29.565532', '스프링 시큐리티 기본 설정하기'),
                                                                                                  (2, '2025-06-28 19:39:38.812156', 28, 4, '2025-06-28 19:39:38.812156', '스프링 시큐리티 권한 부여 및 접근 제한'),
                                                                                                  (2, '2025-06-28 19:39:48.920096', 29, 4, '2025-06-28 19:39:48.920096', 'Spring MVC에서 유효성 검사 및 예외 처리'),
                                                                                                  (2, '2025-06-28 19:40:01.206558', 30, 4, '2025-06-28 19:40:01.206558', ' Spring MVC에서 인터셉터 구현하기'),
                                                                                                  (2, '2025-06-28 19:40:11.564727', 31, 4, '2025-06-28 19:40:11.564727', 'Spring MVC에서 요청 파라미터 처리하기'),
                                                                                                  (2, '2025-06-28 19:40:21.903798', 32, 4, '2025-06-28 19:40:21.903798', 'Spring MVC에서 모델과 뷰 처리'),
                                                                                                  (2, '2025-06-28 19:40:31.378434', 33, 4, '2025-06-28 19:40:31.378434', 'Spring MVC에서 템플릿 엔진 사용 (Thymeleaf)'),
                                                                                                  (2, '2025-06-28 19:41:17.429215', 34, 5, '2025-06-28 19:41:17.429215', '자기 계발 도구 활용하기'),
                                                                                                  (2, '2025-06-28 19:41:32.256351', 35, 5, '2025-06-28 19:41:32.256351', '효율적인 시간 관리 방법 배우기'),
                                                                                                  (2, '2025-06-28 19:41:54.794426', 36, 6, '2025-06-28 19:41:54.794426', '쿠버네티스 클러스터 설정하기'),
                                                                                                  (2, '2025-06-28 19:42:00.440856', 37, 6, '2025-06-28 19:42:00.440856', '애플리케이션 배포하기'),
                                                                                                  (2, '2025-06-28 19:43:09.482154', 38, 6, '2025-06-28 19:43:09.482154', '운영과 모니터링 설정하기'),
                                                                                                  (2, '2025-06-28 19:43:47.980584', 39, 5, '2025-06-28 19:43:47.980584', '습관 만들기와 유지하기'),
                                                                                                  (2, '2025-06-28 19:44:40.010663', 40, 7, '2025-06-28 19:44:40.010663', '애자일 개발 개요 이해하기'),
                                                                                                  (2, '2025-06-28 19:44:47.965396', 41, 7, '2025-06-28 19:44:47.965396', 'Scrum 프레임워크 이해하기'),
                                                                                                  (2, '2025-06-28 19:44:54.890732', 42, 7, '2025-06-28 19:44:54.890732', 'CI/CD 파이프라인 설정하기'),
                                                                                                  (1, '2025-06-28 19:45:28.562716', 43, 8, '2025-06-28 19:45:28.562716', '(Java) 사용자로부터 두 숫자를 입력받고, 더 큰 숫자 출력하기'),
                                                                                                  (1, '2025-06-28 19:45:38.853473', 44, 8, '2025-06-28 19:45:38.853473', '(Java) 사용자로부터 두 숫자를 입력받아 합계 출력하는 Java 프로그램 작성하기'),
                                                                                                  (1, '2025-06-28 19:45:49.774424', 45, 8, '2025-06-28 19:45:49.774424', '(Java) 자신의 이름과 나이를 출력하는 Java 프로그램 작성하기'),
                                                                                                  (2, '2025-06-28 19:46:03.684050', 46, 9, '2025-06-28 19:46:03.684050', '배열을 사용하여 간단한 정렬 알고리즘 구현하기'),
                                                                                                  (2, '2025-06-28 19:46:12.193833', 47, 9, '2025-06-28 19:46:12.193833', '람다 표현식을 활용한 간단한 계산기 프로그램 작성하기'),
                                                                                                  (2, '2025-06-28 19:46:22.099285', 48, 9, '2025-06-28 19:46:22.099285', '문자열 뒤집기 및 대소문자 변환 프로그램 작성하기'),
                                                                                                  (2, '2025-06-28 19:47:07.111735', 49, 9, '2025-06-28 19:47:07.111735', '두 개의 스레드를 생성하여 동시에 실행하기'),
                                                                                                  (2, '2025-06-28 19:47:18.919235', 50, 9, '2025-06-28 19:47:18.919235', '추상 클래스와 인터페이스를 활용한 도형 프로그램 작성하기'),
                                                                                                  (2, '2025-06-28 19:47:31.400733', 51, 9, '2025-06-28 19:47:31.400733', '예외 처리가 포함된 계산기 프로그램 작성하기'),
                                                                                                  (2, '2025-06-28 19:47:42.341032', 52, 9, '2025-06-28 19:47:42.341032', '도형 인터페이스 작성하기'),
                                                                                                  (2, '2025-06-28 19:47:53.244540', 53, 9, '2025-06-28 19:47:53.244540', '도형 클래스와 도형 배열 다루기'),
                                                                                                  (2, '2025-06-28 19:48:03.623697', 54, 9, '2025-06-28 19:48:03.623697', '직원 클래스와 관리자 클래스 작성하기'),
                                                                                                  (2, '2025-06-28 19:48:14.848891', 55, 9, '2025-06-28 19:48:14.848891', '직사각형 클래스 작성하기'),
                                                                                                  (2, '2025-06-28 19:48:37.838702', 56, 10, '2025-06-28 19:48:37.838702', 'AOP를 사용한 트랜잭션 관리 구현하기'),
                                                                                                  (2, '2025-06-28 19:48:54.145537', 57, 10, '2025-06-28 19:48:54.145537', '@Primary를 사용하여 기본 빈 설정하기'),
                                                                                                  (2, '2025-06-28 19:49:02.919852', 58, 10, '2025-06-28 19:49:02.919852', 'Qualifier를 사용하여 동일한 타입의 빈 주입 제어하기'),
                                                                                                  (2, '2025-06-28 19:49:11.649945', 59, 10, '2025-06-28 19:49:11.649945', '프로퍼티 파일을 이용한 환경 설정 주입하기'),
                                                                                                  (2, '2025-06-28 19:49:21.451948', 60, 10, '2025-06-28 19:49:21.451948', '빈 라이프사이클 메서드 활용하기'),
                                                                                                  (2, '2025-06-28 19:49:29.986882', 61, 10, '2025-06-28 19:49:29.986882', 'JavaConfig를 사용한 빈 설정'),
                                                                                                  (2, '2025-06-28 19:49:39.647999', 62, 10, '2025-06-28 19:49:39.647999', '인터페이스를 사용하여 의존성 주입하기'),
                                                                                                  (2, '2025-06-28 19:49:48.154107', 63, 10, '2025-06-28 19:49:48.154107', 'AOP를 사용하여 애스펙트 구현하기'),
                                                                                                  (2, '2025-06-28 19:49:58.411056', 64, 10, '2025-06-28 19:49:58.411056', '싱글톤 빈 스코프와 프로토타입 빈 스코프 구현하기'),
                                                                                                  (2, '2025-06-28 19:50:23.320349', 65, 10, '2025-06-28 19:50:23.320349', '애너테이션을 사용하여 빈 주입하기'),
                                                                                                  (2, '2025-06-28 19:50:34.136422', 66, 10, '2025-06-28 19:50:34.136422', '구성 파일에서 정의된 빈 간 관계 구현하기'),
                                                                                                  (2, '2025-06-28 19:50:56.222115', 67, 11, '2025-06-28 19:50:56.222115', 'Notion으로 문서화 작업하기'),
                                                                                                  (2, '2025-06-28 19:51:03.904992', 68, 11, '2025-06-28 19:51:03.904992', 'Trello 보드로 작업 관리하기'),
                                                                                                  (2, '2025-06-28 19:51:10.516866', 69, 11, '2025-06-28 19:51:10.516866', 'GitHub Issues로 프로젝트 이슈 관리하기'),
                                                                                                  (2, '2025-06-28 19:51:38.252596', 70, 12, '2025-06-28 19:51:38.252596', '포트폴리오 템플릿 만들기'),
                                                                                                  (2, '2025-06-28 19:51:50.916368', 71, 12, '2025-06-28 19:51:50.916368', '프로젝트 설명 작성하기'),
                                                                                                  (2, '2025-06-28 19:52:03.168169', 72, 12, '2025-06-28 19:52:03.168169', '자기소개서 작성하기'),
                                                                                                  (2, '2025-06-28 19:52:46.704740', 73, 13, '2025-06-28 19:52:46.704740', '팀 커뮤니케이션 향상 방법 학습하기'),
                                                                                                  (2, '2025-06-28 19:52:56.385164', 74, 13, '2025-06-28 19:52:56.385164', '역할 분담과 책임 정하기'),
                                                                                                  (2, '2025-06-28 19:53:04.008283', 75, 13, '2025-06-28 19:53:04.008283', '협업 도구 설정 및 사용하기'),
                                                                                                  (2, '2025-06-28 19:53:26.336948', 76, 14, '2025-06-28 19:53:26.336948', 'Spring Boot에서 MyBatis와 JPA를 동시에 사용하기'),
                                                                                                  (2, '2025-06-28 19:53:36.608767', 77, 14, '2025-06-28 19:53:36.608767', 'Spring Boot에서 JPA 사용하여 데이터 CRUD 구현하기'),
                                                                                                  (2, '2025-06-28 19:53:44.433069', 78, 14, '2025-06-28 19:53:44.433069', 'Spring Boot에서 MyBatis 사용하여 데이터 CRUD 구현하기'),
                                                                                                  (2, '2025-06-28 19:53:51.297968', 79, 14, '2025-06-28 19:53:51.297968', '데이터베이스 인덱스 사용하기'),
                                                                                                  (2, '2025-06-28 19:53:58.011849', 80, 14, '2025-06-28 19:53:58.011849', '데이터베이스 성능 최적화 방법 적용하기'),
                                                                                                  (2, '2025-06-28 19:54:04.880058', 81, 14, '2025-06-28 19:54:04.880058', '데이터베이스 테이블 설계하기'),
                                                                                                  (2, '2025-06-28 19:54:11.724201', 82, 14, '2025-06-28 19:54:11.724201', 'MariaDB에서 다양한 데이터 유형 사용하기'),
                                                                                                  (2, '2025-06-28 19:54:18.292247', 83, 14, '2025-06-28 19:54:18.292247', 'MariaDB에서 DML 사용하여 데이터 조작하기'),
                                                                                                  (2, '2025-06-28 19:54:25.529955', 84, 14, '2025-06-28 19:54:25.529955', 'MariaDB에서 DDL 사용하여 테이블 생성하기'),
                                                                                                  (1, '2025-06-28 19:55:22.122562', 85, 16, '2025-06-28 19:55:22.122562', '수강 코드 입력하기'),
                                                                                                  (1, '2025-06-28 19:55:30.769576', 86, 16, '2025-06-28 19:55:30.769576', '구름 K-digital 채널 가입하기'),
                                                                                                  (1, '2025-06-28 19:55:49.120202', 87, 17, '2025-06-28 19:55:49.120202', '과연 승자는'),
                                                                                                  (1, '2025-06-28 19:55:55.396621', 88, 17, '2025-06-28 19:55:55.396621', '합리적 소비'),
                                                                                                  (1, '2025-06-28 19:56:03.969134', 89, 17, '2025-06-28 19:56:03.969134', '합 계산기'),
                                                                                                  (1, '2025-06-28 19:56:19.778588', 90, 18, '2025-06-28 19:56:19.778588', '빵야'),
                                                                                                  (1, '2025-06-28 19:56:24.474184', 91, 18, '2025-06-28 19:56:24.474184', '복제로봇'),
                                                                                                  (1, '2025-06-28 19:56:31.424694', 92, 18, '2025-06-28 19:56:31.424694', '나무꾼 구름이'),
                                                                                                  (1, '2025-06-28 19:56:55.403276', 93, 19, '2025-06-28 19:56:55.403276', 'GameJam'),
                                                                                                  (1, '2025-06-28 19:57:01.389695', 94, 19, '2025-06-28 19:57:01.389695', '구름 RPG 1'),
                                                                                                  (1, '2025-06-28 19:57:07.912111', 95, 19, '2025-06-28 19:57:07.912111', '시간 복잡도'),
                                                                                                  (1, '2025-06-28 19:57:24.436847', 96, 20, '2025-06-28 19:57:24.436847', '하노이의 탑'),
                                                                                                  (1, '2025-06-28 19:57:30.373090', 97, 20, '2025-06-28 19:57:30.373090', 'Bigger than you'),
                                                                                                  (1, '2025-06-28 19:57:48.500498', 98, 21, '2025-06-28 19:57:48.500498', '구름 찾기 깃발'),
                                                                                                  (1, '2025-06-28 19:57:53.374683', 99, 21, '2025-06-28 19:57:53.374683', '구름 RPG - 2'),
                                                                                                  (1, '2025-06-28 19:57:58.559755', 100, 21, '2025-06-28 19:57:58.559755', '문자열 나누기'),
                                                                                                  (1, '2025-06-28 19:58:02.678297', 101, 21, '2025-06-28 19:58:02.678297', '판다는 귀여워'),
                                                                                                  (1, '2025-06-28 19:58:17.456638', 102, 22, '2025-06-28 19:58:17.456638', '섭외하기 대작전'),
                                                                                                  (1, '2025-06-28 19:58:22.251310', 103, 22, '2025-06-28 19:58:22.251310', '가장 가까운 점 찾기'),
                                                                                                  (1, '2025-06-28 19:58:26.367794', 104, 22, '2025-06-28 19:58:26.367794', '게임 마스터'),
                                                                                                  (1, '2025-06-28 19:58:30.543463', 105, 22, '2025-06-28 19:58:30.543463', '방 탈출하기'),
                                                                                                  (1, '2025-06-28 19:58:51.775913', 106, 23, '2025-06-28 19:58:51.775913', '연합'),
                                                                                                  (1, '2025-06-28 19:58:54.394243', 107, 23, '2025-06-28 19:58:54.394243', '가뭄'),
                                                                                                  (1, '2025-06-28 19:58:57.406310', 108, 23, '2025-06-28 19:58:57.406310', '폭탄 해제'),
                                                                                                  (1, '2025-06-28 19:59:14.622565', 109, 24, '2025-06-28 19:59:14.622565', '택시기사 구름이'),
                                                                                                  (1, '2025-06-28 19:59:17.799410', 110, 24, '2025-06-28 19:59:17.799410', '세계 여행'),
                                                                                                  (1, '2025-06-28 19:59:20.714337', 111, 24, '2025-06-28 19:59:20.714337', '작은 노드'),
                                                                                                  (1, '2025-06-28 19:59:22.877320', 112, 24, '2025-06-28 19:59:22.877320', '불이야'),
                                                                                                  (1, '2025-06-28 19:59:25.200901', 113, 24, '2025-06-28 19:59:25.200901', '영상처리'),
                                                                                                  (1, '2025-06-28 19:59:35.752324', 114, 25, '2025-06-28 19:59:35.752324', '초코 쿠키'),
                                                                                                  (1, '2025-06-28 19:59:39.485587', 115, 25, '2025-06-28 19:59:39.485587', '미사일 발사'),
                                                                                                  (1, '2025-06-28 19:59:42.470202', 116, 25, '2025-06-28 19:59:42.470202', '보조 배터리'),
                                                                                                  (1, '2025-06-28 19:59:54.956519', 117, 26, '2025-06-28 19:59:54.956519', '피보나치 수열'),
                                                                                                  (1, '2025-06-28 19:59:58.916005', 118, 26, '2025-06-28 19:59:58.916005', '동전 줍기 대회'),
                                                                                                  (1, '2025-06-28 20:00:02.657371', 119, 26, '2025-06-28 20:00:02.657371', '주사위 여행'),
                                                                                                  (1, '2025-06-28 20:00:06.913606', 120, 26, '2025-06-28 20:00:06.913606', '구슬 게임'),
                                                                                                  (1, '2025-06-28 20:00:10.031315', 121, 26, '2025-06-28 20:00:10.031315', '거리 두기'),
                                                                                                  (1, '2025-06-28 20:00:13.932493', 122, 26, '2025-06-28 20:00:13.932493', '학점 예측하기'),
                                                                                                  (1, '2025-06-28 20:00:46.241327', 123, 27, '2025-06-28 20:00:46.241327', '맛집 탐방'),
                                                                                                  (1, '2025-06-28 20:00:50.320351', 124, 27, '2025-06-28 20:00:50.320351', '달집 태우기'),
                                                                                                  (1, '2025-06-28 20:00:55.683435', 125, 27, '2025-06-28 20:00:55.683435', 'Run and Fly'),
                                                                                                  (1, '2025-06-28 20:01:10.470019', 126, 28, '2025-06-28 20:01:10.470019', '도장 만들기'),
                                                                                                  (1, '2025-06-28 20:01:13.938502', 127, 28, '2025-06-28 20:01:13.938502', '구름 랜드'),
                                                                                                  (1, '2025-06-28 20:01:23.880434', 128, 29, '2025-06-28 20:01:23.880434', '잡초 제거'),
                                                                                                  (1, '2025-06-28 20:01:26.761140', 129, 29, '2025-06-28 20:01:26.761140', '미니 텃밭'),
                                                                                                  (1, '2025-06-28 20:01:48.461394', 130, 30, '2025-06-28 20:01:48.461394', '여유 황금비'),
                                                                                                  (1, '2025-06-28 20:01:51.768038', 131, 30, '2025-06-28 20:01:51.768038', '울타리 치기'),
                                                                                                  (1, '2025-06-28 20:01:56.141868', 132, 30, '2025-06-28 20:01:56.141868', '운동 중독 플레이어'),
                                                                                                  (1, '2025-06-28 20:02:11.196919', 133, 31, '2025-06-28 20:02:11.196919', '이진수 정렬'),
                                                                                                  (1, '2025-06-28 20:02:14.628890', 134, 31, '2025-06-28 20:02:14.628890', '해적왕 구름이'),
                                                                                                  (1, '2025-06-28 20:02:26.898826', 135, 32, '2025-06-28 20:02:26.898826', '묶음 상품'),
                                                                                                  (1, '2025-06-28 20:02:29.931494', 136, 32, '2025-06-28 20:02:29.931494', '아이템 교환'),
                                                                                                  (1, '2025-06-28 20:02:32.984110', 137, 32, '2025-06-28 20:02:32.984110', '재고 정리'),
                                                                                                  (1, '2025-06-28 20:02:37.595434', 138, 32, '2025-06-28 20:02:37.595434', '뒤통수가 따가워'),
                                                                                                  (1, '2025-06-28 20:02:59.410494', 139, 33, '2025-06-28 20:02:59.410494', '이동 비용 구하기'),
                                                                                                  (1, '2025-06-28 20:03:04.559992', 140, 33, '2025-06-28 20:03:04.559992', '단체 이동'),
                                                                                                  (1, '2025-06-28 20:03:07.654199', 141, 33, '2025-06-28 20:03:07.654199', '신호 전달'),
                                                                                                  (1, '2025-06-28 20:03:17.144848', 142, 34, '2025-06-28 20:03:17.144848', '친구 사이'),
                                                                                                  (1, '2025-06-28 20:03:19.903720', 143, 34, '2025-06-28 20:03:19.903720', '절친');

-- 이건 나중에 삭제해야 함
INSERT INTO quest (name, url, is_deleted, type) VALUES
                                                    ('출석체크 하기', '/dashboard', false, 'ATTENDANCE'),
                                                    ('일일 퀘스트 완료하기', '/quest', false, 'QUEST_CLEAR'),
                                                    ('미션 리워드 받기', '/mission', false, 'MISSION_REWARD'),
                                                    ('배움일기 작성하기', '/diary', false, 'WRITE_DIARY'),
                                                    ('칭찬 댓글달기', '/comment', false, 'PRAISE_COMMENT');
-- 상품 더미 데이터
INSERT INTO product_image (id, uuid, extension, path)
VALUES
    (1, 'ed', 'webp', '/application/product-images'),
    (2, 'pb', 'webp', '/application/product-images'),
    (3, 'cu', 'webp', '/application/product-images');

INSERT INTO product (is_deleted, price, quantity, product_image_id, brand, name)
VALUES
    (false, 147, 30, 1, '이디야 커피', '(R)딸기 요거트 플랫치노'),
    (false, 157, 30, 2, '파리바게트', '파리바게트 교환권 5,000원'),
    (false, 157, 30, 3, 'CU', 'CU 모바일 상품권 5천원권');


-- 퀘스트 더미 데이터
INSERT INTO user_quest (is_completable, is_completed, quest_id, user_id)
VALUES
-- 유저 1
(false, false, 1, 1),
(false, false, 2, 1),
(false, false, 3, 1),
(false, false, 4, 1),
(false, false, 5, 1),

-- 유저 2
(false, false, 1, 2),
(false, false, 2, 2),
(false, false, 3, 2),
(false, false, 4, 2),
(false, false, 5, 2),

-- 유저 3
(false, false, 1, 3),
(false, false, 2, 3),
(false, false, 3, 3),
(false, false, 4, 3),
(false, false, 5, 3),

-- 유저 4
(false, false, 1, 4),
(false, false, 2, 4),
(false, false, 3, 4),
(false, false, 4, 4),
(false, false, 5, 4),

-- 유저 5
(false, false, 1, 5),
(false, false, 2, 5),
(false, false, 3, 5),
(false, false, 4, 5),
(false, false, 5, 5);
