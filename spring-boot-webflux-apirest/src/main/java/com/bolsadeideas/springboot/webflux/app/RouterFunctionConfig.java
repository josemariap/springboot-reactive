package com.bolsadeideas.springboot.webflux.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.bolsadeideas.springboot.webflux.app.handler.ProductoHandler;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.*; // es para el GET, POST etc de forma reactiva

/*
 * configuramos las rutas de forma reactiva
 */

@Configuration
public class RouterFunctionConfig {
	

	// configuramos los endpoint de forma funcional (functional endpoints) que usa todo reactivo
	@Bean
	public RouterFunction<ServerResponse> routes(ProductoHandler handler){
		return route(GET("/api/v2/productos").or(GET("/api/v3/productos")), request ->handler.getAll(request))
		   .andRoute(GET("/api/v2/productos/{id}"), request -> handler.findByid(request))
		   .andRoute(POST("/api/v2/productos/create"), request -> handler.create(request))
		   .andRoute(PUT("/api/v2/productos/edit/{id}"), request -> handler.update(request))
		   .andRoute(DELETE("/api/v2/productos/delete/{id}"), request -> handler.delete(request));
			
	}
}
