# VetGo – Sistema de gestión para la Clínica Veterinaria «Patitas»

Trabajo Práctico Obligatorio de **Programación Orientada a Objetos** (UADE) – Propuesta B: Clínica Veterinaria.

- **Integrantes:** Erik Berot (LU 1227491), Matías Rosental (LU 1231032), Máximo Rombolá (LU 1207165)
- **Profesora:** María Ángela León

VetGo es una aplicación de escritorio en Java que permite a una clínica veterinaria registrar dueños y mascotas, asignar y gestionar turnos (incluidas urgencias), llevar la historia clínica de cada mascota, controlar el stock de medicamentos y vacunas por lotes (FEFO), y facturar las consultas con distintos medios de pago. Los datos se guardan en un archivo local y se recuperan al volver a abrir la aplicación.

---

## 1. Descripción del sistema

El sistema se usa desde tres vistas, una por rol. No hay inicio de sesión: al abrir la aplicación se elige con qué rol se quiere trabajar (el dueño de la mascota no opera el sistema, S-01).

| Rol | Qué puede hacer |
|---|---|
| **Recepcionista** | Registrar dueños y mascotas (perro, gato, ave, conejo) · transferir una mascota · asignar, reprogramar y cancelar turnos · registrar ausencias · atender urgencias · facturar consultas confirmadas · anular facturas con nota de crédito · cobrar la seña a dueños con 3 ausencias seguidas · consultar dueños, mascotas y turnos |
| **Veterinario** | Iniciar una consulta desde un turno · registrar peso, diagnóstico y observaciones · indicar tratamientos · aplicar vacunas · confirmar la consulta (descuenta stock) · registrar notas rectificatorias · consultar la historia clínica |
| **Administrador** | Alta/baja/modificación de veterinarios (con horarios y guardias), recepcionistas, productos, vacunas y proveedores · cargar lotes · emitir reportes (turnos por veterinario, stock bajo, lotes próximos a vencer) · emitir recordatorios de vacunas |

### Reglas de negocio principales que implementa

- Duración de turnos por tipo de atención: consulta general 30 min, vacunación 15 min, cirugía 120 min, urgencia 30 min. Un veterinario solo atiende dentro de su horario y no puede tener turnos superpuestos.
- Cancelación sin cargo hasta 2 h antes; después se genera una factura por el 20 % de la consulta general.
- Tres ausencias seguidas bloquean al dueño hasta que pague una seña (monto fijo configurable), que se descuenta de su próxima factura.
- Urgencias: se atienden con el veterinario de guardia; los turnos que pisa se desplazan en cascada y se notifica a cada dueño.
- No se indica medicamento si la consulta no tiene peso registrado. El stock se verifica al indicar el tratamiento pero se descuenta recién al confirmar la consulta, consumiendo primero el lote que vence antes (FEFO) y nunca lotes vencidos. Se genera una alerta al administrador cuando un producto llega al stock mínimo.
- Vacunas: se valida especie y edad mínima, se registra lote y fecha y se calcula el próximo refuerzo; recordatorio 7 días antes y vencida a los 30 días de no aplicar el refuerzo.
- Facturación: efectivo 10 % de descuento, débito sin ajuste, crédito en cuotas 8 % de recargo; los dueños con plan de salud tienen 15 % de descuento (no se acumula con el de efectivo; con crédito se aplica el 15 % y luego el 8 %). Una factura emitida no se modifica: se anula con una nota de crédito por el total.
- Las notificaciones (recordatorios, reprogramaciones, alertas) se simulan por consola; no se envían correos reales.

La especificación completa (casos de uso, supuestos, diagramas y decisiones de diseño) está en el informe final de la carpeta [`docs/`](docs/).

---

## 2. Requisitos

