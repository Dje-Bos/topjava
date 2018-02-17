DELETE FROM user_roles;
DELETE FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password');

INSERT INTO users (name, email, password)
VALUES ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id) VALUES
  ('ROLE_USER', 100000),
  ('ROLE_ADMIN', 100001);
INSERT INTO meals (user_id, calories, description, datetime) VALUES
  (100001, 200, 'breakfast', '2018-06-12 06:45'),
  (100001, 450, 'supper', '2018-06-12 18:11'),
  (100001, 320, 'lunch', '2018-06-12 11:38'),
  (100001, 780, 'lunch', '2018-06-13 10:59'),
  (100000, 540, 'supper', '2018-06-12 12:58'),
  (100000, 1200, 'dinner', '2018-06-12 12:59');