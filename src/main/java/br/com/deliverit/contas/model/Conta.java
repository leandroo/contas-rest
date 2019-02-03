package br.com.deliverit.contas.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@Entity(name="contas")
public class Conta {	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column
	@NotEmpty
	private String nome;
	
	@Column(name = "valor_original")
	@Min(0)
	@NotNull
	private BigDecimal valorOriginal;

	@Column(name = "data_vencimento")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd/MM/yyyy")
	@Temporal(TemporalType.DATE)
	@NotNull
	private Date dataVencimento;
	
	@Column(name = "data_pagamento")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd/MM/yyyy")
	@Temporal(TemporalType.DATE)
	@NotNull
	private Date dataPagamento;
	
	@Column(name = "qtde_dias_atraso")
	@PositiveOrZero
	@JsonProperty(access = Access.READ_ONLY)
	private Long qtdeDiasAtraso = 0L;
	
	@Column
	@PositiveOrZero
	@JsonProperty(access = Access.READ_ONLY)
	private BigDecimal multa = new BigDecimal(0);
	
	@Column(name = "juros_dia")
	@PositiveOrZero
	@JsonProperty(access = Access.READ_ONLY)
	private BigDecimal jurosDia = new BigDecimal(0);
	
	@Column(name = "valor_corrigido")
	@PositiveOrZero
	@JsonProperty(access = Access.READ_ONLY)
	private BigDecimal valorCorrigido;
	
	@JsonIgnore
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public BigDecimal getValorOriginal() {
		return valorOriginal;
	}

	public void setValorOriginal(BigDecimal valorOriginal) {
		this.valorOriginal = valorOriginal;
	}

	@JsonIgnore
	public Date getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public Date getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public Long getQtdeDiasAtraso() {
		return qtdeDiasAtraso;
	}

	public void setQtdeDiasAtraso(Long qtdeDiasAtraso) {
		this.qtdeDiasAtraso = qtdeDiasAtraso;
	}

	@JsonIgnore
	public BigDecimal getMulta() {
		return multa;
	}

	public void setMulta(BigDecimal multa) {
		this.multa = multa;
	}

	@JsonIgnore
	public BigDecimal getJurosDia() {
		return jurosDia;
	}

	public void setJurosDia(BigDecimal jurosDia) {
		this.jurosDia = jurosDia;
	}

	public BigDecimal getValorCorrigido() {
		return valorCorrigido;
	}

	public void setValorCorrigido(BigDecimal valorCorrigido) {
		this.valorCorrigido = valorCorrigido;
	}
	
	@PrePersist
	private void prePresist() {
		// dias em atraso
		Long dias = calculaQtdeDiasAtraso();
		this.setQtdeDiasAtraso(dias);
		this.setValorCorrigido(calculaValorCorrigido());

		// até 3 dias
		if (dias > 0 && dias <= 3) {
			this.setMulta(new BigDecimal(2));
			this.setJurosDia(new BigDecimal("0.1"));
			this.setValorCorrigido(calculaValorCorrigido());
		} 

		// superior a 3 dias
		else if (dias > 3 && dias <= 5) {
			this.setMulta(new BigDecimal(3));
			this.setJurosDia(new BigDecimal("0.2"));
			this.setValorCorrigido(calculaValorCorrigido());
		}

		// superior a 5 dias
		else if (dias > 5) {
			this.setMulta(new BigDecimal(5));
			this.setJurosDia(new BigDecimal("0.3"));
			this.setValorCorrigido(calculaValorCorrigido());
		}
	}

	private Long calculaQtdeDiasAtraso() {
		LocalDate dataV = this.getDataVencimento().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate dataP = this.getDataPagamento().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		Long dias = dataV.until(dataP, ChronoUnit.DAYS);		
		return dias;
	}

	private BigDecimal calculaValorCorrigido() {
		BigDecimal valorMulta, valorCorrigido;
		valorMulta = this.getValorOriginal().multiply(this.getMulta()).divide(new BigDecimal(100));
		valorCorrigido = this.getValorOriginal().add(valorMulta);
		
		for (int i=1; i<=this.getQtdeDiasAtraso(); i++){
			valorCorrigido = valorCorrigido.add( valorCorrigido.multiply(this.getJurosDia()).divide(new BigDecimal(100)) );
		}

		return valorCorrigido;
	}
	
}
