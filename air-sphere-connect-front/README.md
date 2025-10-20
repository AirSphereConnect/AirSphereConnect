# AirSphereConnectFront

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 20.3.1.

## Development server

To start a local development server, run:

```bash
ng serve
```

Once the server is running, open your browser and navigate to `http://localhost:4200/`. The application will automatically reload whenever you modify any of the source files.

## Code scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
ng generate component component-name
```

For a complete list of available schematics (such as `components`, `directives`, or `pipes`), run:

```bash
ng generate --help
```

## Building

To build the project run:

```bash
ng build
```

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

## Running unit tests

To execute unit tests with the [Karma](https://karma-runner.github.io) test runner, use the following command:

```bash
ng test
```

## Running end-to-end tests

For end-to-end (e2e) testing, run:

```bash
ng e2e
```

Angular CLI does not come with an end-to-end testing framework by default. You can choose one that suits your needs.

## Additional Resources

For more information on using the Angular CLI, including detailed command references, visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.




Tous les projets
Air Sphere Connect
🌍 Résumé de l’application (projet Occitanie) 🎯 Objectif Une application web permettant aux utilisateurs de visualiser, comparer et analyser les données environnementales (qualité de l’air, météo) et démographiques (population) de la région Occitanie. 👉 L’idée est de rendre les données pédagogiques, interactives et exploitables, avec une partie publique et une partie privée (authentifiée). 👥 Types d’utilisateurs Visiteur (non connecté) Consultation de cartes et données publiques (population, météo, qualité de l’air). Navigation dans le forum en lecture seule. Utilisateur connecté (User) Création d’un profil, gestion de ses favoris (communes, indicateurs). Réception et gestion de notifications personnalisées (alertes pollution, météo, etc.). Participation au forum (sujets, réponses, likes/dislikes). Administrateur (Admin) Accès au dashboard de gestion (datasets, utilisateurs, notifications globales). Import/traitement de données (CSV, API, GeoJSON). Supervision de l’activité (statistiques, historique). 🗂️ Pages principales Accueil Présentation et accès rapide aux principales fonctionnalités. Carte de l’Occitanie avec un aperçu (population, météo, qualité de l’air). Données environnementales Carte Leaflet de l’Occitanie. Filtres (polluants : PM10, PM2.5, NO2, O3, etc. ; météo : température, humidité, vent). Dropdown pour sélectionner ville, période, type de mesure. Graphiques (Unovis) sous la carte (par ex. bar chart AQI, line chart température). Population Carte avec densité de population par commune. Graphiques d’évolution temporelle (Population par an). Forum Rubriques, fils de discussion, posts avec likes/dislikes. Modal Angular Material pour répondre aux messages. Notifications Liste des notifications reçues (tableau avec filtre : type, date, statut lu/non-lu). Gestion (marquer comme lu, supprimer). Possibilité de créer ses propres alertes (ou via favoris). Historique des mesures Tableau avec filtres avancés (commune, période, type de mesure). Export CSV / PDF. Dashboard admin Import/export datasets (Air, Météo, Population). Gestion des utilisateurs et rôles. Gestion des notifications globales. Statistiques et graphiques de synthèse. 🛠️ Stack technique Frontend : Angular + TailwindCSS (charte graphique nature 🌱 : verts, bleus, terre cuite, lime). Backend : Spring Boot (API REST, sécurité, JPA). Base de données : MariaDB Cartographie : Leaflet (fond de carte + couches GeoJSON). Visualisation : Unovis (graphiques interactifs). Infrastructure : Docker (containers DB + API + frontend). 📊 Données principales Population : via Data Région Occitanie (annuelles, par commune). Qualité de l’air : via API Atmo Occitanie (temps réel ou différé, polluants). Météo : API open-data ou Météo-France. Référentiel géographique : Régions → Départements → Communes (coordonnées + INSEE). 👉 En résumé, tu conçois une application full-stack interactive : cartographique (Leaflet), analytique (graphiques Unovis), collaborative (forum, notifications), multi-utilisateurs (user/admin, rôles).

est ce que tu peux me


AirSphere Database Structure Design
Dernier message il y a 45 minutes
AirSphereConnect App Review
Dernier message il y a 59 minutes
Instructions
Ajouter des instructions pour personnaliser les réponses de Claude.

Fichiers
2 % de la capacité du projet utilisée
Proposition charte graphique
DOC


Mise en place DB + Docker + JPA
DOC



Structure Spring Boot MVC - Bonnes Pratiques.md
394 lignes

md



Structure Projet Minimale - AirSphereConnect.md
319 lignes

md



Structure Angular MVC - AirSphereConnect Frontend.md
560 lignes

md



ModÃ¨le de dossier de spÃ©cifications gÃ©nÃ©rales.docx
722 lignes

docx



ModÃ¨le de dossier de conception.docx
235 lignes

docx



specification_donnees.docx
44 lignes

docx



specification_favoris.docx
68 lignes

docx



specification_forum.docx
95 lignes

docx



specification_gestion_utiilisateur.docx
79 lignes

docx



specification_historique.docx
67 lignes

docx


Claude
Structure Angular MVC - AirSphereConnect Frontend.md
24.23 Ko •560 lignes
Le formatage peut être différent de la source

# Structure Angular MVC - AirSphereConnect Frontend

## 📁 Structure Angular MVC (Bonnes pratiques 2025)

```
airsphere-frontend/
├── src/
│   ├── app/
│   │   ├── core/                           # 🏛️ Singleton services (une seule instance)
│   │   │   ├── services/
│   │   │   │   ├── auth.service.ts
│   │   │   │   ├── api.service.ts
│   │   │   │   ├── notification.service.ts
│   │   │   │   ├── websocket.service.ts
│   │   │   │   └── error-handler.service.ts
│   │   │   ├── guards/
│   │   │   │   ├── auth.guard.ts
│   │   │   │   └── role.guard.ts
│   │   │   ├── interceptors/
│   │   │   │   ├── auth.interceptor.ts
│   │   │   │   ├── error.interceptor.ts
│   │   │   │   └── loading.interceptor.ts
│   │   │   └── models/
│   │   │       ├── user.model.ts
│   │   │       ├── api-response.model.ts
│   │   │       └── auth.model.ts
│   │   │
│   │   ├── shared/                         # 🔄 Composants réutilisables
│   │   │   ├── components/
│   │   │   │   ├── layout/                 # Layout components
│   │   │   │   │   ├── header/
│   │   │   │   │   │   ├── header.component.ts
│   │   │   │   │   │   ├── header.component.html
│   │   │   │   │   │   └── header.component.scss
│   │   │   │   │   ├── sidebar/
│   │   │   │   │   │   ├── sidebar.component.ts
│   │   │   │   │   │   ├── sidebar.component.html
│   │   │   │   │   │   └── sidebar.component.scss
│   │   │   │   │   ├── footer/
│   │   │   │   │   │   ├── footer.component.ts
│   │   │   │   │   │   ├── footer.component.html
│   │   │   │   │   │   └── footer.component.scss
│   │   │   │   │   └── breadcrumb/
│   │   │   │   │       ├── breadcrumb.component.ts
│   │   │   │   │       └── breadcrumb.component.html
│   │   │   │   ├── ui/                     # UI Components réutilisables
│   │   │   │   │   ├── loading-spinner/
│   │   │   │   │   │   ├── loading-spinner.component.ts
│   │   │   │   │   │   ├── loading-spinner.component.html
│   │   │   │   │   │   └── loading-spinner.component.scss
│   │   │   │   │   ├── confirmation-dialog/
│   │   │   │   │   │   ├── confirmation-dialog.component.ts
│   │   │   │   │   │   ├── confirmation-dialog.component.html
│   │   │   │   │   │   └── confirmation-dialog.component.scss
│   │   │   │   │   ├── data-table/
│   │   │   │   │   │   ├── data-table.component.ts
│   │   │   │   │   │   ├── data-table.component.html
│   │   │   │   │   │   └── data-table.component.scss
│   │   │   │   │   ├── search-box/
│   │   │   │   │   │   ├── search-box.component.ts
│   │   │   │   │   │   ├── search-box.component.html
│   │   │   │   │   │   └── search-box.component.scss
│   │   │   │   │   └── notification-snackbar/
│   │   │   │   │       ├── notification-snackbar.component.ts
│   │   │   │   │       └── notification-snackbar.component.html
│   │   │   │   ├── forms/                  # Form components
│   │   │   │   │   ├── city-selector/
│   │   │   │   │   │   ├── city-selector.component.ts
│   │   │   │   │   │   ├── city-selector.component.html
│   │   │   │   │   │   └── city-selector.component.scss
│   │   │   │   │   ├── date-range-picker/
│   │   │   │   │   │   ├── date-range-picker.component.ts
│   │   │   │   │   │   ├── date-range-picker.component.html
│   │   │   │   │   │   └── date-range-picker.component.scss
│   │   │   │   │   └── file-upload/
│   │   │   │   │       ├── file-upload.component.ts
│   │   │   │   │       ├── file-upload.component.html
│   │   │   │   │       └── file-upload.component.scss
│   │   │   │   ├── charts/                 # Chart components
│   │   │   │   │   ├── line-chart/
│   │   │   │   │   │   ├── line-chart.component.ts
│   │   │   │   │   │   ├── line-chart.component.html
│   │   │   │   │   │   └── line-chart.component.scss
│   │   │   │   │   ├── bar-chart/
│   │   │   │   │   │   ├── bar-chart.component.ts
│   │   │   │   │   │   ├── bar-chart.component.html
│   │   │   │   │   │   └── bar-chart.component.scss
│   │   │   │   │   └── gauge-chart/
│   │   │   │   │       ├── gauge-chart.component.ts
│   │   │   │   │       ├── gauge-chart.component.html
│   │   │   │   │       └── gauge-chart.component.scss
│   │   │   │   └── map/                    # Map components
│   │   │   │       ├── leaflet-map/
│   │   │   │       │   ├── leaflet-map.component.ts
│   │   │   │       │   ├── leaflet-map.component.html
│   │   │   │       │   └── leaflet-map.component.scss
│   │   │   │       └── city-marker/
│   │   │   │           ├── city-marker.component.ts
│   │   │   │           └── city-marker.component.html
│   │   │   ├── pipes/
│   │   │   │   ├── air-quality-color.pipe.ts
│   │   │   │   ├── relative-time.pipe.ts
│   │   │   │   ├── truncate.pipe.ts
│   │   │   │   ├── safe-html.pipe.ts
│   │   │   │   └── temperature-unit.pipe.ts
│   │   │   ├── directives/
│   │   │   │   ├── highlight.directive.ts
│   │   │   │   ├── auto-focus.directive.ts
│   │   │   │   ├── click-outside.directive.ts
│   │   │   │   └── lazy-load.directive.ts
│   │   │   ├── validators/
│   │   │   │   ├── email.validator.ts
│   │   │   │   ├── password.validator.ts
│   │   │   │   ├── coordinates.validator.ts
│   │   │   │   └── custom-validators.ts
│   │   │   ├── models/
│   │   │   │   ├── city.model.ts
│   │   │   │   ├── air-quality.model.ts
│   │   │   │   ├── weather.model.ts
│   │   │   │   ├── forum.model.ts
│   │   │   │   ├── favorite.model.ts
│   │   │   │   └── pagination.model.ts
│   │   │   ├── services/
│   │   │   │   ├── storage.service.ts
│   │   │   │   ├── utility.service.ts
│   │   │   │   ├── date.service.ts
│   │   │   │   └── export.service.ts
│   │   │   └── utils/
│   │   │       ├── constants.ts
│   │   │       ├── helpers.ts
│   │   │       ├── validators.ts
│   │   │       └── animations.ts
│   │   │
│   │   ├── features/                       # 📋 Pages/Modules métier
│   │   │   ├── auth/                       # 🔐 Authentication
│   │   │   │   ├── components/
│   │   │   │   │   ├── login/
│   │   │   │   │   │   ├── login.component.ts
│   │   │   │   │   │   ├── login.component.html
│   │   │   │   │   │   └── login.component.scss
│   │   │   │   │   ├── register/
│   │   │   │   │   │   ├── register.component.ts
│   │   │   │   │   │   ├── register.component.html
│   │   │   │   │   │   └── register.component.scss
│   │   │   │   │   ├── forgot-password/
│   │   │   │   │   │   ├── forgot-password.component.ts
│   │   │   │   │   │   ├── forgot-password.component.html
│   │   │   │   │   │   └── forgot-password.component.scss
│   │   │   │   │   └── profile/
│   │   │   │   │       ├── profile.component.ts
│   │   │   │   │       ├── profile.component.html
│   │   │   │   │       └── profile.component.scss
│   │   │   │   ├── services/
│   │   │   │   │   └── auth-feature.service.ts
│   │   │   │   └── auth.routes.ts
│   │   │   │
│   │   │   ├── dashboard/                  # 📊 Dashboard
│   │   │   │   ├── components/
│   │   │   │   │   ├── dashboard/
│   │   │   │   │   │   ├── dashboard.component.ts
│   │   │   │   │   │   ├── dashboard.component.html
│   │   │   │   │   │   └── dashboard.component.scss
│   │   │   │   │   ├── widgets/
│   │   │   │   │   │   ├── air-quality-widget/
│   │   │   │   │   │   │   ├── air-quality-widget.component.ts
│   │   │   │   │   │   │   ├── air-quality-widget.component.html
│   │   │   │   │   │   │   └── air-quality-widget.component.scss
│   │   │   │   │   │   ├── weather-widget/
│   │   │   │   │   │   │   ├── weather-widget.component.ts
│   │   │   │   │   │   │   ├── weather-widget.component.html
│   │   │   │   │   │   │   └── weather-widget.component.scss
│   │   │   │   │   │   ├── population-widget/
│   │   │   │   │   │   │   ├── population-widget.component.ts
│   │   │   │   │   │   │   ├── population-widget.component.html
│   │   │   │   │   │   │   └── population-widget.component.scss
│   │   │   │   │   │   └── recent-activity-widget/
│   │   │   │   │   │       ├── recent-activity-widget.component.ts
│   │   │   │   │   │       ├── recent-activity-widget.component.html
│   │   │   │   │   │       └── recent-activity-widget.component.scss
│   │   │   │   ├── services/
│   │   │   │   │   └── dashboard.service.ts
│   │   │   │   └── dashboard.routes.ts
│   │   │   │
│   │   │   ├── data-visualization/         # 📈 Données environnementales
│   │   │   │   ├── components/
│   │   │   │   │   ├── map-view/
│   │   │   │   │   │   ├── map-view.component.ts
│   │   │   │   │   │   ├── map-view.component.html
│   │   │   │   │   │   └── map-view.component.scss
│   │   │   │   │   ├── city-details/
│   │   │   │   │   │   ├── city-details.component.ts
│   │   │   │   │   │   ├── city-details.component.html
│   │   │   │   │   │   └── city-details.component.scss
│   │   │   │   │   ├── historical-data/
│   │   │   │   │   │   ├── historical-data.component.ts
│   │   │   │   │   │   ├── historical-data.component.html
│   │   │   │   │   │   └── historical-data.component.scss
│   │   │   │   │   ├── data-export/
│   │   │   │   │   │   ├── data-export.component.ts
│   │   │   │   │   │   ├── data-export.component.html
│   │   │   │   │   │   └── data-export.component.scss
│   │   │   │   │   └── filters/
│   │   │   │   │       ├── data-filters.component.ts
│   │   │   │   │       ├── data-filters.component.html
│   │   │   │   │       └── data-filters.component.scss
│   │   │   │   ├── services/
│   │   │   │   │   ├── air-quality.service.ts
│   │   │   │   │   ├── weather.service.ts
│   │   │   │   │   ├── population.service.ts
│   │   │   │   │   └── city.service.ts
│   │   │   │   └── data-visualization.routes.ts
│   │   │   │
│   │   │   ├── favorites/                  # ⭐ Favoris
│   │   │   │   ├── components/
│   │   │   │   │   ├── favorites-list/
│   │   │   │   │   │   ├── favorites-list.component.ts
│   │   │   │   │   │   ├── favorites-list.component.html
│   │   │   │   │   │   └── favorites-list.component.scss
│   │   │   │   │   ├── favorite-card/
│   │   │   │   │   │   ├── favorite-card.component.ts
│   │   │   │   │   │   ├── favorite-card.component.html
│   │   │   │   │   │   └── favorite-card.component.scss
│   │   │   │   │   └── add-favorite-dialog/
│   │   │   │   │       ├── add-favorite-dialog.component.ts
│   │   │   │   │       ├── add-favorite-dialog.component.html
│   │   │   │   │       └── add-favorite-dialog.component.scss
│   │   │   │   ├── services/
│   │   │   │   │   └── favorites.service.ts
│   │   │   │   └── favorites.routes.ts
│   │   │   │
│   │   │   ├── forum/                      # 💬 Forum
│   │   │   │   ├── components/
│   │   │   │   │   ├── forum-home/
│   │   │   │   │   │   ├── forum-home.component.ts
│   │   │   │   │   │   ├── forum-home.component.html
│   │   │   │   │   │   └── forum-home.component.scss
│   │   │   │   │   ├── thread-list/
│   │   │   │   │   │   ├── thread-list.component.ts
│   │   │   │   │   │   ├── thread-list.component.html
│   │   │   │   │   │   └── thread-list.component.scss
│   │   │   │   │   ├── thread-detail/
│   │   │   │   │   │   ├── thread-detail.component.ts
│   │   │   │   │   │   ├── thread-detail.component.html
│   │   │   │   │   │   └── thread-detail.component.scss
│   │   │   │   │   ├── post-form/
│   │   │   │   │   │   ├── post-form.component.ts
│   │   │   │   │   │   ├── post-form.component.html
│   │   │   │   │   │   └── post-form.component.scss
│   │   │   │   │   ├── post-item/
│   │   │   │   │   │   ├── post-item.component.ts
│   │   │   │   │   │   ├── post-item.component.html
│   │   │   │   │   │   └── post-item.component.scss
│   │   │   │   │   └── create-thread-dialog/
│   │   │   │   │       ├── create-thread-dialog.component.ts
│   │   │   │   │       ├── create-thread-dialog.component.html
│   │   │   │   │       └── create-thread-dialog.component.scss
│   │   │   │   ├── services/
│   │   │   │   │   └── forum.service.ts
│   │   │   │   └── forum.routes.ts
│   │   │   │
│   │   │   └── admin/                      # 👑 Administration (optionnel)
│   │   │       ├── components/
│   │   │       │   ├── user-management/
│   │   │       │   ├── content-moderation/
│   │   │       │   └── system-settings/
│   │   │       ├── services/
│   │   │       │   └── admin.service.ts
│   │   │       └── admin.routes.ts
│   │   │
│   │   ├── app.component.ts                # 🏠 Root component
│   │   ├── app.component.html
│   │   ├── app.component.scss
│   │   ├── app.routes.ts                   # 🗺️ Routes principales
│   │   └── app.config.ts                   # ⚙️ Configuration app
│   │
│   ├── assets/                             # 📦 Assets statiques
│   │   ├── images/
│   │   │   ├── logo/
│   │   │   │   ├── logo.svg
│   │   │   │   ├── logo-light.svg
│   │   │   │   └── favicon.ico
│   │   │   ├── icons/
│   │   │   │   ├── air-quality.svg
│   │   │   │   ├── weather.svg
│   │   │   │   ├── forum.svg
│   │   │   │   └── favorites.svg
│   │   │   └── illustrations/
│   │   │       ├── empty-state.svg
│   │   │       └── error-404.svg
│   │   ├── styles/
│   │   │   ├── _variables.scss             # Variables SCSS
│   │   │   ├── _mixins.scss                # Mixins SCSS
│   │   │   ├── _material-theme.scss        # Theme Angular Material
│   │   │   └── _utilities.scss             # Classes utilitaires
│   │   └── i18n/                           # Internationalisation
│   │       ├── en.json
│   │       └── fr.json
│   │
│   ├── environments/                       # 🌍 Environnements
│   │   ├── environment.ts                  # Dev
│   │   ├── environment.prod.ts             # Production
│   │   └── environment.docker.ts           # Docker
│   │
│   ├── styles.scss                         # 🎨 Styles globaux
│   ├── main.ts                            # 🚀 Bootstrap Angular
│   └── index.html                         # 📄 HTML principal
│
├── angular.json                           # ⚙️ Configuration Angular CLI
├── package.json                           # 📦 Dependencies
├── tsconfig.json                          # 🔧 TypeScript config
├── tailwind.config.ts                     # 🎨 Tailwind config
├── eslint.config.js                       # 📝 ESLint config
├── .prettierrc                           # ✨ Prettier config
├── karma.conf.js                          # 🧪 Tests config
├── Dockerfile                            # 🐳 Docker
├── nginx.conf                            # 🌐 Nginx config proxy pour build angular sur serveur
└── README.md                             # 📚 Documentation
```

## 🎯 Principes de l'organisation MVC Angular
### **Model** - Dans `shared/models/` et `core/models/`
### **View** - Components `.html` + `.scss`
### **Controller** - Components `.ts`

## 🎨 Configuration Material + Tailwind
### tailwind.config.ts
### _material-theme.css



src/assets/images/
src/assets/icons/
src/assets/styles/
├── tailwind.css
├── variables.css
src/assets/i18n/
├── fr.json
├── en.json
src/environments/
├── environment.ts
├── environment.prod.ts
