# Punto 4: pruebas de VetGo

## Ejecución de pruebas unitarias

No hay un framework ni un gestor de dependencias configurado en el repositorio.
La suite usa únicamente la biblioteca estándar de Java. Desde la raíz:

```powershell
javac -sourcepath src -d out\test test\PruebasPunto4.java
java -cp out\test PruebasPunto4
```

La suite comprueba roles y datos heredados de personas, construcción y datos
propios de cada especie de mascota, historia clínica inicial, duración de cada
tipo de atención y rechazos ante entradas inválidas de los controladores de
turnos, atención y facturación. Cada comprobación muestra `OK`; un fallo detiene
la ejecución con un `AssertionError`.

Resultado de ejecución en este estado del proyecto: **60 pruebas aprobadas, 0
fallidas**.

Estas pruebas unitarias no demuestran todavía el funcionamiento de los flujos
principales ni de todas las reglas de negocio. Los casos funcionales siguientes
se alinean con los seis casos de uso mínimos de la propuesta B y, en particular,
con los dos diagramas de secuencia entregados: **registrar consulta** y
**asignar turno**.

## Plan de pruebas funcionales

| ID | Caso / regla | Datos de entrada | Resultado esperado | Resultado obtenido |
|---|---|---|---|---|
| PF-01 | Registrar dueños y mascotas | Dueño Ana Pérez, documento `30111222`; registrar un perro Toby, un gato Mora, un ave Lolo y un conejo Nube | Registrar dueño una sola vez; asociar cada mascota al dueño y conservar los datos específicos de especie | **Parcial, solo unitario:** las pruebas construyen las cuatro especies y verifican sus datos. Alta, búsqueda y asociación al dueño no ejecutables: `Clinica` y `Duenio` tienen operaciones sin implementar. |
| PF-02 | Transferir mascota conservando historia clínica | Transferir a Toby del dueño `30111222` al `30444555`, con una consulta previa | Cambia el dueño responsable y mantiene la misma historia clínica y consultas | **No ejecutado:** `Mascota.transferirA`, búsquedas de `Clinica` y gestión de consultas/historia no están implementadas. |
| PF-03 | Secuencia 2: asignar turno válido | Mascota y veterinario existentes; veterinario atiende lunes 9–19; consulta general lunes 10:00 | Se crea un turno programado de 30 minutos y se persiste | **No ejecutado:** `Agenda.asignarTurno`, `Agenda.hayDisponibilidad` y `Veterinario.atiendeEn` no implementan el flujo. |
| PF-04 | Secuencia 2, alternativo: fuera de horario o superposición | Asignar cirugía lunes 18:00; intentar otro turno que se superponga con el existente | Rechaza el turno que excede el horario o se superpone; mantiene intacto el turno previo | **No ejecutado:** disponibilidad, duración del turno y detección de superposición no están implementadas. |
| PF-05 | Duración por tipo de atención | Consulta general, vacunación y cirugía | Duraciones de 30, 15 y 120 minutos, respectivamente | **Parcial, solo unitario:** la suite verifica esos valores en `TipoAtencion`; `Turno.getDuracionMin` sigue pendiente. |
| PF-06 | Reprogramar y cancelar turno | Reprogramar un turno a un horario disponible; cancelar uno 2 h 1 min antes y otro 1 h 59 min antes | Reprogramar sin conflicto; cancelar sin cargo en el primer caso y con cargo del 20 % en el segundo | **No ejecutado:** reprogramación, cancelación, cálculo del cargo y estados de `Agenda`, `Turno` y `Tarifario` no están implementados. |
| PF-07 | Ausencias consecutivas y seña | Registrar tres ausencias seguidas; intentar pedir turno sin pagar y luego pagar la seña configurada | Tras la tercera ausencia requiere seña; una vez pagada permite pedir turno | **No ejecutado:** `Duenio.registrarAusencia`, `Duenio.puedePedirTurno` y `Duenio.pagarSenia` no están implementados. |
| PF-08 | Urgencia y desplazamiento de turnos | Solicitar urgencia con veterinario de guardia ocupado y un turno posterior ya asignado | Asigna el primer horario de guardia; desplaza el turno siguiente y notifica a su dueño | **No ejecutado:** guardias, búsqueda de disponibilidad, desplazamiento y notificación no están implementados. |
| PF-09 | Secuencia 1: abrir consulta y registrar peso | Turno atendido; iniciar con motivo `Control` y registrar peso `12.5 kg` | Se crea consulta en la historia clínica y se registra el peso del día | **No ejecutado:** `HistoriaClinica.abrirConsulta` y `Consulta.registrarPeso` no implementan el flujo. |
| PF-10 | Secuencia 1, alternativo: medicamento sin peso | Consulta sin peso; indicar amoxicilina, dosis `1 ml`, cada 12 h, 5 días | Rechaza el tratamiento y no modifica consulta ni stock | **No ejecutado:** `Consulta.indicarTratamiento` no implementa la regla de peso obligatorio. |
| PF-11 | Tratamiento, stock y lotes | Registrar peso; indicar 2 unidades con lotes de 5 y 10 unidades, uno vencido y otro vigente | Acepta solo con stock vigente suficiente; descuenta primero el lote vigente que vence antes | **No ejecutado:** validación y descuento de stock, vencimientos y orden de consumo no están implementados en `Inventario`, `Producto` y `Lote`. |
| PF-12 | Confirmación y alerta de stock mínimo | Confirmar consulta que deja el producto justo en el mínimo configurado | Descuenta stock al confirmar y genera/notifica alerta al administrador | **No ejecutado:** `Inventario.descontarStock`, `Producto` y notificaciones requeridas no están implementados. |
| PF-13 | Aplicación y calendario de vacunas | Aplicar vacuna a especie elegible y con edad mínima cumplida; repetir con especie no admitida, animal menor o sin stock | Solo acepta la aplicación válida; guarda lote y fecha y calcula el próximo refuerzo | **No ejecutado:** elegibilidad, edad, stock, registro de lote y cálculo del refuerzo no están implementados. |
| PF-14 | Recordatorio y vencimiento de vacuna | Refuerzo con fecha `2026-12-10`; consultar el `2026-12-03` y el `2027-01-09` | Emite recordatorio 7 días antes; considera vencida la vacuna luego de 30 días de demora | **No ejecutado:** el cálculo y envío de recordatorios/vencimientos no están implementados. |
| PF-15 | Facturación y descuentos | Subtotal `$10.000`; probar efectivo sin plan, débito, crédito y dueño con plan | Efectivo: `$9.000`; débito: `$10.000`; crédito: `$10.800`; plan: `$8.500`; aplicar solo el mayor descuento cuando corresponda | **No ejecutado:** `Factura.calcularSubtotal`, `Factura.calcularTotal` y los tres `MedioPago.calcularTotal` no están implementados. |
| PF-16 | Anular factura sin modificarla | Emitir factura y solicitar anulación con motivo y recepcionista válidos | Factura original queda inmutable y se registra nota de crédito; una factura anulada no se vuelve a anular | **No ejecutado:** anulación y nota de crédito no están implementadas en `Factura`/controlador. |
| PF-17 | Consultas de administración | Solicitar turnos por veterinario, productos bajo mínimo y vacunas próximas a vencer | Devuelve resultados que cumplen filtros y fechas solicitados | **No ejecutado:** reportes y consultas de inventario/agenda están pendientes. |

Los casos marcados **No ejecutado** no se cuentan como aprobados. La ejecución
unitaria actual demuestra únicamente los comportamientos listados en la sección
anterior; para completar el punto 4 hay que implementar los flujos pendientes y
volver a ejecutar cada caso, registrando su resultado observado.
