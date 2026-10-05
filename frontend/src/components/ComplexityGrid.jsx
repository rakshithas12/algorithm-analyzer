const items = [['Best case', 'best'], ['Average case', 'average'], ['Worst case', 'worst'], ['Auxiliary space', 'space']];

export default function ComplexityGrid({ complexity }) {
  return (
    <div className="complexity-grid">
      {items.map(([label, key]) => (
        <div className="complexity-card" key={key}>
          <span>{label}</span><strong>{complexity?.[key] || '—'}</strong>
        </div>
      ))}
    </div>
  );
}
