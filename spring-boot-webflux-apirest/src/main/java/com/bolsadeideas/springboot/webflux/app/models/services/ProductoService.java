package com.bolsadeideas.springboot.webflux.app.models.services;

import com.bolsadeideas.springboot.webflux.app.models.documents.Categoria;
import com.bolsadeideas.springboot.webflux.app.models.documents.Producto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductoService {

	public Flux<Producto> findAll();
	
	public Flux<Producto> findAllWithNameUpperCase();
	
	public Flux<Producto> findAllWithNameUpperCaseWithRepeat();
	
	public Mono<Producto> findById(String id);
	
	public Mono<Producto> save(Producto p);
	
	public Mono<Void> delete(Producto p);
	
	public Flux<Categoria> findAllCategoria();
	
	public Mono<Categoria> findCategoriaById(String id);
	
	public Mono<Categoria> save(Categoria c);
	
	public Mono<Producto> findByNombre(String nombre);
}
