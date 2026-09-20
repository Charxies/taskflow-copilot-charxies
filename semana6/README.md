# Proyecto final · Semana 6 · GitHub Copilot

> Plantilla del documento del proyecto final. Se copia a `semana6/README.md` de tu repo
> `taskflow-copilot-charxies` y se llena **con datos de tu repo**: cada sección dice de dónde
> sale cada valor. Borra las líneas que empiezan con `>` cuando termines.

**Alumno:** `Carlos Urie Celestino Cautista` · **Usuario de GitHub:** `<tu-usuario>`

## 1. Qué construí

> Marca **una** fila con `x` y borra las otras dos.

| | Feature | Especificación |
|---|---|---|
| [ x] | `GET /reports/progress` — avance por proyecto | [`specs/progress.md`](../specs/progress.md) |

## 2. El pull request

- **URL del PR (mergeado):** `https://github.com/Charxies/taskflow-copilot-charxies/pull/5/`
- **Commit del merge en `main`:** `f853dacc Merge pull request #5 from Charxies/feat/progresspr`
- **Comentarios de Copilot code review:** `4`

## 3. Cómo lo hice

> Una fila por paso. La columna «Evidencia» apunta a un archivo de esta carpeta o a un comando que
> cualquiera puede repetir.

| Paso | Qué hice | Evidencia |
|---|---|---|
| Rama y spec | `git switch -c feature/progresspr` y copié la spec a `specs/` | `git log --oneline main..feature/progress` (antes del merge) |
| Implementación | `copilot -p "/crear-endpoint-taskflow …"` con `gpt-5-mini` | `semana6/sesion-implementacion.md` (tiene la línea `Skill "crear-endpoint-taskflow" loaded successfully`) |
| Revisión | agente `revisor` sobre `semana6/proyecto-final.diff` | `semana6/revision.md` (termina con `Veredicto:`) |
| Tests | `mvn test` en verde | `Tests run: 80, Failures: 0, Errors: 0, Skipped: 0` |
| Comprobación REST | `verificar.ps1` con `casos-progress.ps1` | sección 5 de este documento |
| Code review | Copilot en el PR | la pestaña *Files changed* del PR |

## 4. Qué hizo el agente y qué corregí yo

> Una fila por cada cosa que **no** quedó como la pedía la spec o la checklist: la encontró el
> revisor, Copilot code review, un test, `verificar.ps1` o tú leyendo el diff. Si el agente la
> corrigió con un prompt tuyo, dilo; si la corregiste a mano, también. Los hallazgos **falsos**
> (el revisor o Copilot pidieron algo que no hacía falta) también van: en «Qué hizo mal» escribe lo que
> pidieron y en «Cómo quedó» cómo comprobaste que era falso.

| # | Qué hizo mal el agente (archivo) | Quién lo detectó | Cómo quedó corregido |
|---|---|---|---|
| 1 | `aveces utilizaba herramientas que no eran necesarias en bastantes prompts, ademas no corria los tests donde se debia` | `yo` | `el prompt de correcion solo era pasarle a ruta correcta` |

**Lo que el agente hizo bien a la primera** (una o dos líneas): `<la mayoria de los archivos, solo toque el archivo de verificaciones para introducir la feat y eso es todo>`

## 5. Comprobaciones REST

> Pega completa la salida de
> `pwsh -NoProfile -File .github/skills/verificar-taskflow/verificar.ps1` en la rama `main` después
> del merge (la última línea es `RESULTADO:`).

```text
URL de la app: http://127.0.0.1:8080
Empaquetando con Maven (mvn -q package -DskipTests), tarda unos segundos...
App arrancando (PID 15952). Esperando a que /info responda...
App lista en 10 s.
[OK]    GET /tasks/overdue devuelve solo la tarea 7
[OK]    GET /tasks/unassigned devuelve las tareas 4 y 6
[OK]    GET /projects/1/summary
[OK]    GET /projects/2/summary
[OK]    GET /projects/3/summary
[OK]    GET /projects/99/summary responde 404
[OK]    GET /projects/1/summary sin token responde 401
[OK]    GET /reports/progress devuelve los 3 proyectos con la semilla
[OK]    El proyecto 1 se llama «Plataforma TaskFlow»
[OK]    Tras pasar la tarea 1 a DONE, el proyecto 1 queda en 2/5 = 40
[OK]    GET /reports/progress sin token responde 401
App detenida (PID 15952).
[OK]    App apagada: el puerto 8080 ya no responde
RESULTADO: 12/12 OK
```

## 6. Créditos de la semana

> **De dónde sale cada número.** El total del mes: `https://github.com/settings/billing`, en el
> resumen por producto, el de Copilot (con **View details** ves el detalle). Los del proyecto final:
> la línea `AI Credits` que imprime cada `copilot -p` al terminar.

| Qué | AI credits |
|---|---|
| Usados en septiembre según github.com (incluye semanas anteriores si usaste Copilot antes) | `359` |
| Implementación con la skill (`AI Credits` del PF-2) | `4.9` |
| Revisión del `revisor` (`AI Credits` del PF-3) | `1.21` |
| Correcciones del PF-4 y del PF-6, si hubo (`AI Credits`) | `0` |
