# AlgorithmAnalyzer

A Java Dynamic Web Project using Servlets, JSP, JDBC, MySQL, and Apache Tomcat.

## Login

The login check currently uses hardcoded demo credentials in `LoginServlet.java`. Authentication is not suitable for a public deployment.

## MySQL Database Feature

When the user selects an algorithm from the dashboard, the request goes to `AlgorithmServlet` using:

```text
algo?algorithm=bubbleSort
```

The servlet connects to MySQL, creates the database/tables if needed, fetches the selected algorithm's name, description, complexity, and steps, then forwards them to `input.jsp`.

JDBC connection settings are configured as context parameters in `src/main/webapp/WEB-INF/web.xml`. The checked-in file uses `YOUR_MYSQL_USERNAME` and `YOUR_MYSQL_PASSWORD` placeholders. For local testing, replace them in your local copy before running the app, then restore the placeholders before committing. This project currently reads credentials from `web.xml` and has no external secret provider.

The app creates these tables automatically:

- `algorithms`
- `algorithm_steps`

Seeded algorithms:

- Bubble Sort
- Linear Search
- Selection Sort
- Insertion Sort
- Binary Search
- Addition

## Project Structure

```text
src/main/java/com/algorithm/controller/
  LoginServlet.java
  AlgorithmServlet.java

src/main/java/com/algorithm/algorithm/
  Algorithm.java
  AlgorithmRegistry.java
  AlgorithmResult.java
  ComplexityInfo.java
  sorting/, searching/, math/

src/main/java/com/algorithm/service/
  AlgorithmService.java

src/main/java/com/algorithm/api/
  AlgorithmApiServlet.java
  AlgorithmMetadataResponse.java
  ApiExecutionResponse.java
  ApiErrorResponse.java

src/main/java/com/algorithm/dao/
  AlgorithmDAO.java

src/main/java/com/algorithm/model/
  AlgorithmInfo.java

src/main/webapp/
  login.jsp
  home.jsp
  input.jsp
  result.jsp
  css/style.css
  WEB-INF/web.xml

src/test/java/
  com/algorithm/algorithm/AlgorithmEngineTest.java
  com/algorithm/service/AlgorithmServiceTest.java
  com/algorithm/api/AlgorithmApiServletTest.java
```

## Run In Eclipse

1. Open Eclipse IDE for Enterprise Java and Web Developers.
2. Import using `File > Import > Maven > Existing Maven Projects`.
3. Select the `AlgorithmAnalyzer` folder.
4. In your local copy of `src/main/webapp/WEB-INF/web.xml`, replace the MySQL username and password placeholders with your local credentials. Restore the placeholders before committing; do not commit personal values.
5. Right-click the project and choose `Maven > Update Project`.
6. Right-click the project and choose `Run As > Run on Server`.
7. Select Apache Tomcat 9.

Open:

```text
http://localhost:8080/AlgorithmAnalyzer/
```

This project uses `javax.servlet-api`, Servlet 4.0, MySQL Connector/J, and Tomcat 9.

## Tests

JUnit 4 tests cover algorithm behavior, registry identifiers, service input handling, and REST API requests. Run them with `mvn test` when Maven is installed.

## REST API

The Servlet-based JSON API is available at:

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/algorithms` | List registered algorithms and complexity metadata |
| GET | `/api/algorithms/{id}` | Get one algorithm; IDs use kebab case, such as `bubble-sort` |
| POST | `/api/algorithms/{id}/execute` | Execute an algorithm |

Example sort request:

```json
{"values":[5,2,8,1]}
```

Example search request:

```json
{"values":[5,2,8,1],"target":8}
```

Example success response:

```json
{
  "success": true,
  "algorithm": "bubble-sort",
  "input": [5, 2, 8, 1],
  "output": [1, 2, 5, 8],
  "message": "Sorted Array: [1, 2, 5, 8]",
  "complexity": {"best":"Θ(n²)","average":"Θ(n²)","worst":"Θ(n²)","space":"O(1)"}
}
```

Errors use JSON with `success: false` and an `error` message. Invalid requests return HTTP 400; unknown algorithms/routes return HTTP 404. Search responses include a `found` flag and, when found, `searchIndex`; Binary Search also returns `sortedValues`. Addition returns its integer sum. Binary Search sorts the input first; its complexity metadata describes the search portion and includes a note about the sort. The API is unauthenticated and does not enable cross-origin access; local React development uses the Vite proxy below.

## React Frontend

The separate React + Vite client lives in `frontend/`. It loads algorithm metadata from `/api/algorithms` and uses the Vite development proxy to forward `/api` requests to the Tomcat context at `http://localhost:8080/AlgorithmAnalyzer`. Start the Java application on that URL first, then run:

```text
cd frontend
npm install
npm run dev
```

Set `VITE_TOMCAT_URL` before starting Vite if Tomcat uses a different base URL. Run `npm run build` to create the frontend build in `frontend/dist/`. The legacy JSP interface remains available while the React frontend is being developed.

## Current Limitations

- The demo login remains hardcoded. JDBC credentials must be configured locally and kept out of source control.
- The REST API is unauthenticated. The application still has no separate frontend; React is planned for a future phase.
- It does not persist user runs or provide benchmarking, execution-time measurements, or algorithm visualizations.

## Algorithm Engine Architecture

Both the existing JSP workflow and the JSON API use the same service and algorithm engine:

```text
JSP forms → AlgorithmServlet ─┐
                              ├→ AlgorithmService → AlgorithmRegistry
JSON API  → AlgorithmApiServlet┘                      ↓
                                           Algorithm implementations
                                                      ↓
                                               AlgorithmResult
```

The registry provides the six existing algorithms and their complexity metadata. The service parses the current form inputs and delegates execution. Algorithm results contain structured input/output information and the user-facing result text; no runtime measurements are generated. Algorithm descriptions and instructional steps continue to come from the existing MySQL tables. Binary Search sorts the submitted values before searching, so its `O(log n)` metadata describes the search portion only; the complete operation also includes sorting.

Input parsing rejects empty comma-separated values, including a trailing comma. Addition expects exactly two integers; Java `int` overflow behavior is preserved.
