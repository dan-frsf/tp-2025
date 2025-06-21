package edu.utn.frsf.isi.dan.reservas_svc.repository;

import edu.utn.frsf.isi.dan.reservas_svc.model.Habitacion;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HabitacionRepository extends MongoRepository<Habitacion, String> {
}
