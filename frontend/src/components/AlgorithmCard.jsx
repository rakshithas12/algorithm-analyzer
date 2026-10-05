export default function AlgorithmCard({ algorithm, selected, busy, onSelect }) {
  return (
    <button className={`algorithm-card${selected ? ' selected' : ''}`} type="button"
      aria-pressed={selected} disabled={busy} onClick={() => onSelect(algorithm.id)}>
      <span className="card-topline">
        <span className="category-pill">{algorithm.category}</span>
        <span className="card-arrow" aria-hidden="true">↗</span>
      </span>
      <span className="algorithm-card-title">{algorithm.name}</span>
      {algorithm.notes && <span className="algorithm-card-note">{algorithm.notes}</span>}
      <span className="card-complexity">
        <span>Worst {algorithm.complexity?.worst || '—'}</span>
        <span>Space {algorithm.complexity?.space || '—'}</span>
      </span>
    </button>
  );
}
