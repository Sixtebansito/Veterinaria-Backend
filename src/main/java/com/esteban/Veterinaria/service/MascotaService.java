package com.esteban.Veterinaria.service;

import com.esteban.Veterinaria.model.Mascota;
import com.esteban.Veterinaria.repository.MascotaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MascotaService {

    private final MascotaRepository mascotaRepository;

    public MascotaService(MascotaRepository mascotaRepository) {
        this.mascotaRepository = mascotaRepository;
    }

    // Traer todos
    public List<Mascota> traerTodos() {
        return mascotaRepository.findAll();
    }

    // Traer uno
    public Optional<Mascota> traerUno(int id) {
        return mascotaRepository.findById(id);
    }

    // Guardar
    public Mascota guardar(Mascota mascota) {
        return mascotaRepository.save(mascota);
    }

    // Editar
    public Optional<Mascota> editar(int id, Mascota mascotaActualizada) {
        return mascotaRepository.findById(id)
                .map(mascota -> {
                    mascota.setNombre(mascotaActualizada.getNombre());
                    mascota.setDueno(mascotaActualizada.getDueno());
                    mascota.setEdad(mascotaActualizada.getEdad());
                    mascota.setEspecie(mascotaActualizada.getEspecie());
                    return mascotaRepository.save(mascota);
                });
    }

    // Eliminar
    public boolean eliminar(int id) {
        if (mascotaRepository.existsById(id)) {
            mascotaRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    // Mascotas de una sola especie, sin importar mayúsculas
    public List<Mascota> porEspecie(String texto) {
        return mascotaRepository.findByEspecieContainingIgnoreCase(texto);
    }

    // Mascotas de un mismo dueño
    public List<Mascota> porDueno(String texto) {
        return mascotaRepository.findByDuenoContainingIgnoreCase(texto);
    }

    // Mascotas de 1 año o menos
    public List<Mascota> cachorros() {
        return mascotaRepository.findAll()
                .stream()
                .filter(mascota -> mascota.getEdad() <= 1)
                .collect(Collectors.toList());
    }

    // Cuántas mascotas hay de cada especie
    public Map<String, Long> conteoEspecies() {
        return mascotaRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(Mascota::getEspecie, Collectors.counting()));
    }
}
