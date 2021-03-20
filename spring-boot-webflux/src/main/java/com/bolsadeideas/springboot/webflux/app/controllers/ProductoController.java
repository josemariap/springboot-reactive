package com.bolsadeideas.springboot.webflux.app.controllers;

import java.time.Duration;
import java.util.Date;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import com.bolsadeideas.springboot.webflux.app.models.documents.Categoria;
import com.bolsadeideas.springboot.webflux.app.models.documents.Producto;
import com.bolsadeideas.springboot.webflux.app.models.services.ProductoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller // para Thimeleaf
//@RestController // rest json
public class ProductoController {

	@Autowired
	private ProductoService service;

	private static final Logger log = LoggerFactory.getLogger(ProductoController.class);

	public Flux<Categoria> categorias(){
		return service.findAllCategoria();
	};
	
	// vistas con Thimeleaf
	@GetMapping({ "/listar", "/" })
	public String listar(Model model) {
		Flux<Producto> productos = service.findAllWithNameUpperCase();

		productos.subscribe(prod -> log.info(prod.getNombre()));

		model.addAttribute("productos", productos);
		model.addAttribute("titulo", "Listado de Productos");
		return "listar";
	}

	// reactive data driven manejo de contrapresion para la vista
	// vistas con Thimeleaf
	@GetMapping("/listar-datadriver")
	public String listarDataDriver(Model model) {
		Flux<Producto> productos = service.findAllWithNameUpperCase()
				.delayElements(Duration.ofSeconds(1));

		productos.subscribe(prod -> log.info(prod.getNombre()));

		model.addAttribute("productos", new ReactiveDataDriverContextVariable(productos, 1)); // pasamos los items de
																								// forma parcelada. De a 1
		model.addAttribute("titulo", "Listado de Productos");
		return "listar";
	}

	// vistas con Thimeleaf
	@GetMapping("/listar-full")
	public String listarFull(Model model) {
		Flux<Producto> productos = service.findAllWithNameUpperCase()
				.repeat(500);// nuestro flujo si contenia 3 elemenos ahora contiene 1500

		model.addAttribute("productos", productos);
		model.addAttribute("titulo", "Listado de Productos");
		return "listar";
	}

	// vistas con Thimeleaf
	//chunked es para mandar por parte los datos, por cantidad de bytes. Manejo de contra presion
	@GetMapping("/listar-chunked") // usa properties, indicamos los nombres de las vistas y la cantidad de bytes de datos que se envian de apoco a las vistas
	public String listarChunked(Model model) {
		Flux<Producto> productos = service.findAllWithNameUpperCaseWithRepeat();
		
		model.addAttribute("productos", productos);
		model.addAttribute("titulo", "Listado de Productos");
		return "listar-chunked";
	}
	
	/************************************************/
	
	/*Formulario*/
	
	@GetMapping("/form")
	public Mono<String> crear(Model model){	
		model.addAttribute("producto", new Producto());
		model.addAttribute("titulo", "Formulario de producto");
		return Mono.just("form");
	}
	
	@PostMapping("/form")
	public Mono<String> guardar(@Valid Producto producto, BindingResult result, Model model){	
		//@Valid es para las validaciones que tiene la entidad producto, y simpre se coloca el BindigResult pegado para ver si hay o no errores en esa validacion
	   if(result.hasErrors()) {
		   model.addAttribute("titulo", "Error en el formulario");
		   return Mono.just("form"); //si hay errores al completar el formulario nos vuelve a llevar al form
	   } else { 
		 // validamos que venga la fecha
		if(producto.getCreateAt()==null) {
			producto.setCreateAt(new Date());
		}
		return service.save(producto).doOnNext(p ->
	        log.info("Producto id: " + p.getId()))
	    		.thenReturn("redirect:/listar");
	   }
	}
	
	@GetMapping("/form/{id}")
	public Mono<String> editar(@PathVariable String id, Model model){	
		return service.findById(id)
				.doOnNext(p -> {
			model.addAttribute("producto", p);
		    model.addAttribute("titulo", "Editar producto");		
			}).defaultIfEmpty(new Producto()) //sino lo encuentra devuelve el producto vacio
			.flatMap(p -> {
				if(p.getId() == null) {
					Mono.error(new InterruptedException("Producto no encontrado")); // lanzo el error para que se ejecute el onErrorResume
				}
				return Mono.just(p);
			})
		    .then(Mono.just("form"))
		    .onErrorResume(ex -> Mono.just("redirect:/listar?error=no+existe+el+producto"));  // eso se ejecuta solo si hay un error
	
	}
	
	@GetMapping("/eliminar/{id}")
	public Mono<String> eliminar(@PathVariable String id){
		return service.findById(id) // si no lo encuentra se ejecuta el defaultIfEmpty
				.defaultIfEmpty(new Producto()) //sino lo encuentra devuelve el producto vacio
				.flatMap(p -> {
					if(p.getId() == null) {
						Mono.error(new InterruptedException("Producto no encontrado para eliminar")); // lanzo el error para que se ejecute el onErrorResume
					}
					return Mono.just(p);
				})
				.flatMap(service::delete) // reference method usa inplicitamente el parametro de la lambda que es el que encontro para eliminar
				.then(Mono.just("redirect:/listar?seccess=producto+eliminado+co+exito")) // se ejcuta si no hay exception y todo fue bien para convertir a Mono String
				.onErrorResume(ex -> Mono.just("redirect:/listar?error=no+existe+el+product")); // solo se ejecuta si se lanzo una Exception
	}
	

	
	/***************************************************/

	/*
	 * Rest
	 * 
	 * @GetMapping("/productos") public Flux<Producto> findAll() { Flux<Producto>
	 * productos = dao.findAll(); return productos; }
	 */

}
