import { useCartStore } from "../../store/useCartStore";

/**
 * Item del carrito con controles de cantidad y eliminar.
 */
const CartItem = ({ item }) => {
  const addToCart = useCartStore((state) => state.addToCart);
  const decreaseQuantity = useCartStore((state) => state.decreaseQuantity);
  const removeFromCart = useCartStore((state) => state.removeFromCart);

  return (
    <div className="grid grid-cols-1 sm:grid-cols-[2fr_1fr_1fr] gap-4 sm:gap-0 items-center border-b border-border py-6 last:border-0 hover:bg-background transition-colors px-2 relative">
      {/* Columna Item: Imagen + Título */}
      <div className="flex items-center gap-4 sm:gap-6">
        <img
          src={item.image}
          alt={item.title}
          className="w-20 h-28 object-cover rounded-md shadow-sm border border-border"
        />
        <div>
          <h3 className="font-bold text-text-main uppercase text-sm tracking-wide mb-1">
            {item.title}
          </h3>
          <p className="text-xs text-text-muted line-clamp-2 max-w-55 leading-relaxed hidden sm:block">
            {item.description}
          </p>
          {/* Precio visible aquí solo en móvil */}
          <p className="text-primary font-bold sm:hidden mt-2">
            ${(item.price * item.quantity).toFixed(2)}
          </p>
        </div>
      </div>

      {/* Columna Cantidad: Botones y numero */}
      <div className="flex items-center justify-start sm:justify-center gap-4 mt-2 sm:mt-0">
        <span className="text-sm text-text-muted sm:hidden">Cant:</span>
        <button
          onClick={() => decreaseQuantity(item.id)}
          className="w-8 h-8 flex items-center justify-center bg-background text-text-body rounded-full hover:bg-primary hover:text-white transition-all shadow-sm"
        >
          -
        </button>
        <span className="text-lg font-semibold w-8 text-center text-text-main">
          {item.quantity}
        </span>
        <button
          onClick={() => addToCart(item)}
          className="w-8 h-8 flex items-center justify-center bg-primary text-white rounded-full hover:bg-primary-dark transition-all shadow-sm shadow-primary/30"
        >
          +
        </button>
      </div>

      {/* Columna Eliminar + Precio (Desktop) */}
      <div className="flex items-center justify-between sm:justify-end gap-8 mt-2 sm:mt-0 absolute top-4 right-4 sm:static">
        <button
          onClick={() => removeFromCart(item.id)}
          className="text-text-muted hover:text-error transition-colors p-2 hover:bg-error/10 rounded-full"
          title="Eliminar del carrito"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            strokeWidth={1.5}
            stroke="currentColor"
            className="w-5 h-5"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              d="m14.74 9-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 0 1-2.244 2.077H8.084a2.25 2.25 0 0 1-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 0 0-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 0 1 3.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 0 0-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 0 0-7.5 0"
            />
          </svg>
        </button>
        <span className="text-lg font-bold min-w-22.5 text-right text-text-main hidden sm:block">
          ${(item.price * item.quantity).toFixed(2)}
        </span>
      </div>
    </div>
  );
};

export default CartItem;
