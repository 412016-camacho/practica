# AI Context - Proyecto Final Full-Stack

## 📋 Descripción General
Proyecto final tipo tesina que integra todas las tecnologías aprendidas. Aplicación full-stack con arquitectura separada (backend + frontend).

---

## 🎯 Stack Tecnológico

### Backend
- **Java 17+**
- **Spring Boot 3.x**
- **Base de datos**: H2 (en memoria para desarrollo)
- **Testing**: JUnit 5, Mockito, Reflection

### Frontend
- **Angular 19** (Standalone components)
- **TypeScript**
- **Tailwind CSS**
- **RxJS**

---

## 📁 Estructura del Proyecto

```
proyecto-final/
├── backend/
│   ├── src/main/java/com/proyecto/
│   │   ├── entity/           # Entidades JPA
│   │   ├── repository/       # Interfaces JPA Repository
│   │   ├── service/          # Interfaces de servicios
│   │   ├── service/impl/     # Implementaciones de servicios
│   │   ├── dto/              # Data Transfer Objects
│   │   ├── controller/       # REST Controllers
│   │   ├── config/           # Configuraciones (ModelMapper, etc.)
│   │   └── util/             # Utilidades (GlobalExceptionHandler, etc.)
│   └── src/main/resources/
│       └── application.properties  # Ya preconfigurado
│
└── frontend/
    └── src/app/
        ├── models/           # Interfaces/modelos TypeScript
        ├── services/         # Servicios Angular
        ├── shared/
        │   └── interceptors/ # Interceptor snake_case ↔ camelCase
        ├── routes/           # Rutas de la aplicación
        ├── environments/     # Configuración de entornos
        └── [components]/     # Componentes por funcionalidad
```

---

## 🔧 Backend - Convenciones y Reglas

### Dependencias Principales
- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-validation`
- `lombok`
- `modelmapper`
- `h2` (runtime)
- `spring-boot-starter-test`

### Nomenclatura
- **Formato interno**: `camelCase`
- **Formato API (JSON)**: `snake_case` (usando `@JsonProperty` en DTOs)

### Estructura de Capas (MVC)

#### 1. **Entity** (`entity/`)
```java
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    // Relaciones, validaciones, etc.
}
```

#### 2. **Repository** (`repository/`)
```java
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}
```

#### 3. **Service** (`service/` + `service/impl/`)

**Interface** (`service/`):
```java
public interface UsuarioService {
    UsuarioDto crear(UsuarioDto dto);
    UsuarioDto buscarPorId(Long id);
    List<UsuarioDto> listarTodos();
    // etc.
}
```

**Implementación** (`service/impl/`):
```java
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {
    
    private final UsuarioRepository repository;
    private final ModelMapper modelMapper;
    
    @Override
    @Transactional
    public UsuarioDto crear(UsuarioDto dto) {
        Usuario entity = modelMapper.map(dto, Usuario.class);
        Usuario guardado = repository.save(entity);
        return modelMapper.map(guardado, UsuarioDto.class);
    }
    
    // Implementar resto de métodos
}
```

#### 4. **DTO** (`dto/`)
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDto {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("nombre_completo")
    @NotBlank(message = "El nombre es obligatorio")
    private String nombreCompleto;
    
    @JsonProperty("correo_electronico")
    @Email(message = "Email inválido")
    private String correoElectronico;
    
    // Otros campos con @JsonProperty para snake_case
}
```

#### 5. **Controller** (`controller/`)
```java
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    
    private final UsuarioService service;
    
    @PostMapping
    public ResponseEntity<UsuarioDto> crear(@Valid @RequestBody UsuarioDto dto) {
        UsuarioDto creado = service.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> buscarPorId(@PathVariable Long id) {
        UsuarioDto dto = service.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }
    
    // Otros endpoints con ResponseEntity<>
}
```

### Manejo Global de Excepciones

