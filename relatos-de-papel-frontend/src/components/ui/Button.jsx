/**
 * Componente de botón reutilizable.
 * Acepta texto, clases de diseño y función onClick.
 */
const Button = ({ onClick, className, children, ...props }) => {
  return (
    <button onClick={onClick} className={className} {...props}>
      {children}
    </button>
  );
};

export default Button;
