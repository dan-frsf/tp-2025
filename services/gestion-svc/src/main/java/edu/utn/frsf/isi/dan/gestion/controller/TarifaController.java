package edu.utn.frsf.isi.dan.gestion.controller;

import edu.utn.frsf.isi.dan.gestion.model.Tarifa;
import edu.utn.frsf.isi.dan.gestion.service.TarifaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tarifas")
public class TarifaController {
    @Autowired
    private TarifaService tarifaService;

    @PostMapping
    public ResponseEntity<Tarifa> create(@RequestBody Tarifa tarifa) {
        return ResponseEntity.ok(tarifaService.save(tarifa));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarifa> getById(@PathVariable Integer id) {
        return tarifaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Tarifa> getAll() {
        return tarifaService.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tarifa> update(@PathVariable Integer id, @RequestBody Tarifa tarifa) {
        if (!tarifaService.findById(id).isPresent()) return ResponseEntity.notFound().build();
        tarifa.setId(id);
        return ResponseEntity.ok(tarifaService.save(tarifa));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!tarifaService.findById(id).isPresent()) return ResponseEntity.notFound().build();
        tarifaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
