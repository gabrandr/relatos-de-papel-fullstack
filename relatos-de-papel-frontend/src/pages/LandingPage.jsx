import { useNavigate } from "react-router-dom";
import useCountdown from "../hooks/useCountdown";
import logo from "../assets/logo.png";

/**
 * Pantalla de bienvenida con countdown de 5 segundos.
 */
const LandingPage = () => {
  const navigate = useNavigate();
  const countdown = useCountdown(5, () => navigate("/home")); //custom hook redirige a /home en 5 segundos

  return (
    <div className="min-h-screen bg-background flex items-center justify-center">
      <div className="text-center">
        <img
          src={logo}
          alt="Relatos de Papel"
          className="w-64 md:w-80 lg:w-96 mx-auto mb-6"
        />
        <h1 className="text-5xl font-bold mb-4 text-primary-dark">
          Relatos de Papel
        </h1>
        <p className="text-xl mb-8 text-text-body">
          Tu librería online favorita
        </p>
        <button
          onClick={() => navigate("/home")}
          className="bg-primary text-white px-6 py-3 rounded-lg font-bold hover:bg-primary-dark transition"
        >
          Entrar ahora
        </button>
        <p className="text-lg text-text-muted mt-6">
          Redirigiendo en {countdown}...
        </p>
      </div>
    </div>
  );
};

export default LandingPage;
