package com.example.repository;

import com.example.model.Persona;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IPersonaRepo {
	
	Mono<Persona> save(Persona p);
	Mono<Persona> edit(Persona p);
	Flux<Persona> getAll();
	Mono<Persona> getById(Integer id);
	Mono<Void> delete(Integer id);
}
