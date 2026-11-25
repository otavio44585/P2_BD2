package br.edu.eseg.escala.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.eseg.escala.dto.painel.DepartamentoDescobertoResponse;
import br.edu.eseg.escala.dto.painel.EspecialidadeResumoResponse;
import br.edu.eseg.escala.dto.painel.MedicosPorRegimeResponse;
import br.edu.eseg.escala.dto.painel.PlantonistaNoturnoResponse;
import br.edu.eseg.escala.dto.painel.PredioResumoResponse;
import br.edu.eseg.escala.dto.painel.RankingCargaResponse;
import br.edu.eseg.escala.dto.painel.ResumoDiaResponse;
import br.edu.eseg.escala.repository.PainelRepository;

@Service
@Transactional(readOnly = true)
public class PainelService {

    private final PainelRepository painel;

    public PainelService(PainelRepository painel) {
        this.painel = painel;
    }

    public List<MedicosPorRegimeResponse> medicosPorRegime() {
        return painel.medicosPorRegime();
    }

    public List<EspecialidadeResumoResponse> especialidades() {
        return painel.especialidades();
    }

    public List<PredioResumoResponse> predios() {
        return painel.predios();
    }

    public List<RankingCargaResponse> rankingCarga(Integer limite) {
        int lim = (limite == null) ? 5 : Math.max(1, Math.min(36, limite));
        return painel.rankingCarga(PageRequest.of(0, lim));
    }

    public List<DepartamentoDescobertoResponse> departamentosDescobertos(Integer dia) {
        return painel.departamentosDescobertos(dia);
    }

    public List<PlantonistaNoturnoResponse> plantonistasNoturnos(Integer minimo) {
        int min = (minimo == null) ? 2 : minimo;
        return painel.plantonistasNoturnos(min);
    }

    public List<ResumoDiaResponse> dias() {
        return painel.dias();
    }
}