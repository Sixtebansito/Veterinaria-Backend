package com.esteban.Veterinaria.repository;

import com.esteban.Veterinaria.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicamentoRepository extends JpaRepository<Medicamento, Integer> {

}
