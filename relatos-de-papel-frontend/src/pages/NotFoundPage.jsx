import { Link } from "react-router-dom";

/**
 * Página 404 para rutas no encontradas.
 */
const NotFoundPage = () => {
  return (
    <div className="max-w-6xl mx-auto px-6 py-16 text-center">
      <h1 className="text-6xl font-bold text-primary mb-4">404</h1>
      <h2 className="text-2xl font-bold mb-4">Página no encontrada</h2>
      <p className="text-text-muted mb-6">
        La página que buscas no existe o fue movida.
      </p>
      <Link
        to="/home"
        className="inline-block bg-primary text-white px-6 py-3 rounded-lg font-bold hover:bg-primary-dark"
      >
        Volver al inicio
      </Link>
    </div>
  );
};

export default NotFoundPage;
