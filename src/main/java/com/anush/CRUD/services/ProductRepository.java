package com.anush.CRUD.services;

import com.anush.CRUD.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository <Product, Integer>{

}
