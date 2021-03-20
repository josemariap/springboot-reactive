package com.bolsadeideas.springboot.webflux.app.models.controllers;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;

import com.bolsadeideas.springboot.webflux.app.models.documents.Producto;
import com.bolsadeideas.springboot.webflux.app.models.services.ProductoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

	@Autowired
	ProductoService service;
	
	@GetMapping
	public Flux<Producto> getAll(){
		return service.findAll();
	}
	
	@GetMapping("/all")
	public Mono<ResponseEntity<Flux<Producto>>> getAllResponseEntity(){
		return Mono.just(ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(service.findAll()));
	}
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Producto>> getAById(@PathVariable String id){
		return service.findById(id).map(p -> ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(p))
				.defaultIfEmpty(ResponseEntity.noContent().build()); // sino lo encuentra no setea el body y manda el not contente de status 204
	}
	
	//retorna un Mono ResponseEntity del tipo Map que estara en su body
	@PostMapping("/create")
	public Mono<ResponseEntity<Map<String, Object>>> create(@Valid @RequestBody Mono<Producto> monoProducto){
		// al capturar el request body de producto en un Mono podemos tratar la validacion de forma reactiva dentro del
		// flatMap
		
		Map<String, Object> respuesta = new HashMap<String, Object>();
		
		return monoProducto.flatMap(producto -> {
			if(producto.getCreateAt() ==  null) {
			    producto.setCreateAt(new Date());
		    }	
			
			return service.save(producto).map(p -> {
				respuesta.put("producto", p);
				respuesta.put("mensaje", "Producto creado con exito");
				respuesta.put("timestamp", new Date());
				return ResponseEntity		
							.created(URI.create("/api/productos/create/" + p.getId()))
							.contentType(MediaType.APPLICATION_JSON)
							.body(respuesta); //metemos el HashMap dentro del body del ResponseEntity
		        });
	    //si hay un error de algun tipo o de validacion de atributos se envia mensaje a on Error Resume
		}).onErrorResume(t -> {
			 return Mono.just(t).cast(WebExchangeBindException.class)
			.flatMap(e -> Mono.just(e.getFieldErrors()))
			.flatMapMany(errors -> Flux.fromIterable(errors))
			.map(fieldError -> "El campo " + fieldError.getField()+ " " + fieldError.getDefaultMessage())
			.collectList()
			.flatMap(list -> {
				respuesta.put("errores", list);
				respuesta.put("mensaje", "El producto no pudo ser creado");
				respuesta.put("status", HttpStatus.BAD_REQUEST.value());
				return Mono.just(ResponseEntity.badRequest().body(respuesta));//metemos el HashMap dentro del body del ResponseEntity
			});
		});
	}
	
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Producto>> update(@RequestBody Producto product, @PathVariable String id){
		if(product.getCreateAt() ==  null) {
			product.setCreateAt(new Date());
		}
		return service.findById(id)
				.flatMap(p -> {
				    p.setNombre(product.getNombre());
				    p.setPrecio(product.getPrecio());
			    	return service.save(p);
		}).map(p -> ResponseEntity.created(URI.create("/api/productos/create/" + p.getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.body(p))
				.defaultIfEmpty(ResponseEntity.noContent().build()); //sino lo encuentra para actualizar el body queda vacio y emite el status 204 not content
	}
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Object>> delete(@PathVariable String id){
		return service.findById(id).flatMap(p -> {
			return service.delete(p).then(Mono.just(ResponseEntity.noContent().build())); //usamos el then porque el delete devuelve un Mono<Void>
		}).defaultIfEmpty(ResponseEntity.notFound().build());
	}		
			
		
	/*******************/
	
	@PostMapping
	public Mono<ResponseEntity<Map<String, Object>>> crear(@Valid @RequestBody Mono<Producto> monoProducto){
		
		Map<String, Object> respuesta = new HashMap<String, Object>();
		
		return monoProducto.flatMap(producto -> {
			if(producto.getCreateAt()==null) {
				producto.setCreateAt(new Date());
			}
			
			return service.save(producto).map(p-> {
				respuesta.put("producto", p);
				respuesta.put("mensaje", "Producto creado con éxito");
				respuesta.put("timestamp", new Date());
				return ResponseEntity
					.created(URI.create("/api/productos/".concat(p.getId())))
					.contentType(MediaType.APPLICATION_JSON)
					.body(respuesta);
				});
			
		}).onErrorResume(t -> {
			return Mono.just(t).cast(WebExchangeBindException.class)
					.flatMap(e -> Mono.just(e.getFieldErrors()))
					.flatMapMany(Flux::fromIterable)
					.map(fieldError -> "El campo "+fieldError.getField() + " " + fieldError.getDefaultMessage())
					.collectList()
					.flatMap(list -> {
						respuesta.put("errors", list);
						respuesta.put("timestamp", new Date());
						respuesta.put("status", HttpStatus.BAD_REQUEST.value());
						return Mono.just(ResponseEntity.badRequest().body(respuesta));
					});
							
		});
		

	}
}
