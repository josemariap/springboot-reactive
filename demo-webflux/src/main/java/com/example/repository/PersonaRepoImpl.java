package com.example.repository;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.example.model.Persona;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class PersonaRepoImpl implements IPersonaRepo{
	
	private static final Logger log = LoggerFactory.getLogger(PersonaRepoImpl.class);

	@Override
	public Mono<Persona> save(Persona p) {
		log.info(p.toString());
		log.info("persona guardada");
		return Mono.just(p);
	}

	@Override
	public Mono<Persona> edit(Persona p) {
		log.info(p.toString());
		log.info("persona modificada");
		return Mono.just(p);
	}

	@Override
	public Flux<Persona> getAll() {
		List<Persona> personas = new ArrayList<Persona>();
		personas.add(new Persona(1, "mito"));
		personas.add(new Persona(2, "code"));
		personas.add(new Persona(3, "jose"));
		log.info("lista de personas");
		return Flux.fromIterable(personas);
	}

	@Override
	public Mono<Persona> getById(Integer id) {
		Persona p =  new Persona(1, "Aruni");
		log.info("persona por id");
		return Mono.just(p);
	}

	@Override
	public Mono<Void> delete(Integer id) {
		log.info("persona eliminada");
		return Mono.empty();
	}

}
