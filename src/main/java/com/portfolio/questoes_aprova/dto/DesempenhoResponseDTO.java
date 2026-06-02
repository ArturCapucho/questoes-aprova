package com.portfolio.questoes_aprova.dto;

import java.util.List;

public record DesempenhoResponseDTO(
        long totalTentativas,
        long totalAcertos,
        long totalErros,
        double percentualAcerto,
        List<TentativaResponseDTO> ultimasTentativas
) {
}
