package com.portfolio.questoes_aprova.service.impl;

import com.portfolio.questoes_aprova.entity.Alternativa;
import com.portfolio.questoes_aprova.entity.Questao;
import com.portfolio.questoes_aprova.service.IAExplanationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class OpenAiExplanationService implements IAExplanationService {

    private static final Duration CACHE_TTL = Duration.ofDays(30);

    private final StringRedisTemplate redisTemplate;

    @Value("${openai.api-key:}")
    private String apiKey;

    @Value("${openai.model:gpt-4o-mini}")
    private String model;

    @Override
    public String gerarExplicacao(Questao questao, Alternativa alternativaEscolhida) {
        String cacheKey = "ia:explicacao:%s:%s:%s".formatted(questao.getId(), questao.getAno(), alternativaEscolhida.getId());
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.hasText(cached)) {
            return cached;
        }

        String explicacao = gerarComLangChain4j(questao, alternativaEscolhida);
        redisTemplate.opsForValue().set(cacheKey, explicacao, CACHE_TTL);
        return explicacao;
    }

    private String gerarComLangChain4j(Questao questao, Alternativa alternativaEscolhida) {
        String prompt = """
                Explique de forma curta e didatica a questao abaixo.
                Enunciado: %s
                Alternativa escolhida: %s - %s
                Informe se a alternativa esta correta e justifique.
                """.formatted(questao.getEnunciado(), alternativaEscolhida.getLetra(), alternativaEscolhida.getTexto());

        if (!StringUtils.hasText(apiKey)) {
            return "Explicacao IA indisponivel: configure OPENAI_API_KEY para ativar LangChain4j/OpenAI.";
        }

        try {
            Class<?> modelClass = Class.forName("dev.langchain4j.model.openai.OpenAiChatModel");
            Object builder = modelClass.getMethod("builder").invoke(null);
            builder.getClass().getMethod("apiKey", String.class).invoke(builder, apiKey);
            builder.getClass().getMethod("modelName", String.class).invoke(builder, model);
            Object chatModel = builder.getClass().getMethod("build").invoke(builder);
            Method generate = chatModel.getClass().getMethod("generate", String.class);
            return String.valueOf(generate.invoke(chatModel, prompt));
        } catch (ReflectiveOperationException ex) {
            return "Explicacao IA indisponivel: nao foi possivel inicializar LangChain4j/OpenAI. " + ex.getMessage();
        }
    }
}
