package com.esteban.Veterinaria.repository;

import com.esteban.Veterinaria.model.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MascotaRepository extends JpaRepository<Mascota, Integer> {

    List<Mascota> findByEspecieContainingIgnoreCase(String texto);

    List<Mascota> findByDuenoContainingIgnoreCase(String texto);
}
