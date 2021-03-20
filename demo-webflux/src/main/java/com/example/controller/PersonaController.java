package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Persona;
import com.example.repository.IPersonaRepo;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/persona")
public class PersonaController {
		
		@Autowired
		private IPersonaRepo repo;
		
		@RequestMapping("/listar")
		public Flux<Persona> getAll() {		
			return repo.getAll();
					
		}
		
		@RequestMapping("/{id}")
		public Mono<Persona> getById(@PathVariable Integer id) {		
			return repo.getById(id);				
		}
		
		@RequestMapping("/registrar")
		public Mono<Persona> save(@RequestBody Persona p) {		
			return repo.save(p);
					
		}
		
		@RequestMapping("/modificar")
		public Mono<Persona> update(@RequestBody Persona p) {		
			return repo.edit(p);
					
		}
		
		@RequestMapping("/eliminar/{id}")
		public Mono<Void> delete(@PathVariable Integer id) {		
			return repo.getById(id)
			    .flatMap(p -> repo.delete(p.getIdPersona()));						
		}
}
