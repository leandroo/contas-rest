package br.com.deliverit.contas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.deliverit.contas.model.Conta;

public interface ContaRepository extends JpaRepository<Conta, Long>{
	
}
