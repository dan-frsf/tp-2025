package edu.utn.frsf.isi.dan.gestion.controller;

import edu.utn.frsf.isi.dan.gestion.model.TipoHabitacion;
import edu.utn.frsf.isi.dan.gestion.service.TipoHabitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tipos-habitacion")
public class TipoHabitacionController {
    @Autowired
    private TipoHabitacionService tipoHabitacionService;

    @PostMapping
    public ResponseEntity<TipoHabitacion> create(@RequestBody TipoHabitacion tipoHabitacion) {
        return ResponseEntity.ok(tipoHabitacionService.save(tipoHabitacion));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoHabitacion> getById(@PathVariable Integer id) {
        return tipoHabitacionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<TipoHabitacion> getAll() {
        return tipoHabitacionService.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoHabitacion> update(@PathVariable Integer id, @RequestBody TipoHabitacion tipoHabitacion) {
        if (!tipoHabitacionService.findById(id).isPresent()) return ResponseEntity.notFound().build();
        tipoHabitacion.setId(id);
        return ResponseEntity.ok(tipoHabitacionService.save(tipoHabitacion));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!tipoHabitacionService.findById(id).isPresent()) return ResponseEntity.notFound().build();
        tipoHabitacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
