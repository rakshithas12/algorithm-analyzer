import ComplexityGrid from './ComplexityGrid.jsx';

export default function ResultPanel({ result, error, algorithm }) {
  return (
    <section className="result-panel" aria-live="polite">
      <div className="section-heading">
        <div><span className="eyebrow">EXECUTION OUTPUT</span><h2>Result</h2></div>
        {result && <span className="success-pill"><span /> Complete</span>}
      </div>
      {error && <div className="result-error" role="alert"><strong>Run could not be completed</strong><span>{error}</span></div>}
      {!result && !error && <div className="empty-result">
        <div className="empty-icon" aria-hidden="true">⌁</div>
        <strong>Your result will appear here</strong><span>Select an algorithm and run it to see the output.</span>
      </div>}
      {result && <>
        <div className="result-summary">
          <span className="result-name">{algorithm.name}</span><span className="result-message">{result.message}</span>
        </div>
        <div className="io-grid">
          <div className="io-card"><span>INPUT</span><code>{JSON.stringify(result.input)}</code></div>
          <div className="io-card output-card"><span>OUTPUT</span><code>{JSON.stringify(result.output)}</code></div>
        </div>
        {result.found !== undefined && <div className={`search-outcome ${result.found ? 'found' : 'not-found'}`}>
          <span className="outcome-mark">{result.found ? '✓' : '–'}</span>
          <span>{result.found ? `Target found${result.searchIndex !== undefined ? ` at index ${result.searchIndex}` : ''}.` : 'Target was not found.'}</span>
        </div>}
        {result.sortedValues && <p className="binary-note">Sorted before searching: <code>{JSON.stringify(result.sortedValues)}</code></p>}
        {result.sum !== undefined && <div className="sum-result">Sum <strong>{result.sum}</strong></div>}
        <div className="result-complexity"><span className="eyebrow">COMPLEXITY</span><ComplexityGrid complexity={result.complexity} /></div>
      </>}
    </section>
  );
}
