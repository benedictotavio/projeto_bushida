package br.org.institutobushido.services.aluno;

import br.org.institutobushido.dtos.aluno.AlunoDTORequest;
import br.org.institutobushido.dtos.aluno.AlunoDTOResponse;
import br.org.institutobushido.dtos.aluno.objects.responsavel.ResponsavelDTORequest;
import br.org.institutobushido.dtos.aluno.objects.responsavel.ResponsavelDTOResponse;

public interface AlunoServicesInterface {
    AlunoDTOResponse adicionarAluno(AlunoDTORequest alunoDTORequest);

    AlunoDTOResponse buscarAlunoPorRg(String rg);

    // public int adicionarFaltaDoAluno(String rg);

    // public int retirarFaltaDoAluno(String rg);

    public ResponsavelDTOResponse adicionarResponsavel(String rg, ResponsavelDTORequest responsavelDTORequest);

    public boolean removerResponsavel(String rg, String cpf);
}
