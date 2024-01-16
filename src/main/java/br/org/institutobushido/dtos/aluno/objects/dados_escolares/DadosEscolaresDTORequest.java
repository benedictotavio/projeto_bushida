package br.org.institutobushido.dtos.aluno.objects.dados_escolares;

import br.org.institutobushido.enums.Turno;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record DadosEscolaresDTORequest(Turno turno, String escola, String serie) {}
