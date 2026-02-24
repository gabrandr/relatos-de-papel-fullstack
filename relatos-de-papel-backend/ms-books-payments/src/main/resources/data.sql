-- Pagos de ejemplo para pruebas
-- 1. Pago COMPLETADO (Usuario 1, Libro 1)
INSERT INTO payments (user_id, book_id, book_title, book_isbn, quantity, unit_price, total_price, purchase_date, status)
VALUES (1, 1, 'El Quijote', '9788467033601', 1, 19.99, 19.99, '2024-01-15T10:00:00', 'COMPLETED');
-- 2. Pago COMPLETADO (Usuario 1, Libro 2)
INSERT INTO payments (user_id, book_id, book_title, book_isbn, quantity, unit_price, total_price, purchase_date, status)
VALUES (1, 2, 'Cien Años de Soledad', '9780307474728', 2, 25.50, 51.00, '2024-01-16T15:30:00', 'COMPLETED');
-- 3. Pago PENDING (Usuario 2, Libro 1) - Para probar cambio de estado
INSERT INTO payments (user_id, book_id, book_title, book_isbn, quantity, unit_price, total_price, purchase_date, status)
VALUES (2, 1, 'El Quijote', '9788467033601', 1, 19.99, 19.99, '2024-02-01T09:00:00', 'PENDING');
-- 4. Pago CANCELLED (Usuario 3, Libro 3) - Para probar filtros
INSERT INTO payments (user_id, book_id, book_title, book_isbn, quantity, unit_price, total_price, purchase_date, status)
VALUES (3, 3, 'El Principito', '9780156012195', 3, 10.00, 30.00, '2024-01-20T11:00:00', 'CANCELLED');