package com.example.model;

public class Persona {

	private Integer idPersona;
	private String nombre;
	
	
	public Persona(Integer idPersona, String nombre) {
		this.idPersona = idPersona;
		this.nombre = nombre;
	}
	public Integer getIdPersona() {
		return idPersona;
	}
	public void setIdPersona(Integer idPersona) {
		this.idPersona = idPersona;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	@Override
	public String toString() {
		return "Persona [idPersona=" + idPersona + ", nombre=" + nombre + "]";
	}
	
	
	
}
