package br.org.institutobushido.dtos.aluno;

import java.util.Date;
import java.util.List;

import br.org.institutobushido.dtos.aluno.objects.dados_escolares.DadosEscolaresDTOResponse;
import br.org.institutobushido.dtos.aluno.objects.dados_sociais.DadosSociaisDTOResponse;
import br.org.institutobushido.dtos.aluno.objects.endereco.EnderecoDTOResponse;
import br.org.institutobushido.dtos.aluno.objects.responsavel.ResponsavelDTOResponse;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record AlunoDTOResponse(
        String nome,
        DadosSociaisDTOResponse dadosSociais,
        DadosEscolaresDTOResponse dadosEscolares,
        Date dataPreenchimento,
        EnderecoDTOResponse endereco,
        String rg,
        List<ResponsavelDTOResponse> responsaveis,
        int faltas,
        boolean status) {
}
