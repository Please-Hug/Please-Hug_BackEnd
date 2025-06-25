-- 개발 테스트용 MySQL 도커 컨테이너 쿼리(유출되도 상관 X)
ALTER USER 'goorm_exp'@'%' IDENTIFIED BY '00000000';
GRANT ALL PRIVILEGES ON goorm_exp.* TO 'goorm_exp'@'%';
FLUSH PRIVILEGES;
