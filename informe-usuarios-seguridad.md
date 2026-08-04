# Informe expositivo de la funcionalidad: Usuarios y Seguridad

## 1. Contexto de la funcionalidad asignada

La funcionalidad asignada corresponde a **Usuarios y Seguridad** dentro del backend de ShipCore. Esta parte del sistema se encarga de permitir que una persona se registre, inicie sesion, reciba un token JWT, acceda a rutas protegidas y sea autorizada segun su rol. Tambien incluye la administracion de usuarios: crear, listar, consultar, actualizar y desactivar usuarios.

En terminos simples, esta funcionalidad responde a estas preguntas:

- Quien eres: autenticacion mediante correo y contrasena.
- Que permisos tienes: autorizacion mediante roles como `ROLE_ADMIN`, `ROLE_OPERATOR` y `ROLE_CLIENT`.
- Como se mantiene tu sesion: mediante un token JWT enviado en la cabecera `Authorization: Bearer <token>`.
- Como se guardan los usuarios: mediante entidades JPA relacionadas con organizaciones y perfiles.
- Como se protege la API: mediante Spring Security, filtros JWT y reglas de acceso.

La logica esta repartida en varias capas:

- **Controller**: recibe las peticiones HTTP.
- **DTO request/response**: define los datos que entran y salen de la API.
- **Service**: contiene la logica de negocio.
- **Repository**: consulta y guarda datos en MySQL.
- **Entity**: representa las tablas de base de datos.
- **Mapper**: transforma DTOs en entidades y entidades en respuestas.
- **Security**: configura autenticacion, autorizacion, filtros, contrasenas y JWT.
- **Exception handling**: devuelve errores ordenados cuando algo falla.

## 2. Archivos principales de la funcionalidad

Los archivos directamente relacionados con Usuarios y Seguridad son:

### Controladores

- `src/main/java/com/shipcore/business/api/controller/AuthController.java`
- `src/main/java/com/shipcore/business/api/controller/UserController.java`

### DTOs de entrada

- `src/main/java/com/shipcore/business/api/dto/request/LoginRequest.java`
- `src/main/java/com/shipcore/business/api/dto/request/RegisterRequest.java`
- `src/main/java/com/shipcore/business/api/dto/request/UserRequest.java`

### DTOs de salida

- `src/main/java/com/shipcore/business/api/dto/response/AuthResponse.java`
- `src/main/java/com/shipcore/business/api/dto/response/UserResponse.java`
- `src/main/java/com/shipcore/business/api/dto/response/OrganizationResponse.java`

### Servicios

- `src/main/java/com/shipcore/business/domain/service/AuthService.java`
- `src/main/java/com/shipcore/business/domain/service/UserService.java`
- `src/main/java/com/shipcore/business/domain/service/impl/AuthServiceImpl.java`
- `src/main/java/com/shipcore/business/domain/service/impl/UserServiceImpl.java`

### Mappers

- `src/main/java/com/shipcore/business/domain/mapper/UserMapper.java`

### Entidades y modelos persistentes

- `src/main/java/com/shipcore/business/data/entity/BaseEntity.java`
- `src/main/java/com/shipcore/business/data/entity/User.java`
- `src/main/java/com/shipcore/business/data/entity/UserProfile.java`
- `src/main/java/com/shipcore/business/data/entity/Organization.java`
- `src/main/java/com/shipcore/business/domain/enums/Role.java`

### Repositorios

- `src/main/java/com/shipcore/business/data/repository/UserRepository.java`
- `src/main/java/com/shipcore/business/data/repository/OrganizationRepository.java`

### Seguridad

- `src/main/java/com/shipcore/security/config/SecurityConfig.java`
- `src/main/java/com/shipcore/security/config/PasswordConfig.java`
- `src/main/java/com/shipcore/security/filter/JwtAuthenticationFilter.java`
- `src/main/java/com/shipcore/security/jwt/JwtService.java`
- `src/main/java/com/shipcore/security/jwt/JwtAuthenticationEntryPoint.java`
- `src/main/java/com/shipcore/security/jwt/JwtAccessDeniedHandler.java`
- `src/main/java/com/shipcore/security/service/CustomUserDetailsService.java`

### Errores

- `src/main/java/com/shipcore/business/api/exception/GlobalExceptionHandler.java`
- `src/main/java/com/shipcore/business/api/exception/ApiError.java`
- `src/main/java/com/shipcore/business/api/exception/ResourceAlreadyExistsException.java`
- `src/main/java/com/shipcore/business/api/exception/ResourceNotFoundException.java`
- `src/main/java/com/shipcore/business/api/exception/BusinessRuleException.java`

## 3. Flujo general de autenticacion

La autenticacion es el proceso de verificar que el usuario realmente es quien dice ser. En este proyecto se hace con correo, contrasena y JWT.

### 3.1 Registro de usuario

Ruta:

```http
POST /api/v1/auth/register
```

Archivo que recibe la peticion:

```text
AuthController.java
```

Flujo:

