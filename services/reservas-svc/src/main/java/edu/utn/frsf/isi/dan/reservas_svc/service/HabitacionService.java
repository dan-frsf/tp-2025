package edu.utn.frsf.isi.dan.reservas_svc.service;

import edu.utn.frsf.isi.dan.reservas_svc.model.Habitacion;
import edu.utn.frsf.isi.dan.reservas_svc.model.Hotel;
import edu.utn.frsf.isi.dan.reservas_svc.repository.HabitacionRepository;
import edu.utn.frsf.isi.dan.shared.HabitacionDTO;
import edu.utn.frsf.isi.dan.shared.HabitacionEvent;
import edu.utn.frsf.isi.dan.shared.HotelDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HabitacionService {
    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Habitacion> findAll() {
        return habitacionRepository.findAll();
    }

    public Optional<Habitacion> findById(String id) {
        return habitacionRepository.findById(id);
    }

    public Habitacion save(Habitacion habitacion) {
        return habitacionRepository.save(habitacion);
    }

    public void deleteById(String id) {
        habitacionRepository.deleteById(id);
    }

    
    public void handleEvent(HabitacionEvent event) {
        switch (event.getTipoEvento()) {
            case CREAR:
                save(mapFromHabitacion(event.getHabitacion()));
                break;
            case ACTUALIZAR_DATOS:
                updateByHabitacionId(event.getHabitacion().getHabitacionId(),mapFromHabitacion(event.getHabitacion()));
                break;
            case ACTUALIZAR_PRECIO:
                // TODO implementar por el alumno
                // en este caso el atributo TarifaDTO tiene 
                // el ID de los tipos de habitaciones que van a tener un nuevo precio y el nuevo precio
                // event.getTarifa()
                break;
            case ELIMINAR:
                deleteByHabitacionId(event.getHabitacion().getHabitacionId());
                break;
            default:
                throw new IllegalArgumentException("Tipo de evento desconocido: " + event.getTipoEvento());
        }
    }

    public Habitacion mapFromHabitacion(HabitacionDTO dto) {
        return Habitacion.builder()
                .habitacionId(dto.getHabitacionId())
                .precioNoche(dto.getPrecioNoche())
                .capacidad(dto.getCapacidad())
                .amenities(dto.getAmenities())
                .hotel(mapFromDto(dto.getHotel()))
                .build();
    }

    public Hotel mapFromDto(HotelDTO dto){
        if(dto == null) {
            return null;
        }
        return Hotel.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .domicilio(dto.getDomicilio())
                .categoria(dto.getCategoria())
                .ubicacion(new GeoJsonPoint(dto.getLatitud(), dto.getLongitud()))
                .build();
    }

    public Optional<Habitacion> findByHabitacionId(Long habitacionId) {
        Query query = new Query(Criteria.where("habitacionId").is(habitacionId));
        Habitacion habitacion = mongoTemplate.findOne(query, Habitacion.class);
        return Optional.ofNullable(habitacion);
    }

    public Habitacion updateByHabitacionId(Long habitacionId, Habitacion nuevaHabitacion) {
        Query query = new Query(Criteria.where("habitacionId").is(habitacionId));
        Update update = new Update()
                .set("precioNoche", nuevaHabitacion.getPrecioNoche())
                .set("capacidad", nuevaHabitacion.getCapacidad())
                .set("amenities", nuevaHabitacion.getAmenities());
        Habitacion actualizada = mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                Habitacion.class
        );
        if (actualizada == null) {
            throw new IllegalArgumentException("No se encontró la habitación con habitacionId: " + habitacionId);
        }
        return actualizada;
    }

    public void deleteByHabitacionId(Long habitacionId) {
        Query query = new Query(Criteria.where("habitacionId").is(habitacionId));
        mongoTemplate.remove(query, Habitacion.class);
    }
}
