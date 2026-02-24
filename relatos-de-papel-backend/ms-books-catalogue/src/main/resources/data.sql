-- ===================================================
-- DATA.SQL - MS BOOKS CATALOGUE
-- Datos de prueba para el catálogo de libros
-- ===================================================

-- Libro 1: Clásico español
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date) 
VALUES ('Don Quijote de la Mancha', 'Miguel de Cervantes', 'Clásicos', '9788467033601', 19.99, 15, 5, true, '1605-01-16');

-- Libro 2: Clásico colombiano
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date) 
VALUES ('Cien Años de Soledad', 'Gabriel García Márquez', 'Clásicos', '9788497592208', 24.99, 8, 5, true, '1967-05-30');

-- Libro 3: Ficción contemporánea
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date) 
VALUES ('1984', 'George Orwell', 'Ficción', '9788499890944', 18.50, 12, 5, true, '1949-06-08');

-- Libro 4: Romance
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date) 
VALUES ('Orgullo y Prejuicio', 'Jane Austen', 'Romance', '9788491050407', 16.99, 20, 4, true, '1813-01-28');

-- Libro 5: Fantasía (Stock bajo)
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date) 
VALUES ('El Señor de los Anillos', 'J.R.R. Tolkien', 'Fantasía', '9788445077528', 35.00, 3, 5, true, '1954-07-29');

-- Libro 6: Ciencia Ficción
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date) 
VALUES ('Dune', 'Frank Herbert', 'Ciencia Ficción', '9788497593786', 28.99, 10, 5, true, '1965-08-01');

-- Libro 7: Clásico (No visible - para probar filtro)
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date) 
VALUES ('Moby Dick', 'Herman Melville', 'Clásicos', '9788490019238', 22.00, 5, 4, false, '1851-10-18');

-- Libro 8: Ficción (Precio bajo)
INSERT INTO books (title, author, category, isbn, price, stock, rating, visible, publication_date) 
VALUES ('El Principito', 'Antoine de Saint-Exupéry', 'Ficción', '9788498381498', 9.99, 25, 5, true, '1943-04-06');