1. El cliente envia un `RegisterRequest` con nombre, apellido, correo, contrasena y `organizationId`.
2. `AuthController.register()` recibe la peticion y llama a `authService.register(request)`.
3. `AuthServiceImpl.register()` valida que el correo no exista usando `userRepository.existsByEmail(request.email())`.
4. Busca la organizacion con `organizationRepository.findById(request.organizationId())`.
5. Crea una entidad `User` usando el builder de Lombok.
6. La contrasena no se guarda en texto plano; se codifica con `passwordEncoder.encode(request.password())`.
7. El usuario queda con rol `ROLE_CLIENT`.
8. El usuario queda activo con `active(true)`.
9. Se guarda en la base de datos con `userRepository.save(user)`.
10. Se genera un JWT con `jwtService.generateToken(...)`.
11. Se devuelve un `AuthResponse` con token, tipo `Bearer`, mensaje, usuario y organizacion.

La parte importante es que el registro no solo crea al usuario; tambien le devuelve un token para que pueda autenticarse inmediatamente.

### 3.2 Login

Ruta:

```http
POST /api/v1/auth/login
```

Flujo:

1. El cliente envia un `LoginRequest` con correo y contrasena.
2. `AuthController.login()` llama a `authService.login(request)`.
3. `AuthServiceImpl.login()` crea un `UsernamePasswordAuthenticationToken` con email y password.
4. Ese token se entrega a `authenticationManager.authenticate(...)`.
5. Spring Security usa el `DaoAuthenticationProvider`, el `CustomUserDetailsService` y el `PasswordEncoder`.
6. `CustomUserDetailsService` busca el usuario por correo en la base de datos.
7. `BCryptPasswordEncoder` compara la contrasena enviada con la contrasena encriptada guardada.
8. Si las credenciales son correctas, se busca el usuario completo.
9. Se genera un JWT.
10. Se devuelve un `AuthResponse`.

Si el login falla, Spring Security no permite avanzar y se responde como error de autenticacion.

## 4. Flujo de autorizacion con JWT

La autorizacion es el proceso que decide si un usuario autenticado puede entrar a una ruta.

### 4.1 Envio del token

Despues del login o registro, el backend devuelve un token. Para usar endpoints protegidos, el cliente debe enviarlo asi:

```http
Authorization: Bearer <token>
```

### 4.2 Filtro JWT

Archivo:

```text
JwtAuthenticationFilter.java
```

Este filtro se ejecuta una vez por peticion porque extiende `OncePerRequestFilter`.

Flujo:

1. Lee la cabecera `Authorization`.
2. Verifica que empiece con `Bearer `.
3. Extrae el token quitando los primeros 7 caracteres.
4. Usa `JwtService.extractUsername(token)` para obtener el correo guardado como subject del token.
5. Carga el usuario con `CustomUserDetailsService.loadUserByUsername(username)`.
6. Valida que el token corresponda al usuario y que no este expirado.
7. Si todo es valido, crea un `UsernamePasswordAuthenticationToken`.
8. Coloca la autenticacion en `SecurityContextHolder`.
9. Continua la cadena con `filterChain.doFilter(request, response)`.

El `SecurityContextHolder` es clave porque ahi Spring Security guarda quien esta autenticado durante la peticion actual.

### 4.3 Rutas publicas y rutas protegidas

Archivo:

```text
SecurityConfig.java
```

Rutas publicas:

```text
/api/v1/auth/**
/swagger-ui/**
/v3/api-docs/**
```

Estas rutas no requieren token. Por eso login, registro y documentacion Swagger pueden abrirse sin autenticacion.

Todas las demas rutas usan:

```java
.anyRequest().authenticated()
```

Eso significa que cualquier otra ruta necesita un token valido.

### 4.4 Restriccion por rol ADMIN

Archivo:

```text
UserController.java
```

El controlador tiene:

```java
@PreAuthorize("hasRole('ADMIN')")
```

Esto quiere decir que todos los endpoints de `/api/v1/users` solo pueden ser usados por usuarios con autoridad equivalente a `ROLE_ADMIN`.

Spring interpreta `hasRole('ADMIN')` como `ROLE_ADMIN`. Por eso el enum `Role` guarda los roles con el prefijo `ROLE_`.

## 5. Explicacion archivo por archivo

## 5.1 `AuthController.java`

