package edu.utn.frsf.isi.dan.gestion.service;

import edu.utn.frsf.isi.dan.gestion.dao.TipoHabitacionRepository;
import edu.utn.frsf.isi.dan.gestion.model.TipoHabitacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoHabitacionService {
    @Autowired
    private TipoHabitacionRepository tipoHabitacionRepository;

    public TipoHabitacion save(TipoHabitacion tipoHabitacion) {
        return tipoHabitacionRepository.save(tipoHabitacion);
    }

    public void deleteById(Integer id) {
        tipoHabitacionRepository.deleteById(id);
    }

    public Optional<TipoHabitacion> findById(Integer id) {
        return tipoHabitacionRepository.findById(id);
    }

    public List<TipoHabitacion> findAll() {
        return tipoHabitacionRepository.findAll();
    }
}
