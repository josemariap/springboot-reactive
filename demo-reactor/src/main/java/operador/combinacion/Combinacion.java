package operador.combinacion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.model.Persona;
import com.example.model.Venta;

import reactor.core.publisher.Flux;


public class Combinacion {

	private static final Logger log = LoggerFactory.getLogger(Combinacion.class);

	public void merge() {
		List<Persona> personas = new ArrayList<Persona>();
		personas.add(new Persona(1, "mito", 29));
		personas.add(new Persona(2, "code", 28));
		personas.add(new Persona(3, "jose", 27));
			
		List<Persona> personas2 = new ArrayList<Persona>();
		personas.add(new Persona(4, "tania", 45));
		personas.add(new Persona(5, "caveira", 23));
		personas.add(new Persona(6, "sia", 18));
		
		List<Venta> ventas = new ArrayList<Venta>();
		ventas.add(new Venta(1, LocalDateTime.now()));
		
		Flux<Persona> fx1 = Flux.fromIterable(personas);
		Flux<Persona> fx2 = Flux.fromIterable(personas2);
		Flux<Venta> fx3 = Flux.fromIterable(ventas);
		
		Flux.merge(fx1, fx2, fx3)
		    .subscribe(p -> log.info(p.toString()));
			
	}
	
	public void zip() {
		List<Persona> personas = new ArrayList<Persona>();
		personas.add(new Persona(1, "mito", 29));
		personas.add(new Persona(2, "code", 28));
		personas.add(new Persona(3, "jose", 27));
			
		List<Persona> personas2 = new ArrayList<Persona>();
		personas.add(new Persona(4, "tania", 45));
		personas.add(new Persona(5, "caveira", 23));
		personas.add(new Persona(6, "sia", 18));
		
		List<Venta> ventas = new ArrayList<Venta>();
		ventas.add(new Venta(1, LocalDateTime.now()));
		
		Flux<Persona> fx1 = Flux.fromIterable(personas);
		Flux<Persona> fx2 = Flux.fromIterable(personas2);
		Flux<Venta> fx3 = Flux.fromIterable(ventas);
		
		Flux.zip(fx1, fx2, fx3)
		    .subscribe(x -> log.info(x.toString()));
		
	}
	
	public void zipWith() {
		List<Persona> personas = new ArrayList<Persona>();
		personas.add(new Persona(1, "mito", 29));
		personas.add(new Persona(2, "code", 28));
		personas.add(new Persona(3, "jose", 27));
			
		List<Persona> personas2 = new ArrayList<Persona>();
		personas.add(new Persona(4, "tania", 45));
		personas.add(new Persona(5, "caveira", 23));
		personas.add(new Persona(6, "sia", 18));
		
		List<Venta> ventas = new ArrayList<Venta>();
		ventas.add(new Venta(1, LocalDateTime.now()));
		
		Flux<Persona> fx1 = Flux.fromIterable(personas);
		Flux<Persona> fx2 = Flux.fromIterable(personas2);
		Flux<Venta> fx3 = Flux.fromIterable(ventas);
		
		//partimos de un flujo y lo combinamos con otro en un mismo elemento
		fx1.zipWith(fx3)
           .subscribe(x -> log.info(x.toString()));
		
	}
}
