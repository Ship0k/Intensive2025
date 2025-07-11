--Этот sql для автоматического заполнения при запуске базы в докере
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    age INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

INSERT INTO users (name, email, age)
VALUES
    ('Артем', 'artem01@mail.com', 24),
    ('Виктория', 'viktoria88@mail.com', 32),
    ('Дмитрий', 'dmitry_22@mail.com', 28),
    ('Елена', 'elena1990@mail.com', 30),
    ('Никита', 'nikita.dev@mail.com', 26),
    ('София', 'sofia_star@mail.com', 27),
    ('Игорь', 'igor.tech@mail.com', 35),
    ('Мария', 'masha_2025@mail.com', 23),
    ('Олег', 'oleg.unit@mail.com', 31),
    ('Толя', 'tolik@mail.com', 33)
    ON CONFLICT (email) DO NOTHING;