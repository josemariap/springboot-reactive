package operador.condicional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.model.Persona;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Condicional {
	
private static final Logger log = LoggerFactory.getLogger(Condicional.class);
	
	// SI EL FLUJO ESTA VACIO RETORNO UN VALOR POR DEFECTO
	public void defaulEmpty() {
		 Mono.just(new Persona(14, "Lession", 26))
		//Mono.empty()// simulo el flujo Mono vacio
		//Flux.empty() // simulo un flujo Flux vacio
		    .defaultIfEmpty(new Persona(15, "DEFAULT", 50))// valor por defecto si esta vacio el flujo
		    .subscribe(x -> log.info(x.toString()));
		
	}
	
	// EMITE EL FLUJO HASTA QUE SE CUMPLE LA CONDICION
	public void takeUntil() {
		List<Persona> personas = new ArrayList<Persona>();
		personas.add(new Persona(1, "mito", 23));// el idPersona tiene hash code
		personas.add(new Persona(2, "code", 30));
		personas.add(new Persona(3, "jose", 27));
					
		Flux.fromIterable(personas)	
		    .takeUntil(p -> p.getEdad() > 28)
			.subscribe(x -> log.info(x.toString()));
			
		}
	
	// 
	public void timeout() {
		List<Persona> personas = new ArrayList<Persona>();
		personas.add(new Persona(1, "mito", 23));// el idPersona tiene hash code
		personas.add(new Persona(2, "code", 30));
		personas.add(new Persona(3, "jose", 27));
						
		Flux.fromIterable(personas)	
			.delayElements(Duration.ofSeconds(3)) // un retraso de 3 segundos
			.timeout(Duration.ofSeconds(2))// si se retrasa mas de 2 segundo sale un timeout
		    .subscribe(x -> log.info(x.toString()));
		
		// pongo un sleep de 5 segundos ya que al hilo del flujo tiene un delay de 3 segundos para que cuando pase los 2 segundos lance un timeout
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
	
}
