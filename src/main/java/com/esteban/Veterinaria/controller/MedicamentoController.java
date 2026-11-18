package com.esteban.Veterinaria.controller;

import com.esteban.Veterinaria.model.Medicamento;
import com.esteban.Veterinaria.service.MedicamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medicamento")
public class MedicamentoController {

    private final MedicamentoService medicamentoService;

    public MedicamentoController(MedicamentoService medicamentoService) {
        this.medicamentoService = medicamentoService;
    }

    // LISTAR TODOS (CRUD - 200 OK)
    @GetMapping
    public List<Medicamento> verTodos() {
        return medicamentoService.traerTodos();
    }

    // TRAER UNO (CRUD - 200 OK o 404 Not Found)
    @GetMapping("/{id}")
    public ResponseEntity<Medicamento> traerUno(@PathVariable int id) {
        return medicamentoService.traerUno(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CREAR UNO (CRUD - 201 Created)
    @PostMapping
    public ResponseEntity<Medicamento> guardar(@RequestBody Medicamento medicamento) {
        Medicamento nuevo = medicamentoService.guardar(medicamento);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }

    // EDITAR UNO (CRUD - 200 OK o 404 Not Found)
    @PutMapping("/{id}")
    public ResponseEntity<Medicamento> editar(@PathVariable int id, @RequestBody Medicamento medicamento) {
        return medicamentoService.editar(id, medicamento)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ELIMINAR UNO (CRUD - 204 No Content o 404 Not Found)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        if (medicamentoService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ESPECIAL: Stock menor al mínimo (si no se envía, 5) (200 OK)
    @GetMapping("/stock-bajo")
    public List<Medicamento> stockBajo(@RequestParam(defaultValue = "5") int minimo) {
        return medicamentoService.stockBajo(minimo);
    }

    // ESPECIAL: Venta libre (no requiere receta) (200 OK)
    @GetMapping("/venta-libre")
    public List<Medicamento> ventaLibre() {
        return medicamentoService.ventaLibre();
    }

    // ESPECIAL: Despachar medicamentos (200 OK, 400 si cantidad <= 0, 404 si no existe, 409 si stock insuficiente)
    @PutMapping("/{id}/despachar")
    public ResponseEntity<Medicamento> despachar(@PathVariable int id, @RequestParam int cantidad) {
        if (cantidad <= 0) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Medicamento> existente = medicamentoService.traerUno(id);
        if (existente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (existente.get().getStock() < cantidad) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return medicamentoService.despachar(id, cantidad)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.CONFLICT).build());
    }
}
