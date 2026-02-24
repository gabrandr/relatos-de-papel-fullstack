import BookCard from "../features/books/BookCard";
import { books } from "../data/books.mock";
import { useSearchParams } from "react-router-dom";

/**
 * Página de inicio con catálogo de libros y búsqueda.
 */
const HomePage = () => {
  const [searchParams] = useSearchParams(); //hook para obtener los query params
  const search = searchParams.get("search") || ""; //obtiene search del query param
  //filtro de libros por titulo
  const filteredBooks = books.filter((book) =>
    book.title.toLowerCase().includes(search.toLowerCase())
  );
  return (
    <div className="max-w-6xl mx-auto px-6 py-8">
      <h1 className="text-3xl font-bold text-text-main mb-6">
        Catálogo de Libros
      </h1>

      {/*grid de libros*/}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {filteredBooks.map((book) => (
          <BookCard key={book.id} book={book} />
        ))}
      </div>
    </div>
  );
};

export default HomePage;
