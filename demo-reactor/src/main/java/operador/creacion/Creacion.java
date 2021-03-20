package operador.creacion;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.model.Persona;

import io.reactivex.Observable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Creacion {

	private static final Logger log = LoggerFactory.getLogger(Creacion.class);
	
	public void justFrom() {
		Mono.just(new Persona(1, "mito", 29));
		//Flux.fromIterable(coleccion);
		//Observable.just(item);
	}
	
	// PARA FLUJOS VACIOS EJEMPLO CONSULTA A BD SIN DATOS
	public void empty() {
		Mono.empty();
		Flux.empty();
		Observable.empty();
	}
	
	// PARA FLUJO DE DATOS A PARTIR DE UN RANGO DE NUMEROS // de >= 0 y <3
	public void range() {
		//doOnNext: es para debuggear el flujo
		Flux.range(0, 3)
		    .doOnNext(i->  log.info("i=" + i)).subscribe();
	}
	
	// Itero y repito 3 veces la iteracion aplica para flux y para mono
	// REPEAT REPITE EL FLUJO CIERTA CANTIDAD DE VECES TANTO EN  FLUX COMO EN MONO
	public void repeat() {
		List<Persona> personas = new ArrayList<Persona>();
		personas.add(new Persona(1, "mito", 29));
		personas.add(new Persona(2, "code", 28));
		personas.add(new Persona(3, "jose", 27));
		
		Flux.fromIterable(personas)
		    .repeat(3)
		    .subscribe(p-> log.info(p.toString()));
		
		/*Mono.just(new Persona(1, "mito", 29))
		    .repeat(3)
	        .subscribe(p -> log.info("[Ractor] Persona: " + p));*/
	}
}
