# Guide SonarQube + JaCoCo - Tests & Qualité du Code

Ce guide explique comment utiliser SonarQube et JaCoCo pour analyser la qualité du code et le coverage des tests dans le projet Air Sphere Connect.

---

## 📋 Table des matières

1. [Prérequis](#prérequis)
2. [Démarrage rapide](#démarrage-rapide)
3. [Scripts disponibles](#scripts-disponibles)
4. [Configuration](#configuration)
5. [Écrire des tests](#écrire-des-tests)
6. [Interprétation des résultats](#interprétation-des-résultats)
7. [Intégration CI/CD](#intégration-cicd)

---

## 🔧 Prérequis

### Outils nécessaires :
- **Docker & Docker Compose** (pour SonarQube)
- **Java 21+** (pour le backend)
- **Node.js 18+** (pour le frontend)
- **Maven** (inclus via wrapper `./mvnw`)

### Vérification :
```bash
docker --version
java --version
node --version
```

---

## 🚀 Démarrage rapide

### 1. Démarrer SonarQube

```bash
# Démarrer SonarQube + PostgreSQL
docker-compose -f docker-compose.dev.yml up -d sonarqube

# Vérifier que SonarQube est prêt (2-3 minutes)
docker logs -f sonarqube
# Attendre le message : "Web Server is operational"
```

Accéder à SonarQube : **http://localhost:9000**

- **Login initial** : `admin` / `admin` (vous serez invité à changer le mot de passe)

### 2. Lancer l'analyse complète

```bash
# Analyse Backend + Frontend
./analyze-all.sh
```

### 3. Consulter les résultats

Ouvrir : **http://localhost:9000/dashboard?id=air-sphere-connect**

---

## 📜 Scripts disponibles

### 🔹 `analyze-all.sh`
Analyse complète (Backend + Frontend)

```bash
./analyze-all.sh
```

### 🔹 `analyze-backend.sh`
Analyse uniquement le backend (Spring Boot + JaCoCo)

```bash
./analyze-backend.sh
```

**Ce qui est analysé :**
- ✅ Compilation du code
- ✅ Exécution des tests unitaires
- ✅ Coverage JaCoCo
- ✅ Analyse qualité SonarQube (bugs, vulnérabilités, code smells)

### 🔹 `analyze-frontend.sh`
Analyse uniquement le frontend (Angular + Karma)

```bash
./analyze-frontend.sh
```

**Ce qui est analysé :**
- ✅ Tests unitaires (Jasmine + Karma)
- ✅ Coverage des tests
- ✅ Analyse TypeScript/HTML/CSS
- ✅ Détection de problèmes de sécurité

---

## ⚙️ Configuration

### Backend (Spring Boot + JaCoCo)

**Fichier** : `air-sphere-connect-back/pom.xml`

JaCoCo est déjà configuré. Le plugin génère automatiquement :
- Rapport binaire : `target/jacoco.exec`
- Rapport HTML : `target/site/jacoco/index.html`

### Frontend (Angular + Karma)

**Fichiers** :
- `air-sphere-connect-front/angular.json` → Config Karma
- `air-sphere-connect-front/sonar-project.properties` → Config SonarQube

Coverage activé par défaut dans `angular.json:87-93`.

---

## ✍️ Écrire des tests

### Backend (JUnit 5)

**Exemple de test** :

```java
// src/test/java/com/airSphereConnect/services/UserServiceImplTest.java
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.findByEmail("test@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }
}
```

**Lancer les tests** :
```bash
cd air-sphere-connect-back
./mvnw test
```

### Frontend (Jasmine + Karma)

**Exemple de test** :

```typescript
// src/app/services/weather.service.spec.ts
describe('WeatherService', () => {
  let service: WeatherService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [WeatherService]
    });
    service = TestBed.inject(WeatherService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should retrieve weather data', () => {
    const mockData = { temp: 20, humidity: 60 };

    service.getWeather('Paris').subscribe(data => {
      expect(data).toEqual(mockData);
    });

    const req = httpMock.expectOne('/api/weather/Paris');
    expect(req.request.method).toBe('GET');
    req.flush(mockData);
  });
});
```

**Lancer les tests** :
```bash
cd air-sphere-connect-front
npm test                    # Mode watch
npm run test:coverage       # Avec coverage
```

---

## 📊 Interprétation des résultats

### Métriques SonarQube

| Métrique | Description | Objectif |
|----------|-------------|----------|
| **Bugs** 🐛 | Erreurs de code qui peuvent causer des crashes | 0 |
| **Vulnerabilities** 🔒 | Failles de sécurité | 0 |
| **Code Smells** 🧹 | Mauvaises pratiques, code difficile à maintenir | < 5% |
| **Coverage** ✅ | % de code couvert par les tests | > 80% |
| **Duplications** 🔄 | Code dupliqué | < 3% |

### Quality Gates

SonarQube bloque le merge si :
- ❌ Bugs critiques > 0
- ❌ Vulnérabilités > 0
- ❌ Coverage < 80%

---

## 🔄 Intégration CI/CD

### Jenkins (À venir)

Le pipeline Jenkins intégrera automatiquement SonarQube :

```groovy
stage('SonarQube Analysis') {
    steps {
        script {
            withSonarQubeEnv('SonarQube') {
                sh './analyze-all.sh'
            }
        }
    }
}

stage('Quality Gate') {
    steps {
        timeout(time: 1, unit: 'HOURS') {
            waitForQualityGate abortPipeline: true
        }
    }
}
```

---

## 🆘 Troubleshooting

### SonarQube ne démarre pas

```bash
# Vérifier les logs
docker logs sonarqube

# Redémarrer proprement
docker-compose -f docker-compose.dev.yml restart sonarqube
```

### Erreur "Project not found"

Vérifiez que le token et la clé de projet sont corrects dans les scripts.

### Tests frontend échouent

```bash
cd air-sphere-connect-front
rm -rf node_modules
npm install
npm test
```

---

## 📚 Ressources

- [Documentation SonarQube](https://docs.sonarsource.com/sonarqube)
- [JaCoCo Maven Plugin](https://www.jacoco.org/jacoco/trunk/doc/maven.html)
- [Karma Coverage](https://karma-runner.github.io/latest/config/coverage.html)

---

**Auteur** : Équipe Air Sphere Connect  
**Dernière mise à jour** : 2025-01-25
