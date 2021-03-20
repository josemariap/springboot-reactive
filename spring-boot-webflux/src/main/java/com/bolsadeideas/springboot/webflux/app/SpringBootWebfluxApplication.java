package com.bolsadeideas.springboot.webflux.app;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.bolsadeideas.springboot.webflux.app.models.dao.ProductoDao;
import com.bolsadeideas.springboot.webflux.app.models.documents.Categoria;
import com.bolsadeideas.springboot.webflux.app.models.documents.Producto;
import com.bolsadeideas.springboot.webflux.app.models.services.ProductoService;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringBootWebfluxApplication implements CommandLineRunner{
	
	private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluxApplication.class);
	
	@Autowired
	private ReactiveMongoTemplate mongoTemplate;
	
	@Autowired
	private ProductoService productoService;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		mongoTemplate.dropCollection("productos").subscribe();//usamos el template para borrar los doc de la collection
		mongoTemplate.dropCollection("categorias").subscribe();//usamos el template para borrar los doc de la collection
		
		Categoria electronica = new Categoria("Electronica");
		Categoria muebles = new Categoria("Muebles");
		Categoria deporte = new Categoria("Deporte");
		
		Flux.just(electronica, muebles, deporte)
		.flatMap(productoService::save)
		.doOnNext(c -> log.info("Categoria id: " + c.getId()))
		.thenMany( // thenMany: hace lo del flujo de categorias y luego crea y hace lo del flujo productos
				Flux.just(new Producto("Ipad", 299.33, electronica),
				new Producto("Placard 5 puertas", 1299.33, muebles),
				new Producto("Set de raquetas padle", 788.39, deporte)
				)
				.flatMap(producto -> { 
					producto.setCreateAt(new Date());
					return productoService.save(producto);
					})
		)		
		.subscribe(producto -> log.info("Insert: " + producto.getId()));
	}

}