**Archivo**: `util/GlobalExceptionHandler.java`

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorApi> handleEntityNotFound(EntityNotFoundException ex) {
        ErrorApi error = ErrorApi.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Not Found")
            .message(ex.getMessage())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorApi> handleValidationException(MethodArgumentNotValidException ex) {
        // Manejar errores de validación
        ErrorApi error = ErrorApi.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Error")
            .message("Datos inválidos")
            .build();
        return ResponseEntity.badRequest().body(error);
    }
    
    // Otros manejadores según sea necesario
}
```

**DTO de Error**: `dto/ErrorApi.java`

```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorApi {
    
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    
    @JsonProperty("status")
    private Integer status;
    
    @JsonProperty("error")
    private String error;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("path")
    private String path;  // opcional
}
```

### Configuración (`config/`)

**ModelMapperConfig.java**:
```java
@Configuration
public class ModelMapperConfig {
    
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
            .setMatchingStrategy(MatchingStrategies.STRICT);
        return mapper;
    }
}
```

### Testing

**IMPORTANTE**: Solo testear `controllers` y `services`.

#### Test de Service (`service/impl/`)
```java
@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {
    
    @Mock
    private UsuarioRepository repository;
    
    @Mock
    private ModelMapper modelMapper;
    
    @InjectMocks
    private UsuarioServiceImpl service;
    
    @Test
    void crear_DeberiaRetornarUsuarioDto() {
        // Arrange
        UsuarioDto dto = UsuarioDto.builder()
            .nombreCompleto("Test User")
            .build();
        Usuario entity = new Usuario();
        
        when(modelMapper.map(dto, Usuario.class)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(modelMapper.map(entity, UsuarioDto.class)).thenReturn(dto);
        
        // Act
        UsuarioDto resultado = service.crear(dto);
        
        // Assert
        assertNotNull(resultado);
        verify(repository).save(entity);
    }
}
```

#### Test de Controller (`controller/`)
```java
@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UsuarioService service;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void crear_DeberiaRetornar201() throws Exception {
        // Arrange
        UsuarioDto dto = UsuarioDto.builder()
            .nombreCompleto("Test")
            .build();
        
        when(service.crear(any())).thenReturn(dto);
        
        // Act & Assert
        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nombre_completo").value("Test"));
    }
}
```

**Uso de Reflection** (cuando sea necesario acceder a campos privados):
```java
@Test
void testConReflection() throws Exception {
    Usuario usuario = new Usuario();
    Field campoPrivado = Usuario.class.getDeclaredField("nombre");
    campoPrivado.setAccessible(true);
    campoPrivado.set(usuario, "Valor Test");
    
    assertEquals("Valor Test", usuario.getNombre());
}
```

---

## 🎨 Frontend - Convenciones y Reglas

### Características Generales
- **Todos los componentes son standalone** (no usar NgModules)
- **Directivas modernas**: `@if`, `@for`, `@switch`
- **Inyección directa**: Usar `inject()` en lugar de constructor injection
- **Formularios**: Template-Driven Forms
- **RxJS**: Usar operadores siempre que sea posible

### Nomenclatura
- **Formato interno**: `camelCase`
- **Formato API**: `snake_case` (convertido automáticamente por interceptor)

### Estructura de Carpetas

```
src/app/
├── models/              # Interfaces TypeScript
├── services/            # Servicios de lógica y HTTP
├── shared/
│   └── interceptors/    # Interceptor snake_case ↔ camelCase (YA CREADO)
├── routes/              # Configuración de rutas
├── environments/        # Variables de entorno
└── [feature-name]/      # Componentes agrupados por funcionalidad
    ├── components/
    ├── services/        # Servicios específicos del feature (opcional)
    └── models/          # Modelos específicos del feature (opcional)
```

### Models (`models/`)

```typescript
// models/usuario.model.ts
export interface Usuario {
  id?: number;
  nombreCompleto: string;
  correoElectronico: string;
  fechaCreacion?: Date;
}

