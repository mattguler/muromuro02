// Any custom vanilla JS functionality used by Muromuro goes into this file.

// This is to help resolve the HTMX issues with Spring Security.
// This solution is inspired from:
// https://dimitri.codes/spring-boot-htmx-intro/
// And from:
// https://stackoverflow.com/questions/75732590/spring-security-thymeleaf-htmx-403-error
function configureHtmxWithCsrf() {
    document.body.addEventListener('htmx:configRequest', (evt) => {
          evt.detail.headers['accept'] = 'text/html-partial';
          if (evt.detail.verb !== 'get') {
              const csrfHeader = document.querySelector('meta[name=csrf-header]').getAttribute('content');
              const csrfToken = document.querySelector('meta[name=csrf-token]').getAttribute('content');
              if (csrfHeader != null && csrfToken != null) {
                  evt.detail.headers[csrfHeader] = csrfToken;
              }
          }
    });
}

// Initializes the CodeMirror editor in your page. Also initializes the
// hidden code input value.
// Make sure you have divs with IDs "editor" and "main-def-input".
function setupCodeMirror(initialMainDef) {
    // Also initialize the hidden code input value.
    document.getElementById("main-def-input").value = initialMainDef;
    const initialState = cm6.createEditorState(initialMainDef, "main-def-input");
    const view = cm6.createEditorView(initialState, "editor");
}