Este controlador expone las rutas de autenticacion:

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`

Anotaciones principales:

- `@RestController`: indica que la clase responde peticiones HTTP y devuelve JSON.
- `@RequestMapping("/api/v1/auth")`: define el prefijo de las rutas.
- `@RequiredArgsConstructor`: Lombok genera un constructor con los campos `final`.

Campo:

```java
private final AuthService authService;
```

Este campo representa la dependencia hacia la capa de servicio. El controlador no implementa la logica de registro o login; solo recibe la peticion y delega.

Constructor:

No aparece escrito manualmente, pero Lombok genera algo equivalente a:

```java
public AuthController(AuthService authService) {
    this.authService = authService;
}
```

Este constructor permite que Spring inyecte automaticamente una implementacion de `AuthService`, que en este caso es `AuthServiceImpl`.

Metodos:

- `register(RegisterRequest request)`: valida el cuerpo con `@Valid`, llama al servicio y responde con estado `201 CREATED`.
- `login(LoginRequest request)`: valida el cuerpo con `@Valid`, llama al servicio y responde con `200 OK`.

## 5.2 `UserController.java`

Este controlador administra usuarios mediante CRUD:

- `POST /api/v1/users`
- `GET /api/v1/users`
- `GET /api/v1/users/{id}`
- `PUT /api/v1/users/{id}`
- `DELETE /api/v1/users/{id}`

Anotaciones:

- `@RestController`: controlador REST.
- `@RequestMapping("/api/v1/users")`: prefijo de rutas.
- `@RequiredArgsConstructor`: constructor automatico para inyeccion.
- `@PreAuthorize("hasRole('ADMIN')")`: solo permite acceso a administradores.

Constructor generado:

```java
public UserController(UserService userService) {
    this.userService = userService;
}
```

Metodos:

- `create(...)`: crea usuario y devuelve `201 CREATED`.
- `findAll()`: lista usuarios activos.
- `findById(Long id)`: obtiene un usuario por ID.
- `update(Long id, UserRequest request)`: actualiza datos del usuario.
- `delete(Long id)`: no borra fisicamente; desactiva el usuario y devuelve `204 NO CONTENT`.

La seguridad de este controlador esta reforzada a nivel de metodo por `@PreAuthorize`.

## 5.3 `LoginRequest.java`

Es un `record`, es decir, un DTO inmutable de entrada.

Campos:

- `email`: correo del usuario.
- `password`: contrasena del usuario.

Validaciones:

- `@Email`: obliga a que el correo tenga formato valido.
- `@NotBlank`: evita valores vacios.

Constructor:

Al ser un record, Java genera automaticamente un constructor canonico equivalente a:

```java
public LoginRequest(String email, String password) {
    this.email = email;
    this.password = password;
}
```

Tambien genera metodos accesores llamados `email()` y `password()`.

Uso:

Se usa exclusivamente para recibir los datos de login desde el cliente.

## 5.4 `RegisterRequest.java`

Es el DTO para registrar usuarios.

Campos:

- `firstName`: nombre.
- `lastName`: apellido.
- `email`: correo.
- `password`: contrasena.
- `organizationId`: organizacion a la que pertenece el usuario.

Validaciones:

- `@NotBlank`: campos de texto obligatorios.
- `@Email`: correo valido.
- `@Size(min = 8, max = 100)`: contrasena entre 8 y 100 caracteres.
- `@NotNull`: organizacion obligatoria.

Constructor:

Al ser record, Java genera:

```java
public RegisterRequest(
        String firstName,
        String lastName,
        String email,
        String password,
        Long organizationId
) {
    ...
}
```

Este constructor se usa cuando Spring convierte el JSON recibido en un objeto Java.

## 5.5 `UserRequest.java`

Es el DTO para crear o actualizar usuarios desde el modulo administrativo.

Campos:

- `firstName`
- `lastName`
- `email`
- `password`
- `role`
- `phone`
- `address`
- `bio`
- `organizationId`

Este DTO es mas completo que `RegisterRequest` porque permite indicar el rol y los datos del perfil.

Constructor:

Como es record, Java genera un constructor canonico con todos los campos:

```java
public UserRequest(
        String firstName,
        String lastName,
        String email,
        String password,
        Role role,
        String phone,
        String address,
        String bio,
        Long organizationId
) {
    ...
}
```

Uso:

- En `UserController.create()`.
- En `UserController.update()`.
- En `UserServiceImpl.create()`.
- En `UserServiceImpl.update()`.
- En `UserMapper.toEntity()`.
- En `UserMapper.updateEntity()`.

## 5.6 `AuthResponse.java`

Es el DTO de respuesta para login y registro.

Campos:

- `accessToken`: token JWT.
- `tokenType`: normalmente `Bearer`.
- `message`: mensaje de exito.
- `user`: datos del usuario autenticado.
- `organization`: datos de la organizacion.

Constructor:

Como record, genera un constructor canonico:

```java
public AuthResponse(
        String accessToken,
        String tokenType,
        String message,
        UserResponse user,
        OrganizationResponse organization
) {
    ...
}
```

En `AuthServiceImpl` se instancia directamente con `new AuthResponse(...)`.

## 5.7 `UserResponse.java`

Es el DTO que sale hacia el cliente cuando se consulta o crea un usuario.

Campos:

- `id`
- `firstName`
- `lastName`
- `email`
- `role`
- `phone`
- `address`
- `bio`
- `active`
- `organizationId`
- `organizationName`

Constructor:

Java genera el constructor canonico del record con todos los campos. Se usa principalmente desde MapStruct y tambien manualmente en `AuthServiceImpl.toUserResponse(...)`.

Importante:

No devuelve la contrasena. Esta es una decision correcta de seguridad: aunque la contrasena este encriptada en base de datos, no debe exponerse en respuestas JSON.

## 5.8 `OrganizationResponse.java`

Aunque no pertenece solamente a seguridad, se usa en `AuthResponse`.

Campos:

- `id`
- `name`
- `ruc`
- `address`
- `phone`
- `country`
- `plan`
- `softLimit`
- `hardLimit`
- `currentUsage`
- `active`

Constructor:

Al ser record, Java genera el constructor con todos esos campos. En autenticacion se usa para devolver la organizacion del usuario logueado o registrado.

## 5.9 `AuthService.java`

Es una interfaz de servicio.

Define el contrato:

```java
AuthResponse register(RegisterRequest request);
AuthResponse login(LoginRequest request);
```

No tiene constructor porque es una interfaz.

Su funcion es separar el contrato de la implementacion. El controlador depende de `AuthService`, no directamente de `AuthServiceImpl`.

## 5.10 `UserService.java`

Es la interfaz del servicio de usuarios.

Define operaciones:

- `create`
- `findAll`
- `findById`
- `update`
- `delete`

No tiene constructor porque tambien es una interfaz.

## 5.11 `AuthServiceImpl.java`

Es la implementacion real de la logica de autenticacion.

Anotaciones:

- `@Service`: Spring registra la clase como servicio.
- `@RequiredArgsConstructor`: Lombok genera constructor con dependencias `final`.
- `@Transactional`: las operaciones se ejecutan dentro de una transaccion.

Dependencias:

- `UserRepository`: consultar y guardar usuarios.
- `OrganizationRepository`: validar la organizacion.
- `PasswordEncoder`: encriptar contrasenas.
- `JwtService`: generar tokens.
- `AuthenticationManager`: autenticar login.

Constructor generado:

```java
public AuthServiceImpl(
        UserRepository userRepository,
        OrganizationRepository organizationRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        AuthenticationManager authenticationManager
) {
    this.userRepository = userRepository;
    this.organizationRepository = organizationRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.authenticationManager = authenticationManager;
}
```

Metodo `register(...)`:

- Verifica si el correo ya existe.
- Busca la organizacion.
- Crea el usuario.
- Codifica la contrasena.
- Asigna rol `ROLE_CLIENT`.
- Activa el usuario.
- Guarda el usuario.
- Genera token.
- Devuelve `AuthResponse`.

Metodo `login(...)`:

- Autentica email y contrasena con Spring Security.
- Busca al usuario por email.
- Genera token.
- Devuelve `AuthResponse`.

Metodo privado `toUserDetails(...)`:

Convierte la entidad `User` en un objeto `UserDetails` de Spring Security. Esto permite generar el token usando el email como username y el rol como autoridad.

Metodo privado `toUserResponse(...)`:

Convierte una entidad `User` en `UserResponse`. Si el usuario no tiene perfil, los datos `phone`, `address` y `bio` salen como `null`.

Metodo privado `toOrganizationResponse(...)`:

Convierte una entidad `Organization` en `OrganizationResponse`.

## 5.12 `UserServiceImpl.java`

Contiene la logica administrativa de usuarios.

Anotaciones:

- `@Service`
- `@RequiredArgsConstructor`
- `@Transactional`

Dependencias:

- `UserRepository`
- `OrganizationRepository`
- `UserMapper`
- `PasswordEncoder`

Constructor generado:

```java
public UserServiceImpl(
        UserRepository userRepository,
        OrganizationRepository organizationRepository,
        UserMapper userMapper,
        PasswordEncoder passwordEncoder
) {
    this.userRepository = userRepository;
    this.organizationRepository = organizationRepository;
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
}
```

Metodo `create(...)`:

1. Verifica que el correo no exista.
2. Busca la organizacion.
3. Convierte `UserRequest` a `User` usando `UserMapper`.
4. Asigna la organizacion.
5. Encripta la contrasena.
6. Activa el usuario.
7. Sincroniza datos del perfil.
8. Guarda el usuario.
9. Devuelve `UserResponse`.

Metodo `findAll()`:

Busca todos los usuarios activos con su organizacion, los transforma a `UserResponse` y devuelve la lista.

Metodo `findById(Long id)`:

Busca el usuario por ID con organizacion. Si no existe, lanza `ResourceNotFoundException`.

Metodo `update(Long id, UserRequest request)`:

1. Busca el usuario.
2. Si el correo cambia, verifica que el nuevo correo no exista.
3. Actualiza campos con `userMapper.updateEntity(...)`.
4. Actualiza organizacion.
5. Vuelve a encriptar la contrasena.
6. Sincroniza perfil.
7. Guarda cambios.

Metodo `delete(Long id)`:

No elimina fisicamente el registro. Aplica borrado logico:

```java
user.setActive(false);
```

Esto conserva el historial y evita romper relaciones de base de datos.

Metodo privado `syncProfile(...)`:

Se encarga de crear o actualizar el perfil del usuario.

Reglas:

- Si no hay `phone`, `address` ni `bio` y el usuario no tiene perfil, no hace nada.
- Si hay datos de perfil y el usuario no tiene perfil, crea un `UserProfile`.
- Asigna telefono, direccion y biografia.

Este metodo conecta la entidad `User` con la entidad `UserProfile`.

## 5.13 `UserMapper.java`

Es una interfaz de MapStruct.

Anotacion:

```java
@Mapper(componentModel = "spring")
```

Esto hace que MapStruct genere una clase implementadora y que Spring pueda inyectarla como bean.

No tiene constructor escrito porque es una interfaz. La implementacion la genera MapStruct durante la compilacion.

Metodo `toEntity(UserRequest request)`:

Convierte un request en entidad `User`. Ignora campos delicados o controlados por el servicio:

- `password`: se ignora porque debe encriptarse manualmente.
- `active`: lo decide el servicio.
- `id`: lo genera la base de datos.
- `createdAt` y `updatedAt`: los maneja `BaseEntity`.
- `organization`: se busca en base de datos.
- `profile`: se maneja con `syncProfile`.
- `quotes`: no pertenece al formulario de usuario.

Metodo `toResponse(User user)`:

Convierte entidad en respuesta y extrae datos anidados:

- `organization.id` a `organizationId`.
- `organization.name` a `organizationName`.
- `profile.phone` a `phone`.
- `profile.address` a `address`.
- `profile.bio` a `bio`.

Metodo `updateEntity(UserRequest request, User user)`:

Actualiza una entidad existente sin tocar los campos que controla la base de datos o el servicio.

## 5.14 `BaseEntity.java`

Es una clase base para entidades.

Anotaciones:

- `@MappedSuperclass`: no crea tabla propia, pero sus campos se heredan en las tablas hijas.
- `@Getter` y `@Setter`: Lombok genera getters y setters.
- `@SuperBuilder`: permite construir entidades hijas incluyendo campos heredados.
- `@NoArgsConstructor`: genera constructor vacio.

Campos:

- `active`: indica si el registro esta activo.
- `createdAt`: fecha de creacion.
- `updatedAt`: fecha de actualizacion.

Constructor:

Lombok genera:

```java
public BaseEntity() {
}
```

Este constructor es necesario para JPA/Hibernate, porque las entidades deben poder construirse mediante reflexion.

Metodos:

- `onCreate()`: se ejecuta antes de insertar y asigna `createdAt` y `updatedAt`.
- `onUpdate()`: se ejecuta antes de actualizar y refresca `updatedAt`.

## 5.15 `User.java`

Representa la tabla `users`.

Anotaciones:

- `@Entity`: entidad JPA.
- `@Table(name = "users")`: tabla asociada.
- `@Getter` y `@Setter`: genera accesores.
- `@NoArgsConstructor`: constructor vacio para JPA.
- `@AllArgsConstructor`: constructor con todos los campos declarados en la entidad.
- `@SuperBuilder`: builder compatible con herencia.

Campos principales:

- `id`: llave primaria.
- `firstName`: nombre.
- `lastName`: apellido.
- `email`: correo unico.
- `password`: contrasena encriptada.
- `role`: rol del usuario.
- `organization`: organizacion a la que pertenece.
- `profile`: perfil del usuario.
- `quotes`: cotizaciones creadas por el usuario.

Constructores generados:

1. Constructor vacio:

```java
public User() {
}
```

Lo usa JPA para crear objetos al leer la base de datos.

2. Constructor con argumentos:

```java
public User(
        Long id,
        String firstName,
        String lastName,
        String email,
        String password,
        Role role,
        Organization organization,
        UserProfile profile,
        List<Quote> quotes
) {
    ...
}
```

Sirve para construir un usuario con todos sus campos directos.

3. Builder:

```java
User.builder()
    .firstName(...)
    .lastName(...)
    .email(...)
    .password(...)
    .role(...)
    .organization(...)
    .active(true)
    .build();
