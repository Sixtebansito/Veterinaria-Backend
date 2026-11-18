package com.esteban.Veterinaria.service;

import com.esteban.Veterinaria.model.Medicamento;
import com.esteban.Veterinaria.repository.MedicamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;

    public MedicamentoService(MedicamentoRepository medicamentoRepository) {
        this.medicamentoRepository = medicamentoRepository;
    }

    // LISTAR TODOS
    public List<Medicamento> traerTodos() {
        return medicamentoRepository.findAll();
    }

    // TRAER UNO
    public Optional<Medicamento> traerUno(int id) {
        return medicamentoRepository.findById(id);
    }

    // GUARDAR
    public Medicamento guardar(Medicamento medicamento) {
        return medicamentoRepository.save(medicamento);
    }

    // EDITAR
    public Optional<Medicamento> editar(int id, Medicamento medicamentoActualizado) {
        return medicamentoRepository.findById(id)
                .map(m -> {
                    m.setNombre(medicamentoActualizado.getNombre());
                    m.setPrecio(medicamentoActualizado.getPrecio());
                    m.setStock(medicamentoActualizado.getStock());
                    m.setRequiereReceta(medicamentoActualizado.isRequiereReceta());
                    return medicamentoRepository.save(m);
                });
    }

    // ELIMINAR
    public boolean eliminar(int id) {
        if (medicamentoRepository.existsById(id)) {
            medicamentoRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    // Stock menor al mínimo indicado
    public List<Medicamento> stockBajo(int minimo) {
        return medicamentoRepository.findAll()
                .stream()
                .filter(m -> m.getStock() < minimo)
                .collect(Collectors.toList());
    }

    // Venta libre
    public List<Medicamento> ventaLibre() {
        return medicamentoRepository.findAll()
                .stream()
                .filter(m -> !m.isRequiereReceta())
                .collect(Collectors.toList());
    }

    // Despachar medicamentos
    public Optional<Medicamento> despachar(int id, int cantidad) {
        return medicamentoRepository.findById(id)
                .filter(m -> m.getStock() >= cantidad)
                .map(m -> {
                    m.setStock(m.getStock() - cantidad);
                    return medicamentoRepository.save(m);
                });
    }
}
