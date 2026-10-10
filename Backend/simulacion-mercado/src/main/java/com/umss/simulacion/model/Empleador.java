package com.umss.simulacion.model;

import jakarta.persistence.*;

/**
 * Entidad que representa a un empleador (organización)
 * que ha respondido la encuesta sobre titulados de Ingeniería en Sistemas.
 */
@Entity
@Table(name = "empleadores")
public class Empleador {

    // ============================================================
    // IDENTIFICADOR
    // ============================================================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ============================================================
    // DATOS DE LA ORGANIZACIÓN
    // ============================================================
    private String nombreEmpresa;
    private String tipoOrganizacion;      // Público / Privado
    private String tamanoOrganizacion;    // Grande / Mediana / Pequeña / Micro
    private String rubro;                 // Desarrollo software, TI, Banca, etc.

    // ============================================================
    // EMPLEABILIDAD
    // ============================================================
    private Boolean contratoUltimos5Anios;      // Sí / No
    private String probabilidadContratacion;    // Alta, Depende, No prevista, No requiere
    private String nivelFormacionRequerido;     // Licenciatura, Diplomado, Especialidad, Maestría...
    private String medioConvocatoria;           // Medios escritos, Internet, Postulación, etc.
    private String cargosDesempenados;          // Texto libre

    // ============================================================
    // REQUERIMIENTOS
    // ============================================================
    @Column(length = 2000)
    private String nuevasAreasConocimiento;

    @Column(length = 2000)
    private String nuevasHerramientas;

    @Column(length = 2000)
    private String habilidadesCompetencias;

    // ============================================================
    // VALORACIÓN LIKERT (1 = Totalmente desacuerdo, 4 = Totalmente acuerdo)
    // ============================================================
    private Integer valConfianza;                 // Confianza en la UMSS
    private Integer valConsistenciaTitulo;        // Consistencia del título
    private Integer valConsultasEmpleadores;      // Consulta regular
    private Integer valConocePerfil;              // Conoce el perfil
    private Integer valCoherenciaPerfil;          // Coherencia perfil vs desempeño
    private Integer valFormacionIntegral;         // Formación permite desempeño destacado
    private Integer valIncorporacionNecesidades;  // Incorpora necesidades del sector
    private Integer valCompetenciasLaborales;     // Evidencia competencias
    private Integer valValoresEtica;              // Valores y ética profesional
    private Integer valParticipacionProcesos;     // Ha participado en procesos

    // ============================================================
    // CONSTRUCTOR VACÍO
    // ============================================================
    public Empleador() {
    }

    // ============================================================
    // GETTERS Y SETTERS
    // ============================================================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }

    public String getTipoOrganizacion() { return tipoOrganizacion; }
    public void setTipoOrganizacion(String tipoOrganizacion) { this.tipoOrganizacion = tipoOrganizacion; }

    public String getTamanoOrganizacion() { return tamanoOrganizacion; }
    public void setTamanoOrganizacion(String tamanoOrganizacion) { this.tamanoOrganizacion = tamanoOrganizacion; }

    public String getRubro() { return rubro; }
    public void setRubro(String rubro) { this.rubro = rubro; }

    public Boolean getContratoUltimos5Anios() { return contratoUltimos5Anios; }
    public void setContratoUltimos5Anios(Boolean contratoUltimos5Anios) { this.contratoUltimos5Anios = contratoUltimos5Anios; }

    public String getProbabilidadContratacion() { return probabilidadContratacion; }
    public void setProbabilidadContratacion(String probabilidadContratacion) { this.probabilidadContratacion = probabilidadContratacion; }

    public String getNivelFormacionRequerido() { return nivelFormacionRequerido; }
    public void setNivelFormacionRequerido(String nivelFormacionRequerido) { this.nivelFormacionRequerido = nivelFormacionRequerido; }

    public String getMedioConvocatoria() { return medioConvocatoria; }
    public void setMedioConvocatoria(String medioConvocatoria) { this.medioConvocatoria = medioConvocatoria; }

    public String getCargosDesempenados() { return cargosDesempenados; }
    public void setCargosDesempenados(String cargosDesempenados) { this.cargosDesempenados = cargosDesempenados; }

    public String getNuevasAreasConocimiento() { return nuevasAreasConocimiento; }
    public void setNuevasAreasConocimiento(String nuevasAreasConocimiento) { this.nuevasAreasConocimiento = nuevasAreasConocimiento; }

    public String getNuevasHerramientas() { return nuevasHerramientas; }
    public void setNuevasHerramientas(String nuevasHerramientas) { this.nuevasHerramientas = nuevasHerramientas; }

    public String getHabilidadesCompetencias() { return habilidadesCompetencias; }
    public void setHabilidadesCompetencias(String habilidadesCompetencias) { this.habilidadesCompetencias = habilidadesCompetencias; }

    public Integer getValConfianza() { return valConfianza; }
    public void setValConfianza(Integer valConfianza) { this.valConfianza = valConfianza; }

    public Integer getValConsistenciaTitulo() { return valConsistenciaTitulo; }
    public void setValConsistenciaTitulo(Integer valConsistenciaTitulo) { this.valConsistenciaTitulo = valConsistenciaTitulo; }

    public Integer getValConsultasEmpleadores() { return valConsultasEmpleadores; }
    public void setValConsultasEmpleadores(Integer valConsultasEmpleadores) { this.valConsultasEmpleadores = valConsultasEmpleadores; }

    public Integer getValConocePerfil() { return valConocePerfil; }
    public void setValConocePerfil(Integer valConocePerfil) { this.valConocePerfil = valConocePerfil; }

    public Integer getValCoherenciaPerfil() { return valCoherenciaPerfil; }
    public void setValCoherenciaPerfil(Integer valCoherenciaPerfil) { this.valCoherenciaPerfil = valCoherenciaPerfil; }

    public Integer getValFormacionIntegral() { return valFormacionIntegral; }
    public void setValFormacionIntegral(Integer valFormacionIntegral) { this.valFormacionIntegral = valFormacionIntegral; }

    public Integer getValIncorporacionNecesidades() { return valIncorporacionNecesidades; }
    public void setValIncorporacionNecesidades(Integer valIncorporacionNecesidades) { this.valIncorporacionNecesidades = valIncorporacionNecesidades; }

    public Integer getValCompetenciasLaborales() { return valCompetenciasLaborales; }
    public void setValCompetenciasLaborales(Integer valCompetenciasLaborales) { this.valCompetenciasLaborales = valCompetenciasLaborales; }

    public Integer getValValoresEtica() { return valValoresEtica; }
    public void setValValoresEtica(Integer valValoresEtica) { this.valValoresEtica = valValoresEtica; }

    public Integer getValParticipacionProcesos() { return valParticipacionProcesos; }
    public void setValParticipacionProcesos(Integer valParticipacionProcesos) { this.valParticipacionProcesos = valParticipacionProcesos; }
}