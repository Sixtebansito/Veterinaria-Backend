package com.esteban.Veterinaria.service;

import com.esteban.Veterinaria.model.Veterinario;
import com.esteban.Veterinaria.repository.VeterinarioRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;

    public VeterinarioService(VeterinarioRepository veterinarioRepository) {
        this.veterinarioRepository = veterinarioRepository;
    }

    // LISTAR TODOS
    public List<Veterinario> traerTodos() {
        return veterinarioRepository.findAll();
    }

    // TRAER UNO
    public Optional<Veterinario> traerUno(int id) {
        return veterinarioRepository.findById(id);
    }

    // GUARDAR
    public Veterinario guardar(Veterinario veterinario) {
        return veterinarioRepository.save(veterinario);
    }

    // EDITAR
    public Optional<Veterinario> editar(int id, Veterinario veterinarioActualizado) {
        return veterinarioRepository.findById(id)
                .map(v -> {
                    v.setNombre(veterinarioActualizado.getNombre());
                    v.setEspecialidad(veterinarioActualizado.getEspecialidad());
                    v.setAniosExperiencia(veterinarioActualizado.getAniosExperiencia());
                    v.setDisponible(veterinarioActualizado.isDisponible());
                    return veterinarioRepository.save(v);
                });
    }

    // ELIMINAR
    public boolean eliminar(int id) {
        if (veterinarioRepository.existsById(id)) {
            veterinarioRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    // Veterinarios de esa especialidad que estén disponibles
    public List<Veterinario> porEspecialidadDisponibles(String especialidad) {
        return veterinarioRepository.findByEspecialidadIgnoreCaseAndDisponibleTrue(especialidad);
    }

    // El de más años de experiencia
    public Optional<Veterinario> masExperimentado() {
        return veterinarioRepository.findAll()
                .stream()
                .max(Comparator.comparingInt(Veterinario::getAniosExperiencia));
    }

    // Cambiar disponibilidad
    public Optional<Veterinario> cambiarDisponibilidad(int id, boolean valor) {
        return veterinarioRepository.findById(id)
                .filter(v -> v.isDisponible() != valor)
                .map(v -> {
                    v.setDisponible(valor);
                    return veterinarioRepository.save(v);
                });
    }
}
