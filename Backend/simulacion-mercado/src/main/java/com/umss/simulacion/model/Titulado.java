package com.umss.simulacion.model;

import jakarta.persistence.*;

/**
 * Entidad que representa a un titulado de Ingeniería en Sistemas
 * que ha respondido la encuesta de análisis de mercado.
 */
@Entity
@Table(name = "titulados")
public class Titulado {

    // ============================================================
    // IDENTIFICADOR
    // ============================================================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ============================================================
    // DATOS GENERALES
    // ============================================================
    private Integer edad;
    private String genero;
    private Integer anioEgreso;

    // ============================================================
    // FORMACIÓN COMPLEMENTARIA
    // ============================================================
    private Boolean formacionComplementaria;
    private String nivelMaximo;
    private String dondeCurso;
    private String financiamiento;
    private String nombrePrograma;

    // ============================================================
    // PREFERENCIAS DE POSGRADO
    // ============================================================
    private Boolean interesadoPosgrado;
    private String areaInteres;
    private String organizacionPreferida;

    // ============================================================
    // EMPLEABILIDAD
    // ============================================================
    private Integer aniosVidaProfesional;
    private Integer tiempoDesempleado;
    private String situacionLaboral;
    private String tiempoPrimerEmpleo;
    private String numeroEmpleos;

    // ============================================================
    // COMPETENCIAS (Escala Likert 1-5)
    // ============================================================
    private Integer competenciasTecnicas;
    private Integer competenciasBlandas;
    private Integer satisfaccion;

    // ============================================================
    // CONTACTO
    // ============================================================
    private String email;
    private String telefono;
    private String linkedin;
    private Boolean redContactos;

    // ============================================================
    // CONSTRUCTOR VACÍO (obligatorio para JPA)
    // ============================================================
    public Titulado() {
    }

    // ============================================================
    // GETTERS Y SETTERS
    // ============================================================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public Integer getAnioEgreso() { return anioEgreso; }
    public void setAnioEgreso(Integer anioEgreso) { this.anioEgreso = anioEgreso; }

    public Boolean getFormacionComplementaria() { return formacionComplementaria; }
    public void setFormacionComplementaria(Boolean formacionComplementaria) { 
        this.formacionComplementaria = formacionComplementaria; 
    }

    public String getNivelMaximo() { return nivelMaximo; }
    public void setNivelMaximo(String nivelMaximo) { this.nivelMaximo = nivelMaximo; }

    public String getDondeCurso() { return dondeCurso; }
    public void setDondeCurso(String dondeCurso) { this.dondeCurso = dondeCurso; }

    public String getFinanciamiento() { return financiamiento; }
    public void setFinanciamiento(String financiamiento) { 
        this.financiamiento = financiamiento; 
    }

    public String getNombrePrograma() { return nombrePrograma; }
    public void setNombrePrograma(String nombrePrograma) { 
        this.nombrePrograma = nombrePrograma; 
    }

    public Boolean getInteresadoPosgrado() { return interesadoPosgrado; }
    public void setInteresadoPosgrado(Boolean interesadoPosgrado) { 
        this.interesadoPosgrado = interesadoPosgrado; 
    }

    public String getAreaInteres() { return areaInteres; }
    public void setAreaInteres(String areaInteres) { this.areaInteres = areaInteres; }

    public String getOrganizacionPreferida() { return organizacionPreferida; }
    public void setOrganizacionPreferida(String organizacionPreferida) { 
        this.organizacionPreferida = organizacionPreferida; 
    }

    public Integer getAniosVidaProfesional() { return aniosVidaProfesional; }
    public void setAniosVidaProfesional(Integer aniosVidaProfesional) { 
        this.aniosVidaProfesional = aniosVidaProfesional; 
    }

    public Integer getTiempoDesempleado() { return tiempoDesempleado; }
    public void setTiempoDesempleado(Integer tiempoDesempleado) { 
        this.tiempoDesempleado = tiempoDesempleado; 
    }

    public String getSituacionLaboral() { return situacionLaboral; }
    public void setSituacionLaboral(String situacionLaboral) { 
        this.situacionLaboral = situacionLaboral; 
    }

    public String getTiempoPrimerEmpleo() { return tiempoPrimerEmpleo; }
    public void setTiempoPrimerEmpleo(String tiempoPrimerEmpleo) { 
        this.tiempoPrimerEmpleo = tiempoPrimerEmpleo; 
    }

    public String getNumeroEmpleos() { return numeroEmpleos; }
    public void setNumeroEmpleos(String numeroEmpleos) { 
        this.numeroEmpleos = numeroEmpleos; 
    }

    public Integer getCompetenciasTecnicas() { return competenciasTecnicas; }
    public void setCompetenciasTecnicas(Integer competenciasTecnicas) { 
        this.competenciasTecnicas = competenciasTecnicas; 
    }

    public Integer getCompetenciasBlandas() { return competenciasBlandas; }
    public void setCompetenciasBlandas(Integer competenciasBlandas) { 
        this.competenciasBlandas = competenciasBlandas; 
    }

    public Integer getSatisfaccion() { return satisfaccion; }
    public void setSatisfaccion(Integer satisfaccion) { 
        this.satisfaccion = satisfaccion; 
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getLinkedin() { return linkedin; }
    public void setLinkedin(String linkedin) { this.linkedin = linkedin; }

    public Boolean getRedContactos() { return redContactos; }
    public void setRedContactos(Boolean redContactos) { 
        this.redContactos = redContactos; 
    }
}