import React, { useMemo, useState } from "react";
import { createRoot } from "react-dom/client";
import {
  BookOpen,
  CheckCircle2,
  KeyRound,
  Layers3,
  ListChecks,
  LogIn,
  LogOut,
  Plus,
  RotateCcw,
  ShieldCheck,
  UserPlus
} from "lucide-react";
import "./styles.css";

const initialRegister = {
  nome: "Artur",
  email: "artur@example.com",
  senha: "123456",
  role: "ALUNO"
};

const initialCategory = {
  nome: "Direito Constitucional",
  slug: "direito-constitucional",
  descricao: "Questoes de direito constitucional"
};

const initialQuestion = {
  ano: 2026,
  enunciado: "A Constituicao Federal de 1988 e considerada a lei maior do Brasil.",
  banca: "Exemplo",
  orgao: "Orgao Exemplo",
  origem: "MANUAL",
  categoriaId: 1,
  alternativas: [
    { letra: "A", texto: "Certo", correta: true },
    { letra: "B", texto: "Errado", correta: false }
  ]
};

function App() {
  const [token, setToken] = useState(localStorage.getItem("qa_token") || "");
  const [usuario, setUsuario] = useState(null);
  const [categorias, setCategorias] = useState([]);
  const [questoes, setQuestoes] = useState([]);
  const [tentativa, setTentativa] = useState(null);
  const [status, setStatus] = useState("Conecte a API Spring Boot em http://localhost:8080 e use o fluxo abaixo.");
  const [loading, setLoading] = useState(false);
  const [register, setRegister] = useState(initialRegister);
  const [login, setLogin] = useState({ email: initialRegister.email, senha: initialRegister.senha });
  const [category, setCategory] = useState(initialCategory);
  const [question, setQuestion] = useState(initialQuestion);
  const [selectedAlternativeId, setSelectedAlternativeId] = useState("");

  const authHeader = useMemo(() => (token ? { Authorization: `Bearer ${token}` } : {}), [token]);

  async function request(path, options = {}) {
    setLoading(true);
    try {
      const response = await fetch(path, {
        ...options,
        headers: {
          "Content-Type": "application/json",
          ...authHeader,
          ...(options.headers || {})
        }
      });
      const text = await response.text();
      const data = text ? JSON.parse(text) : null;

      if (!response.ok) {
        throw new Error(data?.message || `Erro HTTP ${response.status}`);
      }
      return data;
    } finally {
      setLoading(false);
    }
  }

  async function runAction(action, successMessage) {
    try {
      const result = await action();
      setStatus(successMessage);
      return result;
    } catch (error) {
      setStatus(error.message);
      return null;
    }
  }

  async function criarUsuario() {
    const data = await runAction(
      () => request("/api/usuarios", { method: "POST", body: JSON.stringify(register) }),
      "Usuario criado. Agora faca login para receber o JWT."
    );
    if (data) {
      setUsuario(data);
      setLogin({ email: register.email, senha: register.senha });
    }
  }

  async function autenticar() {
    const data = await runAction(
      () => request("/api/auth/login", { method: "POST", body: JSON.stringify(login), headers: {} }),
      "Login realizado. Token JWT armazenado no navegador."
    );
    if (data?.token) {
      setToken(data.token);
      localStorage.setItem("qa_token", data.token);
    }
  }

  function sair() {
    setToken("");
    setTentativa(null);
    localStorage.removeItem("qa_token");
    setStatus("Sessao local encerrada.");
  }

  async function criarCategoria() {
    const data = await runAction(
      () => request("/api/categorias", { method: "POST", body: JSON.stringify({ ...category, categoriaPaiId: null }) }),
      "Categoria criada e pronta para receber questoes."
    );
    if (data) {
      setCategorias((current) => [data, ...current.filter((item) => item.id !== data.id)]);
      setQuestion((current) => ({ ...current, categoriaId: data.id }));
    }
  }

  async function listarCategorias() {
    const data = await runAction(() => request("/api/categorias"), "Categorias carregadas.");
    if (data) {
      setCategorias(data);
      if (data[0]) {
        setQuestion((current) => ({ ...current, categoriaId: data[0].id }));
      }
    }
  }

  async function criarQuestao() {
    const payload = {
      ...question,
      ano: Number(question.ano),
      categoriaId: Number(question.categoriaId),
      alternativas: question.alternativas.map((item) => ({ ...item, correta: Boolean(item.correta) }))
    };
    const data = await runAction(
      () => request("/api/questoes", { method: "POST", body: JSON.stringify(payload) }),
      "Questao criada com alternativas."
    );
    if (data) {
      setQuestoes((current) => [data, ...current.filter((item) => item.id !== data.id || item.ano !== data.ano)]);
      setSelectedAlternativeId(String(data.alternativas?.[0]?.id || ""));
    }
  }

  async function listarQuestoes() {
    const data = await runAction(() => request("/api/questoes?page=0&size=10"), "Questoes carregadas com paginacao.");
    if (data?.content) {
      setQuestoes(data.content);
      setSelectedAlternativeId(String(data.content[0]?.alternativas?.[0]?.id || ""));
    }
  }

  async function responderQuestao(questao) {
    if (!usuario?.id) {
      setStatus("Crie um usuario nesta tela antes de registrar tentativa.");
      return;
    }
    if (!selectedAlternativeId) {
      setStatus("Selecione uma alternativa antes de responder.");
      return;
    }
    const data = await runAction(
      () =>
        request("/api/tentativas", {
          method: "POST",
          body: JSON.stringify({
            usuarioId: usuario.id,
            questaoId: questao.id,
            questaoAno: questao.ano,
            alternativaEscolhidaId: Number(selectedAlternativeId)
          })
        }),
      "Tentativa registrada. O backend calculou acerto e buscou/cacheou a explicacao."
    );
    if (data) {
      setTentativa(data);
    }
  }

  function updateAlternative(index, field, value) {
    setQuestion((current) => ({
      ...current,
      alternativas: current.alternativas.map((item, itemIndex) => {
        if (itemIndex !== index) return field === "correta" ? { ...item, correta: false } : item;
        return { ...item, [field]: value };
      })
    }));
  }

  return (
    <main className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-mark">QA</div>
          <div>
            <strong>Questoes Aprova</strong>
            <span>Portfolio Java + Spring</span>
          </div>
        </div>

        <nav className="nav-list" aria-label="Fluxo da aplicacao">
          <a href="#auth"><KeyRound size={18} /> Autenticacao</a>
          <a href="#categorias"><Layers3 size={18} /> Categorias</a>
          <a href="#questoes"><BookOpen size={18} /> Questoes</a>
          <a href="#tentativa"><ListChecks size={18} /> Tentativa</a>
        </nav>

        <div className="token-box">
          <ShieldCheck size={18} />
          <div>
            <span>Status JWT</span>
            <strong>{token ? "Autenticado" : "Sem token"}</strong>
          </div>
        </div>
      </aside>

      <section className="workspace">
        <header className="topbar">
          <div>
            <h1>Console de estudo</h1>
            <p>Um frontend demonstrativo consumindo a API REST do backend.</p>
          </div>
          <button className="icon-button ghost" onClick={sair} title="Sair">
            <LogOut size={18} />
            Sair
          </button>
        </header>

        <div className={loading ? "status loading" : "status"}>{status}</div>

        <section id="auth" className="panel-grid two-columns">
          <FormPanel title="Cadastro" subtitle="POST /api/usuarios" icon={<UserPlus size={20} />}>
            <Input label="Nome" value={register.nome} onChange={(value) => setRegister({ ...register, nome: value })} />
            <Input label="E-mail" value={register.email} onChange={(value) => setRegister({ ...register, email: value })} />
            <Input label="Senha" type="password" value={register.senha} onChange={(value) => setRegister({ ...register, senha: value })} />
            <button className="primary" onClick={criarUsuario}><UserPlus size={18} /> Criar usuario</button>
          </FormPanel>

          <FormPanel title="Login" subtitle="POST /api/auth/login" icon={<LogIn size={20} />}>
            <Input label="E-mail" value={login.email} onChange={(value) => setLogin({ ...login, email: value })} />
            <Input label="Senha" type="password" value={login.senha} onChange={(value) => setLogin({ ...login, senha: value })} />
            <button className="primary" onClick={autenticar}><LogIn size={18} /> Entrar</button>
          </FormPanel>
        </section>

        <section id="categorias" className="panel-grid two-columns">
          <FormPanel title="Nova categoria" subtitle="POST /api/categorias" icon={<Layers3 size={20} />}>
            <Input label="Nome" value={category.nome} onChange={(value) => setCategory({ ...category, nome: value })} />
            <Input label="Slug" value={category.slug} onChange={(value) => setCategory({ ...category, slug: value })} />
            <Input label="Descricao" value={category.descricao} onChange={(value) => setCategory({ ...category, descricao: value })} />
            <button className="primary" onClick={criarCategoria}><Plus size={18} /> Criar categoria</button>
          </FormPanel>

          <DataPanel title="Categorias" actionLabel="Atualizar" onAction={listarCategorias}>
            {categorias.length === 0 ? <EmptyState text="Nenhuma categoria carregada." /> : categorias.map((item) => (
              <div className="row-item" key={item.id}>
                <strong>{item.nome}</strong>
                <span>#{item.id} / {item.slug}</span>
              </div>
            ))}
          </DataPanel>
        </section>

        <section id="questoes" className="panel-grid">
          <FormPanel title="Nova questao" subtitle="POST /api/questoes" icon={<BookOpen size={20} />}>
            <div className="compact-grid">
              <Input label="Ano" value={question.ano} onChange={(value) => setQuestion({ ...question, ano: value })} />
              <Input label="Categoria ID" value={question.categoriaId} onChange={(value) => setQuestion({ ...question, categoriaId: value })} />
              <Input label="Banca" value={question.banca} onChange={(value) => setQuestion({ ...question, banca: value })} />
              <Input label="Orgao" value={question.orgao} onChange={(value) => setQuestion({ ...question, orgao: value })} />
            </div>
            <label className="field">
              <span>Enunciado</span>
              <textarea value={question.enunciado} onChange={(event) => setQuestion({ ...question, enunciado: event.target.value })} />
            </label>
            {question.alternativas.map((item, index) => (
              <div className="alternative-editor" key={item.letra}>
                <strong>{item.letra}</strong>
                <input value={item.texto} onChange={(event) => updateAlternative(index, "texto", event.target.value)} />
                <label className="checkline">
                  <input type="checkbox" checked={item.correta} onChange={(event) => updateAlternative(index, "correta", event.target.checked)} />
                  Correta
                </label>
              </div>
            ))}
            <button className="primary" onClick={criarQuestao}><Plus size={18} /> Criar questao</button>
          </FormPanel>

          <DataPanel title="Questoes" actionLabel="Carregar" onAction={listarQuestoes}>
            {questoes.length === 0 ? <EmptyState text="Nenhuma questao carregada." /> : questoes.map((questao) => (
              <article className="question-item" key={`${questao.id}-${questao.ano}`}>
                <header>
                  <strong>{questao.banca || "Banca nao informada"} / {questao.ano}</strong>
                  <span>Questao #{questao.id}</span>
                </header>
                <p>{questao.enunciado}</p>
                <div className="alternatives">
                  {questao.alternativas?.map((alt) => (
                    <label className="answer-option" key={alt.id}>
                      <input
                        type="radio"
                        name={`questao-${questao.id}-${questao.ano}`}
                        value={alt.id}
                        checked={selectedAlternativeId === String(alt.id)}
                        onChange={(event) => setSelectedAlternativeId(event.target.value)}
                      />
                      <span>{alt.letra}. {alt.texto}</span>
                    </label>
                  ))}
                </div>
                <button className="secondary" onClick={() => responderQuestao(questao)}>
                  <CheckCircle2 size={18} /> Responder
                </button>
              </article>
            ))}
          </DataPanel>
        </section>

        <section id="tentativa" className="result-panel">
          <div>
            <h2>Resultado da tentativa</h2>
            <p>O backend registra a resposta, calcula se acertou e usa o servico de IA com cache Redis.</p>
          </div>
          {tentativa ? (
            <div className={tentativa.correta ? "result success" : "result error"}>
              <strong>{tentativa.correta ? "Resposta correta" : "Resposta incorreta"}</strong>
              <span>{tentativa.explicacaoIa}</span>
            </div>
          ) : (
            <button className="secondary" onClick={() => setTentativa(null)}>
              <RotateCcw size={18} /> Aguardando tentativa
            </button>
          )}
        </section>
      </section>
    </main>
  );
}

function FormPanel({ title, subtitle, icon, children }) {
  return (
    <section className="panel">
      <header className="panel-header">
        <div className="panel-icon">{icon}</div>
        <div>
          <h2>{title}</h2>
          <span>{subtitle}</span>
        </div>
      </header>
      <div className="form-stack">{children}</div>
    </section>
  );
}

function DataPanel({ title, actionLabel, onAction, children }) {
  return (
    <section className="panel">
      <header className="panel-header split">
        <div>
          <h2>{title}</h2>
          <span>Dados vindos do backend</span>
        </div>
        <button className="icon-button" onClick={onAction}>
          <RotateCcw size={17} />
          {actionLabel}
        </button>
      </header>
      <div className="data-stack">{children}</div>
    </section>
  );
}

function Input({ label, value, onChange, type = "text" }) {
  return (
    <label className="field">
      <span>{label}</span>
      <input type={type} value={value} onChange={(event) => onChange(event.target.value)} />
    </label>
  );
}

function EmptyState({ text }) {
  return <div className="empty-state">{text}</div>;
}

createRoot(document.getElementById("root")).render(<App />);
