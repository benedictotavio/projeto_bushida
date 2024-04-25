package br.org.institutobushido.models.aluno.endereco;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Endereco implements Serializable {
    private static final long serialVersionUID = 2405172041950251807L;
    private String cidade;
    private String estado;
    private String cep;
    private String numero;
    private String logradouro;

    public void setLogradouro(String logradouro) {
        if (logradouro == null) {
            return;
        }
        this.logradouro = logradouro;
    }

    public void setCidade(String cidade) {
        if (cidade == null) {
            return;
        }
        this.cidade = cidade;
    }

    public void setEstado(String estado) {
        if (estado == null) {
            return;
        }
        this.estado = estado;
    }

    public void setCep(String cep) {
        if (cep == null) {
            return;
        }
        this.cep = cep;
    }

    public void setNumero(String numero) {
        if (numero == null) {
            return;
        }
        this.numero = numero;
    }
}