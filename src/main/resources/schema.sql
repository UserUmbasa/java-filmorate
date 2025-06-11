-- создаем базу данных
CREATE DATABASE filmorate_repository;
-- подключились к ней через настройки
-- создаем схему фильмотеки
CREATE SCHEMA filmorate;
--Таблица возрастных рейтингов (age_ratings)
CREATE TABLE age_ratings (
                             rating_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                             rating_name VARCHAR(50) NOT NULL UNIQUE,
                             description TEXT
);
-- Добавляем возрастные рейтинги
INSERT INTO age_ratings (rating_name, description)
VALUES
    ('G', 'Нет возрастных ограничений'),
    ('PG', 'Рекомендуется присутствие родителей'),
    ('PG-13', 'Детям до 13 лет просмотр не желателен'),
    ('R', 'Лицам до 17 лет обязательно присутствие взрослого'),
    ('NC-17', 'Лицам до 18 лет просмотр запрещен');
SELECT * FROM age_ratings; -- вывод

-- создаем таблицу фильм
CREATE TABLE films(
                      film_id BIGINT PRIMARY KEY,
                      name VARCHAR(255) NOT NULL CHECK (name <> ''),
                      description VARCHAR(200) NOT NULL,
                      CHECK (LENGTH(TRIM(description)) >= 1),
                      release_date DATE NOT NULL,
                      CHECK (release_date >= '1895-12-28'),
                      duration BIGINT NOT NULL
                          CHECK (duration > 0),
                      rating_id BIGINT ,
                      FOREIGN KEY (rating_id) REFERENCES age_ratings(rating_id)
);
INSERT INTO films (film_id, name, description, release_date, duration, rating_id)
VALUES
    (1, 'Интерстеллар',
     'Научно-фантастический фильм о путешествии через червоточины.',
     '2014-11-05', 169, 3),

    (2, 'Побег из Шоушенка',
     'История о том, как несправедливо осужденный банкир Энди Дюфрейн',
     '1994-09-10', 142, 5),

    (3, 'Форрест Гамп',
     'История о простом человеке, который становится свидетелем важнейших событий в истории США.',
     '1994-06-23', 142, 3),

    (4, 'Мстители: Финал',
     'Финальная битва земных и космических супергероев.',
     '2019-04-24', 181,4),

    (5, 'Джокер',
     'История о превращении Артура Флека в главного злодея Готэма.',
     '2019-10-04', 122, 5);
SELECT * FROM films; -- вывод
SELECT *
FROM films
         JOIN age_ratings ar on films.rating_id = ar.rating_id;


-- Таблица жанров
CREATE TABLE genres (
                        genre_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                        genre_name VARCHAR(100) NOT NULL UNIQUE,
                        description TEXT
);
-- Связующая таблица
CREATE TABLE movie_genres (
                              film_id BIGINT,
                              genre_id BIGINT,
                              PRIMARY KEY (film_id, genre_id),
                              FOREIGN KEY (film_id) REFERENCES films(film_id),
                              FOREIGN KEY (genre_id) REFERENCES genres(genre_id)
);
-- Добавляем жанры
INSERT INTO genres (genre_name, description)
VALUES
    ('Комедия', 'Юмористическое кино'),
    ('Драма', 'Серьезное кино'),
    ('Фантастика', 'Научно-фантастические фильмы'),
    ('Боевик', 'Фильмы с экшеном'),
    ('Триллер', 'Напряженные фильмы');
SELECT * FROM genres;
-- Добавляем связи для фильма
INSERT INTO movie_genres (film_id, genre_id)
VALUES
    (1, 1), -- Фильм 1 имеет жанр Комедия
    (1, 3), -- Фильм 1 также имеет жанр Фантастика
    (2, 2), -- Фильм 2 имеет жанр Драма
    (3, 4), -- Фильм 3 имеет жанр Боевик
    (3, 5); -- Фильм 3 также имеет жанр Триллер
SELECT * FROM movie_genres;

-- создаем таблицу юзер
CREATE TABLE users (
                       user_id BIGINT PRIMARY KEY,
                       email  VARCHAR(255) NOT NULL,
    --CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$'),
                       login VARCHAR(255) NOT NULL,
    --CHECK (login ~* '^\\S+$'),
                       name VARCHAR(255),
                       birthday DATE NOT NULL,
                       CHECK (birthday <= CURRENT_DATE)
);
INSERT INTO users (user_id, email, login, name, birthday)
VALUES
    (1, 'smith@example.com', 'anna_s', 'Анна', '1995-03-15'),
    (2, 'doe@gmail.com', 'john_d', 'Джон', '1987-11-22'),
    (3, 'petrova@mail.ru', 'maria_p', 'Мария', '2000-07-08'),
    (4, 'king@outlook.com', 'alex_k', 'Алекс', '1992-09-04'),
    (5, 'ivanov@yahoo.com', 'olga_i', 'Ольга', '1998-01-10');
SELECT * FROM users;
-- Таблица лайков
CREATE TABLE film_likes (
                            user_id BIGINT,
                            film_id BIGINT,
                            PRIMARY KEY (user_id, film_id),
                            FOREIGN KEY (user_id) REFERENCES users(user_id),
                            FOREIGN KEY (film_id) REFERENCES films(film_id)
);

