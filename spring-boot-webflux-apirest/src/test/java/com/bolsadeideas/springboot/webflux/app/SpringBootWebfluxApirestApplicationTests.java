package com.bolsadeideas.springboot.webflux.app;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;

import com.bolsadeideas.springboot.webflux.app.models.documents.Producto;
import com.bolsadeideas.springboot.webflux.app.models.services.ProductoService;

import reactor.core.publisher.Mono;


//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // los test se ejecutan levantando Netty con puerto random
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // no usa Netty, se mockea el puerto
@ExtendWith({SpringExtension.class})
@AutoConfigureWebTestClient
class SpringBootWebfluxApirestApplicationTests {
	
	@Autowired
	private WebTestClient client; // cliente dew test Reactive
	
	//@Autowired
	//private MockMvc mockMvc; // prueba de rest endpoint No reactive
	
	@Autowired
	private ProductoService service;
	
	@Value("${config.base.endpoint}")
	private String url_base;  // inyectamos la base de url desde la property

	@Test
	void getAllTest() {
		client.get()
		.uri(url_base)  // usamos la url de property
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBodyList(Producto.class);
		
	}

	@Test
	void getByIdTest() {
		Producto producto = service.findByNombre("Ipad").block(); // se busca un nombre real ya que no se mockea nada y con block quitamos tipo Mono		
		client.get()
		.uri(url_base + "/{id}", producto.getId())
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody()
		.jsonPath("$.id").isNotEmpty()
		.jsonPath("$.nombre").isNotEmpty();
		
	}
	

	@Test
	void createTest() {
		Producto producto =  new Producto("Ipad", 1112.22);
		client.post()
		.uri(url_base + "/create")
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)
		.body(Mono.just(producto), Producto.class)
		.exchange()
		.expectStatus().isCreated()
		.expectBody()
		.jsonPath("$.id").isNotEmpty()
		.jsonPath("$.nombre").isEqualTo("Ipad");	
		
	}
	
	@Test
	void updateTest() {
		Producto producto = service.findByNombre("Ipad").block(); 
		Producto productoEditado = new Producto("Ipad 9", 2222.22);	
		client.put()
		.uri(url_base + "/edit/{id}", Collections.singletonMap("id", producto.getId()))
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)
		.body(Mono.just(productoEditado), Producto.class)
		.exchange()
		.expectStatus().isCreated()
		.expectBody()
		.jsonPath("$.id").isNotEmpty()
		.jsonPath("$.nombre").isEqualTo("Ipad 9");	
		
	}
	
	@Test
	void deleteTest() {
		Producto producto = service.findByNombre("Ipad").block(); 		
		client.delete()
		.uri(url_base + "/delete/{id}", Collections.singletonMap("id", producto.getId()))
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isNoContent()
		.expectBody().isEmpty(); // el delete es void y se comprueba que no devuelva nada
	}
}
