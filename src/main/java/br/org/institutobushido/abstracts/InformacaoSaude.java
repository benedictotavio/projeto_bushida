package br.org.institutobushido.abstracts;

public abstract class InformacaoSaude {
    private boolean resposta;
    private String tipo;

    protected InformacaoSaude() {
    }

    protected InformacaoSaude(String tipo) {

        if (tipo == null || tipo.isEmpty() || tipo.isBlank()) {
            this.tipo = "";
            this.resposta = false;
            return;
        }

        this.resposta = true;
        this.tipo = tipo;
    }

    public boolean getResposta() {
        return resposta;
    }

    public void setResposta(boolean resposta) {
        this.resposta = resposta;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Resposta: " + resposta + ", Tipo: " + tipo;
    }
}
