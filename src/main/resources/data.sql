insert into "member" (member_id, username, password, social_type, name, nickname, email, permission, profile_pic_url, count_attempt, unban_date, created_at, updated_at) values
	(1, 'super', '$2b$12$r.c/Bx9E5G8U8dSrHa4my.QzPd5bSFyHi.2O2N1ZDMzLi5tSVW8eK', 'NORMAL', '송은주', '최상위', 'dgim@example.com', 'SUPER', null, 0, current_date, now(), now()),
	(2, 'admin1', '$2b$12$6m3VRnYCF1BCtVTspxycQeMOmr6XgGVTqaJnFDGehuDtjyN0ePFK.', 'NORMAL', '주민재', '관리자1', 'vi@example.net', 'ADMIN', null, 0, current_date, now(), now()),
	(3, 'admin2', '$2b$12$klhAP7zOjLxfkYzfXsyjRugrgVZ7cbntNxCEjK9RZaWAeKvK41xN2', 'NORMAL', '김채원', '관리자2', 'sunja20@example.com', 'ADMIN', null, 0, current_date, now(), now()),
	(4, 'admin3', '$2b$12$o939sGpqrDgbKX1yngmLtOrIpUgUdVcJwmsPmocOCYMYSpbg7HnPu', 'NORMAL', '최민서', '관리자3', 'ggim@example.com', 'ADMIN', null, 0, current_date, now(), now()),
	(5, 'member1', '$2b$12$2Dld5JKitrSqn45rTMLJ3.W5bc4L57SWahHbcE5n99tG5NAjsJzdG', 'NORMAL', '김중수',  '회원1', 'baggyeonghyi@example.org', 'MEMBER', null, 0, current_date, now(), now()),
	(6, 'member2', '$2b$12$umtmlcBUaSVCcKlPumn7iOm8ez9izvIywc2JY3OpeCVe2LKILQyq.', 'NORMAL', '박은정',  '회원2', 'ojuweon@example.org', 'MEMBER', 'https://culturabcs.gob.mx/defaults/profile.png', 0, current_date, now(), now()),
	(7, 'member3', '$2b$12$xS3vPyzyn8Ke2.aCSSCy9OhI1qRwZVqWmDzZMMiS5k3uXsjTNzVY2', 'NORMAL', '김현준',  '회원3', 'dgim@example.org', 'MEMBER', 'https://culturabcs.gob.mx/defaults/profile.png', 0, current_date, now(), now()),
	(8, 'member4', '$2b$12$jW7q/UIHGbjUpQOMmMILkOY3QFPr/TUPiaE2EK1iseEw8qanRo.Nm', 'NORMAL', '김민석',  '회원4', 'tbag@example.com', 'MEMBER', null, 0, current_date, now(), now()),
	(9, 'member5', '$2b$12$4TRAMzCm3RUnhV0bZzZ94e5N84XbjhKYx/8jqMNyHR1P4CYFjwbEi', 'NORMAL', '손옥순',  '회원5', 'gyeongjai@example.com', 'MEMBER', 'https://culturabcs.gob.mx/defaults/profile.png', 0, current_date, now(), now()),
    (10, 'member6', '$2b$12$4TRAMzCm3RUnhV0bZzZ94e5N84XbjhKYx/8jqMNyHR1P4CYFjwbEi', 'NORMAL', '백지선',  '회원6', 'jiseon@example.com', 'MEMBER', 'https://culturabcs.gob.mx/defaults/profile.png', 0, current_date, now(), now());

insert into "follow" (follow_id, following, followed, created_at, updated_at) values
	(10, 5, 6, now(), now()),
	(11, 5, 7, now(), now()),
	(12, 5, 8, now(), now()),
	(13, 6, 5, now(), now()),
	(14, 6, 7, now(), now()),
	(15, 7, 6, now(), now()),
	(16, 8, 6, now(), now()),
	(17, 9, 5, now(), now());

