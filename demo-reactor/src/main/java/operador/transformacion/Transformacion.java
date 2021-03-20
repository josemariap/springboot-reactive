package operador.transformacion;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.model.Persona;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Transformacion {
	
	private static final Logger log = LoggerFactory.getLogger(Transformacion.class);
	
	// OPERADOR DE TRANSFORMACION MAP
	public void map() {		
		/*List<Persona> personas = new ArrayList<Persona>();
		personas.add(new Persona(1, "mito", 29));
		personas.add(new Persona(2, "code", 28));
		personas.add(new Persona(3, "jose", 27));
		
		Flux.fromIterable(personas)
		    .map(p -> {
		    	p.setEdad(p.getEdad() + 10);
		    	return p;
		    })
		    .subscribe(p -> log.info(p.toString()));*/
		Flux<Integer> fx = Flux.range(0, 10);
		Flux<Integer> fx2 = fx.map(x -> x+10);
		fx2.subscribe(x -> log.info("X: " + x));
		   
	}
	
	// OPERADOR DE TRANSFORMACION FLATMAT NOS PIDE QUE SIEMPRE SE RETORNE UN FLUJO
	public void flatMat() {	
		List<Persona> personas = new ArrayList<Persona>();
		personas.add(new Persona(1, "mito", 29));
		personas.add(new Persona(2, "code", 28));
		personas.add(new Persona(3, "jose", 27));
		
		Flux.fromIterable(personas)
		    .flatMap(p -> {
		    	p.setEdad(p.getEdad() + 10);
		    	return Mono.just(p); //retorno cada persona como un flujo mono
		    })
		    .subscribe(p -> log.info(p.toString()));
		
	}
	
	public void groupBy() {
		List<Persona> personas = new ArrayList<Persona>();
		personas.add(new Persona(1, "mito", 29));
		personas.add(new Persona(1, "code", 28));
		personas.add(new Persona(3, "jose", 27));
		
		Flux.fromIterable(personas)
		    .groupBy(Persona::getIdPersona) // se crean los grupos GrupedFlux
		    .flatMap(idFlux -> idFlux.collectList()) // paso cada GrupedFlux a un flujo mono con una lista agrupada
		    .subscribe(p -> log.info(p.toString()));
	}

}
