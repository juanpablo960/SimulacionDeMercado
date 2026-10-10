package com.umss.simulacion.repository;

import com.umss.simulacion.model.Titulado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para manejar las operaciones de base de datos
 * de la entidad Titulado.
 *
 * Spring Boot genera automáticamente la implementación
 * de todos los métodos CRUD (Create, Read, Update, Delete).
 */
@Repository
public interface TituladoRepository extends JpaRepository<Titulado, Long> {

    // ============================================================
    // MÉTODOS CRUD BÁSICOS (heredados de JpaRepository)
    // ============================================================
    // findAll()           → Lista todos los titulados
    // findById(id)        → Busca un titulado por ID
    // save(titulado)      → Guarda o actualiza un titulado
    // deleteById(id)      → Elimina un titulado por ID
    // count()             → Cuenta cuántos titulados hay
    // existsById(id)      → Verifica si existe un titulado con ese ID

    // ============================================================
    // CONSULTAS PERSONALIZADAS (Spring las genera automáticamente)
    // ============================================================

    /**
     * Busca titulados por género (Hombre/Mujer)
     */
    List<Titulado> findByGenero(String genero);

    /**
     * Busca titulados por área de interés
     */
    List<Titulado> findByAreaInteres(String areaInteres);

    /**
     * Busca titulados por fuente de financiamiento
     */
    List<Titulado> findByFinanciamiento(String financiamiento);

    /**
     * Busca titulados por situación laboral
     */
    List<Titulado> findBySituacionLaboral(String situacionLaboral);

    /**
     * Busca titulados por año de egreso
     */
    List<Titulado> findByAnioEgreso(Integer anioEgreso);

    /**
     * Busca titulados que estén interesados en posgrado
     */
    List<Titulado> findByInteresadoPosgrado(Boolean interesadoPosgrado);
}