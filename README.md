# Tarea: API con Varios Modelos y Base de Datos Real
## Opción 3: Clínica Veterinaria

Proyecto desarrollado con **Spring Boot**, **Spring Data JPA** y conectado a una base de datos relacional real (**PostgreSQL**), implementando una arquitectura por capas independiente para cada modelo.

---

## 1. Información General

- **Estudiante / Desarrollador:** Esteban
- **Opción elegida:** Opción 3 - Clínica veterinaria
- **Nivel:** Intermedio
- **Modelos independientes:**
  1. `Mascota` (Ruta base: `/api/mascota` | Tabla: `mascota`)
  2. `Medicamento` (Ruta base: `/api/medicamento` | Tabla: `medicamento`)
  3. `Veterinario` (Ruta base: `/api/veterinario` | Tabla: `veterinario`)
- **Gestor de base de datos:** **PostgreSQL**
- **Base de datos:** `veterinaria`
- **Usuario de la aplicación:** `esteban` (clave: `123`)
- **Puerto del servidor:** `9090`

---

## 2. Estructura del Proyecto

El proyecto sigue estrictamente la arquitectura por capas desacoplada (sin relaciones entre entidades ni claves foráneas):

```text
Veterinaria/
├── pom.xml                                   # Driver PostgreSQL, sin H2
├── database/
│   └── script.sql                            # Creación de base, usuario y datos de prueba (5 por tabla)
├── postman/
│   └── clinica_veterinaria.postman_collection.json # Colección de pruebas completa (con 400, 404, 409)
├── README.md
└── src/
    ├── main/
    │   ├── java/com/esteban/Veterinaria/
    │   │   ├── VeterinariaApplication.java
    │   │   ├── model/
    │   │   │   ├── Mascota.java
    │   │   │   ├── Medicamento.java
    │   │   │   └── Veterinario.java
    │   │   ├── repository/
    │   │   │   ├── MascotaRepository.java
    │   │   │   ├── MedicamentoRepository.java
    │   │   │   └── VeterinarioRepository.java
    │   │   ├── service/
    │   │   │   ├── MascotaService.java
    │   │   │   ├── MedicamentoService.java
    │   │   │   └── VeterinarioService.java
    │   │   └── controller/
    │   │       ├── MascotaController.java
    │   │       ├── MedicamentoController.java
    │   │       └── VeterinarioController.java
    │   └── resources/
    │       └── application.properties        # Configuración de PostgreSQL en puerto 9090
    └── test/
        └── java/com/esteban/Veterinaria/
            ├── VeterinariaApplicationTests.java
            └── VeterinariaIntegrationTests.java # Pruebas automatizadas de todos los endpoints
```

---

## 3. Configuración y Ejecución

### 3.1 Base de Datos (PostgreSQL)
1. Abrir pgAdmin o la consola de PostgreSQL con el usuario administrador `postgres`.
2. Ejecutar la primera sección de `database/script.sql`:
   ```sql
   CREATE USER esteban WITH PASSWORD '123';
   CREATE DATABASE veterinaria;
   ALTER DATABASE veterinaria OWNER TO esteban;
   ```
3. Conectarse a la base `veterinaria` y otorgar permisos:
   ```sql
   \c veterinaria;
   GRANT ALL ON SCHEMA public TO esteban;
   GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO esteban;
   GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO esteban;
   ```

### 3.2 Arrancar la Aplicación
Compilar y ejecutar la aplicación desde la raíz del proyecto:
```bash
./mvnw clean spring-boot:run
```
Al arrancar, Hibernate creará automáticamente las tres tablas (`mascota`, `medicamento`, `veterinario`) en PostgreSQL gracias a `spring.jpa.hibernate.ddl-auto=update`.

### 3.3 Cargar Datos de Prueba
Una vez que las tablas existan en la base de datos, ejecutar la sección de inserts en `database/script.sql` (contiene 5 registros por tabla).

---

## 4. Tabla Completa de Endpoints

### 4.1 Modelo: Mascota (`/api/mascota`)

