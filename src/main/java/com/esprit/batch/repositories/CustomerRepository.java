package com.esprit.batch.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.esprit.batch.beans.Customer;

@Repository
public interface CustomerRepository extends JpaRepository <Customer, Long>{



}
