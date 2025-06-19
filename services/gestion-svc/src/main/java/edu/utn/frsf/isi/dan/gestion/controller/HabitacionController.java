/**
 * Controlador REST para la gestión de entidades {@link Habitacion}.
 * Proporciona endpoints para crear, obtener, actualizar y eliminar habitaciones.
 * 
 * <p>Las rutas expuestas por este controlador están bajo el prefijo <code>/habitaciones</code>.</p>
 * 
 * <ul>
 *     <li><b>POST /habitaciones</b>: Crea una nueva habitación.</li>
 *     <li><b>GET /habitaciones/{id}</b>: Obtiene una habitación por su identificador.</li>
 *     <li><b>GET /habitaciones</b>: Obtiene la lista de todas las habitaciones.</li>
 *     <li><b>PUT /habitaciones/{id}</b>: Actualiza una habitación existente.</li>
 *     <li><b>DELETE /habitaciones/{id}</b>: Elimina una habitación por su identificador.</li>
 * </ul>
 * 
 * @author martindominguez
 */
package edu.utn.frsf.isi.dan.gestion.controller;

import edu.utn.frsf.isi.dan.gestion.model.Habitacion;
import edu.utn.frsf.isi.dan.gestion.service.HabitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/habitaciones")
public class HabitacionController {
    @Autowired
    private HabitacionService habitacionService;

    @PostMapping
    public ResponseEntity<Habitacion> create(@RequestBody Habitacion habitacion) {
        return ResponseEntity.ok(habitacionService.save(habitacion));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Habitacion> getById(@PathVariable Integer id) {
        return habitacionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Habitacion> getAll() {
        return habitacionService.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Habitacion> update(@PathVariable Integer id, @RequestBody Habitacion habitacion) {
        if (!habitacionService.findById(id).isPresent()) return ResponseEntity.notFound().build();
        habitacion.setId(id);
        return ResponseEntity.ok(habitacionService.save(habitacion));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!habitacionService.findById(id).isPresent()) return ResponseEntity.notFound().build();
        habitacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