export interface ErrorApi {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path?: string;
}
```

### Services (`services/`)

```typescript
// services/usuario.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, catchError } from 'rxjs';
import { Usuario } from '../models/usuario.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/usuarios`;

  obtenerTodos(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(this.apiUrl).pipe(
      map(usuarios => usuarios.map(u => ({
        ...u,
        fechaCreacion: new Date(u.fechaCreacion!)
      }))),
      catchError(error => {
        console.error('Error al obtener usuarios', error);
        throw error;
      })
    );
  }

  crear(usuario: Usuario): Observable<Usuario> {
    return this.http.post<Usuario>(this.apiUrl, usuario);
  }

  // Otros métodos CRUD
}
```

### Comunicación entre Componentes

#### Opción 1: Parent-Child con `@Input` / `@Output`
```typescript
// hijo.component.ts
import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-hijo',
  standalone: true,
  template: `
    <button (click)="emitirEvento()">
      {{ dato() }}
    </button>
  `
})
export class HijoComponent {
  // Input (nuevo formato Angular 19)
  dato = input.required<string>();
  
  // Output
  eventoClick = output<string>();
  
  emitirEvento() {
    this.eventoClick.emit('Dato desde hijo');
  }
}
```

```typescript
// padre.component.ts
import { Component } from '@angular/core';
import { HijoComponent } from './hijo/hijo.component';

@Component({
  selector: 'app-padre',
  standalone: true,
  imports: [HijoComponent],
  template: `
    <app-hijo 
      [dato]="mensaje"
      (eventoClick)="manejarEvento($event)"
    />
  `
})
export class PadreComponent {
  mensaje = 'Hola desde padre';
  
  manejarEvento(dato: string) {
    console.log('Recibido:', dato);
  }
}
```

#### Opción 2: Componentes sin relación - Subject/BehaviorSubject
```typescript
// services/comunicacion.service.ts
import { Injectable } from '@angular/core';
import { Subject, BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ComunicacionService {
  // Subject - no tiene valor inicial
  private mensajeSubject = new Subject<string>();
  public mensaje$ = this.mensajeSubject.asObservable();
  
  // BehaviorSubject - tiene valor inicial
  private estadoSubject = new BehaviorSubject<boolean>(false);
  public estado$ = this.estadoSubject.asObservable();
  
  enviarMensaje(mensaje: string) {
    this.mensajeSubject.next(mensaje);
  }
  
  cambiarEstado(estado: boolean) {
    this.estadoSubject.next(estado);
  }
  
  obtenerEstadoActual(): boolean {
    return this.estadoSubject.value;
  }
}
```

### Template-Driven Forms

```typescript
// formulario.component.ts
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UsuarioService } from '../../services/usuario.service';
import { Usuario } from '../../models/usuario.model';

@Component({
  selector: 'app-formulario-usuario',
  standalone: true,
  imports: [FormsModule],
  template: `
    <form #usuarioForm="ngForm" (ngSubmit)="onSubmit(usuarioForm)">
      <div class="mb-4">
        <label class="block text-sm font-medium mb-2">
          Nombre Completo
        </label>
        <input
          type="text"
          name="nombreCompleto"
          [(ngModel)]="usuario.nombreCompleto"
          required
          minlength="3"
          #nombreCompleto="ngModel"
          class="w-full px-3 py-2 border rounded-lg"
          [class.border-red-500]="nombreCompleto.invalid && nombreCompleto.touched"
        />
        @if (nombreCompleto.invalid && nombreCompleto.touched) {
          <p class="text-red-500 text-sm mt-1">
            El nombre debe tener al menos 3 caracteres
          </p>
        }
      </div>

      <div class="mb-4">
        <label class="block text-sm font-medium mb-2">
          Email
        </label>
        <input
          type="email"
          name="correoElectronico"
          [(ngModel)]="usuario.correoElectronico"
          required
          email
          #email="ngModel"
          class="w-full px-3 py-2 border rounded-lg"
          [class.border-red-500]="email.invalid && email.touched"
        />
        @if (email.invalid && email.touched) {
          <p class="text-red-500 text-sm mt-1">
            Email inválido
          </p>
        }
      </div>

      <button
        type="submit"
        [disabled]="usuarioForm.invalid"
        class="px-4 py-2 bg-blue-500 text-white rounded-lg disabled:opacity-50"
      >
        Guardar
      </button>
    </form>
  `
})
export class FormularioUsuarioComponent {
  private usuarioService = inject(UsuarioService);
  
