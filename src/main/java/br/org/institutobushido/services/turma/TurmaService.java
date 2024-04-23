package br.org.institutobushido.services.turma;

import java.util.List;

import br.org.institutobushido.resources.exceptions.LimitQuantityException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import br.org.institutobushido.controllers.dtos.turma.DadosTurmaDTOResponse;
import br.org.institutobushido.controllers.dtos.turma.TurmaAlunoDTOResponse;
import br.org.institutobushido.controllers.dtos.turma.TurmaDTORequest;
import br.org.institutobushido.controllers.dtos.turma.TurmaDTOResponse;
import br.org.institutobushido.mappers.turma.TurmaMapper;
import br.org.institutobushido.models.admin.Admin;
import br.org.institutobushido.models.admin.turmas.TurmaResponsavel;
import br.org.institutobushido.models.aluno.Aluno;
import br.org.institutobushido.models.turma.Turma;
import br.org.institutobushido.models.turma.tutor.Tutor;
import br.org.institutobushido.repositories.AdminRepositorio;
import br.org.institutobushido.repositories.TurmaRepositorio;
import br.org.institutobushido.resources.exceptions.AlreadyRegisteredException;
import br.org.institutobushido.resources.exceptions.EntityNotFoundException;

@Service
public class TurmaService implements TurmaServiceInterface {

    private final TurmaRepositorio turmaRepositorio;
    private final MongoTemplate mongoTemplate;
    private final AdminRepositorio adminRepositorio;

    public TurmaService(TurmaRepositorio turmaRepositorio, MongoTemplate mongoTemplate,
            AdminRepositorio adminRepositorio) {
        this.mongoTemplate = mongoTemplate;
        this.turmaRepositorio = turmaRepositorio;
        this.adminRepositorio = adminRepositorio;
    }

    @Override
    public String criarNovaTurma(TurmaDTORequest turma) {
        boolean turmaExiste = this.verificaSeTurmaExiste(turma.nome());

        if (turmaExiste) {
            throw new AlreadyRegisteredException("Turma " + turma.nome() + " ja está registrada.");
        }

        Turma novaTurma = TurmaMapper.mapToTurma(turma);

        Admin admin = this.vinculrTurmaAoAdmin(turma.tutor().email(),
                new TurmaResponsavel(novaTurma.getEndereco(), novaTurma.getNome()));

        novaTurma.setTutor(
                new Tutor(admin.getNome(), admin.getEmail()));

        this.turmaRepositorio.save(novaTurma);
        return "Turma " + turma.nome() + " foi criada com sucesso!";
    }

    @Override
    public String deletarTurma(String emailAdmin, String nomeTurma) {

        boolean turmaExiste = this.verificaSeTurmaExiste(nomeTurma);

        if (!turmaExiste) {
            throw new EntityNotFoundException("Turma com esse nome não existe");
        }

        if (this.vefrificarSeExistemAlunosAtivosNaTurma(nomeTurma)) {
            throw new LimitQuantityException("Turma com alunos não pode ser deletada");
        }

        this.desvicularTurmaDoAdmin(emailAdmin, nomeTurma);

        this.turmaRepositorio.deleteByNome(nomeTurma);
        return "Turma " + nomeTurma + " deletada com sucesso";
    }

    @Override
    public List<TurmaDTOResponse> listarTurmas() {
        List<Turma> turmas = this.turmaRepositorio.findAll();
        return TurmaMapper.mapToListTurmaDTOResponse(turmas);
    }

    @Override
    public TurmaDTOResponse buscarTurmaPorNome(String nomeTurma) {
        return TurmaMapper.mapToTurmaDTOResponse(this.encontrarTurmaPeloNome(nomeTurma));
    }

    @Override
    public DadosTurmaDTOResponse listarAlunosDaTurma(String nomeTurma) {
        Turma turma = this.encontrarTurmaPeloNome(nomeTurma);
        return new DadosTurmaDTOResponse(turma.getTutor().getEmail(), this.listarAlunosAtivosDaTurma(nomeTurma));
    }

    private List<TurmaAlunoDTOResponse> listarAlunosAtivosDaTurma(String nomeTurma) {
        AggregationOperation match = Aggregation.match(Criteria.where("nome").is(nomeTurma));

        AggregationOperation lookup = Aggregation.lookup("alunos", "nome", "turma", "alunos_turma");

        AggregationOperation unwind = Aggregation.unwind("$alunos_turma");

        AggregationOperation project = Aggregation.project()
                .and("alunos_turma.nome").as("nome")
                .and("alunos_turma._id").as("rg")
                .and("alunos_turma.genero").as("genero")
                .and("alunos_turma.dataNascimento").as("dataNascimento")
                .andExclude("_id");

        Aggregation aggregation = Aggregation.newAggregation(match, lookup, unwind, project);
        return mongoTemplate.aggregate(aggregation, "turmas", TurmaAlunoDTOResponse.class).getMappedResults();
    }

    private boolean verificaSeTurmaExiste(String nomeTurma) {
        return this.turmaRepositorio.findByNome(nomeTurma).isPresent();
    }

    private Admin vinculrTurmaAoAdmin(String emailAdmin, TurmaResponsavel turma) {
        Admin admin = this.encontrarAdminPeloEmail(emailAdmin);
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(emailAdmin));
        Update update = new Update();
        update.push("turmas", admin.adicionarTurma(turma));
        this.mongoTemplate.updateFirst(query, update, Admin.class);
        return admin;
    }

    private void desvicularTurmaDoAdmin(String emailAdmin, String nomeTurma) {
        Admin admin = this.encontrarAdminPeloEmail(emailAdmin);
        String turma = admin.removerTurma(nomeTurma);
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(emailAdmin));
        Update update = new Update();
        update.pull("turmas", Query.query(Criteria.where("nome").is(turma)));
        this.mongoTemplate.updateFirst(query, update, Admin.class);
    }

    private boolean vefrificarSeExistemAlunosAtivosNaTurma(String nomeTurma) {
        Query query = new Query();
        query.addCriteria(
                Criteria.where("turma").is(nomeTurma).and("graduacao").elemMatch(Criteria.where("status").is(true)));
        return this.mongoTemplate.exists(query, Aluno.class);
    }

    private Turma encontrarTurmaPeloNome(String nomeTurma) {
        return this.turmaRepositorio.findByNome(nomeTurma).orElseThrow(
                () -> new EntityNotFoundException("Turma com esse nome não existe"));
    }

    private Admin encontrarAdminPeloEmail(String email) {
        return this.adminRepositorio.findByEmailAdmin(email).orElseThrow(
                () -> new EntityNotFoundException("Admin com esse email não existe"));
    }
}
