package com.basic.batch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.basic.batch.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

}