  usuario: Usuario = {
    nombreCompleto: '',
    correoElectronico: ''
  };
  
  onSubmit(form: any) {
    if (form.valid) {
      this.usuarioService.crear(this.usuario).subscribe({
        next: (resultado) => {
          console.log('Usuario creado:', resultado);
          form.resetForm();
        },
        error: (error) => {
          console.error('Error:', error);
        }
      });
    }
  }
}
```

### Directivas Modernas (@if, @for, @switch)

```typescript
@Component({
  selector: 'app-lista-usuarios',
  standalone: true,
  imports: [DatePipe],
  template: `
    <div class="p-4">
      @if (cargando) {
        <p>Cargando usuarios...</p>
      } @else if (error) {
        <div class="bg-red-100 text-red-700 p-4 rounded">
          {{ mensajeError }}
        </div>
      } @else {
        <div class="grid gap-4">
          @for (usuario of usuarios; track usuario.id) {
            <div class="border p-4 rounded-lg">
              <h3 class="font-bold">{{ usuario.nombreCompleto }}</h3>
              <p class="text-gray-600">{{ usuario.correoElectronico }}</p>
              
              @switch (usuario.estado) {
                @case ('activo') {
                  <span class="text-green-500">Activo</span>
                }
                @case ('inactivo') {
                  <span class="text-gray-500">Inactivo</span>
                }
                @default {
                  <span class="text-yellow-500">Pendiente</span>
                }
              }
            </div>
          } @empty {
            <p class="text-gray-500">No hay usuarios registrados</p>
          }
        </div>
      }
    </div>
  `
})
export class ListaUsuariosComponent implements OnInit {
  private usuarioService = inject(UsuarioService);
  
  usuarios: Usuario[] = [];
  cargando = false;
  error = false;
  mensajeError = '';
  
  ngOnInit() {
    this.cargarUsuarios();
  }
  
  cargarUsuarios() {
    this.cargando = true;
    this.error = false;
    
    this.usuarioService.obtenerTodos().subscribe({
      next: (data) => {
        this.usuarios = data;
        this.cargando = false;
      },
      error: (err) => {
        this.error = true;
        this.mensajeError = err.error?.message || 'Error al cargar usuarios';
        this.cargando = false;
      }
    });
  }
}
```

### RxJS - Operadores Comunes

```typescript
import { Component, inject, OnInit } from '@angular/core';
import { 
  map, 
  filter, 
  debounceTime, 
  distinctUntilChanged,
  switchMap,
  catchError,
  tap,
  finalize
} from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-busqueda',
  standalone: true,
  template: `
    <input
      type="text"
      (input)="onBuscar($event)"
      placeholder="Buscar usuarios..."
      class="w-full px-4 py-2 border rounded-lg"
    />
    
    @for (resultado of resultados; track resultado.id) {
      <div class="p-2 border-b">{{ resultado.nombreCompleto }}</div>
    }
  `
})
export class BusquedaComponent {
  private usuarioService = inject(UsuarioService);
  
  resultados: Usuario[] = [];
  busquedaSubject = new Subject<string>();
  
  ngOnInit() {
    this.busquedaSubject.pipe(
      debounceTime(300),                    // Esperar 300ms después de que el usuario deje de escribir
      distinctUntilChanged(),                // Solo si el valor cambió
      filter(termino => termino.length >= 3), // Solo buscar si hay 3+ caracteres
      tap(() => console.log('Buscando...')), // Efecto secundario
      switchMap(termino =>                   // Cancelar búsqueda anterior
        this.usuarioService.buscar(termino).pipe(
          catchError(error => {
            console.error('Error en búsqueda', error);
            return of([]);                   // Retornar array vacío en caso de error
          })
        )
      ),
      finalize(() => console.log('Búsqueda finalizada'))
    ).subscribe(resultados => {
      this.resultados = resultados;
    });
  }
  
  onBuscar(event: Event) {
    const termino = (event.target as HTMLInputElement).value;
    this.busquedaSubject.next(termino);
  }
}
```

### Interceptor (YA CREADO - No modificar)

**Nota**: El interceptor ya está configurado y transforma automáticamente:
- `camelCase` → `snake_case` en requests
- `snake_case` → `camelCase` en responses

No es necesario agregar lógica adicional en los servicios para la transformación de nombres.

### Environments (`environments/`)

```typescript
// environments/environment.development.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};

