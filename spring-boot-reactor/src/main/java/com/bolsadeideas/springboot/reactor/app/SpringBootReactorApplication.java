package com.bolsadeideas.springboot.reactor.app;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.bolsadeideas.springboot.reactor.app.models.Comentarios;
import com.bolsadeideas.springboot.reactor.app.models.Usuario;
import com.bolsadeideas.springboot.reactor.app.models.UsuarioComentarios;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SpringBootReactorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		//ejemplosVariosFlux();
		//ejemplosFlatMap();
		//ejemplosToString();
		//transformarAMonoList();
		//combinarDosFlujosConFlatMap();
		//combinarDosFlujosConZipWith();
		//combinarDosFlujosConZipWithForma2();
		//ejemplosRange();
		//ejemploInterval();
		//ejemploDelayElements();
		//ejemploIntervalInfinito();
		//ejemploIntervalCreateFlux();
		//ejemploContraPresionManual();
		ejemploContraPresionConOperador();

	}
	
	// Elastico
	// manejo del backPressure de forma automatica con operador limitRate,  como regular cantidad de los elementos para el subscribe
	public void ejemploContraPresionConOperador() {
		Flux.range(1, 10)
		.log() // log nos permite ver la traza completa
		.limitRate(2) // 2 elementos por lote para manejar la contra presion en el subscribe
		.subscribe(e -> log.info(e.toString()));
		
    }
	
	// Elastico
	// manejo del backPressure de forma manual como regular cantidad de los elementos para el subscribe
	// desde el subscribe/request modificamos la cantidad de elementos que queremos recibir
	public void ejemploContraPresionManual() {
		Flux.range(1, 10)
		.log() // log nos permite ver la traza completa
		.subscribe(new Subscriber<Integer>() {

			private Subscription s;
			private Integer limite = 2; // limite de elementos por lote
			private Integer consumido = 0; // nos indicara cuantos elementos hemos consumido del flujo
			
			@Override
			public void onSubscribe(Subscription s) {
                this.s = s;
            //    s.request(Long.MAX_VALUE);// pedimos la maxima cantidad de elementos, como viene por defecto
                  s.request(limite); // usamos nuestro limite de elementos para decir cuantos elementos queremos recibir en el lote
			}

			@Override
			public void onNext(Integer t) {
				// cada elemento que se emite
				// cuando se procesan los dos elementos del lote, volvemos a pedir dos elementos mas hasta terminar el flujo
				log.info(t.toString());
				consumido++;
				if(limite == consumido) {
					consumido = 0;
					s.request(limite); //cuando los elementos del lote ya se consumieron vuelvo a pedir otro lote de dos con el request
				}
				
			}

			@Override
			public void onError(Throwable t) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
	public void ejemploIntervalCreateFlux() {
		Flux.create(emitter ->{
			Timer timer =  new Timer();
			timer.schedule(new TimerTask() {
				
				private Integer contador = 0;
				
				@Override
				public void run() {
				  emitter.next(++contador);
				  if(contador == 10) {
					  timer.cancel();
					  emitter.complete();
				  }
				}
			}, 1000, 1000);
		})
		.doOnComplete(()-> log.info("Hemos terminado")) // se ejecuta cundo termina de emitir todos los elementos del flujo
		.subscribe(e -> log.info(e.toString()), error -> log.error(error.getMessage()), ()-> log.info("Fin!")); // en una lambda tratamos el item ok y en la otra el item con error y la ultima lambda se ejecuta cuando finalizo todos los items
	}
	
	public void ejemploIntervalInfinito() throws InterruptedException {
		CountDownLatch latch =  new CountDownLatch(1);
		
		Flux.interval(Duration.ofSeconds(1))
		    .doOnTerminate(() -> latch.countDown())
		    .flatMap(i -> {
		    	if(i >= 5) {
		    		return Flux.error(new InterruptedException("Solo hasta 5"));
		       }
		       return Flux.just(i);
		    })
		    .map(i -> "Hola "  + i)
		    .retry(2) // si falla se ejecuta nuevamente el flujo N cantidad de veces
		    .subscribe(s -> log.info(s), e -> log.error(e.getMessage()));
		
		latch.await();
	}
	
	public void ejemploDelayElements() {
		Flux<Integer> rango = Flux.range(0, 12)
				.delayElements(Duration.ofSeconds(1))
				.doOnNext(r -> log.info(r.toString()));	
		//rango.subscribe(); // el subscribe es sin bloqueo y no podriamos ver el retraso
		rango.blockLast();// BlockLast es como el subscribe pero con bloqueo, nos permite ver el retraso
		//IMPORTANTE
		//como es asincrono sin bloqueo, termina de ejecutar el codigo y en paralelo sigue ejecutando el flujo con retraso
			    
	}
	
	public void ejemploInterval() {
		Flux<Integer> rango = Flux.range(0, 12);
		Flux<Long> retraso = Flux.interval(Duration.ofSeconds(1)); //delay
		
		rango.zipWith(retraso, (ran, retr) -> ran)
		    .doOnNext(r -> log.info(r.toString()))
		    //.subscribe() // el subscribe es sin bloqueo y no podriamos ver el retraso
		    .blockLast();// BlockLast es como el subscribe pero con bloqueo, nos permite ver el retraso
		    
		// como es asincrono sin bloqueo, termina de ejecutar el codigo y en paralelo sigue ejecutando el flujo con retraso
	}
	
	public void ejemplosRange() {
		// generamos un stream flux con elementos de un rango determinado por parametro
		Flux<Integer> rangoNumeros = Flux.range(0, 5);
		rangoNumeros.subscribe(i-> log.info(""+i));
	}
	
	public void combinarDosFlujosConZipWith() {
		// Flujo1: creamos mono de Usuario
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("Jhon", "Doe"));
		
		// Flujo2: creamos mono de comentario
		Mono<Comentarios> comnentariosMono = Mono.fromCallable(() -> {
			Comentarios comentarios =  new Comentarios();
			comentarios.addComentario("Hola que tal!");
			comentarios.addComentario("Cuantos años tienes?");
			comentarios.addComentario("Soy de Bs As");
			return comentarios;					
		});
		
		// combinamos ambos flujos en uno solo, usamos zipWith
		Mono<UsuarioComentarios> flujoCombinado = usuarioMono
				.zipWith(comnentariosMono, (usuario, comentraios) -> new UsuarioComentarios(usuario, comentraios));
		// nos subscribimos al flujo combinado
		flujoCombinado.subscribe(uc -> log.info(uc.toString()));
	}
	
	
	public void combinarDosFlujosConZipWithForma2() {
		// Flujo1: creamos mono de Usuario
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("Jhon", "Doe"));
		
		// Flujo2: creamos mono de comentario
		Mono<Comentarios> comnentariosMono = Mono.fromCallable(() -> {
			Comentarios comentarios =  new Comentarios();
			comentarios.addComentario("Hola que tal!");
			comentarios.addComentario("Cuantos años tienes?");
			comentarios.addComentario("Soy de Bs As");
			return comentarios;					
		});
		
		// combinamos ambos flujos en uno solo, usamos zipWith T2 (forma 2)
		// usa una tupla que contiene los dos flujos para combinar
		Mono<UsuarioComentarios> flujoCombinado = usuarioMono
				.zipWith(comnentariosMono)
				.map(tuple -> {
					Usuario u = tuple.getT1();
					Comentarios c = tuple.getT2();
					return new UsuarioComentarios(u, c);
				});
				
		// nos subscribimos al flujo combinado
		flujoCombinado.subscribe(uc -> log.info(uc.toString()));
	}
	
	
	public void combinarDosFlujosConFlatMap() {
		// Flujo1: creamos mono de Usuario
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("Jhon", "Doe"));
		
		// Flujo2: creamos mono de comentario
		Mono<Comentarios> comnentariosMono = Mono.fromCallable(() -> {
			Comentarios comentarios =  new Comentarios();
			comentarios.addComentario("Hola que tal!");
			comentarios.addComentario("Cuantos años tienes?");
			comentarios.addComentario("Soy de Bs As");
			return comentarios;				
		});
		
		// combinamos ambos flujos en uno solo, usamos flatMap primero porque es el que devuelve el flujo combinado
		Mono<UsuarioComentarios> flujoCombinado = usuarioMono
				.flatMap(u -> comnentariosMono.map(c -> new UsuarioComentarios(u, c))); // aplanamos los dos flujos en uno solo
		
		// nos subscribimos al flujo combinado
		flujoCombinado.subscribe(uc -> log.info(uc.toString()));
	}
	
	
	public void transformarAMonoList() {
		// a partir de un flux que es la lista con los items, devolvemos un mono que seria la propia lista completa
		List<Usuario> usuariosList = new ArrayList<>();
		usuariosList.add(new Usuario("Mira", "Watson"));
		usuariosList.add(new Usuario("Iana", "Morgan"));
		usuariosList.add(new Usuario("Mira", "Morgan"));
		usuariosList.add(new Usuario("Caveira", "Claus"));
		
	    Flux.fromIterable(usuariosList)
				.collectList()// devuelve un mono del tipo list del tipo usuario (un solo elemento que es la lista)
				.subscribe(listMono -> log.info(listMono.toString()));

		
	}
	
	public void ejemplosToString() {
		// en el map pasamos a String el nombre extraido del objeto usuario y en el flatMap lo filtramos
		List<Usuario> usuariosList = new ArrayList<>();
		usuariosList.add(new Usuario("Mira", "Watson"));
		usuariosList.add(new Usuario("Iana", "Morgan"));
		usuariosList.add(new Usuario("Mira", "Morgan"));
		usuariosList.add(new Usuario("Caveira", "Claus"));
		
	    Flux.fromIterable(usuariosList)
				.map(usuario -> usuario.getNombre().concat(" ").concat(usuario.getApellido()))
				.flatMap(nombre -> {
					if(nombre.contains("Mira")) {
						return Mono.just(nombre); // si cumple la condicion el flataMap retorna un Mono de usuario (nuevo observable) y nos quedamos finalemnte con un Flux de usuario
					}else {
						return Mono.empty();
					}
				})
				.subscribe(u -> log.info(u.toString()));

		
	}

	public void ejemplosFlatMap() {
		// el flatMap a diferencia del mat, retorna flujo Mono o flux y hace aplanamiento
		List<String> usuariosList = new ArrayList<>();
		usuariosList.add("Mira Watson");
		usuariosList.add("Ela Lee");
		usuariosList.add("Lession Willis");
		usuariosList.add("Iana Rose");
		usuariosList.add("Iana Morgan");
		
	    Flux.fromIterable(usuariosList)
				.map(nombre -> new Usuario(nombre.split(" ")[0], nombre.split(" ")[1]))
				.flatMap(usuario -> {
					if(usuario.getNombre().equals("Iana")) {
						return Mono.just(usuario); // si cumple la condicion el flataMap retorna un Mono de usuario (nuevo observable) y nos quedamos finalemnte con un Flux de usuario
					}else {
						return Mono.empty();
					}
				})
				.subscribe(u -> log.info(u.toString()));

		
	}

	public void ejemplosVariosFlux() {
		List<String> usuariosList = new ArrayList<>();
		usuariosList.add("Mira Watson");
		usuariosList.add("Ela Lee");
		usuariosList.add("Lession Willis");
		usuariosList.add("Iana Rose");
		usuariosList.add("Caveira Morgan");
		// pasamos a flux a partir de un arreglo comun ya que la bases de datos
		// relacionales no tiene soporte para flujos reactivos
		Flux<String> nombres = Flux.fromIterable(usuariosList);

		// Flux es un publisher o Observable al igual que Mono
		// Flux<String> nombres = Flux.just("Mira Watson", "Ela Lee", "Lession Willis",
		// "Iana Rose", "Caveira Morgan", "Caveira Morgan");
		// los observables son inmutables simpre tengo el flujo anterior
		// a aprtir de un operador se crea un nuevo flujo con instancias nuevas de sus
		// elementos sin perder la del flujo anterior original
		Flux<Usuario> usuarios = nombres.map(nombre -> new Usuario(nombre.split(" ")[0], nombre.split(" ")[1]))
				.filter(usuario -> usuario.getApellido().equals("Morgan")).doOnNext(usuario -> {
					if (usuario == null) {
						throw new RuntimeException("Nombre vacio");
					}
					System.out.println(usuario.getNombre().concat(" ").concat(usuario.getApellido()));
				}).map(usuario -> {
					String nombre = usuario.getNombre().toLowerCase();
					usuario.setNombre(nombre);
					return usuario;
				});

		usuarios.subscribe(u -> log.info(u.toString()), error -> log.error(error.getMessage()), new Runnable() {

			@Override
			public void run() {
				log.info("Ha finalizado la ejecucion del observable con exito");

			}
		});

		/*
		 * SUBSCRIBE PARAMETERS: en una lambda tratamos los elementos correctos y en la
		 * lambda de error tratamos los elementos que tienen error ante un error de un
		 * elemento se termina el flujo (onComplete )como tercer parametro ejecutamos
		 * Runnable que se ejecutara el terminar de usar todos los elementos del flujo
		 * (onComplete)
		 */
	}

}