package br.org.institutobushido.services.aluno;

import br.org.institutobushido.controllers.dtos.aluno.AlunoDTORequest;
import br.org.institutobushido.controllers.dtos.aluno.AlunoDTOResponse;
import br.org.institutobushido.controllers.dtos.aluno.UpdateAlunoDTORequest;
import br.org.institutobushido.controllers.dtos.aluno.graduacao.GraduacaoDTOResponse;
import br.org.institutobushido.controllers.dtos.aluno.graduacao.faltas.FaltaDTORequest;
import br.org.institutobushido.controllers.dtos.aluno.responsavel.ResponsavelDTORequest;
import br.org.institutobushido.controllers.dtos.aluno.responsavel.ResponsavelDTOResponse;

public interface AlunoServicesInterface {
    AlunoDTOResponse adicionarAluno(AlunoDTORequest alunoDTORequest);

    AlunoDTOResponse buscarAluno(String rg);

    public String editarAlunoPorRg(String rg, UpdateAlunoDTORequest updateAlunoDTORequest);

    public String adicionarFaltaDoAluno(String rg, FaltaDTORequest faltas, long novaFalta);

    public String retirarFaltaDoAluno(String rg, String faltasId);

    public ResponsavelDTOResponse adicionarResponsavel(String rg, ResponsavelDTORequest responsavelDTORequest);

    public String removerResponsavel(String rg, String cpf);

    public String adicionarDeficiencia(String rg, String deficiencia);

    public String removerDeficiencia(String rg, String deficiencia);

    public String adicionarAcompanhamentoSaude(String rg, String acompanhamentoSaude);

    public String removerAcompanhamentoSaude(String rg, String acompanhamentoSaude);

    public GraduacaoDTOResponse finalizarGraduacao(String rg);

    public Object editarHistoricoDeSaude(String rg, String campo, String historicoDeSaude, boolean resposta);
}