```

Este builder se usa en `AuthServiceImpl.register()`.

Implementacion de `UserDetails`:

La entidad `User` implementa `UserDetails`, que es una interfaz de Spring Security. Por eso define:

- `getAuthorities()`: devuelve el rol como autoridad.
- `getUsername()`: devuelve el email.
- `isAccountNonExpired()`: siempre `true`.
- `isAccountNonLocked()`: siempre `true`.
- `isCredentialsNonExpired()`: siempre `true`.
- `isEnabled()`: depende de `active`.

Esto permite que el usuario de base de datos sea entendido por Spring Security.

## 5.16 `UserProfile.java`

Representa la tabla `user_profiles`.

Campos:

- `id`
- `phone`
- `address`
- `bio`
- `user`

Relacion:

```java
@OneToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "user_id", nullable = false, unique = true)
private User user;
```

Cada perfil pertenece a un solo usuario. La columna `user_id` es unica, por eso un usuario solo puede tener un perfil.

Constructores generados:

- Constructor vacio para JPA.
- Constructor con todos los campos directos.
- Builder con `@SuperBuilder`.

Uso:

Se crea o actualiza desde `UserServiceImpl.syncProfile(...)`.

## 5.17 `Organization.java`

Representa la tabla `organizations`.

Campos relevantes para Usuarios y Seguridad:

- `id`
- `name`
- `ruc`
- `address`
- `phone`
- `country`
- `plan`
- `softLimit`
- `hardLimit`
- `currentUsage`
- `users`

Relacion con usuarios:

```java
@OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
private List<User> users = new ArrayList<>();
```

Una organizacion puede tener muchos usuarios.

Del lado del usuario:

```java
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "organization_id", nullable = false)
private Organization organization;
```

Muchos usuarios pueden pertenecer a una misma organizacion.

Constructores generados:

- Constructor vacio para JPA.
- Constructor con todos los campos directos.
- Builder con `@SuperBuilder`.

## 5.18 `Role.java`

Es un enum que define los roles disponibles:

```java
ROLE_ADMIN
ROLE_OPERATOR
ROLE_CLIENT
```

No tiene constructor explicito. Java crea internamente las instancias del enum.

Uso:

- En `User.role`.
- En `UserRequest.role`.
- En `UserResponse.role`.
- En `AuthServiceImpl.register()`, donde se asigna `ROLE_CLIENT`.
- En Spring Security, para validar permisos.

## 5.19 `UserRepository.java`

Extiende:

```java
JpaRepository<User, Long>
```

Esto le da metodos como:

- `save`
- `findById`
- `findAll`
- `delete`
- `existsById`

Metodos propios:

- `findByEmail(String email)`: busca usuario por correo.
- `existsByEmail(String email)`: verifica duplicados.
- `findAllActiveWithOrganization()`: lista usuarios activos con organizacion.
- `findByIdWithOrganization(Long id)`: busca usuario por ID cargando organizacion.

No tiene constructor porque es una interfaz. Spring Data JPA genera una implementacion automaticamente en tiempo de ejecucion.

## 5.20 `OrganizationRepository.java`

Extiende:

```java
JpaRepository<Organization, Long>
```

Metodos:

- `findByRuc(String ruc)`
- `existsByRuc(String ruc)`
- `findAllActive()`

En esta funcionalidad se usa principalmente para validar que la organizacion exista antes de registrar o crear usuarios.

## 5.21 `SecurityConfig.java`

Es la configuracion central de seguridad.

Anotaciones:

- `@Configuration`: clase de configuracion de Spring.
- `@EnableMethodSecurity`: activa anotaciones como `@PreAuthorize`.
- `@RequiredArgsConstructor`: constructor con dependencias.

Dependencias:

- `JwtAuthenticationFilter`
- `CustomUserDetailsService`
- `JwtAuthenticationEntryPoint`
- `JwtAccessDeniedHandler`
- `PasswordEncoder`

Constructor generado:

```java
public SecurityConfig(
        JwtAuthenticationFilter jwtAuthenticationFilter,
        CustomUserDetailsService userDetailsService,
        JwtAuthenticationEntryPoint authenticationEntryPoint,
        JwtAccessDeniedHandler accessDeniedHandler,
        PasswordEncoder passwordEncoder
) {
    ...
}
```

Bean `securityFilterChain(...)`:

Configura:

- CSRF desactivado.
- Manejo de errores 401 y 403.
- Sesiones stateless.
- Rutas publicas.
- Resto de rutas autenticadas.
- Proveedor de autenticacion.
- Filtro JWT antes de `UsernamePasswordAuthenticationFilter`.

Por que `STATELESS`:

Porque con JWT el servidor no guarda sesion. Cada peticion debe traer su token.

Bean `authenticationProvider()`:

Crea un `DaoAuthenticationProvider`, le asigna:

- `CustomUserDetailsService`
- `PasswordEncoder`

Este provider es el que valida usuarios contra la base de datos.

Bean `authenticationManager(...)`:

Expone el `AuthenticationManager` para que `AuthServiceImpl.login()` pueda autenticar.

## 5.22 `PasswordConfig.java`

Define el bean encargado de encriptar y comparar contrasenas.

Metodo:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

No tiene constructor explicito.

Importancia:

BCrypt es un algoritmo de hashing seguro para contrasenas. No permite recuperar la contrasena original, solo comparar una contrasena enviada con el hash almacenado.

## 5.23 `JwtService.java`

Se encarga de crear y validar tokens JWT.

Anotacion:

- `@Service`: Spring lo registra como servicio.

Campos configurados desde `application.yaml`:

- `secret`: clave secreta base64.
- `expiration`: tiempo de vida del token.

Constructor:

No tiene constructor explicito. Spring crea la clase con constructor vacio implicito e inyecta los valores usando `@Value`.

Metodos:

- `getSignKey()`: decodifica la clave base64 y crea una llave HMAC.
- `generateToken(UserDetails userDetails)`: crea un JWT con subject, fecha de emision, expiracion y firma HS256.
- `extractUsername(String token)`: obtiene el email desde el subject.
- `extractClaim(...)`: extrae cualquier dato del token.
- `isTokenValid(...)`: valida que el username coincida y que el token no haya expirado.
- `isTokenExpired(...)`: revisa la fecha de expiracion.

## 5.24 `JwtAuthenticationFilter.java`

Filtro que autentica cada peticion con JWT.

Anotaciones:

- `@Component`: Spring lo registra como componente.
- `@RequiredArgsConstructor`: constructor con dependencias.

Dependencias:

- `JwtService`
- `CustomUserDetailsService`

Constructor generado:

```java
public JwtAuthenticationFilter(
        JwtService jwtService,
        CustomUserDetailsService userDetailsService
) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
}
```

Metodo principal:

- `doFilterInternal(...)`: extrae token, valida usuario, crea autenticacion y continua la peticion.

Metodo auxiliar:

- `getTokenFromRequest(...)`: obtiene el token desde la cabecera `Authorization`.

## 5.25 `CustomUserDetailsService.java`

Adapta los usuarios de la base de datos al formato que Spring Security necesita.

Anotaciones:

- `@Service`
- `@RequiredArgsConstructor`

Dependencia:

- `UserRepository`

Constructor generado:

```java
public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
}
```

Metodo:

```java
loadUserByUsername(String email)
```

Aunque se llama `username`, en este sistema se usa el correo como identificador.

Devuelve un objeto `UserDetails` con:

- username: email.
- password: password encriptada.
- authorities: rol del usuario.
- disabled: depende de `active`.

Si el usuario no existe, lanza `UsernameNotFoundException`.

## 5.26 `JwtAuthenticationEntryPoint.java`

Maneja errores `401 Unauthorized`.

Ocurre cuando:

- No se envio token.
- El token no es valido.
- El usuario no esta autenticado.

Constructor:

No tiene constructor explicito. Java genera constructor vacio.

Metodo:

```java
commence(...)
```

Construye una respuesta JSON con:

- timestamp
- status `401`
- error `Unauthorized`
- message
- path

## 5.27 `JwtAccessDeniedHandler.java`

Maneja errores `403 Forbidden`.

Ocurre cuando:

- El usuario si esta autenticado.
- Pero no tiene permisos para acceder al recurso.

Constructor:

No tiene constructor explicito. Java genera constructor vacio.

Metodo:

```java
handle(...)
```

Devuelve JSON con:

- timestamp
- status `403`
- error `Forbidden`
- message
- path

## 5.28 `GlobalExceptionHandler.java`

Centraliza errores de negocio y validacion.

Anotacion:

- `@RestControllerAdvice`: captura excepciones de controladores y devuelve JSON.

Constructor:

No tiene constructor explicito. Java genera constructor vacio.

Metodos:

- `handleResourceNotFound(...)`: devuelve `404`.
- `handleAlreadyExists(...)`: devuelve `409`.
- `handleBusinessRule(...)`: devuelve `400`.
- `handleValidation(...)`: devuelve `400` cuando fallan anotaciones como `@NotBlank`.
- `handleException(...)`: devuelve `500` para errores no controlados.

## 5.29 `ApiError.java`

Modelo de error devuelto por la API.

Anotaciones:

- `@Data`: genera getters, setters, `toString`, `equals` y `hashCode`.
- `@Builder`: genera un builder.

Campos:

- `timestamp`
- `status`
- `error`
- `message`
- `path`

Constructor:

Aunque no esta escrito, `@Builder` permite construir objetos asi:

```java
ApiError.builder()
    .timestamp(...)
    .status(...)
    .error(...)
    .message(...)
    .path(...)
    .build();
