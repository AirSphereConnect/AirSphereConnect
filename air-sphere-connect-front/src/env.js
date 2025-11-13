// Fichier de fallback pour le développement local (sans Docker)
// En production Docker, ce fichier est généré dynamiquement par envsubst
(function(window) {
  window.__env = window.__env || {};
  // Fallback dev: pointe vers localhost
  window.__env.apiUrl = "http://localhost:8080/api";
}(this));
