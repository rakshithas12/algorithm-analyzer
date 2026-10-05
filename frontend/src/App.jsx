import { useEffect, useMemo, useState } from 'react';
import AlgorithmCard from './components/AlgorithmCard.jsx';
import ComplexityGrid from './components/ComplexityGrid.jsx';
import InputPanel from './components/InputPanel.jsx';
import ResultPanel from './components/ResultPanel.jsx';
import { executeAlgorithm, getAlgorithm, getAlgorithms } from './services/api.js';

export default function App() {
  const [algorithms, setAlgorithms] = useState([]);
  const [selectedId, setSelectedId] = useState('');
  const [selected, setSelected] = useState(null);
  const [result, setResult] = useState(null);
  const [listLoading, setListLoading] = useState(true);
  const [detailsLoading, setDetailsLoading] = useState(false);
  const [running, setRunning] = useState(false);
  const [pageError, setPageError] = useState('');
  const [runError, setRunError] = useState('');

  useEffect(() => {
    let active = true;
    getAlgorithms()
      .then((items) => {
        if (!active) return;
        if (!items.length) {
          setPageError('The backend returned an empty algorithm library.');
          return;
        }
        setAlgorithms(items);
        setSelectedId(items[0].id);
      })
      .catch((error) => active && setPageError(error.message))
      .finally(() => active && setListLoading(false));
    return () => { active = false; };
  }, []);

  useEffect(() => {
    if (!selectedId) return undefined;
    let active = true;
    setDetailsLoading(true);
    setSelected(null);
    getAlgorithm(selectedId)
      .then((item) => active && setSelected(item))
      .catch((error) => active && setRunError(error.message))
      .finally(() => active && setDetailsLoading(false));
    return () => { active = false; };
  }, [selectedId]);

  const selectedCard = useMemo(() => algorithms.find((item) => item.id === selectedId), [algorithms, selectedId]);

  function chooseAlgorithm(id) {
    setSelectedId(id);
    setResult(null);
    setRunError('');
  }

  async function run(payload) {
    if (!selected || running) return;
    setRunning(true);
    setRunError('');
    setResult(null);
    try {
      setResult(await executeAlgorithm(selected.id, payload));
    } catch (error) {
      setRunError(error.message);
    } finally {
      setRunning(false);
    }
  }

  return (
    <div className="app-shell">
      <header className="topbar">
        <a className="brand" href="#top" aria-label="Algorithm Analyzer home">
          <span className="brand-mark">A<span>.</span></span>
          <span>Algorithm<span className="brand-light"> Analyzer</span></span>
        </a>
        <div className="topbar-right"><span className="status-dot" /> API-powered workspace</div>
      </header>

      <main id="top" className="page-content">
        <section className="hero">
          <div className="hero-copy">
            <span className="eyebrow">ALGORITHM LAB <span className="eyebrow-line" /></span>
            <h1>Understand what<br />happens <em>under the hood.</em></h1>
            <p>Interactive platform for executing, understanding, and analyzing fundamental algorithms.</p>
          </div>
          <div className="hero-stat"><strong>{algorithms.length.toString().padStart(2, '0')}</strong><span>algorithms<br />in the library</span></div>
        </section>

        <section className="library-section">
          <div className="section-heading library-heading">
            <div><span className="eyebrow">01 / SELECT</span><h2>Algorithm library</h2></div>
            <span className="section-meta">{listLoading ? 'Loading library…' : `${algorithms.length} available`}</span>
          </div>
          {pageError && <div className="page-error" role="alert"><strong>Could not load algorithms</strong><span>{pageError}</span><button onClick={() => window.location.reload()} type="button">Try again</button></div>}
          {!pageError && listLoading && <div className="loading-library">Connecting to the algorithm service…</div>}
          {!pageError && !listLoading && <div className="algorithm-grid">
            {algorithms.map((algorithm) => <AlgorithmCard key={algorithm.id} algorithm={algorithm} busy={running}
              selected={algorithm.id === selectedId} onSelect={chooseAlgorithm} />)}
          </div>}
        </section>

        {selectedCard && !pageError && <section className="workspace">
          <div className="workspace-main">
            <div className="section-heading selected-heading">
              <div>
                <span className="eyebrow">ALGORITHM DETAILS</span>
                <div className="selected-title-row"><h2>{selected?.name || selectedCard.name}</h2><span className="category-pill">{selected?.category || selectedCard.category}</span></div>
                <p className="details-note">{detailsLoading ? 'Loading metadata…' : selected?.notes || 'Metadata supplied by the algorithm registry.'}</p>
                {selected?.id === 'binary-search' && <p className="binary-note">The current implementation sorts the input before performing the binary search.</p>}
              </div>
              <span className="step-number">02</span>
            </div>
            {selected && <ComplexityGrid complexity={selected.complexity} />}
            {selected && <InputPanel key={selected.id} algorithm={selected} busy={running || detailsLoading} onRun={run} />}
            {!selected && !detailsLoading && <div className="page-error" role="alert">{runError || 'Algorithm metadata is unavailable.'}</div>}
          </div>
          <ResultPanel result={result} error={runError} algorithm={selected || selectedCard} />
        </section>}
        <footer className="footer"><span>Algorithm Analyzer</span><span>Powered by the existing Servlet API</span></footer>
      </main>
    </div>
  );
}