- **JDK 11 o superior** (el código usa `String.isBlank()`). Se compiló y probó con OpenJDK 21.
- Para la interfaz gráfica (`AppSwing` y la ventana de selección de `AppConsola`) hace falta un JDK/JRE con soporte de escritorio (no la variante *headless*) y un entorno gráfico.
- No usa librerías externas ni gestor de dependencias: solo la biblioteca estándar de Java.

---

## 3. Instrucciones de ejecución

Todos los comandos se ejecutan **desde la raíz del repositorio** (el archivo de datos `vetgo.dat` se crea en el directorio desde el que se lanza la aplicación).

### Compilar

Linux / macOS:

```bash
mkdir -p out/main
javac -encoding UTF-8 -d out/main $(find src -name '*.java')
```

Windows (PowerShell):

```powershell
New-Item -ItemType Directory -Force out\main | Out-Null
javac -encoding UTF-8 -d out\main (Get-ChildItem -Recurse src -Filter *.java).FullName
```

### Ejecutar

Hay dos puntos de entrada:

| Comando | Qué abre |
|---|---|
| `java -cp out/main AppSwing` | Aplicación completa con **interfaz gráfica (Swing)**. Una ventana inicial permite elegir Recepción, Veterinario o Administración. |
| `java -cp out/main AppConsola` | Una ventana inicial para elegir el rol, y el menú de ese rol **en la consola** (se responde por teclado en la terminal donde se lanzó). |

(En Windows, el *classpath* es el mismo: `java -cp out\main AppSwing`.)

También se puede abrir el proyecto en Eclipse o IntelliJ IDEA marcando `src` como carpeta de fuentes y ejecutando `AppSwing` o `AppConsola`.

### Datos iniciales y persistencia

- La **primera vez** que se ejecuta (cuando no existe `vetgo.dat`) el sistema carga datos de prueba: dos dueños (Carlos Gómez, doc. `10000001`, con plan de salud; María López, doc. `10000002`), veterinarios Juan Pérez (`20000001`, de guardia) y Laura Fernández (`20000002`), recepcionistas, administradores, cuatro mascotas (Max, Luna, Piolín y Nube), tres turnos para el día siguiente, proveedores, dos vacunas y dos productos con stock.
- Tarifario inicial: consulta general $10.000, vacunación $7.000, cirugía $50.000, urgencia $15.000, seña $2.000 y cargo por cancelación tardía 20 %.
- Cada operación exitosa guarda el estado completo en `vetgo.dat`; al cerrar la ventana principal se vuelve a guardar. Para **volver al estado inicial** basta con cerrar la aplicación y borrar `vetgo.dat`.
- El archivo usa serialización de Java: si se modifican las clases del modelo, un `vetgo.dat` creado con una versión anterior puede no poder leerse y el sistema generará uno nuevo con los datos de prueba.

### Recorrido sugerido (con los datos de prueba)

1. **Recepción → Ver turnos**: se ven los turnos de mañana. Se puede asignar uno nuevo, reprogramarlo o cancelarlo.
2. **Veterinario → Iniciar consulta** sobre un turno programado → registrar peso → indicar tratamiento (`PROD-001`) o aplicar vacuna (`VAC-001`) → **confirmar consulta**.
3. **Recepción → Facturar consulta** eligiendo el medio de pago; luego, si hace falta, **Anular factura**.
4. **Administración → Reportes** (stock bajo, lotes por vencer, turnos por veterinario) y **Emitir recordatorios**.

---

## 4. Pruebas

La suite no requiere JUnit ni otras dependencias (usa un pequeño marco propio, `test/EjecutorPruebas.java`). Con el proyecto ya compilado en `out/main`:

Linux / macOS:

```bash
mkdir -p out/test
javac -encoding UTF-8 -cp out/main -d out/test test/*.java
java -cp out/main:out/test TodasLasPruebas
```

Windows (PowerShell):

```powershell
New-Item -ItemType Directory -Force out\test | Out-Null
javac -encoding UTF-8 -cp out\main -d out\test (Get-ChildItem test -Filter *.java).FullName
java -cp "out\main;out\test" TodasLasPruebas
```

