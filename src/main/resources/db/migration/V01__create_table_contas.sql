CREATE TABLE IF NOT EXISTS `contas` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(45) NOT NULL,
  `valor_original` DECIMAL(10,2) NOT NULL,
  `data_vencimento` DATE NOT NULL,
  `data_pagamento` DATE NOT NULL,
  `qtde_dias_atraso` INT NOT NULL DEFAULT 0,
  `multa` DECIMAL(10,2) NOT NULL DEFAULT 0,
  `juros_dia` DECIMAL(10,2) NOT NULL DEFAULT 0,
  `valor_corrigido` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`id`)) ENGINE = InnoDB;