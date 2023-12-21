package br.org.institutobushido.services.aluno;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.MongoException;

import br.org.institutobushido.dtos.aluno.AlunoDTORequest;
import br.org.institutobushido.dtos.aluno.AlunoDTOResponse;
import br.org.institutobushido.model.aluno.Aluno;
import br.org.institutobushido.repositories.AlunoRepositorio;

@Service
public class AlunoServices implements AlunoServicesInterface {

    @Autowired
    private AlunoRepositorio alunoRepositorio;

    public AlunoDTOResponse adicionarAluno(AlunoDTORequest alunoDTORequest) {
        Optional<Aluno> alunoEncontrado = alunoRepositorio.findByRg(alunoDTORequest.rg());
        if (!alunoEncontrado.isPresent()) {
            Aluno aluno = new Aluno();
            aluno.setNome(alunoDTORequest.nome());
            aluno.setBolsaFamilia(alunoDTORequest.bolsaFamilia());
            aluno.setImovel(alunoDTORequest.imovel());
            aluno.setAuxilioBrasil(alunoDTORequest.auxilioBrasil());
            aluno.setNumerosDePessoasNaCasa(alunoDTORequest.numerosDePessoasNaCasa());
            aluno.setCidade(alunoDTORequest.cidade());
            aluno.setCpfResponsavel(alunoDTORequest.cpfResponsavel());
            aluno.setDataPreenchimento(alunoDTORequest.dataPreenchimento());
            aluno.setContribuintesDaRendaFamiliar(alunoDTORequest.contribuintesDaRendaFamiliar());
            aluno.setEstado(alunoDTORequest.estado());
            aluno.setAlunoContribuiParaRenda(alunoDTORequest.alunoContribuiParaRenda());
            aluno.setRendaFamiliarEmSalariosMinimos(alunoDTORequest.rendaFamiliarEmSalariosMinimos());
            aluno.setTransporte(alunoDTORequest.transporte());
            aluno.setVemAcompanhado(alunoDTORequest.vemAcompanhado());
            aluno.setTurno(alunoDTORequest.turno());
            aluno.setRg(alunoDTORequest.rg());
            aluno.setFaltas(alunoDTORequest.faltas());
            aluno.setActive(alunoDTORequest.status());

            Aluno novoAluno = alunoRepositorio.save(aluno);

            return new AlunoDTOResponse(novoAluno.getNome(), novoAluno.isBolsaFamilia(), novoAluno.isAuxilioBrasil(),
                    novoAluno.getImovel(), novoAluno.getNumerosDePessoasNaCasa(),
                    novoAluno.getContribuintesDaRendaFamiliar(), novoAluno.isAlunoContribuiParaRenda(),
                    novoAluno.getRendaFamiliarEmSalariosMinimos(), novoAluno.getTransporte(),
                    novoAluno.isVemAcompanhado(),
                    novoAluno.getTurno(), novoAluno.getDataPreenchimento(),
                    novoAluno.getCidade(), novoAluno.getEstado(), novoAluno.getRg(), novoAluno.getCpfResponsavel(),
                    novoAluno.getFaltas(), novoAluno.isActive());
        }
        throw new MongoException("O Aluno com o rg " + alunoDTORequest.rg() + " ja esta cadastrado!");
    }

    @Override
    public AlunoDTOResponse buscarAlunoPorRg(String rg) {
        Aluno alunoEncontrado = alunoRepositorio.findByRg(rg)
                .orElseThrow(() -> new MongoException("Email: " + rg + " não encontrado"));

        return new AlunoDTOResponse(rg, alunoEncontrado.isBolsaFamilia(), alunoEncontrado.isAuxilioBrasil(),
                alunoEncontrado.getImovel(), alunoEncontrado.getNumerosDePessoasNaCasa(),
                alunoEncontrado.getContribuintesDaRendaFamiliar(), alunoEncontrado.isAlunoContribuiParaRenda(),
                alunoEncontrado.getRendaFamiliarEmSalariosMinimos(), alunoEncontrado.getTransporte(),
                alunoEncontrado.isVemAcompanhado(), alunoEncontrado.getTurno(), alunoEncontrado.getDataPreenchimento(),
                alunoEncontrado.getCidade(), alunoEncontrado.getEstado(), alunoEncontrado.getRg(),
                alunoEncontrado.getCpfResponsavel(), alunoEncontrado.getFaltas(), alunoEncontrado.checarStatus());
    }
}