// environments/environment.ts
export const environment = {
  production: true,
  apiUrl: 'https://api-produccion.com/api'
};
```

### Routes (`routes/`)

```typescript
// app.routes.ts
import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },
  {
    path: 'home',
    loadComponent: () => import('./home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'usuarios',
    loadComponent: () => import('./usuarios/lista/lista-usuarios.component')
      .then(m => m.ListaUsuariosComponent)
  },
  {
    path: 'usuarios/nuevo',
    loadComponent: () => import('./usuarios/formulario/formulario-usuario.component')
      .then(m => m.FormularioUsuarioComponent)
  },
  {
    path: '**',
    redirectTo: 'home'
  }
];
```

---

## 🎯 Buenas Prácticas Generales

### Backend
✅ Usar `@Transactional` en métodos de servicio que modifiquen datos  
✅ Validar datos de entrada con `@Valid` en controllers  
✅ Usar `ResponseEntity<>` en todos los endpoints  
✅ Manejar excepciones de forma centralizada con `@RestControllerAdvice`  
✅ Usar DTOs para evitar exponer entidades directamente  
✅ Nombrar endpoints en plural: `/api/usuarios`, `/api/productos`  
✅ Usar verbos HTTP correctamente (GET, POST, PUT, DELETE)  

### Frontend
✅ Usar `inject()` para inyección de dependencias  
✅ Preferir signals cuando sea apropiado (Angular 19)  
✅ Unsubscribe de Observables (o usar `async` pipe)  
✅ Usar `trackBy` en `@for` para optimizar rendimiento  
✅ Componentes pequeños y reutilizables  
✅ Separar lógica de negocio en servicios  
✅ Usar Tailwind con clases utilitarias  
✅ Lazy loading de rutas cuando sea posible  

### Testing
✅ Testear solo `controllers` y `services`  
✅ Usar nombres descriptivos: `crear_DeberiaRetornarUsuarioDto`  
✅ Estructura AAA: Arrange, Act, Assert  
✅ Mockear dependencias externas  
✅ Usar `@WebMvcTest` para controllers  
✅ Usar `@ExtendWith(MockitoExtension.class)` para services  

---

## 🚀 Comandos Útiles

### Backend
```bash
# Ejecutar aplicación
./mvnw spring-boot:run

# Ejecutar tests
./mvnw test

# Consola H2 (si está habilitada)
http://localhost:8080/h2-console
```

### Frontend
```bash
# Instalar dependencias
npm install

# Servidor de desarrollo
ng serve

# Build de producción
ng build

# Ejecutar tests
ng test

# Generar componente standalone
ng generate component nombre --standalone

# Generar servicio
ng generate service services/nombre
```

---

## 📝 Notas Adicionales

- **H2 Database**: Configurada en memoria, los datos se pierden al reiniciar la aplicación
- **CORS**: Debe estar configurado en Spring Boot para permitir requests desde Angular
- **Interceptor**: Ya maneja la conversión de nombres, no requiere configuración adicional
- **application.properties**: Ya está preconfigurado, no modificar sin necesidad
- **Standalone Components**: No usar `@NgModule`, todos los componentes son standalone
- **Template-Driven Forms**: Preferir sobre Reactive Forms para este proyecto

---

## 💡 Cuando generes código:

### Para Backend:
1. Empezar por la entidad
2. Crear el repository
3. Definir interface del service
4. Implementar el service
5. Crear los DTOs con `@JsonProperty`
6. Implementar el controller
7. Escribir tests para service y controller

### Para Frontend:
1. Definir el modelo (interface)
2. Crear el servicio con métodos HTTP
3. Crear el componente standalone
4. Implementar la lógica con RxJS
5. Diseñar el template con Tailwind
6. Configurar la ruta si es necesario

---

**Fecha de creación**: Febrero 2026  
**Versión**: 1.0  
**Propósito**: Proyecto final integrador - Tesina