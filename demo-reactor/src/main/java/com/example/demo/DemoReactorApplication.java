package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.model.Persona;

import io.reactivex.Observable;
import operador.combinacion.Combinacion;
import operador.condicional.Condicional;
import operador.creacion.Creacion;
import operador.error.ErrorOp;
import operador.filtrado.Filtrado;
import operador.matematico.Matematico;
import operador.transformacion.Transformacion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class DemoReactorApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(DemoReactorApplication.class);

	// MONO REPRESENTA UN FLUJO DE DATOS ASINCRONO
	public void reactor() {
		Mono.just(new Persona(1, "mito", 29))
		    .doOnNext(p -> log.info("[Ractor] Persona: " + p))
		    .subscribe(p -> log.info("[Ractor] Persona: " + p));
	}

	public void rxjava2() {
		Observable.just(new Persona(1, "mito", 29))
		    .doOnNext(p -> log.info("[RxJava2] Persona: " + p))
		    .subscribe(p -> log.info("[RxJava2] Persona: " + p));
	}
	
	// MONO REPRESENTA UN FLUJO DE DATOS ASINCRONO DE UN ELEMENTO
	public void mono() {
		Mono.just(new Persona(1, "mito", 29))
		    .subscribe(p -> log.info("[Ractor] Persona: " + p));
	}

	// FLUX REPRESENTA UN FLUJO DE DATOS ASINCRONO DE VARIOS ELEMENTOS
	public void flux() {
		List<Persona> personas = new ArrayList<Persona>();
		personas.add(new Persona(1, "mito", 29));
		personas.add(new Persona(2, "code", 28));
		personas.add(new Persona(3, "jose", 27));
		
		Flux.fromIterable(personas).subscribe(p-> log.info(p.toString()));
	}
	
	// PASAMOS DE FLUX A MONO
	public void fluxMono() {
		List<Persona> personas = new ArrayList<Persona>();
		personas.add(new Persona(1, "mito", 29));
		personas.add(new Persona(2, "code", 28));
		personas.add(new Persona(3, "jose", 27));
		
		Flux<Persona> fx = Flux.fromIterable(personas);
		fx.collectList().subscribe(lista-> log.info(lista.toString())); //VEMOS LA LISTA COMPLETA EN MONO SIN ITERAR COMO EN SOLO ELEMNENTO
	}
	
	
	/*****************************************/
	public static void main(String[] args) {
		SpringApplication.run(DemoReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("Run ok");
		//reactor();
		//rxjava2();
		//mono();
		//flux();
		//fluxMono();
		//Creacion app =  new Creacion();
		//app.range();
		//app.repeat();
		//Transformacion app =  new Transformacion();
		//app.map();
		//app.flatMat(); // flatMap hay que retornar otro flujo
		//app.groupBy();
		//Filtrado app =  new Filtrado();
		//app.filter();
		//app.distinct();
		//app.take();
		//app.takeLast();
		//app.skip();
		//app.skipLast();
		//Combinacion app = new Combinacion();
		//app.merge();
		//app.zip();
		//app.zipWith();
		//ErrorOp app =  new ErrorOp();
		//app.retry();
		//app.errorReturn();
		//app.errorResume();
		//app.errorMap();
		//Condicional app =  new Condicional();
		//app.defaulEmpty();
		//app.takeUntil();
		//app.timeout();
		Matematico app  =  new Matematico();
		//app.average();
		//app.count();
		//app.min();
		//app.sum();
		app.summarizing();
	}
	
	
	
	
	/* NOTAS:
	 * Mono-> un flujo con un elemento
	 * Flux-> un flujo con varios elementos
	 * Para saber lo que pasa en un flujo siempre hay que suscribirse
	 * 
	 * .doOnNext(p -> log.info("[Ractor] Persona: " + p)) Para depuracion
	 */

}
