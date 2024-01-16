package br.org.institutobushido.services.aluno;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mongodb.MongoException;

import br.org.institutobushido.dtos.aluno.AlunoDTORequest;
import br.org.institutobushido.dtos.aluno.AlunoDTOResponse;
import br.org.institutobushido.dtos.aluno.objects.responsavel.ResponsavelDTORequest;
import br.org.institutobushido.dtos.aluno.objects.responsavel.ResponsavelDTOResponse;
import br.org.institutobushido.mappers.DadosEscolaresMapper;
import br.org.institutobushido.mappers.DadosSociaisMapper;
import br.org.institutobushido.mappers.EnderecoMapper;
import br.org.institutobushido.mappers.GraduacaoMapper;
import br.org.institutobushido.mappers.ResponsavelMapper;
import br.org.institutobushido.model.aluno.Aluno;
import br.org.institutobushido.model.aluno.object.Responsavel;
import br.org.institutobushido.repositories.AlunoRepositorio;

@Service
public class AlunoServices implements AlunoServicesInterface {

    @Autowired
    private AlunoRepositorio alunoRepositorio;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public AlunoDTOResponse adicionarAluno(AlunoDTORequest alunoDTORequest) {

        Optional<Aluno> alunoEncontrado = alunoRepositorio.findByRg(alunoDTORequest.rg());

        if (!alunoEncontrado.isPresent()) {
            Aluno aluno = new Aluno();
            aluno.setNome(alunoDTORequest.nome());
            aluno.setRg(alunoDTORequest.rg());
            aluno.setGraduacao(GraduacaoMapper.mapToGraduacao(alunoDTORequest.graduacao()));
            aluno.setResponsaveis(ResponsavelMapper.mapToResponsaveis(alunoDTORequest.responsaveis()));
            aluno.setEndereco(EnderecoMapper.mapToEndereco(alunoDTORequest.endereco()));
            aluno.setDadosSociais(DadosSociaisMapper.mapToDadosSociais(alunoDTORequest.dadosSociais()));
            aluno.setDadosEscolares(DadosEscolaresMapper.mapToDadosEscolares(alunoDTORequest.dadosEscolares()));
            Aluno novoAluno = alunoRepositorio.save(aluno);

            return AlunoDTOResponse.builder()
                    .withNome(novoAluno.getNome())
                    .withDataPreenchimento(novoAluno.getDataPreenchimento())
                    .withRg(novoAluno.getRg())
                    .withResponsaveis(ResponsavelMapper.mapToResponsaveisDTOResponse(novoAluno.getResponsaveis()))
                    .withEndereco(EnderecoMapper.mapToEnderecoDTOResponse(novoAluno.getEndereco()))
                    .withDadosSociais(DadosSociaisMapper.mapToDadosSociaisDTOResponse(novoAluno.getDadosSociais()))
                    .withDadosEscolares(
                            DadosEscolaresMapper.mapToDadosEscolaresDTOResponse(novoAluno.getDadosEscolares()))
                    .withGraduacao(GraduacaoMapper.mapToGraduacaoDTOResponse(novoAluno.getGraduacao()))
                    .build();
        }

        throw new MongoException("O Aluno com o rg " + alunoDTORequest.rg() + " ja esta cadastrado!");
    }

    @Override
    public AlunoDTOResponse buscarAlunoPorRg(String rg) {
        Aluno alunoEncontrado = encontrarAlunoPorRg(rg);

        return AlunoDTOResponse.builder()
                .withNome(alunoEncontrado.getNome())
                .withDadosSociais(DadosSociaisMapper.mapToDadosSociaisDTOResponse(alunoEncontrado.getDadosSociais()))
                .withDataPreenchimento(alunoEncontrado.getDataPreenchimento())
                .withRg(alunoEncontrado.getRg())
                .withResponsaveis(ResponsavelMapper.mapToResponsaveisDTOResponse(alunoEncontrado.getResponsaveis()))
                .withEndereco(EnderecoMapper.mapToEnderecoDTOResponse(alunoEncontrado.getEndereco()))
                .withDadosEscolares(
                        DadosEscolaresMapper.mapToDadosEscolaresDTOResponse(alunoEncontrado.getDadosEscolares()))
                .withGraduacao(GraduacaoMapper.mapToGraduacaoDTOResponse(alunoEncontrado.getGraduacao()))
                .build();
    }

    @Override
    public ResponsavelDTOResponse adicionarResponsavel(String rg, ResponsavelDTORequest responsavelDTORequest) {
        Aluno aluno = encontrarAlunoPorRg(rg);
        Optional<Responsavel> responsavel = encontrarResponsavelPorCpf(aluno, responsavelDTORequest.cpf());
        if (aluno.getResponsaveis().size() < 5 && responsavel.isEmpty()) {
            Query query = new Query();
            query.addCriteria(Criteria.where("rg").is(aluno.getRg()));
            Update update = new Update().push("responsaveis", responsavelDTORequest);
            mongoTemplate.updateFirst(query, update, Aluno.class);
            return ResponsavelDTOResponse.builder().withCpf(responsavelDTORequest.cpf())
                    .withEmail(responsavelDTORequest.email()).withFiliacao(responsavelDTORequest.filiacao().toString())
                    .withNome(responsavelDTORequest.nome())
                    .withTelefone(responsavelDTORequest.telefone()).build();
        }
        throw new MongoException("Não foi possivel adicionar esse responsavel");
    }

    @Override
    public boolean removerResponsavel(String rg, String cpf) {
        Aluno aluno = encontrarAlunoPorRg(rg);
        Optional<Responsavel> responsavel = encontrarResponsavelPorCpf(aluno, cpf);
        if (responsavel.isPresent() && aluno.getResponsaveis().size() > 1) {
            Query query = new Query();
            query.addCriteria(Criteria.where("rg").is(aluno.getRg()));
            Update update = new Update().pull("responsaveis", Query.query(Criteria.where("cpf").is(cpf)));
            mongoTemplate.updateFirst(query, update, Aluno.class);
            return true;
        }
        throw new MongoException("O aluno deve ter pelo menos 1 responsavel!");
    }

    protected Aluno encontrarAlunoPorRg(String rgAluno) {
        return alunoRepositorio.findByRg(rgAluno)
                .orElseThrow(() -> new MongoException("Email: " + rgAluno + " não encontrado"));
    }

    protected Optional<Responsavel> encontrarResponsavelPorCpf(Aluno aluno, String cpf) {
        return aluno.getResponsaveis().stream()
                .filter(responsaveis -> responsaveis.getCpf().equals(cpf))
                .findFirst();
    }
}
