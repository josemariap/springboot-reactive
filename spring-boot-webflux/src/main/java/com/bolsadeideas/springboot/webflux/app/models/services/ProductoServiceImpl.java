package com.bolsadeideas.springboot.webflux.app.models.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolsadeideas.springboot.webflux.app.models.dao.CategoriaDao;
import com.bolsadeideas.springboot.webflux.app.models.dao.ProductoDao;
import com.bolsadeideas.springboot.webflux.app.models.documents.Categoria;
import com.bolsadeideas.springboot.webflux.app.models.documents.Producto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductoServiceImpl implements ProductoService {
	
	@Autowired
	private ProductoDao dao;
	
	@Autowired
	private CategoriaDao categoriaDao;

	@Override
	public Flux<Producto> findAll() {
		return dao.findAll();
	}

	@Override
	public Mono<Producto> findById(String id) {
		return dao.findById(id);
	}

	@Override
	public Mono<Producto> save(Producto p) {
		return dao.save(p);
	}

	@Override
	public Mono<Void> delete(Producto p) {
		return dao.delete(p);
	}

	@Override
	public Flux<Producto> findAllWithNameUpperCase() {
		return dao.findAll().map(producto -> {
			producto.setNombre(producto.getNombre().toUpperCase());
			return producto;
	        });
	}

	@Override
	public Flux<Producto> findAllWithNameUpperCaseWithRepeat() {
		return dao.findAll().map(producto -> {
			producto.setNombre(producto.getNombre().toUpperCase());
			return producto;
	        }).repeat(500);// envio flujo que si contenia 3 elemenos ahora contiene 1500
	}

	@Override
	public Flux<Categoria> findAllCategoria() {
	    return categoriaDao.findAll();
	}

	@Override
	public Mono<Categoria> findCategoriaById(String id) {
	    return categoriaDao.findById(id);
	}

	@Override
	public Mono<Categoria> save(Categoria c) {
	    return categoriaDao.save(c);
	}


}
