package com.esteban.Veterinaria.controller;

import com.esteban.Veterinaria.model.Mascota;
import com.esteban.Veterinaria.service.MascotaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mascota")
public class MascotaController {

    private final MascotaService mascotaService;

    public MascotaController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    // LISTAR TODOS
    @GetMapping
    public List<Mascota> verTodos() {
        return mascotaService.traerTodos();
    }

    // TRAER UNO
    @GetMapping("/{id}")
    public ResponseEntity<Mascota> traerUno(@PathVariable int id) {
        return mascotaService.traerUno(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CREAR UNO
    @PostMapping
    public ResponseEntity<Mascota> guardar(@RequestBody Mascota mascota) {
        Mascota nuevaMascota = mascotaService.guardar(mascota);
        return new ResponseEntity<>(nuevaMascota, HttpStatus.CREATED);
    }

    // EDITAR UNO
    @PutMapping("/{id}")
    public ResponseEntity<Mascota> editar(@PathVariable int id, @RequestBody Mascota mascota) {
        return mascotaService.editar(id, mascota)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ELIMINAR UNO
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        if (mascotaService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Mascotas de esa especie, sin importar mayúsculas
    @GetMapping("/especie/{texto}")
    public List<Mascota> porEspecie(@PathVariable String texto) {
        return mascotaService.porEspecie(texto);
    }

    // Mascotas de un mismo dueño
    @GetMapping("/dueno/{texto}")
    public List<Mascota> porDueno(@PathVariable String texto) {
        return mascotaService.porDueno(texto);
    }

    // Mascotas de 1 año o menos
    @GetMapping("/cachorros")
    public List<Mascota> cachorros() {
        return mascotaService.cachorros();
    }

    // ESPECIAL: Cuántas mascotas hay de cada especie
    @GetMapping("/conteo-especies")
    public Map<String, Long> conteoEspecies() {
        return mascotaService.conteoEspecies();
    }
}
