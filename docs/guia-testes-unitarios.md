# Guia de Testes Unitarios no Questoes Aprova

Este guia e um material de estudo para entender os testes adicionados ao projeto. A ideia nao e decorar tudo, mas conseguir explicar com seguranca o que esta acontecendo.

## 1. O Que E Um Teste Unitario?

Um teste unitario testa uma parte pequena do sistema, geralmente uma classe ou um metodo.

No nosso projeto, os testes focam principalmente na camada de service:

```text
UsuarioServiceImpl
QuestaoServiceImpl
TentativaServiceImpl
```

Essas classes contem regras de negocio, por exemplo:

- nao permitir email duplicado;
- criptografar senha;
- exigir exatamente uma alternativa correta;
- impedir resposta com alternativa de outra questao;
- calcular desempenho do aluno.

O teste unitario tenta responder:

> Se eu chamar este metodo com esses dados, ele se comporta como esperado?

## 2. Por Que Testar Service?

No projeto, o fluxo normal e:

```text
Controller -> DTO -> Service -> Repository -> Banco
```

O controller recebe a requisicao HTTP.

O DTO representa os dados que entram ou saem da API.

O service aplica regra de negocio.

O repository acessa o banco.

O teste unitario foca no service porque ele e onde ficam as decisoes importantes.

Exemplo:

```text
O usuario pode ser criado?
A questao tem exatamente uma alternativa correta?
A alternativa pertence a questao respondida?
O percentual de acerto foi calculado corretamente?
```

Essas sao regras de negocio.

## 3. Por Que Nao Usar Banco Real No Teste Unitario?

Se o teste depender de PostgreSQL, Docker, Redis ou API externa, ele deixa de ser simples.

Para teste unitario, queremos algo:

- rapido;
- isolado;
- previsivel;
- facil de rodar;
- sem depender de infraestrutura.

Por isso usamos mocks.

## 4. O Que E Mock?

Mock e um objeto falso que substitui uma dependencia real.

Exemplo real do projeto:

```java
@Mock
private UsuarioRepository usuarioRepository;
```

Leia assim:

```text
Mockito, crie um UsuarioRepository falso.
```

O `UsuarioRepository` real conversa com o banco.

O `UsuarioRepository` mock nao conversa com banco. Ele so responde o que o teste mandar.

## 5. Exemplo Sem Mock

Imagine o metodo real:

```java
public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
    if (usuarioRepository.existsByEmail(dto.email())) {
        throw new BusinessException("Ja existe usuario com este e-mail.");
    }

    Usuario usuario = new Usuario();
    usuario.setNome(dto.nome());
    usuario.setEmail(dto.email());
    usuario.setSenhaHash(passwordEncoder.encode(dto.senha()));

    return UsuarioResponseDTO.from(usuarioRepository.save(usuario));
}
```

Esse metodo depende de:

```text
usuarioRepository
passwordEncoder
```

No sistema real:

```text
usuarioRepository -> consulta/salva no PostgreSQL
passwordEncoder   -> criptografa senha com BCrypt
```

No teste unitario, nao queremos banco real. Entao criamos mocks.

## 6. O Que Significa `when(...).thenReturn(...)`?

Exemplo:

```java
when(usuarioRepository.existsByEmail(dto.email())).thenReturn(false);
```

Traduza assim:

```text
Quando alguem chamar existsByEmail com esse email,
o repository falso deve devolver false.
```

O metodo `existsByEmail` ja existe no projeto.

Ele esta em:

```java
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByEmail(String email);
}
```

O teste nao cria esse metodo. O teste apenas combina qual resposta o mock deve devolver.

## 7. Por Que Devolver `false`?

Porque queremos testar este cenario:

```text
Criar usuario quando o email ainda nao existe.
```

Se o email nao existe, a resposta esperada e:

```java
false
```

Entao o service deve continuar e salvar o usuario.

Em outro teste, podemos simular o contrario:

```java
when(usuarioRepository.existsByEmail(dto.email())).thenReturn(true);
```

Isso significa:

```text
Finja que esse email ja existe.
```

Nesse caso, o service deve bloquear a criacao.

## 8. O Que Significa `verify(...)`?

`verify` confere se algum metodo foi chamado.

Exemplo:

```java
verify(usuarioRepository).save(captor.capture());
```

Traduza assim:

```text
Verifique se o metodo save foi chamado no usuarioRepository.
```

Outro exemplo:

```java
verify(usuarioRepository, never()).save(any());
```

Traduza assim:

```text
Verifique se o metodo save nunca foi chamado.
```

Isso e util quando esperamos que uma regra bloqueie a operacao antes de salvar.

## 9. O Que Significa `assertThat(...)`?

`assertThat` verifica se o resultado e o esperado.

Exemplo:

```java
assertThat(response.email()).isEqualTo("artur@email.com");
```

Traduza assim:

```text
Espero que o email da resposta seja artur@email.com.
```

Outro exemplo:

```java
assertThat(tentativaSalva.getCorreta()).isTrue();
```

Traduza assim:

```text
Espero que a tentativa salva esteja marcada como correta.
```

## 10. O Que Significa `assertThatThrownBy(...)`?

Esse comando testa erro esperado.

Exemplo:

```java
assertThatThrownBy(() -> usuarioService.criar(dto))
        .isInstanceOf(BusinessException.class)
        .hasMessage("Ja existe usuario com este e-mail.");
```

