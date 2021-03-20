package com.bolsadeideas.springboot.webflux.app.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.springframework.web.reactive.function.BodyInserters.*;

import java.net.URI;
import java.util.Date;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.bolsadeideas.springboot.webflux.app.models.documents.Producto;
import com.bolsadeideas.springboot.webflux.app.models.services.ProductoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/*
 * Esta seria una clase como un tipo controller ya que usa al service de la entidad
 */

@Component
public class ProductoHandler {
	
	@Autowired
	private ProductoService service;
	
	@Autowired
    private Validator validator; // validador de spring

	public Mono <ServerResponse> getAll(ServerRequest request) {
		return ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(service.findAll(), Producto.class);
	}
	
	public Mono <ServerResponse> findByid(ServerRequest request) {
		String id = request.pathVariable("id");
		return service.findById(id).flatMap(p -> ServerResponse
				.ok()
				.body(fromObject(p)))
				.switchIfEmpty(ServerResponse.noContent().build());// sino lo encontro por id se ejecuta el Empty
	}
	
	public Mono <ServerResponse> create(ServerRequest request) {
		Mono<Producto> producto = request.bodyToMono(Producto.class);
		return producto.flatMap(p -> {
			// validation de spring
			Errors errors =  new BeanPropertyBindingResult(p, Producto.class.getName());
			validator.validate(p, errors);
			
			if(errors.hasErrors()) {
				// creamos un Flux de los errores y luego un Flux de string con los mensajes de los errores
				return Flux.fromIterable(errors.getFieldErrors())
				    .map(fieldError -> "El campo " + fieldError.getField() + " " + fieldError.getDefaultMessage())
				    .collectList()
				    .flatMap(list -> ServerResponse.badRequest().body(fromObject(list)));			    
			}else {
				if(p.getCreateAt()==null) {
					p.setCreateAt(new Date());
				}
				return service.save(p)
		        .flatMap(pdb -> ServerResponse
				.created(URI.create("/api/v2/productos/create".concat(pdb.getId())))
				.body(fromObject(pdb)));
			}
			
		});
			
	}
	
	public Mono <ServerResponse> update(ServerRequest request) {
		Mono<Producto> producto = request.bodyToMono(Producto.class);
		String id = request.pathVariable("id");
		Mono<Producto> productoBd = service.findById(id);
		
		// combinamos con zipWith los datos del producto de la bd con el producto con los datos para actualizar
		return productoBd.zipWith(producto, (proDb, pro) ->{
	        proDb.setNombre(pro.getNombre());
	        proDb.setPrecio(pro.getPrecio());
	        proDb.setCategoria(pro.getCategoria());
	        return proDb;
		}).flatMap(p -> ServerResponse
				.created(URI.create("/api/v2/productos/edit".concat(p.getId())))
				.body(service.save(p), Producto.class))
		.switchIfEmpty(ServerResponse.noContent().build());// sino lo encontro por id se ejecuta el Empty
	}
	
	public Mono <ServerResponse> delete(ServerRequest request) {
		String id = request.pathVariable("id");	
		Mono<Producto> productoBd = service.findById(id);
		return productoBd.flatMap(p -> service.delete(p).then(ServerResponse.noContent().build()))
				.switchIfEmpty(ServerResponse.notFound().build());// sino lo encontro por id se ejecuta el Empty
	}
}