Cada caso imprime `[PASA]`, `[FALLA]`, `[ERROR]` u `[OMITIDA]` y el programa termina con código 0 solo si todos pasan.

| Archivo | Contenido |
|---|---|
| `test/PruebasUnitarias.java` | 39 pruebas unitarias (`U-01` a `U-39`) del modelo y los servicios: reglas de dueños, veterinarios, turnos y agenda, consultas, vacunas, inventario (FEFO, alertas), medios de pago, facturación, recordatorios y persistencia. |
| `test/PruebasFuncionales.java` | 19 casos funcionales (`PF-01` a `PF-18`) que recorren los casos de uso principales a través de los **mismos controladores que usan las vistas**, con un repositorio en memoria. |
| `test/EjecutorPruebas.java`, `test/TodasLasPruebas.java` | Marco de pruebas y ejecutor de la suite completa. |

Algunos casos funcionales (cancelación con cargo y urgencias) dependen de la hora actual y se **omiten automáticamente** entre las ~22:40 y las ~00:15.

El plan de pruebas funcionales, con datos de entrada, resultados esperados y resultados obtenidos, está en [`docs/plan-pruebas.md`](docs/plan-pruebas.md); la salida de la última ejecución, en [`docs/resultados-pruebas.txt`](docs/resultados-pruebas.txt).

> Las pruebas no ejercitan las ventanas Swing; esa verificación es manual (ver el listado de la sección 5 del plan de pruebas).

---

## 5. Estructura del proyecto

```
VetGo/
├── src/
│   ├── AppSwing.java / AppConsola.java   Puntos de entrada (armado de controladores y vistas)
│   ├── modelo/
│   │   ├── personas/      Persona, Duenio, Veterinario, HorarioAtencion, Recepcionista, Administrador, Clinica
│   │   ├── mascotas/      Mascota (abstracta), Perro, Gato, Ave, Conejo, HistoriaClinica
│   │   ├── agenda/        Turno, Agenda
│   │   ├── atencion/      Consulta, Tratamiento, VacunaAplicada, NotaRectificatoria
│   │   ├── inventario/    Inventario, Producto, Vacuna, Lote, Proveedor, AlertaStock
│   │   ├── facturacion/   Factura, LineaFactura, NotaCredito, Tarifario
│   │   │   └── pago/      MedioPago (interfaz), Efectivo, Debito, Credito
│   │   └── enumeradores/  TipoAtencion, EstadoTurno, EstadoConsulta, EstadoFactura, Especie, Sexo, TamanioPerro
│   ├── controlador/       ControladorRegistro, ControladorTurnos, ControladorAtencion,
│   │                      ControladorFacturacion, ControladorAdministracion
│   ├── servicio/          GestorUrgencias, GeneradorReportes, ServicioRecordatorios,
│   │                      Notificador (interfaz), NotificadorConsola
│   ├── persistencia/      Repositorio (interfaz), RepositorioArchivo, EstadoSistema
│   └── vistas/            interfaces/ (contratos por rol), consola/, swing/
├── test/                  Pruebas unitarias y funcionales
├── docs/                  Documentación de la etapa 1 (UML, casos de uso), plan de pruebas e informe final
└── vetgo.dat              Datos de la aplicación (se genera al ejecutar; no se versiona)
```

**Arquitectura Modelo-Vista-Controlador (MVC):** el sistema sigue el patrón MVC, separando las responsabilidades en tres componentes. El **Modelo** contiene las entidades del dominio y las reglas de negocio; la **Vista** se encarga de la interacción con el usuario, tanto mediante consola como mediante Swing; y el **Controlador** recibe las acciones de las vistas, coordina las operaciones y delega la lógica correspondiente en el modelo. La persistencia se gestiona mediante `Repositorio`, mientras que las notificaciones se abstraen mediante la interfaz `Notificador`, permitiendo cambiar sus implementaciones sin afectar la lógica del sistema.
