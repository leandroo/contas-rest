package br.com.deliverit.contas.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.deliverit.contas.model.Conta;
import br.com.deliverit.contas.repository.ContaRepository;


@RestController
@RequestMapping("/contas")
public class ContaController {
	
	@Autowired
	private ContaRepository contas;

	@GetMapping
	public List<Conta> listar() {
		return contas.findAll();
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Conta adicionar(@Valid @RequestBody Conta conta) {		
		return contas.save(conta);
	}

}
