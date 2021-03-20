package operador.error;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.model.Persona;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public class ErrorOp {

	private static final Logger log = LoggerFactory.getLogger(ErrorOp.class);
	
	// 	REINTENTOS DE EJECUTAR EL FLUJO SI HAY UN ERROR
	public void retry() {
		List<Persona> personas = new ArrayList<Persona>();
		personas.add(new Persona(1, "mito", 29));// el idPersona tiene hash code
		personas.add(new Persona(2, "code", 28));
		personas.add(new Persona(3, "jose", 27));
				
		Flux.fromIterable(personas)
			.concatWith(Flux.error(new RuntimeException("Error"))) //simulamos un tipo de error en el flujo
			.retry(2)//veces que queremos que se vuelva a ejecutar si hay un error
			.doOnNext(x -> log.info(x.toString()))//vemos que pasa en el flujo
			.subscribe();//nos subcribimos al flujo de datos
	}
	
	// AGREGAMOS QUE QUEREMOS RETORNAR EN CASO DE UN EN ERROR EN EL FLUJO
	public void errorReturn() {
		List<Persona> personas = new ArrayList<Persona>();
		personas.add(new Persona(1, "mito", 29));// el idPersona tiene hash code
		personas.add(new Persona(2, "code", 28));
		personas.add(new Persona(3, "jose", 27));
				
		Flux.fromIterable(personas)
		.concatWith(Flux.error(new RuntimeException("Error"))) //simulamos un tipo de error en el flujo
		.onErrorReturn(new Persona(100, "Por defecto", 999)) // retorno por defecto  de persona si hay un error
		.subscribe(x -> log.info(x.toString()));
			
	}
	
	// AGREGAMOS QUE QUEREMOS RETORNAR EN CASO DE UN EN ERROR EN EL FLUJO CONTROLAMOS EL ERROR EN EL PARAMETRO DE LA LAMBDA
	public void errorResume() {
		List<Persona> personas = new ArrayList<Persona>();
		personas.add(new Persona(1, "mito", 29));// el idPersona tiene hash code
		personas.add(new Persona(2, "code", 28));
		personas.add(new Persona(3, "jose", 27));
					
		Flux.fromIterable(personas)
		.concatWith(Flux.error(new RuntimeException("Error"))) //simulamos un tipo de error en el flujo
		.onErrorResume(error -> Mono.just(new Persona(110, "Iana", 33)))// retorno un flujo Mono con una persona si hay un error o flujo con mensaje de error o error
		.subscribe(x -> log.info(x.toString()));			
		}
	
	// 
	public void errorMap() {
		List<Persona> personas = new ArrayList<Persona>();
		personas.add(new Persona(1, "mito", 29));// el idPersona tiene hash code
		personas.add(new Persona(2, "code", 28));
		personas.add(new Persona(3, "jose", 27));
						
		Flux.fromIterable(personas)
		.concatWith(Flux.error(new RuntimeException("Error"))) //simulamos un tipo de error en el flujo
		.onErrorMap(error -> new InterruptedException(error.getMessage())) // tratamos el error y mostramos un mensaje de error
		.subscribe(x -> log.info(x.toString()));			
		}


}