Traduza assim:

```text
Quando eu tentar criar esse usuario,
espero que o metodo lance uma BusinessException
com essa mensagem.
```

Isso e muito usado para testar regras de negocio que bloqueiam algo.

## 11. O Padrao Arrange, Act, Assert

Grande parte dos testes segue esta estrutura:

```text
Arrange = preparar o cenario
Act     = executar o metodo
Assert  = verificar o resultado
```

Exemplo:

```java
@Test
void criarDeveBloquearEmailDuplicado() {
    // Arrange
    UsuarioRequestDTO dto = new UsuarioRequestDTO("Artur", "artur@email.com", "123456", "ALUNO");
    when(usuarioRepository.existsByEmail(dto.email())).thenReturn(true);

    // Act + Assert
    assertThatThrownBy(() -> usuarioService.criar(dto))
            .isInstanceOf(BusinessException.class)
            .hasMessage("Ja existe usuario com este e-mail.");

    verify(usuarioRepository, never()).save(any());
}
```

Em portugues:

```text
Arrange:
Crio um DTO e combino que o email ja existe.

Act:
Tento criar o usuario.

Assert:
Espero uma BusinessException e confirmo que nada foi salvo.
```

## 12. Testes Criados No Projeto

### UsuarioServiceImplTest

Arquivo:

```text
src/test/java/com/portfolio/questoes_aprova/service/impl/UsuarioServiceImplTest.java
```

Testa:

- criar usuario com senha criptografada;
- usar `ALUNO` como role padrao;
- bloquear email duplicado;
- bloquear role invalida.

Regra principal:

```text
Usuario nao pode ser criado com email ja existente.
```

### QuestaoServiceImplTest

Arquivo:

```text
src/test/java/com/portfolio/questoes_aprova/service/impl/QuestaoServiceImplTest.java
```

Testa:

- criar questao com categoria;
- criar alternativas vinculadas a questao;
- transformar letra da alternativa para maiuscula;
- exigir exatamente uma alternativa correta;
- falhar se categoria nao existir.

Regra principal:

```text
Uma questao deve ter exatamente uma alternativa correta.
```

### TentativaServiceImplTest

Arquivo:

```text
src/test/java/com/portfolio/questoes_aprova/service/impl/TentativaServiceImplTest.java
```

Testa:

- responder usando o usuario autenticado por email/JWT;
- registrar tentativa correta;
- gerar explicacao de IA via service;
- bloquear alternativa que pertence a outra questao;
- calcular desempenho do aluno.

Regra principal:

```text
O cliente nao escolhe usuarioId ao responder.
O backend identifica o aluno pelo usuario autenticado.
```

## 13. Por Que Isso E Importante Para O Projeto?

Antes, o projeto tinha funcionalidades.

Agora, alem das funcionalidades, ele tem testes garantindo regras importantes.

Isso ajuda em tres pontos:

```text
1. Confianca
```

Se alguem alterar o service e quebrar uma regra, o teste pode avisar.

```text
2. Portfolio
```

Mostra que voce conhece JUnit, Mockito e testes de regra de negocio.

```text
3. Entrevista
```

Voce consegue explicar que nao testou apenas "se sobe", mas regras reais do sistema.

## 14. Como Rodar Os Testes?

Na raiz do projeto:

```cmd
"C:\Users\artur\.m2\wrapper\dists\apache-maven-3.9.15-bin\4rlcemksed9vjmkvgss0jpc4po\apache-maven-3.9.15\bin\mvn.cmd" test
```

Ou, em uma maquina com Maven configurado:

```bash
./mvnw test
```

Resultado esperado:

```text
Tests run: 10
Failures: 0
Errors: 0
BUILD SUCCESS
```

## 15. Como Explicar Em Entrevista?

Uma resposta simples:

```text
Adicionei testes unitarios na camada de service usando JUnit e Mockito.
Usei mocks para simular repositories e dependencias externas, como PasswordEncoder e IAExplanationService.
Assim consigo validar regras de negocio sem subir banco, Redis ou a aplicacao inteira.
```

Uma resposta um pouco mais completa:

```text
Testei regras como email duplicado, role invalida, questao com exatamente uma alternativa correta,
tentativa vinculada ao usuario autenticado e calculo de desempenho do aluno.
Esses testes rodam rapido porque isolam os services e substituem dependencias reais por mocks.
```

## 16. Resumo Para Memorizar

```text
JUnit
= framework para escrever e rodar testes.

Mockito
= biblioteca para criar mocks.

Mock
= objeto falso usado no lugar de uma dependencia real.

when(...).thenReturn(...)
= combina a resposta de um metodo do mock.

verify(...)
= confere se um metodo foi chamado ou nao.

assertThat(...)
= verifica se o resultado e o esperado.

assertThatThrownBy(...)
= verifica se um erro esperado foi lancado.

Teste unitario
= testa uma parte pequena e isolada do sistema.
```

## 17. O Que Voce Deve Entender Primeiro?

Antes de tentar entender todos os testes, entenda so este fluxo:

```text
Service precisa de Repository.
Repository real fala com banco.
No teste unitario, nao queremos banco.
Entao usamos Repository mock.
Com when, dizemos o que o mock deve responder.
Chamamos o metodo real do service.
Com assert/verify, conferimos se a regra foi respeitada.
```

Se isso fizer sentido, voce entendeu a base de testes unitarios com Mockito.
