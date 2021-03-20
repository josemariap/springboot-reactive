package operador.matematico;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.model.Persona;

import reactor.core.publisher.Flux;


public class Matematico {

	private static final Logger log = LoggerFactory.getLogger(Matematico.class);
	
	// PROMEDIO
	public void average() {
		List<Persona> personas = new ArrayList<Persona>();
		personas.add(new Persona(1, "mito", 23));// el idPersona tiene hash code
		personas.add(new Persona(2, "code", 30));
		personas.add(new Persona(3, "jose", 27));
					
		Flux.fromIterable(personas)	
		    .collect(Collectors.averagingInt(Persona::getEdad))
			.subscribe(x -> log.info(x.toString()));
	}
	
	// CONTADOR
	public void count() {
		List<Persona> personas = new ArrayList<Persona>();
		personas.add(new Persona(1, "mito", 23));// el idPersona tiene hash code
		personas.add(new Persona(2, "code", 30));
		personas.add(new Persona(3, "jose", 27));
					
		Flux.fromIterable(personas)	
		    .count()
			.subscribe(x -> log.info(x.toString()));
	}
	
	// MINIMO
	public void min() {
		List<Persona> personas = new ArrayList<Persona>();
		personas.add(new Persona(1, "mito", 23));// el idPersona tiene hash code
		personas.add(new Persona(2, "code", 30));
		personas.add(new Persona(3, "jose", 27));
						
		Flux.fromIterable(personas)	
			.collect(Collectors.minBy(Comparator.comparing(Persona::getEdad)))
		    .subscribe(x -> log.info(x.get().toString())); // hago un x.get  porque devuelve un Optional de Persona, y con el get saco a la personas
		}
	
	// SUMA
	public void sum() {
		List<Persona> personas = new ArrayList<Persona>();
		personas.add(new Persona(1, "mito", 23));// el idPersona tiene hash code
		personas.add(new Persona(2, "code", 30));
		personas.add(new Persona(3, "jose", 27));
						
		Flux.fromIterable(personas)	
		    .collect(Collectors.summingInt(Persona::getEdad))
		    .subscribe(x -> log.info(x.toString()));
	   }
	
	// RESUMEN DE CONTADOR, PROMEDIO, SUMA, MIN Y MAX
	public void summarizing() {
		List<Persona> personas = new ArrayList<Persona>();
		personas.add(new Persona(1, "mito", 23));// el idPersona tiene hash code
		personas.add(new Persona(2, "code", 30));
		personas.add(new Persona(3, "jose", 27));
							
		Flux.fromIterable(personas)	
			.collect(Collectors.summarizingInt(Persona::getEdad))
			.subscribe(x -> log.info(x.toString()));
		}
}
