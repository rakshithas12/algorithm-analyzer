import { useState } from 'react';

function parseValues(text) {
  const parts = text.split(',').map((part) => part.trim());
  if (!text.trim() || parts.some((part) => !part)) throw new Error('Enter comma-separated integers.');
  const values = parts.map(Number);
  if (values.some((value) => !Number.isInteger(value) || value < -2147483648 || value > 2147483647)) {
    throw new Error('Each value must be a 32-bit integer.');
  }
  return values;
}

export default function InputPanel({ algorithm, busy, onRun }) {
  const [valuesText, setValuesText] = useState(algorithm.id === 'addition' ? '10, 20' : '5, 2, 8, 1');
  const [targetText, setTargetText] = useState('8');
  const [validationError, setValidationError] = useState('');
  const isSearch = algorithm.category === 'SEARCHING';
  const isAddition = algorithm.id === 'addition';

  function submit(event) {
    event.preventDefault();
    setValidationError('');
    try {
      const values = parseValues(valuesText);
      if (isAddition && values.length !== 2) throw new Error('Addition requires exactly two integers.');
      const payload = { values };
      if (isSearch) {
        if (!targetText.trim()) throw new Error('Enter a target value to search for.');
        const target = Number(targetText.trim());
        if (!Number.isInteger(target) || target < -2147483648 || target > 2147483647) {
          throw new Error('Target must be a 32-bit integer.');
        }
        payload.target = target;
      }
      onRun(payload);
    } catch (error) {
      setValidationError(error.message);
    }
  }

  return (
    <form className="input-panel" onSubmit={submit}>
      <div className="section-heading compact-heading">
        <div><span className="eyebrow">YOUR INPUT</span><h3>Set up a run</h3></div>
        <span className="step-number">02</span>
      </div>
      <label htmlFor="values">{isAddition ? 'Two integers' : 'Values'}</label>
      <input id="values" value={valuesText} onChange={(event) => setValuesText(event.target.value)}
        placeholder={isAddition ? '10, 20' : '5, 2, 8, 1'} autoComplete="off" required />
      <p className="field-hint">Separate whole numbers with commas.</p>
      {isSearch && <>
        <label htmlFor="target">Target</label>
        <input id="target" type="number" step="1" value={targetText}
          onChange={(event) => setTargetText(event.target.value)} placeholder="8" required />
      </>}
      {isAddition && <p className="field-hint addition-hint">Addition accepts exactly two integers.</p>}
      {validationError && <div className="inline-error" role="alert">{validationError}</div>}
      <button className="run-button" type="submit" disabled={busy}>
        {busy ? <><span className="spinner" /> Running…</> : <>Run algorithm <span aria-hidden="true">→</span></>}
      </button>
    </form>
  );
}
