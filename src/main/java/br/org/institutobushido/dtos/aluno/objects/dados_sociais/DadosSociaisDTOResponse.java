package br.org.institutobushido.dtos.aluno.objects.dados_sociais;

import br.org.institutobushido.enums.Imovel;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record DadosSociaisDTOResponse(
        boolean bolsaFamilia,
        boolean auxilioBrasil,
        Imovel imovel,
        int numerosDePessoasNaCasa,
        int contribuintesDaRendaFamiliar,
        boolean alunoContribuiParaRenda,
        int rendaFamiliarEmSalariosMinimos) {
}
