package operador.filtrado;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.model.Persona;
import reactor.core.publisher.Flux;

public class Filtrado {

	private static final Logger log = LoggerFactory.getLogger(Filtrado.class);

	// OPERADOR DE FILTRADO FILTER
	public void filter() {	
		List<Persona> personas = new ArrayList<Persona>();
		personas.add(new Persona(1, "mito", 29));
		personas.add(new Persona(2, "code", 28));
		personas.add(new Persona(3, "jose", 27));
			
		Flux.fromIterable(personas)
		    .filter(p -> p.getEdad() > 28)
			.subscribe(p -> log.info(p.toString()));
	}
	
	// OPERADOR DE FILTRADO DISTINCT
	public void distinct() {	
		List<Persona> personas = new ArrayList<Persona>();
		personas.add(new Persona(1, "mito", 29));// el idPersona tiene hash code
		personas.add(new Persona(1, "code", 28));
		personas.add(new Persona(3, "jose", 27));
			
		Flux.fromIterable(personas)
			.distinct()
		    .subscribe(p -> log.info(p.toString()));
		}
	
	// OPERADOR DE FILTRADO TAKE SE QUEDA CON LA CANTIDAD DE ELEMENTOS PRIMEROS INDICADA COMO PARAMETRO
	public void take() {	
		List<Persona> personas = new ArrayList<Persona>();
		personas.add(new Persona(1, "mito", 29));// el idPersona tiene hash code
		personas.add(new Persona(2, "code", 28));
		personas.add(new Persona(3, "jose", 27));
				
		Flux.fromIterable(personas)
			.take(2)
			.subscribe(p -> log.info(p.toString()));
		}
	
	// OPERADOR DE FILTRADO TAKE SE QUEDA CON LA CANTIDAD DE ELEMENTOS ULTIMOS INDICADA COMO PARAMETRO
	public void takeLast() {	
		List<Persona> personas = new ArrayList<Persona>();
		personas.add(new Persona(1, "mito", 29));// el idPersona tiene hash code
		personas.add(new Persona(2, "code", 28));
		personas.add(new Persona(3, "jose", 27));
					
		Flux.fromIterable(personas)
			.takeLast(2)
			.subscribe(p -> log.info(p.toString()));
		}
	
	// OPERADOR DE FILTRADO SKIP QUITA DE LA LISTA EL ELEMENTO DE LA POSICION INDICADO COMO PARFAMETRO EMPEZANDO DEL PRINCIPIO
	public void skip() {	
		List<Persona> personas = new ArrayList<Persona>();
		personas.add(new Persona(1, "mito", 29));// el idPersona tiene hash code
		personas.add(new Persona(2, "code", 28));
		personas.add(new Persona(3, "jose", 27));
						
		Flux.fromIterable(personas)
			.skip(1)
			.subscribe(p -> log.info(p.toString()));
			}
	
	// OPERADOR DE FILTRADO SKIP QUITA DE LA LISTA EL ELEMENTO DE LA POSICION INDICADO COMO PARFAMETRO EMPEZANDO DEL FINAL
	public void skipLast() {	
		List<Persona> personas = new ArrayList<Persona>();
		personas.add(new Persona(1, "mito", 29));// el idPersona tiene hash code
		personas.add(new Persona(2, "code", 28));
		personas.add(new Persona(3, "jose", 27));
							
		Flux.fromIterable(personas)
			.skipLast(1)
			.subscribe(p -> log.info(p.toString()));
			}

}