| Método | Ruta | Descripción | Códigos HTTP |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/mascota` | Lista todos los pacientes registrados | `200` |
| `GET` | `/api/mascota/{id}` | Obtiene una mascota por su ID | `200`, `404` |
| `POST` | `/api/mascota` | Registra una nueva mascota | `201` |
| `PUT` | `/api/mascota/{id}` | Edita una mascota existente | `200`, `404` |
| `DELETE` | `/api/mascota/{id}` | Elimina una mascota | `204`, `404` |
| `GET` | `/api/mascota/especie/{especie}` | **Especial:** Mascotas de esa especie (sin importar mayúsculas) | `200` |
| `GET` | `/api/mascota/conteo-especies` | **Especial:** Conteo agrupado de mascotas por especie (Map JSON) | `200` |
| `GET` | `/api/mascota/dueno/{dueno}` | **Especial:** Mascotas de un mismo dueño | `200` |
| `GET` | `/api/mascota/cachorros` | **Especial:** Mascotas de 1 año o menos (edad <= 1) | `200` |

### 4.2 Modelo: Medicamento (`/api/medicamento`)

| Método | Ruta | Descripción | Códigos HTTP |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/medicamento` | Lista todos los medicamentos de la farmacia | `200` |
| `GET` | `/api/medicamento/{id}` | Obtiene un medicamento por su ID | `200`, `404` |
| `POST` | `/api/medicamento` | Registra un nuevo medicamento | `201` |
| `PUT` | `/api/medicamento/{id}` | Edita un medicamento existente | `200`, `404` |
| `DELETE` | `/api/medicamento/{id}` | Elimina un medicamento | `204`, `404` |
| `GET` | `/api/medicamento/stock-bajo?minimo=5` | **Especial:** Medicamentos con stock menor al mínimo (defecto: 5) | `200` |
| `GET` | `/api/medicamento/venta-libre` | **Especial:** Medicamentos que no requieren receta médica | `200` |
| `PUT` | `/api/medicamento/{id}/despachar?cantidad=2` | **Especial:** Descuenta stock | `200`, `400` (si cantidad <= 0), `404`, `409` (si no alcanza el stock) |

### 4.3 Modelo: Veterinario (`/api/veterinario`)

| Método | Ruta | Descripción | Códigos HTTP |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/veterinario` | Lista todos los veterinarios de la clínica | `200` |
| `GET` | `/api/veterinario/{id}` | Obtiene un veterinario por su ID | `200`, `404` |
| `POST` | `/api/veterinario` | Registra un nuevo profesional | `201` |
| `PUT` | `/api/veterinario/{id}` | Edita los datos de un veterinario | `200`, `404` |
| `DELETE` | `/api/veterinario/{id}` | Elimina un veterinario | `204`, `404` |
| `GET` | `/api/veterinario/especialidad/{especialidad}` | **Especial:** Veterinarios de esa especialidad que estén disponibles | `200` |
| `GET` | `/api/veterinario/mas-experimentado` | **Especial:** El veterinario con más años de experiencia | `200`, `404` (si no hay ninguno) |
| `PUT` | `/api/veterinario/{id}/disponibilidad?valor=false` | **Especial:** Cambia el estado de disponibilidad | `200`, `404`, `409` (si ya tenía ese valor) |

---

## 5. Pruebas con Postman

La colección se encuentra en `postman/clinica_veterinaria.postman_collection.json` lista para importar.
Está organizada en **3 carpetas independientes** (una por modelo):
- **1. Mascota:** Contiene las 5 peticiones CRUD, las situaciones especiales y casos de prueba `404`.
- **2. Medicamento:** Contiene las 5 peticiones CRUD, filtros especiales y los casos de error `400` (cantidad $\le 0$), `404` (inexistente) y `409` (stock insuficiente).
- **3. Veterinario:** Contiene las 5 peticiones CRUD, especiales y los casos de error `404` y `409` (intentar asignar el mismo estado de disponibilidad que ya tenía).

---

## 6. Capturas de Pantalla

*(Espacio reservado para adjuntar las capturas requeridas por la rúbrica antes de la entrega final)*

### 6.1 Tablas con datos en el gestor (pgAdmin / DBeaver)
- Tabla `mascota`:
  `[Insertar captura de pgAdmin con los registros de mascota]`
- Tabla `medicamento`:
  `[Insertar captura de pgAdmin con los registros de medicamento]`
- Tabla `veterinario`:
  `[Insertar captura de pgAdmin con los registros de veterinario]`

### 6.2 Respuestas de Postman
- Respuesta exitosa con código `200` / `201`:
  `[Insertar captura de Postman]`
- Respuesta de situación especial:
  `[Insertar captura de Postman]`
- Respuesta con código de error (`400`, `404` o `409`):
  `[Insertar captura de Postman]`
