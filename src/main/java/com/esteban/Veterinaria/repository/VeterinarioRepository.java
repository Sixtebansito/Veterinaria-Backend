package com.esteban.Veterinaria.repository;

import com.esteban.Veterinaria.model.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VeterinarioRepository extends JpaRepository<Veterinario, Integer> {

    List<Veterinario> findByEspecialidadIgnoreCaseAndDisponibleTrue(String especialidad);
}
