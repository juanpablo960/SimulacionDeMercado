package com.umss.simulacion.repository;

import com.umss.simulacion.model.Empleador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para manejar las operaciones de base de datos
 * de la entidad Empleador.
 */
@Repository
public interface EmpleadorRepository extends JpaRepository<Empleador, Long> {

    // Métodos personalizados
    List<Empleador> findByTipoOrganizacion(String tipoOrganizacion);
    List<Empleador> findByRubro(String rubro);
    List<Empleador> findByContratoUltimos5Anios(Boolean contrato);
}
