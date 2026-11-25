package br.edu.eseg.escala.repository;

import java.util.List;

import br.edu.eseg.escala.dto.painel.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import br.edu.eseg.escala.model.Alocacao;

public interface PainelRepository extends Repository<Alocacao, Long> {

    /** R0 (EXEMPLO RESOLVIDO). */
    @Query("""
            select new br.edu.eseg.escala.dto.painel.MedicosPorRegimeResponse(
                   r.descricao, r.cargaHorariaSemanal, count(m.id))
            from Medico m
            join m.regime r
            group by r.id, r.descricao, r.cargaHorariaSemanal
            order by r.cargaHorariaSemanal, r.descricao
            """)
    List<MedicosPorRegimeResponse> medicosPorRegime();


    @Query("""
        select new br.edu.eseg.escala.dto.painel.EspecialidadeResumoResponse(
               e.nome,
               count(distinct m.id),
               count(distinct a.medico.id),
               coalesce(sum(t.horas), 0L)
        )
        from Especialidade e
        left join e.medicos m
        left join Alocacao a on a.medico = m
        left join a.turno t
        group by e.id, e.nome
        order by coalesce(sum(t.horas), 0L) desc, e.nome asc
        """)
    List<EspecialidadeResumoResponse> especialidades();


    @Query("""
        select new br.edu.eseg.escala.dto.painel.PredioResumoResponse(
               p.nome,
               count(distinct d.id),
               count(distinct a.id),
               count(distinct a.medico.id),
               coalesce(sum(t.horas), 0L)
        )
        from Predio p
        left join Departamento d on d.predio = p
        left join Alocacao a on a.departamento = d
        left join a.turno t
        group by p.id, p.nome
        order by p.nome asc
        """)
    List<PredioResumoResponse> predios();


    @Query("""
        select new br.edu.eseg.escala.dto.painel.RankingCargaResponse(
               m.nome,
               e.nome,
               r.descricao,
               sum(t.horas),
               r.cargaHorariaSemanal,
               round(sum(t.horas) * 100.0 / r.cargaHorariaSemanal, 1)
        )
        from Alocacao a
        join a.medico m
        join m.especialidade e
        join m.regime r
        join a.turno t
        group by m.id, m.nome, e.nome, r.descricao, r.cargaHorariaSemanal
        order by round(sum(t.horas) * 100.0 / r.cargaHorariaSemanal, 1) desc,
                 sum(t.horas) desc,
                 m.nome asc
        """)
    List<RankingCargaResponse> rankingCarga(PageRequest pageable);


    @Query("""
        select new br.edu.eseg.escala.dto.painel.DepartamentoDescobertoResponse(
               d.id,
               d.nome,
               p.nome
        )
        from Departamento d
        join d.predio p
        where not exists (
            select 1
            from Alocacao a
            where a.departamento = d
              and a.diaSemana = :dia
        )
        order by p.nome asc, d.nome asc
        """)
    List<DepartamentoDescobertoResponse> departamentosDescobertos(@Param("dia") Integer dia);


    @Query("""
        select new br.edu.eseg.escala.dto.painel.PlantonistaNoturnoResponse(
               m.nome,
               e.nome,
               count(a.id)
        )
        from Alocacao a
        join a.medico m
        join m.especialidade e
        join a.turno t
        where lower(t.nome) = 'noite'
        group by m.id, m.nome, e.nome
        having count(a.id) >= :minimo
        order by count(a.id) desc, m.nome asc
        """)
    List<PlantonistaNoturnoResponse> plantonistasNoturnos(@Param("minimo") Integer minimo);


    @Query("""
        select new br.edu.eseg.escala.dto.painel.ResumoDiaResponse(
               a.diaSemana,
               case a.diaSemana
                   when 1 then 'Segunda-feira'
                   when 2 then 'Terça-feira'
                   when 3 then 'Quarta-feira'
                   when 4 then 'Quinta-feira'
                   when 5 then 'Sexta-feira'
                   when 6 then 'Sábado'
                   when 7 then 'Domingo'
                   else 'Desconhecido'
               end,
               count(distinct a.medico.id),
               count(distinct a.departamento.id),
               coalesce(sum(t.horas), 0L)
        )
        from Alocacao a
        join a.turno t
        group by a.diaSemana
        order by a.diaSemana asc
        """)
    List<ResumoDiaResponse> dias();

}