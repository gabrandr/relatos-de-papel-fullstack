/**
 * Pie de página con derechos de autor.
 */
const Footer = () => {
  return (
    <footer className="bg-white border-t border-border px-6 py-6">
      <div className="max-w-6xl mx-auto text-center">
        <p className="text-text-muted text-sm">
          &copy; {new Date().getFullYear()} Relatos de Papel. Todos los derechos
          reservados.
        </p>
      </div>
    </footer>
  );
};

export default Footer;
