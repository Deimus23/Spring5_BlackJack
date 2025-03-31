# Blackjack API - Spring Boot with WebFlux

## Introducción

La API de Blackjack es un servicio desarrollado en Java utilizando Spring Boot y WebFlux, que permite gestionar partidas de Blackjack de manera reactiva y eficiente. La API se conecta a dos bases de datos: MongoDB y MySQL, para almacenar y gestionar la información de las partidas, jugadores y mazos. Con esta API, los usuarios pueden crear partidas, realizar jugadas como "hit" y "stand", y eliminar partidas, entre otras funcionalidades.

Además, se incluye documentación interactiva a través de Swagger para facilitar la interacción con la API.

### Descripción del juego de Blackjack

Blackjack es un popular juego de cartas en el que el objetivo es conseguir una mano cuyo valor total se acerque a 21 sin pasarse. Los jugadores pueden tomar cartas adicionales (hit) o quedarse con su mano actual (stand). La API permite gestionar las partidas, llevar un seguimiento de las manos de los jugadores y proporcionar un mazo de cartas para cada partida.

## Características

- **Creación de partidas:** Permite iniciar una nueva partida de Blackjack.
- **Consulta de partidas:** Consulta el estado de una partida existente.
- **Jugada "hit":** Permite a un jugador recibir una carta adicional en su mano.
- **Jugada "stand":** Permite a un jugador quedarse con su mano actual.
- **Eliminación de partidas:** Elimina una partida en curso.
- **Ver el mazo restante:** Proporciona el mazo restante de cartas disponibles para el juego.
- **Documentación interactiva con Swagger:** Acceso a una interfaz interactiva para probar los endpoints de la API.

## Endpoints

### 1. **Crear una nueva partida**
- **Método HTTP:** `POST`
- **Ruta:** `/games/new`
- **Parámetros de entrada:** Ninguno
- **Respuesta esperada:** 
  - `201 Created` con el objeto de la partida creada, incluyendo el ID de la partida.
  
### 2. **Consultar una partida existente**
- **Método HTTP:** `GET`
- **Ruta:** `/games/{id}`
- **Parámetros de entrada:** 
  - `id` (ruta): El ID de la partida.
- **Respuesta esperada:** 
  - `200 OK` con la información detallada de la partida.
  - `404 Not Found` si no se encuentra la partida.

### 3. **Realizar jugada "hit"**
- **Método HTTP:** `POST`
- **Ruta:** `/games/{id}/hit`
- **Parámetros de entrada:** 
  - `id` (ruta): El ID de la partida.
- **Respuesta esperada:** 
  - `200 OK` con la mano actualizada del jugador.
  - `404 Not Found` si no se encuentra la partida.

### 4. **Realizar jugada "stand"**
- **Método HTTP:** `POST`
- **Ruta:** `/games/{id}/stand`
- **Parámetros de entrada:** 
  - `id` (ruta): El ID de la partida.
- **Respuesta esperada:** 
  - `200 OK` con la mano final del jugador.
  - `404 Not Found` si no se encuentra la partida.

### 5. **Eliminar una partida**
- **Método HTTP:** `DELETE`
- **Ruta:** `/games/{id}/delete`
- **Parámetros de entrada:** 
  - `id` (ruta): El ID de la partida.
- **Respuesta esperada:** 
  - `200 OK` si la partida se elimina correctamente.
  - `404 Not Found` si no se encuentra la partida.

### 6. **Ver el mazo restante**
- **Método HTTP:** `GET`
- **Ruta:** `/games/{id}/deck`
- **Parámetros de entrada:** 
  - `id` (ruta): El ID de la partida.
- **Respuesta esperada:** 
  - `200 OK` con el mazo restante de cartas para la partida.
  - `404 Not Found` si no se encuentra la partida.

## Tecnologías Utilizadas

- **Java 17**: Versión de Java utilizada para desarrollar la API.
- **Spring Boot (Spring WebFlux)**: Framework para desarrollar aplicaciones reactivas.
- **MongoDB**: Base de datos NoSQL utilizada para almacenar información de las partidas.
- **MySQL**: Base de datos SQL utilizada para almacenar jugadores y estadísticas de las partidas.
- **Swagger**: Herramienta para generar documentación interactiva de la API.
- **JUnit y Mockito**: Herramientas para realizar pruebas unitarias y de integración.
- **Maven**: Herramienta de gestión de dependencias y construcción del proyecto.

## Instalación y Configuración

### Clonación del repositorio

```bash
git clone https://github.com/tu-usuario/blackjack-api.git
cd blackjack-api
