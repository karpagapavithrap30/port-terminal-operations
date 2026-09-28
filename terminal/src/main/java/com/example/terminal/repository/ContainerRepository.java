package com.example.terminal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.terminal.model.Container;

public interface ContainerRepository extends JpaRepository<Container, Long> {
	boolean existsByContainerNumberIgnoreCase(String containerNumber);
}