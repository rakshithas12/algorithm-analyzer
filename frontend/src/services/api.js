const API_ROOT = '/api/algorithms';

async function request(path, options) {
  let response;
  try {
    response = await fetch(`${API_ROOT}${path}`, options);
  } catch {
    throw new Error('Unable to connect to the Algorithm Analyzer backend. Start Tomcat and try again.');
  }

  let body;
  try {
    body = await response.json();
  } catch {
    throw new Error('The backend returned an unreadable response.');
  }

  if (!response.ok) {
    if (response.status === 400 && body?.error) throw new Error(body.error);
    if (response.status === 404) throw new Error('That algorithm or API endpoint was not found.');
    if (response.status >= 500) throw new Error('The backend could not complete the request.');
    throw new Error(body?.error || 'The request could not be completed.');
  }
  return body;
}

export async function getAlgorithms() {
  const algorithms = await request('');
  if (!Array.isArray(algorithms) || algorithms.some((item) => !item?.id || !item?.name || !item?.category || !item?.complexity)) {
    throw new Error('The backend returned an invalid algorithm list.');
  }
  return algorithms;
}

export async function getAlgorithm(id) {
  const algorithm = await request(`/${encodeURIComponent(id)}`);
  if (!algorithm?.id || !algorithm?.complexity) throw new Error('The backend returned invalid algorithm metadata.');
  return algorithm;
}

export async function executeAlgorithm(id, payload) {
  const result = await request(`/${encodeURIComponent(id)}/execute`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
    body: JSON.stringify(payload),
  });
  if (!result || typeof result.success !== 'boolean' || !result.algorithm || !Array.isArray(result.input)
      || !('output' in result) || !result.complexity || typeof result.message !== 'string') {
    throw new Error('The backend returned an invalid execution result.');
  }
  if (!result.success) throw new Error(result.error || 'The algorithm could not be executed.');
  return result;
}
