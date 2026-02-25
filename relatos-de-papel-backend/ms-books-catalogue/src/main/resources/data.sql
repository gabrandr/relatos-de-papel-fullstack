-- ===================================================
-- DATA.SQL - MS BOOKS CATALOGUE
-- Datos de prueba para el catálogo de libros
-- ===================================================

-- Libro 1: Clásico español
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date) 
VALUES ('Don Quijote de la Mancha', 'Miguel de Cervantes', 'Clásicos', '9788484030300', 19.99, 15, 5, true, '1605-01-16');

-- Libro 2: Clásico colombiano
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date) 
VALUES ('Cien Años de Soledad', 'Gabriel García Márquez', 'Clásicos', '9788497592208', 24.99, 8, 5, true, '1967-05-30');

-- Libro 3: Ficción contemporánea
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date) 
VALUES ('1984', 'George Orwell', 'Ficción', '9788499890944', 18.50, 12, 5, true, '1949-06-08');

-- Libro 4: Romance
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date) 
VALUES ('Orgullo y Prejuicio', 'Jane Austen', 'Romance', '9788469833346', 16.99, 20, 4, true, '1813-01-28');

-- Libro 5: Fantasía (Stock bajo)
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date) 
VALUES ('El Señor de los Anillos', 'J.R.R. Tolkien', 'Fantasía', '9780261102385', 35.00, 3, 5, true, '1954-07-29');

-- Libro 6: Ciencia Ficción
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date) 
VALUES ('Dune', 'Frank Herbert', 'Ciencia Ficción', '9780441172719', 28.99, 10, 5, true, '1965-08-01');

-- Libro 7: Clásico (No visible - para probar filtro)
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date) 
VALUES ('Moby Dick', 'Herman Melville', 'Clásicos', '9780142437247', 22.00, 5, 4, false, '1851-10-18');

-- Libro 8: Ficción (Precio bajo)
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date) 
VALUES ('El Principito', 'Antoine de Saint-Exupéry', 'Ficción', '9788498381498', 9.99, 25, 5, true, '1943-04-06');

-- Libro 9: Distopía
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date)
VALUES ('Fahrenheit 451', 'Ray Bradbury', 'Ciencia Ficción', '9781451673319', 17.99, 14, 5, true, '1953-10-19');

-- Libro 10: Clásico estadounidense
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date)
VALUES ('The Great Gatsby', 'F. Scott Fitzgerald', 'Clásicos', '9780743273565', 15.99, 18, 5, true, '1925-04-10');

-- Libro 11: Clásico contemporáneo
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date)
VALUES ('To Kill a Mockingbird', 'Harper Lee', 'Clásicos', '9780061120084', 18.99, 12, 5, true, '1960-07-11');

-- Libro 12: Distopía
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date)
VALUES ('Brave New World', 'Aldous Huxley', 'Ciencia Ficción', '9780060850524', 16.99, 13, 5, true, '1932-01-01');

-- Libro 13: Clásico ruso
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date)
VALUES ('Crime and Punishment', 'Fyodor Dostoevsky', 'Clásicos', '9780143058144', 19.99, 9, 5, true, '1866-01-01');

-- Libro 14: Clásico ruso
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date)
VALUES ('Anna Karenina', 'Leo Tolstoy', 'Clásicos', '9780143035008', 20.99, 10, 5, true, '1878-01-01');

-- Libro 15: Clásico moderno
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date)
VALUES ('The Catcher in the Rye', 'J.D. Salinger', 'Ficción', '9780316769488', 17.49, 15, 4, true, '1951-07-16');

-- Libro 16: Fantasía
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date)
VALUES ('The Hobbit', 'J.R.R. Tolkien', 'Fantasía', '9780547928227', 21.99, 11, 5, true, '1937-09-21');

-- Libro 17: Ficción contemporánea
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date)
VALUES ('The Alchemist', 'Paulo Coelho', 'Ficción', '9780061122415', 16.49, 16, 5, true, '1988-01-01');

-- Libro 18: Misterio histórico
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date)
VALUES ('The Name of the Rose', 'Umberto Eco', 'Misterio', '9780156001311', 18.49, 8, 5, true, '1980-01-01');

-- Libro 19: Misterio
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date)
VALUES ('The Shadow of the Wind', 'Carlos Ruiz Zafón', 'Misterio', '9780143034902', 19.49, 12, 5, true, '2001-01-01');

-- Libro 20: Thriller
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date)
VALUES ('The Girl with the Dragon Tattoo', 'Stieg Larsson', 'Thriller', '9780307454546', 18.99, 10, 5, true, '2005-01-01');

-- Libro 21: No ficción
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date)
VALUES ('Sapiens A brief Story of Human Kind', 'Yuval Noah Harari', 'No Ficción', '9780062316097', 22.99, 14, 5, true, '2011-01-01');

-- Libro 22: Clásico breve
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date)
VALUES ('The metamorphosis', 'Franz Kafka', 'Clásicos', '9780553213690', 12.99, 19, 5, true, '1915-01-01');

-- Libro 23: Clásico épico
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date)
VALUES ('War And Peace', 'Leo Tolstoy', 'Clásicos', '9780199232765', 24.99, 7, 5, true, '1869-01-01');

-- Libro 24: Terror clásico
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date)
VALUES ('Dracula', 'Bram Stoker', 'Terror', '9780486411095', 14.99, 17, 5, true, '1897-01-01');

-- Libro 25: Terror clásico
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date)
VALUES ('Frankenstein', 'Mary Shelley', 'Terror', '9780486282114', 13.99, 15, 5, true, '1818-01-01');

-- Libro 26: Clásico gótico
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date)
VALUES ('The Picture of Dorian Gray', 'Oscar Wilde', 'Clásicos', '9780141439570', 15.49, 13, 5, true, '1890-01-01');

-- Libro 27: Clásico ruso
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date)
VALUES ('The Brothers Karamazov', 'Fyodor Dostoevsky', 'Clásicos', '9780374528379', 23.99, 8, 5, true, '1880-01-01');

-- Libro 28: Clásico francés
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date)
VALUES ('Notre-Dame of Paris', 'Victor Hugo', 'Clásicos', '9780140443530', 17.99, 9, 4, true, '1831-01-01');
