package com.esteban.Veterinaria.controller;

import com.esteban.Veterinaria.model.Veterinario;
import com.esteban.Veterinaria.service.VeterinarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/veterinario")
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    public VeterinarioController(VeterinarioService veterinarioService) {
        this.veterinarioService = veterinarioService;
    }

    // LISTAR TODOS
    @GetMapping
    public List<Veterinario> verTodos() {
        return veterinarioService.traerTodos();
    }

    // TRAER UNO
    @GetMapping("/{id}")
    public ResponseEntity<Veterinario> traerUno(@PathVariable int id) {
        return veterinarioService.traerUno(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CREAR UNO
    @PostMapping
    public ResponseEntity<Veterinario> guardar(@RequestBody Veterinario veterinario) {
        Veterinario nuevo = veterinarioService.guardar(veterinario);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }

    // EDITAR UNO
    @PutMapping("/{id}")
    public ResponseEntity<Veterinario> editar(@PathVariable int id, @RequestBody Veterinario veterinario) {
        return veterinarioService.editar(id, veterinario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ELIMINAR UNO
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        if (veterinarioService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Veterinarios de esa especialidad que estén disponibles
    @GetMapping("/especialidad/{especialidad}")
    public List<Veterinario> porEspecialidad(@PathVariable String especialidad) {
        return veterinarioService.porEspecialidadDisponibles(especialidad);
    }

    // El de más años de experiencia
    @GetMapping("/mas-experimentado")
    public ResponseEntity<Veterinario> masExperimentado() {
        return veterinarioService.masExperimentado()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Cambia la disponibilidad
    @PutMapping("/{id}/disponibilidad")
    public ResponseEntity<Veterinario> cambiarDisponibilidad(@PathVariable int id, @RequestParam boolean valor) {
        Optional<Veterinario> existente = veterinarioService.traerUno(id);
        if (existente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (existente.get().isDisponible() == valor) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return veterinarioService.cambiarDisponibilidad(id, valor)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.CONFLICT).build());
    }
}