```

Este patron se usa en `GlobalExceptionHandler`.

## 5.30 Excepciones personalizadas

Archivos:

- `ResourceAlreadyExistsException.java`
- `ResourceNotFoundException.java`
- `BusinessRuleException.java`

Todas extienden `RuntimeException`.

Cada una tiene un constructor explicito:

```java
public ResourceAlreadyExistsException(String message) {
    super(message);
}
```

La misma idea aplica para las otras excepciones.

Funcion del constructor:

- Recibe un mensaje.
- Lo envia a la clase padre `RuntimeException`.
- Luego `GlobalExceptionHandler` puede leer `ex.getMessage()` y devolverlo al cliente.

## 6. Relaciones entre entidades

## 6.1 Usuario y organizacion

Relacion:

- Una organizacion tiene muchos usuarios.
- Un usuario pertenece a una organizacion.

En `User.java`:

```java
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "organization_id", nullable = false)
private Organization organization;
```

En `Organization.java`:

```java
@OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
private List<User> users = new ArrayList<>();
```

Base de datos:

- Tabla `users`
- Columna `organization_id`
- Llave foranea hacia `organizations.id`

## 6.2 Usuario y perfil

Relacion:

- Un usuario puede tener un perfil.
- Un perfil pertenece a un usuario.

En `User.java`:

```java
@OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
private UserProfile profile;
```

En `UserProfile.java`:

```java
@OneToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "user_id", nullable = false, unique = true)
private User user;
```

El `cascade = CascadeType.ALL` permite que, al guardar el usuario, tambien se persista el perfil asociado.

## 6.3 Usuario y cotizaciones

En `User.java`:

```java
@OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
private List<Quote> quotes = new ArrayList<>();
```

Esta relacion indica que un usuario puede haber creado varias cotizaciones. Aunque no es la parte central de login, forma parte del modelo de usuario.

## 7. Modelo de base de datos relacionado

Las migraciones Flyway y el script fijo incluyen tablas relacionadas con esta funcionalidad:

- `users`
- `user_profiles`
- `organizations`

Tabla `users`:

- Guarda datos basicos del usuario.
- Tiene correo unico.
- Guarda password encriptado.
- Guarda rol.
- Tiene `organization_id`.
- Hereda campos logicos como `active`, `created_at`, `updated_at`.

Tabla `user_profiles`:

- Guarda telefono, direccion y biografia.
- Tiene `user_id` unico.
- Permite separar datos personales adicionales del usuario principal.

Tabla `organizations`:

- Agrupa usuarios.
- Permite que el sistema sea multi-organizacion.

## 8. Dependencias generales del proyecto y para que sirven

Las dependencias estan en `pom.xml`.

### `spring-boot-starter-web`

Permite crear API REST con Spring MVC. Aporta controladores, JSON, manejo HTTP y servidor embebido.

Se usa en:

- `@RestController`
- `@RequestMapping`
- `@PostMapping`
- `@GetMapping`
- `ResponseEntity`

### `spring-boot-starter-data-jpa`

Permite usar JPA/Hibernate para mapear clases Java a tablas de base de datos.

Se usa en:

- `@Entity`
- `@Table`
- `@Id`
- `@ManyToOne`
- `@OneToOne`
- `JpaRepository`

### `spring-boot-starter-security`

Permite manejar autenticacion y autorizacion.

Se usa en:

- `SecurityFilterChain`
- `AuthenticationManager`
- `DaoAuthenticationProvider`
- `UserDetailsService`
- `UserDetails`
- `@PreAuthorize`
- filtros de seguridad.

### `spring-boot-starter-validation`

Permite validar DTOs con anotaciones.

Se usa en:

- `@Valid`
- `@NotBlank`
- `@NotNull`
- `@Email`
- `@Size`

### `mysql-connector-j`

Driver para conectar la aplicacion con MySQL.

Se usa mediante:

```yaml
spring.datasource.url: jdbc:mysql://localhost:3306/shipcore_db
```

### `lombok`

Reduce codigo repetitivo.

Se usa para generar:

- constructores
- getters
- setters
- builders
- metodos comunes

Anotaciones vistas:

- `@Getter`
- `@Setter`
- `@NoArgsConstructor`
- `@AllArgsConstructor`
- `@RequiredArgsConstructor`
- `@SuperBuilder`
- `@Data`
- `@Builder`

### `spring-boot-devtools`

Ayuda durante el desarrollo. Permite reinicios automaticos y mejoras al trabajar localmente.

No es una dependencia de la logica de seguridad, pero facilita el desarrollo.

### `jjwt-api`

Define la API principal para trabajar con JWT.

Se usa en `JwtService` para crear, parsear y validar tokens.

### `jjwt-impl`

Implementacion en tiempo de ejecucion de JJWT.

Es necesaria para que la API de JWT funcione al ejecutar la aplicacion.

### `jjwt-jackson`

Integra JJWT con Jackson para serializacion/deserializacion JSON de claims.

Ayuda a procesar internamente la informacion del token.

### `mapstruct`

Genera mappers entre DTOs y entidades.

Se usa en:

- `UserMapper`

Evita escribir manualmente conversiones repetitivas.

### `springdoc-openapi-starter-webmvc-ui`

Genera documentacion OpenAPI/Swagger.

Permite revisar y probar endpoints desde Swagger UI.

Rutas permitidas sin autenticacion:

- `/swagger-ui/**`
- `/v3/api-docs/**`

### `flyway-core`

Administra migraciones de base de datos.

Se usa con archivos como:

- `V1__init_schema.sql`
- `V2__align_existing_schema.sql`
- `V3__frontend_models_alignment.sql`
- `V4__repair_preexisting_frontend_tables.sql`

### `flyway-mysql`

Complemento de Flyway para trabajar especificamente con MySQL.

### `spring-boot-starter-actuator`

Expone endpoints de monitoreo y estado de la aplicacion.

Sirve para observar salud, metricas y comportamiento operativo.

### `spring-boot-starter-test`

Dependencia para pruebas automatizadas con Spring Boot.

Incluye herramientas para test unitarios y de integracion.

### `spring-security-test`

Agrega utilidades para probar seguridad en tests.

Permite simular usuarios autenticados, roles y validaciones de seguridad.

## 9. Configuracion relacionada

Archivo:

```text
src/main/resources/application.yaml
```

Configuracion principal:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/shipcore_db?createDatabaseIfNotExist=true&serverTimezone=America/Lima
    username: root
    password: ${DB_PASSWORD:190706}

  jpa:
    hibernate:
      ddl-auto: validate

jwt:
  secret: ...
  expiration: 86400000
```

Puntos importantes:

- La base de datos se llama `shipcore_db`.
- Hibernate esta en `validate`, por eso no crea tablas automaticamente; valida que lo existente coincida con las entidades.
- Flyway se encarga de migraciones.
- El token dura `86400000` milisegundos, es decir, 24 horas.
- La clave JWT se toma desde `jwt.secret`.

## 10. Resumen de responsabilidades por capa

| Capa | Archivos | Responsabilidad |
|---|---|---|
| API | `AuthController`, `UserController` | Recibir peticiones y devolver respuestas HTTP |
| DTO request | `LoginRequest`, `RegisterRequest`, `UserRequest` | Definir datos de entrada y validaciones |
| DTO response | `AuthResponse`, `UserResponse`, `OrganizationResponse` | Definir datos de salida sin exponer password |
| Servicio | `AuthServiceImpl`, `UserServiceImpl` | Aplicar reglas de negocio |
| Repositorio | `UserRepository`, `OrganizationRepository` | Consultar y guardar datos |
| Entidad | `User`, `UserProfile`, `Organization`, `BaseEntity` | Representar tablas y relaciones |
| Mapper | `UserMapper` | Convertir entre DTOs y entidades |
| Seguridad | `SecurityConfig`, `JwtAuthenticationFilter`, `JwtService`, `CustomUserDetailsService` | Proteger rutas, validar token y cargar usuarios |
| Errores | `GlobalExceptionHandler`, `ApiError`, excepciones | Devolver errores consistentes |

## 11. Explicacion final de la logica completa

Cuando un usuario se registra, el sistema valida sus datos, revisa que el correo no este repetido, confirma que la organizacion exista, encripta la contrasena y guarda el usuario con rol `ROLE_CLIENT`. Luego crea un token JWT y lo devuelve junto con los datos del usuario y la organizacion.

Cuando un usuario inicia sesion, el sistema usa Spring Security para comprobar el correo y la contrasena. La contrasena enviada se compara con la version encriptada guardada en base de datos. Si todo es correcto, se genera un JWT.

Cuando el cliente quiere entrar a una ruta protegida, debe mandar el JWT en la cabecera `Authorization`. El filtro `JwtAuthenticationFilter` extrae el token, obtiene el correo, carga el usuario desde base de datos, valida que el token sea correcto y registra la autenticacion en el contexto de Spring Security.

La configuracion de seguridad permite entrar libremente a login, registro y Swagger, pero exige autenticacion para el resto de rutas. Ademas, el controlador de usuarios exige rol administrador. Por eso un usuario comun puede autenticarse, pero no necesariamente administrar usuarios.

La administracion de usuarios permite crear, listar, consultar, actualizar y desactivar usuarios. La eliminacion es logica, porque se cambia `active` a `false` en lugar de borrar el registro. Esto conserva la integridad de datos y permite mantener historial.

En conjunto, esta funcionalidad resuelve la identidad del sistema: quien entra, como entra, que permisos tiene, como se representa en base de datos y como se protege la API.
